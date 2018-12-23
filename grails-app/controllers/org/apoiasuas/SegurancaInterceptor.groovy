package org.apoiasuas

import grails.util.Environment
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema
import org.apoiasuas.util.Credencial

/**
 * Created by cless on 21/12/2018.
 */
class SegurancaInterceptor {
    SegurancaInterceptor() {
        matchAll()
                .excludes(controller: 'inicio');
    }

    boolean before() {

        if (! session.credencial) {
//            if (Environment.current == Environment.PRODUCTION)
                forward(controller: 'inicio', action: 'logout')
//            else {
//                session.credencial = new Credencial(ServicoSistema.get(8), UsuarioSistema.get(5));
//            }

        }

        return true;
    }
}
