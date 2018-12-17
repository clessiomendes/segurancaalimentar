package org.apoiasuas.cidadao

import org.apoiasuas.util.CollectionUtils

import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

class Cidadao {

    public static final String PARENTESCO_REFERENCIA = "Pessoa Responsavel pela Unidade Familiar"

    Familia familia

    String NIS
    String nome
    String parentesco
    Date dataNascimento
    String racaCor

    Boolean deficiencia
    Boolean cegueira
    Boolean baixaVisao
    Boolean surdez
    Boolean deficienciaFisica
    Boolean deficienciaMental
    Boolean sindromeDown
    Boolean transtornoMental
//    Date lastUpdated, dateCreated

    static belongsTo = [familia: Familia] //importado

    static transients = ['idade', 'vulnerabilidades']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_cidadao']
        version column: 'versao'
//        dateCreated column: 'data_criacao', type: 'date'
//        lastUpdated column: 'ultima_alteracao', type: 'date'
    }

    public String getVulnerabilidades() {
        List result=[];
        if (deficiencia)
            result << "deficiência"
        if (cegueira)
            result << "cegueira"
        if (surdez)
            result << "surdez severa/profunda"

        if (baixaVisao)
            result << "baixa visão"
        if (deficienciaFisica)
            result << "deficiência física"
        if (deficienciaMental)
            result << "deficiência mental ou intelectual"
        if (sindromeDown)
            result << "síndrome de Down"
        if (transtornoMental)
            result << "transtorno/doença mental"

        return CollectionUtils.join(result, ", ");
    }

    public Integer getIdade() {
        if (! dataNascimento)
            return null;
        LocalDate date = dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(date, LocalDate.now()).years
    }


}
