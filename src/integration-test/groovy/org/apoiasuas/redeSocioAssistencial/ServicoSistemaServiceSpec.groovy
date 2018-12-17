package org.apoiasuas.redeSocioAssistencial

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ServicoSistemaServiceSpec extends Specification {

    ServicoSistemaService servicoSistemaService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new ServicoSistema(...).save(flush: true, failOnError: true)
        //new ServicoSistema(...).save(flush: true, failOnError: true)
        //ServicoSistema servicoSistema = new ServicoSistema(...).save(flush: true, failOnError: true)
        //new ServicoSistema(...).save(flush: true, failOnError: true)
        //new ServicoSistema(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //servicoSistema.id
    }

    void "test get"() {
        setupData()

        expect:
        servicoSistemaService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<ServicoSistema> servicoSistemaList = servicoSistemaService.list(max: 2, offset: 2)

        then:
        servicoSistemaList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        servicoSistemaService.count() == 5
    }

    void "test delete"() {
        Long servicoSistemaId = setupData()

        expect:
        servicoSistemaService.count() == 5

        when:
        servicoSistemaService.delete(servicoSistemaId)
        sessionFactory.currentSession.flush()

        then:
        servicoSistemaService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        ServicoSistema servicoSistema = new ServicoSistema()
        servicoSistemaService.save(servicoSistema)

        then:
        servicoSistema.id != null
    }
}
