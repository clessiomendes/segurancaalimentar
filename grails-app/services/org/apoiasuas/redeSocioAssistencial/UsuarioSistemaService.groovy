package org.apoiasuas.redeSocioAssistencial

import grails.gorm.services.Service

@Service(UsuarioSistema)
abstract class UsuarioSistemaService {

    public abstract UsuarioSistema get(Serializable id)

    public abstract List<UsuarioSistema> list(Map args)

    public abstract Long count()

    public abstract void delete(Serializable id)

    public abstract UsuarioSistema save(UsuarioSistema usuarioSistema)

    public UsuarioSistema buscaPeloEmail(String email) {
        return UsuarioSistema.findByEmail(email)
    }

}