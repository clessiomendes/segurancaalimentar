<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apoiasuas.util.SegurancaHelper" %>
<!doctype html>
<html lang="pt-br" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" integrity="sha384-gfdkjb5BdAXd+lj+gudLWI+BXq4IuLW5IT+brZEZsLFm++aCMlF1V92rMkPaX4PP" crossorigin="anonymous">
    <title>
        <g:layoutTitle default="Provisão Alimentar"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <asset:script src="login.js"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <asset:stylesheet src="application.css"/>

    <g:layoutHead/>
</head>
<body>

    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header" style="margin-top: 20px; margin-bottom: 10px;">
%{--
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
--}%
                <a href="/#">
                    %{--<asset:image src="grails.svg" alt="Grails Logo"/>--}%
                    %{--<asset:image src="seguranca-alimentar-2.png"/>--}%
                    <h1 style="font-family: 'Arial Black', Gadget, sans-serif; font-size: 1.25em; margin: 0">Provisão Alimentar para Famílias em Extrema Pobreza</h1>
                    <g:if test="${SegurancaHelper.logado(session)}">
                        <h2 style="font-family: 'Arial', Gadget, sans-serif; font-size: 1em; margin: 0">
                        %{--<span style="position: absolute; top: 10px; right: 10px">--}%
                            %{--CRAS NOVO AARÃO REIS - BRASILINA MARIA DE OLIVEIRA--}%
                            ${SegurancaHelper.getCredencial(session)?.nomeOperador}
                            <span style="color: #666666">
                                %{--<button title="Clique para entrar com o email e senha do seu serviço" id="signin-button" onclick="handleSignInClick()">Trocar</button>--}%
                                <button id="signout-button" onclick="handleSignOutClick()">Sair</button>
                            </span>
                        %{--</span>--}%
                        </h2>
                    </g:if>
                </a>
            </div>
            <div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">
                <ul class="nav navbar-nav navbar-right">
                    <g:pageProperty name="page.nav" />
                </ul>
            </div>
        </div>
    </div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <asset:javascript src="application.js"/>
<script async defer src="https://apis.google.com/js/api.js"
        onload="this.onload=function(){};handleClientLoad()"
        onreadystatechange="if (this.readyState === 'complete') this.onload()">
</script>

</body>
</html>
