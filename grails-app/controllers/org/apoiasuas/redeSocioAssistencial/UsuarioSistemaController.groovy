package org.apoiasuas.redeSocioAssistencial

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UsuarioSistemaController {

    UsuarioSistemaService usuarioSistemaService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond usuarioSistemaService.list(params), model:[usuarioSistemaCount: usuarioSistemaService.count()]
    }

    def show(Long id) {
        respond usuarioSistemaService.get(id)
    }

    def create() {
        respond new UsuarioSistema(params)
    }

    def save(UsuarioSistema usuarioSistema) {
        if (usuarioSistema == null) {
            notFound()
            return
        }

        try {
            usuarioSistemaService.save(usuarioSistema)
        } catch (ValidationException e) {
            respond usuarioSistema.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'usuarioSistema.label', default: 'UsuarioSistema'), usuarioSistema.id])
                redirect usuarioSistema
            }
            '*' { respond usuarioSistema, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond usuarioSistemaService.get(id)
    }

    def update(UsuarioSistema usuarioSistema) {
        if (usuarioSistema == null) {
            notFound()
            return
        }

        try {
            usuarioSistemaService.save(usuarioSistema)
        } catch (ValidationException e) {
            respond usuarioSistema.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'usuarioSistema.label', default: 'UsuarioSistema'), usuarioSistema.id])
                redirect usuarioSistema
            }
            '*'{ respond usuarioSistema, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        usuarioSistemaService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuarioSistema.label', default: 'UsuarioSistema'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuarioSistema.label', default: 'UsuarioSistema'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
