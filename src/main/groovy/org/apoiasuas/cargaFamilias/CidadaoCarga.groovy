package org.apoiasuas.cargaFamilias

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvDate

class CidadaoCarga {

    public Integer linha;
    public String erro;

    @CsvBindByName(column = "CÓDIGO FAMILIAR")
    public String codigoFamiliar;

    @CsvBindByName(column = "NÚMERO NIS")
    public String nis;

    @CsvBindByName(column = "NOME DA PESSOA")
    public String nome;

    @CsvBindByName(column = "PARENTESCO")
    public String parentesco;

    @CsvBindByName(column = "BAIRRO")
    public String bairro;

    @CsvBindByName(column = "DDD 2")
    public String ddd2;

    @CsvBindByName(column = "TERRITÓRIO CRAS")
    public String nomeServico;

    @CsvBindByName(column = "REGIONAL")
    public String regional;

    @CsvBindByName(column = "DATA DE NASCIMENTO")
    @CsvDate("dd/MM/yyyy")
    public Date dataNascimento;

/*
CÓDIGO FAMILIAR
NÚMERO NIS
NOME DA PESSOA
DATA DE NASCIMENTO
IDADE
CPF
PARENTESCO
RAÇA/COR
FAIXA DE IDADE
VALOR RENDA MÉDIA
FAIXA RPC
VALOR RENDA TOTAL
BENEFICIÁRIO PBF
NOME DA MÃE
 NOME DO PAI
PESSOA TEM DEFICIÊNCIA
CEGUEIRA
BAIXA VISÃO
SURDEZ SEVERA/ PROFUNDA
BAIXA VISÃO
DEFICIÊNCIA FÍSICA
DEFICIÊNCIA MENTAL OU INTELECTUAL
SINDROME DE DOWN
TRANSTORNO/DOENÇA MENTAL
TIPO LOGRADOURO
LOGRADOURO
NÚMERO LOGRADOURO
COMPLEMENTO
COMPLEMENTO ADICIONAL
CEP
BAIRRO
DDD 1
TELEFONE 1
DDD 2
TELEFONE 2
TERRITÓRIO CRAS
REGIONAL
variável N_NUTRIZ
variável N_GESTANTE
*/

}
