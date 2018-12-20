<!DOCTYPE html>
<%@ page import="org.apoiasuas.redeSocioAssistencial.Acesso; org.apoiasuas.GestaoController; java.text.DateFormatSymbols; org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>
<html>
    <head>
        <meta name="layout" content="main" />
        <title>Gestão</title>
        %{--<asset:javascript  src="hammer.js"/>--}%
        <asset:javascript src="Chart-2.7.3.js"/>
        %{--<script src="https://cdnjs.com/libraries/Chart.js"/>--}%
        <asset:javascript src="chartjs-plugin-datalabels-0.5.0.js"/>
        <asset:javascript src="gestao.js"/>
        <asset:javascript src="reimg.js"/>
        <asset:stylesheet src="gestao.less"/>
        %{--<asset:javascript  src="chartjs-plugin-zoom-0.6.6.js"/>--}%

        <script>
            var urlSelectRegional = "${raw(g.createLink(action: 'selectRegional'))}";
        </script>
    </head>
    <body>

        <g:render template="/inicio/menu"/>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

<div class="painel-graficos">
    <div class="row">
<!--                         FAMILIAS  HOJE                                                -->
        <tmpl:chart acessos="${[Acesso.ENCAMINHAMENTO, Acesso.GESTAO]}" idGrafico="idFamiliasAtual" filtros='${[nomeRegional: "${nomeRegional}", idServico: "${idServico}"]}'
                    action="${g.createLink(action:'obtemFamiliasAtual')}" json="${raw(jsonFamiliasAtual)}" />
<!--                         FAMILIAS ATENDIDAS POR REGIONAL OU POR SERVICO                                         -->
        <tmpl:chart acessos="${[Acesso.ENCAMINHAMENTO, Acesso.GESTAO]}" idGrafico="idFamiliasAtendidas" filtros="${[nomeRegional: "${nomeRegional}", ano: "${ano}", mes: "${mes}"]}"
                    action="${g.createLink(action:'obtemFamiliasAtendidas')}" json="${raw(jsonConcessoesAnual)}" />
    </div>
    <div class="row">
        <!--                          CESTAS CONCEDIDAS MES A MES               -->
        <tmpl:chart idGrafico="idConcessoesMensal" filtros="${[nomeRegional: "${nomeRegional}", idServico: "${idServico}", ano: "${ano}"]}"
                    action="${g.createLink(action:'obtemConcessoesMensal')}" json="${raw(jsonConcessoesMensal)}" />
        <!--                          ACOES EMPREENDIDAS MES A MES                  -->
        <tmpl:chart acessos="${[Acesso.ENCAMINHAMENTO, Acesso.GESTAO]}" idGrafico="idHistorico" filtros="${[nomeRegional: "${nomeRegional}", idServico: "${idServico}", ano: "${ano}"]}"
                    action="${g.createLink(action:'obtemHistorico')}" json="${raw(jsonHistorico)}" />
    </div>

%{--
<!--                         FAMILIAS ATENDIDAS POR REGIONAL OU POR SERVICO                                         -->

        <div class="div-chart col-lg-6">
            <g:select name="nomeRegional" from="${session.regionais}"
                      class="many-to-one select-regional" noSelection="['': 'Regional']"/>
            <g:select name="ano" from="${session.anosOperacao ?: ['2017', '2018']}"
                      value="2017" class="many-to-one select-ano"
                      onchange="changeSelectAno(this)"/>
            <g:select name="mes" from="${GestaoController.mapaMeses()}"
                      optionKey="key" optionValue="value"
                      class="many-to-one select-mes"
                      noSelection="['': 'Mês']"/>
            <input class="download-button fa" type="button" value="&#xf0ab" title="Download do gráfico"
                   onclick="donwloadGrafico(this)"/>
            <input class="clean-button fa" type="button" value="&#xf2ea" title="Voltar para configuração inicial"
                   onclick="limparFiltros(this)"/>
            <input class="refresh-button fa" type="button" value="&#xf0b0" title="Aplicar filtros e atualizar"
                   onclick='atualiza(this, "${g.createLink( action:'obtemFamiliasAtendidas')}")'/>
            --}%
%{--div + canvas inseridos aqui com jquery--}%%{--

            <script>
                var divChart = getElementos(document.currentScript).divChart;
                divChart.segalimChart = inicializaComponenteChart(${raw(jsonConcessoesAnual)});
            </script>
        </div>

    </div>
    <div class="row">
        <!--                          CESTAS CONCEDIDAS MES A MES               -->

        <div class="div-chart col-lg-6">
            <g:select name="nomeRegional" from="${session.regionais}"
                      class="many-to-one select-regional"
                      noSelection="['': 'Regional']" onchange="changeSelectRegional(this)"/>
            <g:select name="idServico" from="${session.servicos}"
                      optionKey="id" optionValue="nome"
                      class="many-to-one select-servico"
                      noSelection="['': 'Serviço']"/>
            <g:select name="ano" from="${session.anosOperacao ?: ['2017', '2018']}"
                      value="2017" class="many-to-one select-ano"
                      onchange="changeSelectAno(this)"/>
            <input class="download-button fa" type="button" value="&#xf0ab" title="Download do gráfico"
                   onclick="donwloadGrafico(this)"/>
            <input class="clean-button fa" type="button" value="&#xf2ea" title="Voltar para configuração inicial"
                   onclick="limparFiltros(this)"/>
            <input class="refresh-button fa" type="button" value="&#xf0b0" title="Aplicar filtros e atualizar"
                   onclick='atualiza(this, "${g.createLink( action:'obtemConcessoesMensal')}")'/>
            --}%
%{--div + canvas inseridos aqui com jquery--}%%{--

            <script>
                var divChart = getElementos(document.currentScript).divChart;
                divChart.segalimChart = inicializaComponenteChart(${raw(jsonConcessoesMensal)});
            </script>
        </div>

        <!--                          ACOES EMPREENDIDAS MES A MES                  -->

        <div class="div-chart col-lg-6">
            <g:select name="nomeRegional" from="${session.regionais}"
                      class="many-to-one select-regional"
                      noSelection="['': 'Regional']" onchange="changeSelectRegional(this)"/>
            <g:select name="idServico" from="${session.servicos}"
                      optionKey="id" optionValue="nome"
                      class="many-to-one select-servico"
                      noSelection="['': 'Serviço']"/>
            <g:select name="ano" from="${session.anosOperacao ?: ['2017', '2018']}"
                      value="2017" class="many-to-one select-ano"
                      onchange="changeSelectAno(this)"/>
            <input class="download-button fa" type="button" value="&#xf0ab" title="Download do gráfico"
                   onclick="donwloadGrafico(this)"/>
            <input class="clean-button fa" type="button" value="&#xf2ea" title="Voltar para configuração inicial"
                   onclick="limparFiltros(this)"/>
            <input class="refresh-button fa" type="button" value="&#xf0b0" title="Aplicar filtros e atualizar"
                   onclick='atualiza(this, "${g.createLink( action:'obtemHistorico')}")'/>
            --}%
%{--div + canvas inseridos aqui com jquery--}%%{--

            <script>
                var divChart = getElementos(document.currentScript).divChart;
                divChart.segalimChart = inicializaComponenteChart(${raw(jsonHistorico)});
            </script>
        </div>
    </div>
--}%
</div>

    </body>
</html>