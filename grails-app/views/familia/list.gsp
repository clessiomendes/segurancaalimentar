<!DOCTYPE html>
<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
        <asset:javascript src="familia"/>
        <asset:stylesheet src="familia"/>
    </head>
    <body>

        <g:render template="/inicio/menu"/>

        <a href="#list-familia" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

        <div id="list-familia" class="content scaffold-list" role="main">

            <g:render template="/inicio/mensagens"/>

            <g:render template="filtros"/>

            <table class="tabela-familias">
                <thead>
                <tr>
                    <th>Nome</th>
                    <th class="hide-on-mobile">Endereço</th>
                    <th>Situação</th>
                    <th class="hide-on-mobile" >NIS</th>
                    <th>Indicadores</th>
                </tr>
                </thead>
                <tbody>
                    <g:each in="${cidadaoList}" status="i" var="cidadaoInstance">
                        <% org.apoiasuas.cidadao.Cidadao cidadao = cidadaoInstance %>
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td class="coluna-referencia"><g:link action="show" id="${cidadao.familia.id}">${cidadao.nome}</g:link></td>
                            <td class="coluna-endereco hide-on-mobile" >${cidadao.familia.getEnderecoBasico()}</td>
                            <td>${cidadao.familia.situacao}</td>
                            <td class="hide-on-mobile" >${cidadao.NIS}</td>
                            <td class="coluna-vulnerabilidades">
                                <g:vulnerabilidades familia="${cidadao.familia}"/>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

            <div class="pagination">
                <g:paginate total="${cidadaoCount ?: 0}" />
            </div>
        </div>

    </body>
</html>