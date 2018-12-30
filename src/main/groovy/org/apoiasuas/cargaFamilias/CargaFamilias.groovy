package org.apoiasuas.cargaFamilias

import ch.qos.logback.classic.Level
import ch.qos.logback.core.util.StatusPrinter
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.exceptions.CsvRequiredFieldEmptyException
import grails.validation.ValidationException
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.SituacaoPrograma
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.grails.orm.hibernate.HibernateDatastore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//@Slf4j
class CargaFamilias {

    final static Logger log = LoggerFactory.getLogger(CargaFamilias.class);
/*
    def static log = new Object() {
        public void debug(String a) {
            println(a);
        }
    }
*/

    static final String JDBC_DRIVER = "org.postgresql.Driver";
//    static final String DB_URL = "jdbc:postgresql://localhost:5432/sa";
    static final String DB_URL = "jdbc:p6spy:postgresql://localhost:5432/sa";

    //  Database credentials
    static final String USER = "postgres";
    static final String PASS = "senha";
    public static final String FORA_AREA_CRAS = "Endereço Fora Área CRAS";
    public static final String NAO_LOCALIZADO = "Endereço não georreferenciado";
    public static final String DESCRICAO_EPSB = "EPSB";

    public static void arquivado() {
        System.out.println('rodou' + SituacaoPrograma.INSERCAO_RECUSADA_GESTAO);
        ClassLoader.systemClassLoader.URLs.each{ println it }

        // Read in 'config.groovy' for the development environment.
//        ConfigObject conf = new ConfigSlurper("development").parse(new File("application.groovy").toURI().toURL());

//        def a = ClassLoader.systemClassLoader;
        final GroovyClassLoader a = new GroovyClassLoader();
        Class scriptClass = a.parseClass('segurancaalimentar.Application')
//        Class scriptClass = getClass().classLoader.loadClass('application')
//        CargaFamilias.getResource()
        ConfigObject config = new ConfigSlurper().parse(scriptClass);

        log.debug('a');
        log.debug(config.toString());
    }

