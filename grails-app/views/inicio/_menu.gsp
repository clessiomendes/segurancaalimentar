<%@ page import="org.apoiasuas.redeSocioAssistencial.Acesso; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.util.Credencial" %>
<% Credencial credencial = SegurancaHelper.getCredencial(session) %>

<div class="header-main">Provisão Alimentar para Famílias do SUAS BH</div>
<div class="topnav" id="myTopnav">
    %{--<a href="#home" class="active">Home</a>--}%
    <g:link controller="inicio">Início</g:link>
    <g:temAcesso acessos="${Acesso.ENCAMINHAMENTO}">
        <g:link controller="familia" action="create">Indicar Nova Família</g:link>
    </g:temAcesso>
    <g:link controller="gestao">Gestão</g:link>

    %{--<g:link class="opcao-menu-direita" onclick="handleSignOutClick(event)">Sair</g:link>--}%
    <g:link class="opcao-menu-direita" controller="inicio" action="logout">Sair</g:link>
    <a href="javascript:void(0);" class="icon" onclick="menuCelular()">
        <i class="fa fa-bars"></i>
    </a>
</div>

%{--   Modelo de menu do bootstrap
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <p class="navbar-brand" href="#">Provisão Alimentar</p>
        </div>
        <ul class="nav navbar-nav">
            <g:if test="${credencial.encaminhamento}">
                <li><g:link controller="familia" action="create">Indicar Nova Família</g:link></li>
            </g:if>
            <li><g:link controller="gestao">Gestão</g:link></li>
        </ul>
    </div>
</nav>
--}%
