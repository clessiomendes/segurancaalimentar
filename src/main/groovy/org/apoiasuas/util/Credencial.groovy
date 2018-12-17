package org.apoiasuas.util

import org.apoiasuas.redeSocioAssistencial.Acesso
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema

class Credencial {
    ServicoSistema servicoSistema;
    UsuarioSistema usuarioSistema;

    public Credencial(ServicoSistema servicoSistema, UsuarioSistema usuarioSistema) {
        this.servicoSistema = servicoSistema;
        this.usuarioSistema = usuarioSistema;
    }

    public String getNomeOperador() {
        if (servicoSistema)
            return servicoSistema.nome
        return usuarioSistema.nome
    }

    public Acesso getAcesso() {
        if (servicoSistema)
            return servicoSistema.acesso
        //FIXME: como sera a gestao de permissoes no LDAP da PRODABEL?
        return Acesso.GESTAO
    }

    public boolean isGestao() {
        return acesso == Acesso.GESTAO
    }
    public boolean isEncaminhamento() {
        return acesso == Acesso.ENCAMINHAMENTO
    }
    public boolean isAtendimento() {
        return acesso == Acesso.ATENDIMENTO
    }
}
