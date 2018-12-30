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

    def show() {
        //ao inves de usar o bind automatico do grails, carrega a familia com todas as colecoes internas em uma unica ida ao banco de dados
        Familia familia = familiaService.get(params.id, true);
        if (! familia)
            notFound()
        else {
            ConcessaoFamilia ultimaConcessao = familia.concessoes.sort{it.id}.reverse()[0];
            render view: 'show', model: [familia: familia, ultimaConcessao: ultimaConcessao, historicoCompleto: familia.historico]
        }
//            respond familia, [model: [ultimaConcessao: familiaService.getUltimaConcessao(familia), historicoCompleto: familiaService.getHistoricoCompleto(familia)]]
    }

    def create() {
        respond new Familia(params)
    }

    def inserir(Familia familia) {
        familia.situacao = SituacaoPrograma.INSERIDA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.sucesso = "Familia inserida no programa. RF: "+familia.nomeReferencia
        forward(action: 'index');

/*
        familiaService.imprimirFormulario(familia, SegurancaHelper.getCredencial(session));
        response.contentType = 'application/octet-stream'
        if (reportsDTO) {
            //Usa o nome de arquivo indicado no primeiro (em geral unico) elemento da lista de reportsDTO
            response.setHeader 'Content-disposition', "attachment; filename=\"${reportsDTO[0].nomeArquivo}\""
            apoiaSuasService.appendReports(response.outputStream, reportsDTO)
//            reportsDTO.report.process(reportsDTO.context, response.outputStream);
        } else {
            response.setHeader 'Content-disposition', "signal; filename=\"erro-favor-cancelar\""
        }
        response.outputStream.flush()
*/

    }

    def naoAtender(Familia familia) {
        familia.situacao = SituacaoPrograma.NAO_ATENDIDA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.sucesso = "Familia marcada como não atendida. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def naoLocalizada(Familia familia) {
        familia.situacao = SituacaoPrograma.NAO_LOCALIZADA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.sucesso = "Familia marcada como não localizada. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def liberarInsercao(Familia familia) {
        familia.situacao = SituacaoPrograma.PRE_SELECIONADA;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.sucesso = "Familia liberada para inserção no programa. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def indeferirPelaGestao(Familia familia) {
        familia.situacao = SituacaoPrograma.INSERCAO_RECUSADA_GESTAO;
        familiaService.grava(familia, SegurancaHelper.getCredencial(session));
        flash.sucesso = "Familia indefirida. RF: "+familia.nomeReferencia
        forward(action: 'index');
    }

    def registrarConcessao(Familia familia) {
        familiaService.registrarConcessao(familia, SegurancaHelper.getCredencial(session));
        forward(action: 'index');
    }

/*
    def saveOld(Familia familia) {
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
                flash.sucesso = 'Nova indicação registrada com sucesso.'
                redirect familia
            }
            '*' { respond familia, [status: CREATED] }
        }
    }
*/

    def edit(Long id) {
        respond familiaService.get(id)
    }

/*
    def updateOld(Familia familia) {
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
                flash.sucesso = 'Indicação alterada com sucesso.'
                redirect familia
            }
            '*'{ respond familia, [status: OK] }
        }
    }
*/

    def save(Familia familia) {
        if (! familia)
            return notFound()

        boolean modoCriacao = familia.id == null

        if (modoCriacao) {
            familia.servicoSistemaSeguranca = ServicoSistema.get(session.credencial.servicoSistema.id);
            familia.sincronizadoCadUnico = false;
        }

        //Validações:
        boolean validado = familia.validate();
        if ((! familia.monoparentalFeminina) || ! (familia.getTotalIndicadores() > 2)) {
            validado = false;
            familia.errors.rejectValue("", "", "Para ser indicada a família deve ser obrigatoriamente monoparental feminina, " +
                    "de renda zero e com pelo menos dois outros indicadores de vulnerabilidade.");
        }
        if (! familia.nisReferencia || (! familia.nisReferencia.matches("[0-9]+")) || (familia.nisReferencia.length() != 11)) {
            validado = false;
            familia.errors.rejectValue("", "", "NIS é obrigatório e deve conter 11 digitios numéricos");
        }

        //Grava
        if (validado) {
            familiaService.grava(familia, session.credencial);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit", model: [familia: familia]) //, model: getModelEdicao(servicoInstance, urlImagem))
        }

        if (modoCriacao)
            flash.sucesso = "Indicação registrada com sucesso."
        else
            flash.sucesso = "Familia alterada com sucesso."
//        show(familia);
        forward action: 'show', params: [id: familia.id]
//        render view: 'show', model: [familia: familia];
    }

    def excluir(Familia familia) {
        if (! familia || familia.id == null) {
            notFound()
            return
        }

        familiaService.delete(familia.id)

        request.withFormat {
            form multipartForm {
                flash.sucesso = "Familia de ${familia.nomeReferencia} excluida da listagem"
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
