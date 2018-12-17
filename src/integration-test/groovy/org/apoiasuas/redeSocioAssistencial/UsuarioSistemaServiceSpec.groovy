package org.apoiasuas.redeSocioAssistencial

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class UsuarioSistemaServiceSpec extends Specification {

    UsuarioSistemaService usuarioSistemaService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new UsuarioSistema(...).save(flush: true, failOnError: true)
        //new UsuarioSistema(...).save(flush: true, failOnError: true)
        //UsuarioSistema usuarioSistema = new UsuarioSistema(...).save(flush: true, failOnError: true)
        //new UsuarioSistema(...).save(flush: true, failOnError: true)
        //new UsuarioSistema(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //usuarioSistema.id
    }

    void "test get"() {
        setupData()

        expect:
        usuarioSistemaService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<UsuarioSistema> usuarioSistemaList = usuarioSistemaService.list(max: 2, offset: 2)

        then:
        usuarioSistemaList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        usuarioSistemaService.count() == 5
    }

    void "test delete"() {
        Long usuarioSistemaId = setupData()

        expect:
        usuarioSistemaService.count() == 5

        when:
        usuarioSistemaService.delete(usuarioSistemaId)
        sessionFactory.currentSession.flush()

        then:
        usuarioSistemaService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        UsuarioSistema usuarioSistema = new UsuarioSistema()
        usuarioSistemaService.save(usuarioSistema)

        then:
        usuarioSistema.id != null
    }
}
