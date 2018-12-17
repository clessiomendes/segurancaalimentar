package org.apoiasuas.redeSocioAssistencial

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class ServicoSistemaController {

    ServicoSistemaService servicoSistemaService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond servicoSistemaService.list(params), model:[servicoSistemaCount: servicoSistemaService.count()]
    }

    def show(Long id) {
        respond servicoSistemaService.get(id)
    }

    def create() {
        respond new ServicoSistema(params)
    }

    def save(ServicoSistema servicoSistema) {
        if (servicoSistema == null) {
            notFound()
            return
        }

        try {
            servicoSistemaService.save(servicoSistema)
        } catch (ValidationException e) {
            respond servicoSistema.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'servicoSistema.label', default: 'ServicoSistema'), servicoSistema.id])
                redirect servicoSistema
            }
            '*' { respond servicoSistema, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond servicoSistemaService.get(id)
    }

    def update(ServicoSistema servicoSistema) {
        if (servicoSistema == null) {
            notFound()
            return
        }

        try {
            servicoSistemaService.save(servicoSistema)
        } catch (ValidationException e) {
            respond servicoSistema.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'servicoSistema.label', default: 'ServicoSistema'), servicoSistema.id])
                redirect servicoSistema
            }
            '*'{ respond servicoSistema, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        servicoSistemaService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'servicoSistema.label', default: 'ServicoSistema'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'servicoSistema.label', default: 'ServicoSistema'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
