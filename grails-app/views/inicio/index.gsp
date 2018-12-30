<%@ page import="org.apoiasuas.redeSocioAssistencial.Acesso; grails.util.Environment" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="header-main">Provisão Alimentar para Famílias do SUAS BH</div>
        <h1 style="text-align: center">
                <g:if test="${request.mensagemErro}">
                    <ul class="errors" role="alert">
                        <li>${request.mensagemErro}</li>
                    </ul>
                </g:if>
                <g:if test = "${grails.util.Environment.current != grails.util.Environment.PRODUCTION}">
                    Ambiente de testes. Escolha um perfil de uso:<br><br>
                    <g:select name="selectPerfil" class="form-control" optionKey="name" optionValue="descricao"
                              style="width:15em; display: inline-block" from="${Acesso.values()}"/><br><br>
                </g:if>
                Entre com o email e senha do seu serviço<br><br>

                <button title="Clique para entrar com o email e senha do seu serviço"
                        class="btn btn-default " onclick="handleSignInClick()">
                    <i class="fas fa-envelope"></i> @pbh
                </button>

                <br><br>
        </h1>

        %{--Javascript para carregar a API de login do google--}%
    
        <g:if env="production">
            <script defer src="https://apis.google.com/js/api.js"
                    onload="this.onload=function(){};handleClientLoad()"
                    onerror="alert('Erro contactando Gmail. Atualize a pagina para tentar novamente');"
                    onreadystatechange="if (this.readyState === 'complete') this.onload()">
            </script>
        </g:if>
        <g:else>
            <script defer src="https://apis.google.com/js/api.js"
                    onload="this.onload=function(){};handleClientLoad()"
                    onreadystatechange="if (this.readyState === 'complete') this.onload()">
            </script>
        </g:else>

    </body>
</html>