<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<g:set var="beanCamposEdicao" scope="request" value="${familia}"/>

%{--Mensagens de erro--}%
<g:hasErrors bean="${this.familia}">
    <div class="alert alert-danger">
        <g:eachError bean="${this.familia}" var="error">
            <i class="fas fa-exclamation-circle"></i> <g:message error="${error}"/><br>
        </g:eachError>
    </div>
</g:hasErrors>

<g:hiddenField name="id" value="${this.familia.id}"/>

<div class="row">
    <g:campoEdicaoTexto titulo="Nome da Referência" classesDiv="col-sm-6" name="nomeReferencia"  maxlenght="100"/>
    <g:campoEdicaoTexto titulo="NIS da Referência" classesDiv="col-sm-3" name="nisReferencia"  maxlenght="20"/>
</div>

<g:if test="${org.apoiasuas.util.SegurancaHelper.getCredencial(session).gestao}">
    <div class="row">
        <g:campoEdicaoSelect name="servicoSistemaSeguranca" titulo="Serviço"
                             from="${org.apoiasuas.util.Cache.servicosEncaminhamento}"
                             classesDiv="col-sm-6"
                             optionKey="id" objectValue="${familia.servicoSistemaSeguranca}"
                             noSelection="['': '']"/>
    </div>
</g:if>

<div class="row">
    <div class="form-group col-sm-12 ">
        <label>Indicadores de Vulnerabilidade</label>
        <div class="form-vulnerabilidades">
            <label class="checkbox-inline"><g:checkBox name="monoparentalFeminina" value="${familia.monoparentalFeminina}"/> Monoparental Feminina</label>
            <label class="checkbox-inline"><g:checkBox name="pcd" value="${familia.pcd}"/> Pessoa com Deficiência</label>
            <label class="checkbox-inline"><g:checkBox name="gestante" value="${familia.gestante}"/> Gestante</label>
            <label class="checkbox-inline"><g:checkBox name="nutriz" value="${familia.nutriz}"/> Nutriz</label>
            <label class="checkbox-inline"><g:checkBox name="criancaPequena" value="${familia.criancaPequena}"/> Criança 0 a 5</label>
            <label class="checkbox-inline"><g:checkBox name="crianca" value="${familia.crianca}"/> Criança 6 a 11</label>
            <label class="checkbox-inline"><g:checkBox name="adolescente" value="${familia.adolescente}"/> Adolescente 12 a 17</label>
            <label class="checkbox-inline"><g:checkBox name="idoso" value="${familia.idoso}"/> Idoso</label>
        </div>
    </div>
</div>
