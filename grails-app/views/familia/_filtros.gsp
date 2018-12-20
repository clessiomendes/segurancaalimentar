<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>
<g:form name="formFiltragem" class="form-filtro form-inline" action="list">

    <g:if test="${! SegurancaHelper.getCredencial(session).atendimento}"> %{--ocultar quando for o banco de alimentos--}%
        <div class="input-group">
            <span class="input-group-addon">Situação</span>
            <g:select name="situacao" from="${org.apoiasuas.cidadao.SituacaoPrograma.situacoes()}"
                               optionKey="key" optionValue="value" value="${filtrosOperador?.situacao}"
                               style="width: 10em;"
                               class="many-to-one form-control" noSelection="['': 'Todas']" onChange="atualiza();"/>
        </div>
%{--
        <div class="form-group">
            <label>Situação</label>
            <g:select name="situacao" from="${org.apoiasuas.cidadao.SituacaoPrograma.situacoes()}"
                               optionKey="key" optionValue="value" value="${filtrosOperador?.situacao}"
                               style="width: 10em;"
                               class="many-to-one form-control" noSelection="['': 'Todas']" onChange="atualiza();"/>
        </div>
--}%
%{--
        <span class="campoFiltragem">
            Situação <g:select name="situacao" from="${org.apoiasuas.cidadao.SituacaoPrograma.situacoes()}"
                               optionKey="key" optionValue="value" value="${filtrosOperador?.situacao}"
                               style="width: 10em;"
                               class="many-to-one form-control" noSelection="['': 'Todas']" onChange="atualiza();"/>
        </span>
--}%
    </g:if>

    <g:if test="${! SegurancaHelper.getCredencial(session).encaminhamento}">
        <div class="input-group">
            <span class="input-group-addon">Serviço</span>
            <g:select name="servicoSistema" from="${ServicoSistema.findAll().sort{it.nome} }"
                              optionKey="id" optionValue="nome" value="${filtrosOperador?.servicoSistema?.id}"
                              style="width: 15em;"
                              class="many-to-one form-control" noSelection="['': 'Todos']" onChange="atualiza();"/>
        </div>
%{--
        <span class="campoFiltragem">
            Serviço <g:select name="servicoSistema" from="${ServicoSistema.findAll().sort{it.nome} }"
                              optionKey="id" optionValue="nome" value="${filtrosOperador?.servicoSistema?.id}"
                              style="width: 15em;"
                              class="many-to-one form-control" noSelection="['': 'Todos']" onChange="atualiza();"/>
        </span>
--}%
    </g:if>

    <div class="input-group">
        <span class="input-group-addon">Nome ou NIS</span>
        <g:textField style="width: 15em;" name="nomeOuNis" class="form-control" onkeydown="keydownNomeOuNis(event)"/>
    </div>
%{--
    <span class="campoFiltragem">
        Nome ou NIS <g:textField style="width: 15em;" name="nomeOuNis" onkeydown="keydownNomeOuNis(event)"/>
    </span>
--}%
</g:form>
