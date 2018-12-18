<%@ page import="org.apoiasuas.redeSocioAssistencial.Acesso; grails.util.Environment" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <h1 style="text-align: center">
            <g:if test = "${grails.util.Environment.current != grails.util.Environment.PRODUCTION}">
                Ambiente de testes. Escolha um perfil de uso:<br>
                <g:select name="selectPerfil" optionKey="name" optionValue="descricao" from="${Acesso.values()}"/><br><br>
            </g:if>
            Entre com o email e senha do seu serviço<br>
            <button title="Clique para entrar com o email e senha do seu serviço" id="signin-button" onclick="handleSignInClick()">Gmail</button>
        </h1>
    </body>
</html>