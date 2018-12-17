<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <h1 style="text-align: center">
            <g:if test="${request.mensagemErro}">
                <ul class="errors" role="alert">
                    <li>${request.mensagemErro}</li>
                </ul>
            </g:if>
            Entre com o email e senha do seu serviço<br><br>
            <button title="Clique para entrar com o email e senha do seu serviço" id="signin-button" onclick="handleSignInClick()">Gmail</button>
        </h1>
    </body>
</html>