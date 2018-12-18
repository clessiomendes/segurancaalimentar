package org.apoiasuas.redeSocioAssistencial

class ServicoSistema {
    String nome
    String email
    String regional
    Acesso acesso

    static mapping = {
        table 'servico'
        id generator: 'native', params: [sequence: 'sq_servico']
        version column: 'versao'
    }

    public String toString() {
        return nome;
    }

}

public enum Acesso {
    ENCAMINHAMENTO('Serviço do SUAS'), ATENDIMENTO('Banco de Alimentos'), GESTAO('Gestão')

    private String descricao

    public getName() {
        return name();
    }

    public Acesso(String descricao) {
        this.descricao = descricao
    }
}
