package org.apoiasuas.cidadao

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema

class ConcessaoFamilia {
    Date data;
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
simulacao de estatisticas:

insert into estatistica_concessoes (id, mes, servico_id, quantidade)
select nextval('sq_estatistica_concessoes'), to_date('01/12/2017','DD/MM/YYYY'), id, floor(random()*(50+1)) from servico where acesso = 'ENCAMINHAMENTO';

insert into estatistica_concessoes_familia (id, mes, servico_id, quantidade)
select nextval('sq_estatistica_concessoes_familia'), to_date('01/12/2017','DD/MM/YYYY'), id, floor(random()*(50+1)) from servico where acesso = 'ENCAMINHAMENTO';
*/

    public String toString() {
        return "Concessão de cesta em ${data.format('dd/MM/yyyy')}"
    }
}
