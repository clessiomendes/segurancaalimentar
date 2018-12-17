package org.apoiasuas.redeSocioAssistencial

import grails.gorm.services.Service

@Service(ServicoSistema)
abstract class ServicoSistemaService {

    public abstract ServicoSistema get(Serializable id)

    public abstract List<ServicoSistema> list(Map args)

    public abstract Long count()

    public abstract void delete(Serializable id)

    public abstract ServicoSistema save(ServicoSistema servicoSistema)

    public ServicoSistema buscaPeloEmail(String email) {
        return ServicoSistema.findByEmail(email)
    }

}