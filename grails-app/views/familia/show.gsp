<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
        <asset:stylesheet src="familia.css"/>
    </head>
    <body>
        <g:render template="/inicio/menu"/>

        <div id="show-familia" class="content scaffold-show" role="main">
            <h1>Familia de ${this.familia.nomeReferencia}</h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div style="display: inline-block">
                <g:acoesFamilia familia="${this.familia}"/>
            </div>

            <ol class="property-list familia" style="margin-top: -10px">
                <g:campoExibicao conteudo="${this.familia.situacao}" titulo="Situação"/>
                <g:campoExibicao conteudo="${this.ultimaConcessao ? this.ultimaConcessao.data.format("dd/MM/yyyy") : 'nenhuma'}" titulo="Ultima Concessão"/>
                <g:campoExibicao conteudo="${this.familia.enderecoBasico}" titulo="Endereço"/>
                <g:campoExibicao conteudo="${this.familia.telefones}" titulo="Telefone(s)"/>
                <g:campoExibicao conteudo="${this.familia.servicoSistemaSeguranca?.nome}" titulo="Serviço"/>
                <g:campoExibicao titulo="Indicadores">
                    <g:vulnerabilidades familia="${this.familia}"/>
                </g:campoExibicao>

                <g:if test="${this.familia.membros}">
                    <g:campoExibicao titulo="Composição Familiar">
                        <g:render template="membros"/>
                    </g:campoExibicao>
                </g:if>
                <g:else>
                    <g:campoExibicao conteudo="${this.familia.nomeReferencia}" titulo="Referência Familiar"/>
                </g:else>

                <g:if test="${this.historicoCompleto}">
                    <g:campoExibicao titulo="Histórico">
                        <g:render template="historicoCompleto"/>
                    </g:campoExibicao>
                </g:if>
            </ol>


%{--
            <g:form resource="${this.familia}" method="PUT">
                <g:hiddenField name="id" value="${this.familia?.id}" />
                <g:hiddenField name="version" value="${this.familia?.version}" />
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.familia}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				    <g:actionSubmit action="inserir" class="save" value="Inserir no programa" onclick="this.form.action='${createLink(action:'inserir')}';"/>
                </fieldset>
            </g:form>
--}%
        </div>
    </body>
</html>
