<!DOCTYPE html>
<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema; org.apoiasuas.util.SegurancaHelper; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia" %>
<html>
    <head>
        <meta name="layout" content="main" />
        <title>Gestão</title>
        %{--<asset:javascript  src="hammer.js"/>--}%
        <asset:javascript src="Chart-2.7.3.js"/>
        %{--<script src="https://cdnjs.com/libraries/Chart.js"/>--}%
        <asset:javascript src="chartjs-plugin-datalabels-0.5.0.js"/>
        <asset:javascript src="gestao.js"/>
        %{--<asset:javascript  src="chartjs-plugin-zoom-0.6.6.js"/>--}%
        %{--<asset:stylesheet src="familia.css"/>--}%
    </head>
    <body>

        asdasdasd

        <script>
            $( document ).ready(function() {

                Chart.defaults.global.defaultFontColor = 'black';
                Chart.defaults.global.devicePixelRatio = 2;
                Chart.defaults.global.defaultFontSize = 20;
                Chart.defaults.global.legend.labels.fontSize = 13;

                //espaço entre a legenda e o gráfico (
                Chart.Legend.prototype.afterFit = function() {
                    this.height = this.height + 30;
                };

                //          CESTAS CONCEDIDAS MES A MES
                var maiorLimite = 1000; //fixme: calcular no controller
                var ctx = document.getElementById("myChart");
                var myChart = new Chart(ctx, {
                    type: 'bar',
                    data: ${raw(jsonConcessoesMensal)},
                    options: {
                        legend: { display: true, },
//                        title: {
//                            display: true,
//                            text: 'Cestas Concedidas'
//                        },
                        scales: {
                            yAxes: [{
                                offset: true,
                                ticks: {
                                    suggestedMax: maiorLimite * 1.05, //5% de folga no topo do eixo y
                                    beginAtZero:true,
                                    offset: true,
                                    fontSize: 13,
                                }
                            }]
                        },
                        plugins: {
                            datalabels: {
                                display: 'auto',
                                anchor: 'end',
                                align: 'top',
                                font: {
                                  size: 13,
                                }
                            }
                        }
                    }
                });

                //          ACOES EMPREENDIDAS MES A MES
                var ctx2 = document.getElementById("myChart2");
                var myChart2 = new Chart(ctx2, {
                    type: 'line',
                    data: ${raw(jsonHistorico)},
                    options: {
                        scales: {
                            xAxes: [{
                                offset: true
                            }],
                            yAxes: [{
                                offset: true,
                                ticks: {
                                    beginAtZero:true,
                                    fontSize: 13,
                                }
                            }],
                        },
//                        pan: {
//                          enabled: true,
//                          mode: "y",
//                        },
//                        zoom: {
//                          enabled: true,
//                          mode: "y",
//                        },
                        plugins: {
                            datalabels: {
                                align: 'top',
                                display: 'auto',
                                font: {
                                  size: 13,
                                }
                            }
                        },
                    }
                });

                //      CESTAS CONCEDIDAS POR REGIONAL
                var ctx3 = document.getElementById("myChart3");
                var myChart3 = new Chart(ctx3, {
                    type: 'doughnut',
                    data: ${raw(jsonConcessoesAnual)},
                    options: {
                        title: {
                            display: true,
                            text: 'Cestas Concedidas'
                        },
                        plugins: {
                            datalabels: {
//                                color: '#fff',
                                font: {
                                    size: 10,
                                }
                            }
                        }
                    }
                });
            });

            function donwloadGrafico(chart) {
                var canvasDataUrl = chart.toDataURL("image/png", 1.0).replace("image/png", "image/octet-stream");
                var link = document.createElement('a');
                link.download = "grafico.png";
                link.href = canvasDataUrl;
                link.click();
            }
        </script>

        <g:render template="/inicio/menu"/>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>


%{--                         FAMILIAS  HOJE                                                --}%
        <div style="min-width: 500px; width: 48%; float: left; border: medium">
            %{--div + canvas inseridos aqui com jquery--}%
            <input type="button" value="Download"/>
            <script>
                {
                    var $canvas = inicializaComponenteChart({
                        type: 'doughnut',
                        data: ${raw(jsonFamiliasAtual)},
                        options: {
                            title: {
                                display: true,
                                text: 'Familias Hoje'
                            },
                            plugins: {
                                datalabels: {
//                                color: '#333333',
                                    font: {
                                        size: 10,
                                    }
                                }
                            }
                        }
                    });
                }
            </script>
        </div>

        <div style="min-width: 500px; width: 48%; float: left">
            <div>
                <canvas id="myChart3"></canvas>
            </div>
            <input type="button" value="download" onclick="donwloadGrafico(document.getElementById('myChart3'))"/>
        </div>

        <div style="min-width: 500px; width: 48%; float: left">
            <div>
                <canvas id="myChart"></canvas>
            </div>
            <input type="button" value="download" onclick="donwloadGrafico(document.getElementById('myChart'))"/>
        </div>

        <div style="min-width: 500px; width: 48%; float: left">
            <div>
                <canvas id="myChart2"></canvas>
            </div>
            <input type="button" value="download" onclick="donwloadGrafico(document.getElementById('myChart2'))"/>
        </div>

    </body>
</html>