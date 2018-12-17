<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<table class="tabelaMembros">
   %{-- <thead>
    <tr>
        <th>Composição Familiar</th>
    </tr>
    </thead>--}%
    <tbody>
    <g:each in="${this.historicoCompleto}" status="i" var="acao">
        <tr class="linha-nome">
            <td colspan="4">${acao.toString()}</td>
        </tr>
    </g:each>
    </tbody>
</table>
%{--</div>--}%