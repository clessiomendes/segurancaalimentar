package org.apoiasuas.cidadao

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class FamiliaServiceSpec extends Specification {

    FamiliaService familiaService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Familia(...).save(flush: true, failOnError: true)
        //new Familia(...).save(flush: true, failOnError: true)
        //Familia familia = new Familia(...).save(flush: true, failOnError: true)
        //new Familia(...).save(flush: true, failOnError: true)
        //new Familia(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //familia.id
    }

    void "test get"() {
        setupData()

        expect:
        familiaService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Familia> familiaList = familiaService.list(max: 2, offset: 2)

        then:
        familiaList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        familiaService.count() == 5
    }

    void "test delete"() {
        Long familiaId = setupData()

        expect:
        familiaService.count() == 5

        when:
        familiaService.delete(familiaId)
        sessionFactory.currentSession.flush()

        then:
        familiaService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Familia familia = new Familia()
        familiaService.save(familia)

        then:
        familia.id != null
    }
}
