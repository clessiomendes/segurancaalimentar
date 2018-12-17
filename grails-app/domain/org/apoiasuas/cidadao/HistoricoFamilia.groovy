package org.apoiasuas.cidadao

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema

/**
 * Created by cless on 07/12/2018.
 */
class HistoricoFamilia {
    Date data;
    SituacaoPrograma acao;
    UsuarioSistema usuarioSistema;
    ServicoSistema servicoSistemaSeguranca;
    Familia familia;

    static belongsTo = [familia: Familia] //importado

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_historico_familia']
        version column: 'versao'
        servicoSistemaSeguranca column: 'servico_id'
    }

/*
desligar autocommit

CREATE TEMP TABLE acao (
  valor VARCHAR(255)
) ON COMMIT DROP;
insert into acao values ('PRE_SELECIONADA'), ('INDICADA_SERVICO'), ('INSERIDA'), ('NAO_ATENDIDA'), ('NAO_LOCALIZADA'), ('REMOVIDA');
insert into estatistica_historico_familia (id, periodo, servico_id, quantidade, acao)
select nextval('sq_estatistica_concessoes'), to_date('01/12/2017','DD/MM/YYYY'), id, floor(random()*(50+1)), b.valor
from servico, acao b
where acesso = 'ENCAMINHAMENTO';

executar APENAS no final

update estatistica_historico_familia set quantidade = quantidade * 4 where acao = 'PRE_SELECIONADA'
*/

    public String toString() {
        switch (acao) {
            case SituacaoPrograma.INSERIDA: return "Família inserida no programa em ${data.format('dd/MM/yyyy')}";
            case SituacaoPrograma.INDICADA_SERVICO: return "Família indicada por ${servicoSistemaSeguranca.nome} em ${data.format('dd/MM/yyyy')}";
            case SituacaoPrograma.INSERCAO_LIBERADA: return "Família autorizada pela gestão em ${data.format('dd/MM/yyyy')}";
            case SituacaoPrograma.NAO_ATENDIDA: return "Família não será atendida (${data.format('dd/MM/yyyy')})";
            case SituacaoPrograma.PRE_SELECIONADA: return "Família pré selecionada em ${data.format('dd/MM/yyyy')}";
            default: return "ação '${acao}' em '${data.format('dd/MM/yyyy')}'"
        }
    }
}
