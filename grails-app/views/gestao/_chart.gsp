<%@ page import="org.apoiasuas.GestaoController; java.text.DateFormatSymbols; org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>

<g:temAcesso acessos="${acessos ?: org.apoiasuas.redeSocioAssistencial.Acesso.values()}">

    <div class="div-chart col-lg-6" id="${idGrafico}">

    <!--    FILTROS SELECIONAVEIS    -->

        <g:if test="${filtros.containsKey('ano')}">
            <g:select name="ano" from="${session.anosOperacao ?: ['2017', '2018']}"
                      value="${filtros.ano}"
                      value="2017" class="many-to-one select-ano form-control"
                      onchange="changeSelectAno(this)"/>
        </g:if>
        <g:if test="${filtros.containsKey('mes')}">
            <g:select name="mes" from="${GestaoController.mapaMeses()}"
                      value="${filtros.mes}"
                      optionKey="key" optionValue="value"
                      class="many-to-one select-mes form-control"
                      noSelection="['': 'Mês']"/>
        </g:if>
        <g:if test="${filtros.containsKey('nomeRegional')}">
            <g:select name="nomeRegional" from="${ org.apoiasuas.util.Cache.regionais }"
                      class="many-to-one select-regional form-control"
                      value="${filtros.nomeRegional}"
                      noSelection="['': 'Regional']" onchange="changeSelectRegional(this)"/>
        </g:if>
        <g:if test="${filtros.containsKey('idServico')}">
            <g:select name="idServico" from="${ org.apoiasuas.util.Cache.servicosEncaminhamento }"
                      optionKey="id" optionValue="nome"
                      value="${filtros.idServico}"
                      class="many-to-one select-servico form-control"
                      noSelection="['': 'Serviço']"/>
        </g:if>

    <!--    BOTÕES DE AÇÃO    -->

        <div class="btn-group">
            <button type="button" class="btn btn-default refresh-button" title="Aplicar filtros e atualizar"
                    onclick='atualiza(this, "${action}")'/>
            <button type="button" class="btn btn-default clean-button" title="Voltar para configuração inicial"
                    onclick="limparFiltros(this)"/>
            <button type="button" class="btn btn-default download-button" title="Download do gráfico"
                    onclick="donwloadGrafico(this)"/>
        </div>

        %{--div + canvas inseridos aqui com jquery--}%

    <!--    JAVASCRIPT DE RENDERIZAÇÃO DO GRÁFICO    -->

        <script>
        {
             /*
            //descobre o elemento div dentro do qual o elemento <script> encontra-se aninhado
            var currentScript = document.currentScript || (function () {
                        var scripts = document.getElementsByTagName('script');
                        return scripts[scripts.length - 1];
                    })();
    */
            //cria os elementos necessarios para a geracao do grafico e os insere dentro do div principal reservado para o mesmo
            var $divChart = jQuery("#${idGrafico}");
            var $divCanvas = $('<div><canvas/></div>');
            $divCanvas.appendTo($divChart);
            var $canvas = $divCanvas.find('canvas');

            //inicializa o grafico com as opcoes iniciais
            $divChart[0].segalimChart = new Chart($canvas, ${json});

    /*
            //chama ajax para obter a primeira versao do grafico
            $.getJSON("${action}",
                      {
                          nomeRegional: "${filtros.nomeRegional}",
                          idServico: "${filtros.idServico}",
                          ano: "${filtros.ano}",
                          mes: "${filtros.mes}",
                      },
                      function( result ) {
                          inicializaComponenteChart(result, jQuery("#${idGrafico}"));
                      })
                .fail(function(erro) {
                    alert("Erro ao atualizar os dados do grafico"
                        );
                    console.
                    log(erro.responseText);
                });
    */
        }
        </script>
    </div>

</g:temAcesso>