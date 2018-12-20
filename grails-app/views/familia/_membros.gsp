<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<table class="tabelaMembros">
   %{-- <thead>
    <tr>
        <th>Composição Familiar</th>
    </tr>
    </thead>--}%
    <tbody>
    <g:each in="${this.familia.getMembrosOrdemPadrao()}" status="i" var="cidadaoInstance">
        <% org.apoiasuas.cidadao.Cidadao cidadao = cidadaoInstance %>
        <tr class="linha-nome">
            <td colspan="4">${cidadao.nome}</td>
        </tr><tr class="linha-detalhes">
            <td>${cidadao.parentesco == Cidadao.PARENTESCO_REFERENCIA ? "R.F." : cidadao.parentesco}</td>
            <td>${cidadao.idade} anos</td>
            <td>NIS ${cidadao.NIS}</td>
            <td>${cidadao.vulnerabilidades}</td>
%{--
            <td class="hide-on-mobile" style='width: 380px;' >${cidadao.familia.getEnderecoBasico()}</td>
            <td>${cidadao.familia.situacao}</td>
            <td class="hide-on-mobile" >${cidadao.NIS}</td>
            <td style="padding-bottom: 0; width: 500px;">
                <g:vulnerabilidades familia="${cidadao.familia}"/>
            </td>
--}%
        </tr>
    </g:each>
    </tbody>
</table>
%{--</div>--}%