    static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Locale.setDefault(new Locale('pt', 'BR'));
        log.debug("Data ${new Date()}");
        bd();
        try {
            csv();
        } catch (Exception e) {
            log.error("Erro carregando arquivo de famílias sugeridas para o programa", e);
            log.debug(e.message);
        }
    }

    private static void csv() {
        InputStream is = new BufferedInputStream(new FileInputStream("C:\\Dropbox\\develop\\segalim\\data\\cargaFamilia.csv"),30000);
        EstruturaBusca estruturaBusca;

        try {
            is.mark(0);
                estruturaBusca = processaCsv_Passo1(is, "ISO-8859-1");
        } catch (Exception e) {
            if (e instanceof CsvRequiredFieldEmptyException || e.cause instanceof CsvRequiredFieldEmptyException) {
                //erro na validacao dos cabecalhos pode-se tentar novamente com outro mapa de caracteres
                log.warn("Não conseguiu ler usando ISO-8859-1");
                is.reset();
            estruturaBusca = processaCsv_Passo1(is, "UTF-8");
            }
        } finally {
            is?.close();
        }
        Familia.withTransaction { status ->

            Map<String, ServicoSistema> servicos = ServicoSistema.findAll().collectEntries{ [(it.nome): it] } as HashMap;

            //Primeiramente, tenta buscar familias correspondentes para aquelas que nunca foram sincronizadas (indicadas diretamente pelos servicos)
            //usando o NIS da referencia como criterio de busca
            Familia.findAllBySincronizadoCadUnico(false).each { Familia familia ->
                List<CidadaoCarga> membrosCarga = estruturaBusca.nisMembro_membrosDaFamilia.get(familia.nisReferencia);

                if (membrosCarga) {
                    //encontrada uma familia correspondente no CSV carregado. atualiza o codigo familiar para que possa ser encontrada
                    //no proximo passo do processamento
                    familia.codigoFamiliar = membrosCarga[0].codigoFamiliar
                    familia.save(flush: true);
                    estruturaBusca.familiasSincronizadas++;
                    log.info("Familia 'indicada' identificada para sinconrizacao: NIS ${familia.nisReferencia}, codigo familiar ${familia.codigoFamiliar} ");
//                    processaFamilia_Passo2(familia, membrosCarga, servicos, estruturaBusca.erros);
                }
            }

            //atualiza/insere familias e seus membros no banco de dados
            estruturaBusca.codigoFamiliar_membrosDaFamilia.each { String codigoFamiliar, List<CidadaoCarga> membrosCarga ->
                Familia aFamilia = Familia.findByCodigoFamiliar(codigoFamiliar, [fetch: [membros: 'join']]);
                boolean familiaExistente = (aFamilia != null);
                if (! familiaExistente)
                    aFamilia = new Familia(codigoFamiliar: codigoFamiliar, situacao: SituacaoPrograma.PRE_SELECIONADA);

                if (processaFamilia_Passo2(aFamilia, membrosCarga, servicos, estruturaBusca)) {
                    if (familiaExistente)
                        estruturaBusca.familiasAtualizadas++
                    else
                        estruturaBusca.familiasNovas++;
                }

            }
        }

        estruturaBusca.logResultados();

        log.debug("Fim "+new Date())
    }

    /**
     * Realiza o parsing do CSV passado como parametro e retorna uma estrutura de dados (EstruturaBusca) contendo as
     * familias e membros obtidos em dois mapas distintos (alem de uma lista de possiveis erros na leitura do CSV)
     */
    private static EstruturaBusca processaCsv_Passo1(InputStream is, String mapaCaracteres) {
        Reader r = new InputStreamReader(is, mapaCaracteres);
        CsvToBean<CidadaoCarga> csvToBean = new CsvToBeanBuilder(r)
                .withSeparator(';' as char)
                .withIgnoreLeadingWhiteSpace(true)
//                    .withCSVParser(null)
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .withType(CidadaoCarga.class).build();

        EstruturaBusca result = new EstruturaBusca();
        csvToBean.getCapturedExceptions().each {
            result.erros << new CidadaoCarga(linha: it.lineNumber, erro: it.message)
        }

        //Armazenando na estrutura "codigoFamiliar_membrosFamilia"
        csvToBean.iterator().eachWithIndex {CidadaoCarga cidadaoCarga, Integer linha ->
            cidadaoCarga.linha = linha+1;
            if (! cidadaoCarga.codigoFamiliar)
                return;

            List<CidadaoCarga> membrosDaFamilia = result.codigoFamiliar_membrosDaFamilia.get(cidadaoCarga.codigoFamiliar);
            if (! membrosDaFamilia)
                //inicializa nova lista de membros e a guarda no mapa
                result.codigoFamiliar_membrosDaFamilia.put(cidadaoCarga.codigoFamiliar, [cidadaoCarga])
            else
                //adiciona o membro aa lista ja existente
                membrosDaFamilia << cidadaoCarga;
        }

        //Armazenando na estrutura "nis_membrosDaFamilia"
        result.codigoFamiliar_membrosDaFamilia.each { String codigoFamiliar, List<CidadaoCarga> membrosDaFamilia ->
            membrosDaFamilia?.each { CidadaoCarga membro ->
                if (! membro.nis)
                    return;
                result.nisMembro_membrosDaFamilia.put(membro.nis, membrosDaFamilia)
            }
        }

        return result;
    }

    /**
     * Atualiza/insere no banco os dados da familia e de seus membros
     * IMPORTANTE: presume uma transacao de banco aberta
     */
    private static boolean processaFamilia_Passo2(Familia aFamilia, List<CidadaoCarga> membrosCarga, Map<String, ServicoSistema> servicos, EstruturaBusca estruturaBusca) {
        List<String> tmpErros = [];
        Integer linha = null;
        String nisReferencia = null;
        try {
            //atualiza existentes ou insere novos membros
            CidadaoCarga referenciaCarga = null;
            membrosCarga.each { CidadaoCarga membroCarga ->
                //verifica se o cidadao ja existe ou cria um novo
                Cidadao cidadao = aFamilia.membros.find{ it.NIS == membroCarga.nis } ?: new Cidadao(NIS: membroCarga.nis);

                preencheCidadao(cidadao, membroCarga);

                aFamilia.addToMembros(cidadao);
                //caso exista um parentesco indicativo da referencia, usa o membro correspondente
                if (membroCarga.parentesco == Cidadao.PARENTESCO_REFERENCIA)
                    referenciaCarga = membroCarga
            }

            if (! referenciaCarga) {
                linha = membrosCarga[0].linha;
                tmpErros << "Família sem referência, codigo familiar ${membrosCarga[0].codigoFamiliar}"
            } else {
                linha = referenciaCarga.linha;
                nisReferencia = referenciaCarga.nis;
                if (! preencheFamilia(referenciaCarga, estruturaBusca, servicos, aFamilia, tmpErros))
                    return false;
            }

            if (tmpErros) {
                aFamilia.discard()
                estruturaBusca.familiasComErro++;
                return false;
            } else {
                //Remove os cidadaos que nao estao mais na familia
                List<String> nisMembrosCarga = membrosCarga.collect {it.nis};
                List<Cidadao> tmpMembros = [] + (aFamilia.membros ?: [])
                tmpMembros.each { Cidadao membro ->
                    if (! nisMembrosCarga.contains(membro.NIS))
                        aFamilia.removeFromMembros(membro);
                }

                aFamilia.save(flush: true);
                log.info("Familia processada com sucesso. NIS RF ${aFamilia.nisReferencia}. Linhas ${ membrosCarga.collect{it.linha}.join(', ') }.")
                return true;
            }
        } catch (ValidationException e) {
            estruturaBusca.familiasComErro++;
            tmpErros << e.message;
            return false;
        } finally {
            //Registra os possiveis erros
            tmpErros?.each {
                estruturaBusca.erros << new CidadaoCarga(linha: linha, nis: nisReferencia, erro: it);
            }
        }
    }

    private static boolean preencheFamilia(CidadaoCarga referenciaCarga, EstruturaBusca estruturaBusca, Map<String, ServicoSistema> servicos, Familia aFamilia, ArrayList<String> tmpErros) {
        //vinculação ao serviço
        if (referenciaCarga.nomeServico?.toUpperCase()?.trim() == FORA_AREA_CRAS.toUpperCase().trim())
            referenciaCarga.nomeServico = DESCRICAO_EPSB + " " + referenciaCarga.regional
        else if (referenciaCarga.nomeServico?.toUpperCase()?.trim() == NAO_LOCALIZADO.toUpperCase().trim()) {
            //ignorar familias sem georeferenciamento
            estruturaBusca.familiasSemGeo++;
            return false;
        }
        aFamilia.servicoSistemaSeguranca = servicos.get(referenciaCarga.nomeServico);
        if (!aFamilia.servicoSistemaSeguranca)
            tmpErros << "Impossível identificar o serviço vinculado: " + referenciaCarga.nomeServico;

        //atualiza informacoes da familia usando a linha da referencia
        aFamilia.nisReferencia = referenciaCarga.nis;
        aFamilia.bairro = referenciaCarga.bairro;
        aFamilia.nomeReferencia = referenciaCarga.nome;
        aFamilia.sincronizadoCadUnico = true;

        //...

        return true;
    }

    private static boolean preencheCidadao(Cidadao cidadao, CidadaoCarga membroCarga) {
        cidadao.nome = membroCarga.nome;
        cidadao.dataNascimento = membroCarga.dataNascimento;
        cidadao.parentesco = membroCarga.parentesco;

        //...

        return true;
    }

    private static void bd() {
/*
        Connection conn = null;
        Statement stmt = null;

        //STEP 2: Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        //STEP 4: Execute a query
        System.out.println("Creating statement...");
        stmt = conn.createStatement();
        String sql;
        sql = "select codigo_familiar from familia_tmp order by codigo_familiar";
        ResultSet rs = stmt.executeQuery(sql);
*/

        Map configuration = [
            'hibernate.hbm2ddl.auto':'none',
            'dataSource.url':DB_URL,
            'dataSource.dbCreate':'none',
            'grails.gorm.failOnError':true,
            'grails.gorm.default.constraints': { '*'(nullable: true) },
            'dataSource.dialect':"org.hibernate.dialect.PostgreSQLDialect",

            'dataSource.driverClassName':"com.p6spy.engine.spy.P6SpyDriver",
//            'dataSource.driverClassName':"org.postgresql.Driver",
            'dataSource.username':USER,
            'dataSource.password':PASS,
        ]
        HibernateDatastore datastore = new HibernateDatastore( configuration, Familia, Cidadao, ServicoSistema);

/*
        Familia.withTransaction {
            log.debug(Familia.get(49).nomeReferencia);
            def a = Familia.get(1080);
            a.bairro = "BEBETANIA";
            return a.save();
        }
*/
//        GrailsHibernateTransactionManager transactionManager = datastore.getTransactionManager();
//        transactionManager.
//        datastore.connect();
    }
}
