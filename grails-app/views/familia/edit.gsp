<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
        <asset:javascript src="familia"/>
        <asset:stylesheet src="familia"/>
    </head>
    <body>
        <g:render template="/inicio/menu"/>
        <div id="edit-familia" class="content scaffold-edit" role="main">
            <h1>Sugerir Nova Família para o Programa</h1>

            <g:form action="save">
            %{--<g:form resource="${this.familia}" method="PUT">--}%
                <g:hiddenField name="version" value="${this.familia?.version}" />
                <fieldset class="form">
                    <g:render template="/familia/form"/>
                </fieldset>
                <fieldset class="buttons">
                    <button class="btn btn-default"><i class="fas fa-save"></i> Gravar</button>
                    <g:link controller="familia" action="list" class="btn btn-default"><i class="fas fa-undo-alt"></i> Cancelar</g:link>
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
