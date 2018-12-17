package segurancaalimentar

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService

class BootStrap {

    ServicoSistemaService servicoSistemaService

    def init = { servletContext ->
        Locale.setDefault(new Locale('pt', 'BR'));

//        def ss = new ServicoSistema(nome: 'hv', email: 'cras.havaiventosa@pbh.gov.br')
//        servicoSistemaService.save(ss);
    }
    def destroy = {
    }
}
