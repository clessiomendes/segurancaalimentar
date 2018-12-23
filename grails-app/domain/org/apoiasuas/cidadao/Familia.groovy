package org.apoiasuas.cidadao

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.CollectionUtils

class Familia {

    String sigps
    String nomeReferencia
    String nisReferencia

    String codigoFamiliar
    Double rendaPerCapita


    SituacaoPrograma situacao

//    String endereco

    String tipoLogradouro
    String nomeLogradouro
    String numeroLogradouro
    String complemento
    String complementoAdicional
    String CEP
    String bairro
//    String municipio
//    String UF
    String DDD1
    String telefone1
    String DDD2
    String telefone2

    Date dataAtualizacaoCadunico

//    Date dateCreated, lastUpdated;

    ServicoSistema servicoSistemaSeguranca

    Boolean pcd
    Boolean gestante
    Boolean nutriz
    Boolean criancaPequena
    Boolean crianca
    Boolean adolescente
    Boolean idoso
    Boolean monoparentalFeminina

    Set<Cidadao> membros = []
    static hasMany = [membros: Cidadao]

    static transients = ['enderecoBasico', 'telefones']

    static constraints = {
        nomeReferencia (nullable: false)
        situacao (nullable: true)
        bairro (nullable: true)
        nisReferencia (nullable: true)
        sigps (nullable: true)
        servicoSistemaSeguranca (nullable: false)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_familia']
        servicoSistemaSeguranca column: 'servico_id'
        version column: 'versao'
//        dateCreated column: 'data_criacao', type: 'date'
//        lastUpdated column: 'ultima_alteracao', type: 'date'
    }

    public String obtemTipoENomeLogradouro() {
        return nomeLogradouro ? (tipoLogradouro ? tipoLogradouro + " ": "") + nomeLogradouro : null
    }

    public String getEnderecoBasico() {
        return CollectionUtils.join([obtemTipoENomeLogradouro(), numeroLogradouro, complemento, complementoAdicional, bairro], ", ") ?: ""
    }

    public String getTelefones() {
        String fone1, fone2;
        if (telefone1 && telefone1 != '0')
            fone1 = CollectionUtils.join([DDD1, telefone1], "-");
        if (telefone2 && telefone2 != '0')
            fone2 = CollectionUtils.join([DDD2, telefone2], "-");
        return CollectionUtils.join([fone1, fone2], ", ")
    }

    /**
     * Devolve todos os membros em uma ordem padrão: 1º a referencia, depois do mais velho para o mais novo.
     * obs: a lista retornada não mantem a ligação com a sessão de persistência
     * @return
     */
    public List<Cidadao> getMembrosOrdemPadrao() {
        return membros?.findAll().sort{ a,b ->
            if (a.parentesco == Cidadao.PARENTESCO_REFERENCIA)
                return -1;
            if (b.parentesco == Cidadao.PARENTESCO_REFERENCIA)
                return 1;
            Date aDataNascimento = a.dataNascimento
            Date bDataNascimento = b.dataNascimento
            if (aDataNascimento && ! bDataNascimento)
                return -1;
            if (bDataNascimento && ! aDataNascimento)
                return 1;
            if ((! bDataNascimento && ! aDataNascimento) || aDataNascimento.equals(bDataNascimento)) {
                if (a.nome && b.nome)
                    return a.nome.compareTo(b.nome)
                else
                    return 0;
            }
            return aDataNascimento.compareTo(bDataNascimento)
        };
    }

    public int getTotalIndicadores() {
        return  (monoparentalFeminina ? 1 : 0) +
                (pcd ? 1 : 0) +
                (gestante ? 1 : 0) +
                (nutriz ? 1 : 0) +
                (criancaPequena ? 1 : 0) +
                (crianca ? 1 : 0) +
                (adolescente ? 1 : 0) +
                (idoso ? 1 : 0);
    }
}

/**
 * ENUM
 */
enum SituacaoPrograma {
//    NOVA("Nova"),
    PRE_SELECIONADA("Pré Selecionada", "Sugestões do CadUnico"),
    INDICADA_SERVICO("Indicada pelo Serviço", "Indicar"),
    INSERIDA("Inserida no Programa", "Inserir no Programa"),
    INSERCAO_LIBERADA("Liberada pela Gestão", "Liberar para Inserção"),
    INSERCAO_RECUSADA_GESTAO("Inserção Indeferida pela Gestão", "Indeferir"),
    NAO_ATENDIDA("Inserção Indefirida pelo Serviço", "Não Inserir Atualmente"),
    NAO_LOCALIZADA("Não Localizada", "Não Localizada"),
    REMOVIDA("Desligada do Programa", "Desligar do Programa")

    String descricao
    String acao

    public SituacaoPrograma(String descricao, String acao) {
        this.descricao = descricao
        this.acao = acao
    }

    public String toString() {
        descricao
    }

    public static Map situacoes() {
        Map result = [:];
        values().each {
            result.put(it.name(), it.descricao)
        }
        return result;
    }
}