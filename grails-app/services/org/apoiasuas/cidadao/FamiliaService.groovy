package org.apoiasuas.cidadao

import grails.gorm.PagedResultList
import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.sql.Sql
import org.apoiasuas.redeSocioAssistencial.Acesso
import org.apoiasuas.util.Credencial
import org.apoiasuas.util.HqlPagedResultList
import org.apoiasuas.util.StringUtils
import org.hibernate.SQLQuery
import org.hibernate.Session

@Service(Familia)
@Transactional(readOnly = true)
abstract class FamiliaService {

    public abstract Familia get(Serializable id)

    public abstract List<Familia> list(Map args)

    public abstract Long count()

    @Transactional
    public abstract void delete(Serializable id)

    @Transactional
    public abstract Familia save(Familia familia)

    @Transactional
    public void grava(Familia familia, Credencial credencial) {
        boolean nova = (familia.id == null);
        Cidadao novaReferencia = null;
        HistoricoFamilia acao = null;
        if (nova) {
            if (credencial.acesso == Acesso.ENCAMINHAMENTO) {
                familia.situacao = SituacaoPrograma.INDICADA_SERVICO;
                familia.servicoSistemaSeguranca = credencial.servicoSistema;
            } else if (credencial.acesso == Acesso.GESTAO)
                familia.situacao = SituacaoPrograma.PRE_SELECIONADA;
            else {
                throw new RuntimeException("Acesso negado a Sugestão de Família")
            }
            novaReferencia = new Cidadao(
                    nome: familia.nomeReferencia,
                    NIS: familia.nisReferencia,
                    parentesco: Cidadao.PARENTESCO_REFERENCIA,
                    familia: familia
            );
            familia.membros << novaReferencia;
        }
        this.save(familia);
        acao = new HistoricoFamilia(
                familia: familia,
                data: new Date(),
                acao: familia.situacao,
                usuarioSistema: credencial.usuarioSistema,
                servicoSistemaSeguranca: credencial.servicoSistema,
        );
        acao.save();
    }

    @Transactional
    public void registrarConcessao(Familia familia, Credencial credencial) {
        //validacoes
        if (! credencial.acesso in [Acesso.ATENDIMENTO, Acesso.GESTAO])
            throw RuntimeException("Operador não autorizado a registrar concessão de cesta");
        if (familia.situacao != SituacaoPrograma.INSERIDA)
            throw RuntimeException("Família não inserida no programa");

        ConcessaoFamilia novaConcessao = new ConcessaoFamilia(
                familia: familia,
                usuarioSistema: credencial.usuarioSistema,
                servicoSistemaSeguranca: credencial.servicoSistema,
                data: new Date()
        )
        novaConcessao.save();
    }

    public ConcessaoFamilia getUltimaConcessao(Familia familia) {
        ConcessaoFamilia ultima = ConcessaoFamilia.findByFamilia(familia,
               [max: 1, offset: 0, sort: "data", order: "desc"])
        return ultima;
    }

    /**
     * Retorna uma lista contendo instancias tanto de HistoricoFamilia quanto de ConcessoesFamilia, intercaladas por data decrescente
     * @param familia
     * @return
     */
    public List getHistoricoCompleto(Familia familia) {
        List<ConcessaoFamilia> concessoes = ConcessaoFamilia.findAllByFamilia(familia,
               //apenas as 20 mais recentes, para que nao polua a tela
               [max: 20, offset: 0, sort: "data", order: "desc"]);
        List<HistoricoFamilia> acoes = HistoricoFamilia.findAllByFamilia(familia,
               //apenas as 20 mais recentes, para que nao polua a tela
               [max: 20, offset: 0, sort: "data", order: "desc"]);
        //Une as concessoes de cestas e o historico da familia em uma unica lista
        def result = [];
        result.addAll(concessoes);
        result.addAll(acoes);
        //ordenada a lista por data decrescente
        return result.sort { it.data }.reverse();
    }

    public HqlPagedResultList procurar(GrailsParameterMap params, FiltroFamiliaCommand filtrosOperador) {
        String filtroNome
        String filtroNis
        if (filtrosOperador?.nomeOuNis) {
            if (StringUtils.PATTERN_TEM_LETRAS.matcher(filtrosOperador.nomeOuNis))
                filtroNome = filtrosOperador.nomeOuNis
            else
                filtroNis = filtrosOperador.nomeOuNis
        }

        def filtrosSql = [:]

        String sqlSelectCount = "select count(*) ";
        String sqlSelectList = "select distinct {a.*}, {b.*} ";
        String sqlFrom = " FROM cidadao a LEFT OUTER JOIN familia b ON a.familia_id=b.id "
//                + " LEFT OUTER JOIN usuario_sistema c ON a.operador_responsavel_id = c.id ";

//================= FILTRAGEM ==============

        String sqlWhere = ' where 1=1 ';

        //Se não estiver procurando por um individuo especifico, filtrar as familias
        if (! filtrosOperador.nomeOuNis) {
            sqlWhere += ' and a.parentesco = :parentesco ';
            filtrosSql.put('parentesco', Cidadao.PARENTESCO_REFERENCIA)
        }

//                + 'and a.servico_sistema_seguranca_id = :servicoSistema'
//        filtrosSql << [servicoSistema: segurancaService.getServicoLogado()]

        if (filtrosOperador.situacao) {
            sqlWhere += ' and b.situacao = :situacao';
            filtrosSql.put('situacao', filtrosOperador.situacao.name())
        }

        if (filtrosOperador.servicoSistema) {
            sqlWhere += ' and b.servico_id = :servicoSistema';
            filtrosSql.put('servicoSistema', filtrosOperador.servicoSistema.id)
        }

        if (filtroNis) {
            sqlWhere += ' and a.nis = :nis'
            filtrosSql << [nis: filtroNis]
        }

        String[] nomes = filtroNome?.split(" ");
        nomes?.eachWithIndex { nome, i ->
            String label = 'nome'+i
            sqlWhere += " and lower(a.nome) like :"+label+" "
            filtrosSql.put(label, '%'+nome?.toLowerCase()+'%')
        }

//================= ORDENACAO ==============

        String sqlOrder = ' order by a.id desc ';

//================= EXECUCAO ==============

//        Holders.getGrailsApplication().mainContext?.
//        def sql = new Sql(dataSource);
//        def employeeList = sql.rows(query);
        Familia.withSession { Session sess ->

    //        Session sess = sessionFactory.getCurrentSession();
            SQLQuery queryCount = sess.createSQLQuery(sqlSelectCount + sqlFrom + sqlWhere);

            SQLQuery queryList = sess.createSQLQuery(sqlSelectList + sqlFrom  + sqlWhere + sqlOrder)
                    .addEntity("a", Cidadao.class)
                    .addJoin("b", "a.familia") //eager join for better performance
    //                .addJoin("c", "a.operadorResponsavel"); //eager join for better performance
            queryList.setFirstResult(params.offset ? new Integer(params.offset) : 0);
            queryList.setMaxResults(params.max ? new Integer(params.max) : 20);

            filtrosSql.each { key, value ->
                queryCount.setParameter(key, value);
                queryList.setParameter(key, value);
            }
            Integer count = queryCount.uniqueResult();
            List resultado = queryList.list();
            List<Cidadao> cidadaos = [];

            Iterator<Cidadao> iterator = resultado.iterator()
            while (iterator.hasNext()) {
                Cidadao cidadao = iterator.next()[0] //seleciona o cidadao e ignora o resto
                cidadaos << cidadao;
            }
            return new HqlPagedResultList(cidadaos, count)
        }

//==============================================

    }


}