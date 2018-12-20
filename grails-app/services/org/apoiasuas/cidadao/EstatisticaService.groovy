package org.apoiasuas.cidadao

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.hibernate.SQLQuery
import org.hibernate.Session

@Transactional(readOnly = true)
class EstatisticaService {

    public Map<Date, Integer> getEstatisticaConcessoesMensal(String nomeRegional, Long idServico, Integer ano) {
        String sqlSelect = "select a.mes, sum(a.quantidade) ";
        String sqlFrom = " FROM estatistica_concessoes a join servico b on a.servico_id = b.id ";
        String sqlWhere = ' where 1=1 ';
        String sqlGroup = ' group by a.mes ';
        String sqlOrder = ' order by a.mes ';

        def filtrosSql = [:]

        if (ano) {
            sqlWhere += " and a.mes >= :mesInicial and a.mes < :mesFinal ";
            filtrosSql.put('mesInicial', new GregorianCalendar(ano, 0, 1).getTime());
            filtrosSql.put('mesFinal', new GregorianCalendar(ano+1, 0, 1).getTime());
        }
        if (idServico) {
            sqlWhere += " and b.id = :idServico "
            filtrosSql.put('idServico', idServico)
        } else if (nomeRegional) {
            sqlWhere += " and b.regional = :nomeRegional ";
            filtrosSql.put('nomeRegional', nomeRegional)
        }

        Familia.withSession { Session sess ->

            SQLQuery query = sess.createSQLQuery(sqlSelect + sqlFrom  + sqlWhere + sqlGroup + sqlOrder)

            //por precaução, limita resultado a 2000 registros
            query.setMaxResults(20000);

            filtrosSql.each { key, value ->
                query.setParameter(key, value);
            }

            List resultado = query.list();
            Map<Date, Integer> result = [:]

            Iterator iterator = resultado.iterator()
            while (iterator.hasNext()) {
                def row = iterator.next();
                result.put(row[0], row[1])
            }
            return result;
        }
    }

    public Map<String, Integer> getEstatisticaFamiliasAtendidas(String nomeRegional, Integer ano, Integer mes) {
        String campoAgregacao = (nomeRegional ? "b.nome" /*servico*/ : "b.regional");
        String tabelaOrigem = (mes ? "estatistica_concessoes" : "estatistica_concessoes_familia");

        String sqlSelect = "select $campoAgregacao, sum(quantidade) ";
        String sqlFrom = " FROM $tabelaOrigem a join servico b on a.servico_id = b.id "
        String sqlWhere = " where 1=1 ";
        String sqlGroup = " group by $campoAgregacao ";
        String sqlOrder = " order by $campoAgregacao ";
        def filtrosSql = [:]

        if (nomeRegional) {
            sqlWhere += " and b.regional = :nomeRegional ";
            filtrosSql.put('nomeRegional', nomeRegional)
        }
        if (ano) {
            if (mes) {
                sqlWhere += " and a.mes = :mes ";
                filtrosSql.put('mes', new GregorianCalendar(ano, mes-1, 1).getTime());
            } else { //ano
                sqlWhere += "  and a.ano = :ano";
                filtrosSql.put('ano', new GregorianCalendar(ano, 0, 1).getTime());
            }
        }

        Familia.withSession { Session sess ->

            SQLQuery query = sess.createSQLQuery(sqlSelect + sqlFrom  + sqlWhere + sqlGroup + sqlOrder)

            //por precaução, limita resultado a 2000 registros
            query.setMaxResults(20000);

            filtrosSql.each { key, value ->
                query.setParameter(key, value);
            }

            List resultado = query.list();
            Map result = [:]

            Iterator iterator = resultado.iterator()
            while (iterator.hasNext()) {
                def row = iterator.next();
                result.put(row[0], row[1])
            }
            return result;
        }
    }

