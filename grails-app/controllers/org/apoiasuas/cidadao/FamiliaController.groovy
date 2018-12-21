package org.apoiasuas.cidadao

import grails.validation.ValidationException
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.Credencial
import org.apoiasuas.util.SegurancaHelper
import org.apoiasuas.util.HqlPagedResultList

import static org.springframework.http.HttpStatus.*

class FamiliaController {

    FamiliaService familiaService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        forward action: 'list'
    }

    def list(Integer max, FiltroFamiliaCommand filtrosOperador) {
        Credencial credencial = SegurancaHelper.getCredencial(session);
        if (credencial.encaminhamento)
            filtrosOperador.servicoSistema = credencial.servicoSistema
        else if (credencial.atendimento)
            filtrosOperador.situacao = SituacaoPrograma.INSERIDA
        params.max = Math.min(max ?: 30, 100);
        HqlPagedResultList result = familiaService.procurar(params, filtrosOperador)
        respond result.resultList, model:[cidadaoCount: result.totalCount, filtrosOperador: filtrosOperador];
//        log.error(result.join(", "));
//        render(view: 'list', model:[cidadaoList: result.resultList, cidadaoCount: result.totalCount])
//        respond familiaService.list(params), model:[familiaCount: familiaService.count()];
    }

    def show(Long id) {
        Familia familia = familiaService.get(id)
        if (! familia)
            respond null
        else
            respond familia, [model: [ultimaConcessao: familiaService.getUltimaConcessao(familia), historicoCompleto: familiaService.getHistoricoCompleto(familia)]]
    }

    def create() {
        respond new Familia(params)
    }

    def inserir(Familia familia) {
        familia.situacao = SituacaoPrograma.INSERIDA;
//        familiaService.imprimirFormulario(familia, SegurancaHelper.getCredencial(session));
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.message = "Família inserida no programa. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def naoAtender(Familia familia) {
        familia.situacao = SituacaoPrograma.NAO_ATENDIDA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.message = "Família marcada como não atendida. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def naoLocalizada(Familia familia) {
        familia.situacao = SituacaoPrograma.NAO_LOCALIZADA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.message = "Família marcada como não localizada. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def liberarInsercao(Familia familia) {
        familia.situacao = SituacaoPrograma.PRE_SELECIONADA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.message = "Família liberada para inserção no programa. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def registrarConcessao(Familia familia) {
        familiaService.registrarConcessao(familia, SegurancaHelper.getCredencial(session));
        forward(action: 'index');
    }

    def save(Familia familia) {
        if (familia == null) {
            notFound()
            return
        }

        try {
            familiaService.grava(familia, session.credencial)
        } catch (ValidationException e) {
            respond familia.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'familia.label', default: 'Familia'), familia.id])
                redirect familia
            }
            '*' { respond familia, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond familiaService.get(id)
    }

    def update(Familia familia) {
        if (familia == null) {
            notFound()
            return
        }

        try {
            familiaService.save(familia)
        } catch (ValidationException e) {
            respond familia.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Familia'), familia.id])
                redirect familia
            }
            '*'{ respond familia, [status: OK] }
        }
    }

    def excluir(Familia familia) {
        if (! familia || familia.id == null) {
            notFound()
            return
        }

        familiaService.delete(familia.id)

        request.withFormat {
            form multipartForm {
                flash.message = "Familia de ${familia.nomeReferencia} excluida da listagem"
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'familia.label', default: 'Familia'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}

//@grails.validation.Validateable
class FiltroFamiliaCommand implements Serializable {

    String nomeOuNis
//    String cartorioOuMinicipio
    SituacaoPrograma situacao
    ServicoSistema servicoSistema
//    Boolean cartorioIndefinido
}
