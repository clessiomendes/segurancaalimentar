package org.apoiasuas

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistemaService
import org.apoiasuas.util.SegurancaHelper

/**
 * Created by clessio on 05/11/2018.
 */
class InicioController {

    ServicoSistemaService servicoSistemaService;
    UsuarioSistemaService usuarioSistemaService;

    def index() {
        if (SegurancaHelper.logado(session))
            return forward (controller: 'familia')
        else
            return render(view:'index');
    }

    def enter() {
        String idToken = params["id_token"];
        if (! idToken)
            return render(view:'index');
        GoogleIdToken.Payload payLoad = SegurancaHelper.getPayload(idToken);
        if (payLoad) {
            if (payLoad.getHostedDomain() != 'pbh.gov.br') {
                request.mensagemErro = "Operador ${payLoad.getEmail()} não autorizado."
                return render(view:'index');
            }
            SegurancaHelper.logout(session);
            ServicoSistema ss = servicoSistemaService.buscaPeloEmail(payLoad.getEmail());
            UsuarioSistema us = usuarioSistemaService.buscaPeloEmail(payLoad.getEmail());
            if (! us) {
                request.mensagemErro = "Operador ${payLoad.getEmail()} não autorizado."
                return render(view: 'index');
            }
            SegurancaHelper.login(session, ss, us);
        }
        forward controller: 'familia'
    }

    def logout() {
        SegurancaHelper.logout(session);
//        session['nomeOperadorLogado'] = null;
//        session['emailOperadorLogado'] = null;
        forward action:'index'
    }

}