    public Map<SituacaoPrograma, Integer> getEstatisticaFamiliasAtual(List<SituacaoPrograma> situacoes,
                                                                      String nomeRegional, Long idServico) {
        String sqlSelect = "select a.situacao, count(a.id) ";
        String sqlFrom = " FROM familia a join servico b on a.servico_id = b.id ";
        String sqlWhere = ' where 1=1 ';
        String sqlGroup = ' group by a.situacao ';
        String sqlOrder = ' order by a.situacao ';

        def filtrosSql = [:]

        if (idServico) {
            sqlWhere += " and b.id = :idServico "
            filtrosSql.put('idServico', idServico)
        } else if (nomeRegional) {
            sqlWhere += " and b.regional = :nomeRegional ";
            filtrosSql.put('nomeRegional', nomeRegional)
        }

        Familia.withSession { Session sess ->

            SQLQuery query = sess.createSQLQuery(sqlSelect + sqlFrom  + sqlWhere + sqlGroup + sqlOrder)

            //por precaução, limita resultado a 2000 registros
            query.setMaxResults(20000);

            filtrosSql.each { key, value ->
                query.setParameter(key, value);
            }

            List resultado = query.list();
            Map<SituacaoPrograma, Integer> result = [:]

            //todas as situacoes previstas devem aparecer, mesmo que nao aparecam na consulta ao banco
            situacoes.each {
                result.put(it, 0)
            }

            Iterator iterator = resultado.iterator()
            while (iterator.hasNext()) {
                def row = iterator.next();
                SituacaoPrograma situacao = SituacaoPrograma.valueOf(row[0]);
                //confere antes se esta eh uma situacao que deve fazer parte do resultado
                if (situacoes.contains(situacao))
                    result.put(situacao, row[1])
            }
            return result;
        }
    }

    public ResultadoEstatistica getEstatisticaHistoricoFamilias(List<SituacaoPrograma> acoes,
                                                                String nomeRegional, Long idServico, Integer ano) {
        String sqlSelect = "select a.mes, a.acao, sum(a.quantidade) ";
        String sqlFrom = " FROM estatistica_historico_familia a join servico b on a.servico_id = b.id ";
        String sqlWhere = ' where 1 = 1 ';
//        String sqlWhere = ' where quantidade > 60 ';
        String sqlGroup = ' group by a.mes, a.acao ';
        String sqlOrder = ' order by a.mes, a.acao ';

        def filtrosSql = [:]

        if (ano) {
            sqlWhere += " and a.mes >= :mesInicial and a.mes < :mesFinal ";
            filtrosSql.put('mesInicial', new GregorianCalendar(ano, 0, 1).getTime());
            filtrosSql.put('mesFinal', new GregorianCalendar(ano+1, 0, 1).getTime());
        }
        if (idServico) {
            sqlWhere += " and b.id = :idServico "
            filtrosSql.put('idServico', idServico)
        } else if (nomeRegional) {
            sqlWhere += " and b.regional = :nomeRegional ";
            filtrosSql.put('nomeRegional', nomeRegional)
        }

        Familia.withSession { Session sess ->

            SQLQuery query = sess.createSQLQuery(sqlSelect + sqlFrom  + sqlWhere + sqlGroup + sqlOrder)

            //por precaução, limita resultado a 2000 registros
            query.setMaxResults(20000);

            filtrosSql.each { key, value ->
                query.setParameter(key, value);
            }

            List resultado = query.list();

            //Inicializacao dos periodos (meses) previstos na resposta
            List<Date> periodos = [];
            for (int i = 0; i<12; i++)
                periodos << new GregorianCalendar(2017, i, 1).getTime(); //mes janeiro = ZERO ... dezembro = 11

            ResultadoEstatistica result = new ResultadoEstatistica(acoes, periodos);

            Iterator iterator = resultado.iterator()
            while (iterator.hasNext()) {
                def row = iterator.next();
                Date periodo = row[0];
                SituacaoPrograma acao = SituacaoPrograma.valueOf(row[1])
                Integer quantidade = row[2];
                result.add(acao, periodo, quantidade);
            }
            return result;
        }
    }
}

@Slf4j
public class ResultadoEstatistica {
    public List<SituacaoPrograma> acoes
    public List<Date> periodos
    public int[][] valores

    public ResultadoEstatistica(List<SituacaoPrograma> acoes, List<Date> periodos) {
        this.acoes = acoes;
        this.periodos = periodos;
        //matriz de quantidades para cada periodo/acao
        this.valores = new int[acoes.size()][periodos.size()];
    }

    public void add(SituacaoPrograma acao, Date periodo, int quantidade) {
        //identifica a posicao da matriz a ser alimentada
        if (periodos.contains(periodo) && acoes.contains(acao))
            valores[acoes.indexOf(acao)][periodos.indexOf(periodo)] += quantidade
        else
            log.error(" [${periodo.format("dd/MM/yyyy")},${acao}]=${quantidade} não previsto no resultado.")
    }

    public List get(SituacaoPrograma acao) {
        //busca a linha da matriz correspondente aa acao desejada
        if (acoes.contains(acao))
            return valores[acoes.indexOf(acao)].toList();
        else
            log.error("ação [${acao}] não prevista no resultado.")
    }

}

/*
public class DatasetEstatistica {
    private Date periodo
    private List<Integer> quantidade
}*/
