<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<g:set var="beanCamposEdicao" scope="request" value="${familia}"/>
<g:campoEdicaoTexto titulo="Nome da Referência" name="nomeReferencia" size="60" maxlenght="100"/>
<g:campoEdicaoTexto titulo="NIS da Referência" name="nisReferencia" size="15" maxlenght="20"/>
<g:if test="${org.apoiasuas.util.SegurancaHelper.getCredencial(session).gestao}">
    <g:campoEdicaoSelect name="servicoSistemaSeguranca" titulo="Serviço"
                         from="${ServicoSistema.findAll().sort{it.nome}}"
                         optionKey="id" objectValue="${familia.servicoSistemaSeguranca}"
                         noSelection="['': '']"/>
</g:if>
<g:campoEdicao titulo="Gestante(s)" name="gestante">
    <g:checkBox name="gestante" value="${familia.gestante}"/> sim
</g:campoEdicao>
<g:campoEdicao titulo="Nutriz" name="nutriz">
    <g:checkBox name="nutriz" value="${familia.nutriz}"/> sim
</g:campoEdicao>

%{--
<f:with bean="familia">
    <f:field property="sigps"/>
    <f:field property="nisReferencia"/>
    <f:field property="endereco"/>
    <f:field property="bairro"/>
    <f:field property="situacao"/>
    <f:field property="servicoSistemaSeguranca"/>
</f:with>
--}%
