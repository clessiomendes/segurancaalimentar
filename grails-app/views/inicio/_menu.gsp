<%@ page import="org.apoiasuas.util.SegurancaHelper; org.apoiasuas.util.Credencial" %>
<% Credencial credencial = SegurancaHelper.getCredencial(session) %>

<div class="nav" role="navigation">
    <ul>
        <g:if test="${credencial.encaminhamento}">
            <li><g:link class="create" action="create">Indicar Nova Família</g:link></li>
        </g:if>
        %{--<g:if test="${credencial.gestao}">--}%
            <li><g:link class="create" controller="gestao">Gestão</g:link></li>
        %{--</g:if>--}%
    </ul>
</div>
