<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>
<g:form name="formFiltragem" action="list">
    <g:if test="${! SegurancaHelper.getCredencial(session).atendimento}">
        <span class="campoFiltragem">
            Situação <g:select name="situacao" from="${org.apoiasuas.cidadao.SituacaoPrograma.situacoes()}"
                               optionKey="key" optionValue="value" value="${filtrosOperador?.situacao}"
                               style="width: 10em;"
                               class="many-to-one" noSelection="['': 'Todas']" onChange="atualiza();"/>
        </span>
    </g:if>

    <g:if test="${! SegurancaHelper.getCredencial(session).encaminhamento}">
        <span class="campoFiltragem">
            Serviço <g:select name="servicoSistema" from="${ServicoSistema.findAll().sort{it.nome} }"
                              optionKey="id" optionValue="nome" value="${filtrosOperador?.servicoSistema?.id}"
                              style="width: 15em;"
                              class="many-to-one" noSelection="['': 'Todos']" onChange="atualiza();"/>
        </span>
    </g:if>

    <span class="campoFiltragem">
        Nome ou NIS <g:textField style="width: 15em;" name="nomeOuNis" onkeydown="keydownNomeOuNis(event)"/>
    </span>
</g:form>
