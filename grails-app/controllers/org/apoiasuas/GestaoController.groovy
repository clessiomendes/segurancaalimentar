package org.apoiasuas

import grails.converters.JSON
import groovy.json.JsonOutput
import org.apoiasuas.cidadao.EstatisticaService
import org.apoiasuas.cidadao.ResultadoEstatistica
import org.apoiasuas.cidadao.SituacaoPrograma
import org.apoiasuas.redeSocioAssistencial.Acesso
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.Cache
import org.apoiasuas.util.Credencial
import org.apoiasuas.util.SegurancaHelper

class GestaoController {

    EstatisticaService estatisticaService;
    static final LinkedHashMap<String, String> cores = [
            Blue: '0, 130, 200',
            Red: '230, 25, 75',
            Green: '60, 180, 75',
            Orange: '245, 130, 48',
            Purple: '145, 30, 180',
            Maroon: '128, 0, 0',
            Yellow: '255, 225, 25',
            Olive: '128, 128, 0',
            Cyan: '70, 240, 240',
            Lime: '210, 245, 60',
            Teal: '0, 128, 128',
            Lavender: '230, 190, 255',
            Beige: '255, 250, 200',
            Brown: '170, 110, 40',
            Mint: '170, 255, 195',
            Apricot: '255, 215, 180',
            Navy: '0, 0, 128',
            Grey: '128, 128, 128',
            Magenta: '240, 50, 230',
            Pink: '250, 190, 190',
    ];

    def index() {
        //Alimenta os caches de regionais e servicos, que serao utilizados na renderizacao da pagina
//        getRegionaisComCache();
//        getServicosComCache();

        //filtros padrao de acordo com o servico logado e o perfil do usuario
        Credencial credencial = SegurancaHelper.getCredencial(session);
        if (credencial.acesso == Acesso.ENCAMINHAMENTO) {
            request.idServico = credencial.servicoSistema.id;
            request.nomeRegional = credencial.servicoSistema.regional;
        }
        request.ano = 2017 //fixme: buscar o ultimo ano de uma das tabelas de estatistica (guardar em cache de sessao)

        //chama as actions de todos os graficos, converte para string e armazena no request para serem renderizadas
        //na tela quando esta é exibidia pela primeira vez. Nas atualizacoes subsequentes de cada grafico individual,
        //a action correspondente é chamada via ajax e, nesse momento, a resposta é passada no formato JSON
        request.jsonFamiliasAtual = obtemFamiliasAtual(request.nomeRegional, request.idServico);
        request.jsonConcessoesAnual = obtemFamiliasAtendidas(request.nomeRegional, request.ano, request.mes);
        request.jsonConcessoesMensal = obtemConcessoesMensal(request.nomeRegional, request.idServico, request.ano);
        request.jsonHistorico = obtemHistorico(request.nomeRegional, request.idServico, request.ano);

        render view: 'index'
    }

    /**
     * API que retorna os servicos aa partir de uma regional (ou todos os servicos, se o parametro for vazio).
     * Usa cache para evitar acessos ao banco de dados
     * @param regional
     * @return JSON com uma lista de pares [id: nome] dos servicos
     */
    def selectRegional(String regional) {
//        Map result = getServicosComCache(regional).collectEntries { [(it.id.toString()): it.nome] }
        Map result = Cache.getServicosEncaminhamento(regional).collectEntries { [(it.id.toString()): it.nome] }
        render result as JSON;
    }

    /**
     * Retorna uma descricao textual dos filtros escolhidos (Para ser usado nos titulos dos graficos)
     */
    private String completaTituloGrafico(String nomeRegional, Long idServico, Integer ano, Integer mes) {
        String result = "";
        if (mes) {
            if (ano)
                result += ", " + mapaMeses().find{ it.key == mes }.value + "/" + ano
        } else if (ano) {
            result += ", "+ano
        }

        if (idServico) {
            //busca do cache para evitar consultas excessivas ao banco de dados
            ServicoSistema ss = Cache.getServico(idServico);
            if (ss)
                result += ", "+ss.nome
        } else if (nomeRegional) {
            result += ", "+nomeRegional
        }
        return result;
    }

    def obtemConcessoesMensal(String nomeRegional, Long idServico, Integer ano) {

        String tituloGrafico = 'Cestas Concedidas'+completaTituloGrafico(nomeRegional, idServico, ano, null)

        Map<Date, Integer> concessoes = estatisticaService.getEstatisticaConcessoesMensal(nomeRegional, idServico, ano);

        List labels = concessoes.keySet().collect { it.format('MMM') }.toList();

        Map datasetConcedidas = [
                data: concessoes.values().toList(),
                backgroundColor: "rgba(${cores.values().getAt(0)}, 0.2)",
                borderColor: "rgba(${cores.values().getAt(0)}, 1)",
                borderWidth: 2,
                label: "cestas concedidas = ${concessoes.values().sum() ?: 0}",
        ]

        List limites = [750] * concessoes.values().size();
        Map datasetLimites = [
                type: 'line',
                fill: false,
                data: limites,
                backgroundColor: "rgba(${cores.values().getAt(1)}, 0.2)",
                borderColor: "rgba(${cores.values().getAt(1)}, 1)",
                borderWidth: 2,
                datalabels: [
                        display: false,
                ],
                label: "limite",
        ]

        //mostra limites apenas quando nenhuma regional ou servico tiverem sido selecionados
        List datasets = (nomeRegional || idServico) ? [datasetConcedidas] : [datasetConcedidas, datasetLimites];

        Map chartDefinition = [
                type: 'bar',
                data: [
                        labels: labels,
                        datasets: datasets,
                ],
                options: [
                        title: [
                                display: true,
                                text: tituloGrafico,
                                fontSize: 15,
                        ],
                        scales: [
                            xAxes: [[
                                offset: true
                            ]],
                            yAxes: [[
                                ticks: [
                                        beginAtZero:true,
                                        fontSize: 13,
    //                                    suggestedMax: limites.max() ? limites.max() * 1.05 : 0,
                                ]
                            ]],
                        ],
                        plugins: [
                                centertext: [
                                        text: concessoes?.values() ? "" : "0", //mostra ZERO para sinalizar que não há dados para retornar
                                        sidePadding: 50 //Default 20 (as a percentage)
                                ],
                                datalabels: [
                                    align: 'top',
                                    display: 'auto',
                                    font: [
                                      size: 13,
                                    ]
                                ]
                        ],
                ]
        ]

        //depende de quem chamou:
        withFormat {
            //chamada ajax esperando um resultado json
            json { return render(chartDefinition as JSON) }
            //metodo chamado diretamente de outra action do controller
            '*' { return groovy.json.JsonOutput.prettyPrint((chartDefinition as JSON).toString()) }
        }
    }

    def obtemHistorico(String nomeRegional, Long idServico, Integer ano) {

        String tituloGrafico = 'Histórico do Programa'+completaTituloGrafico(nomeRegional, idServico, ano, null)

        //Inicializacao das situacoes (SituacaoPrograma) previstos na resposta
        Map<SituacaoPrograma, String> mapaAcoes = [
                (SituacaoPrograma.INSERIDA): 'Inserções',
                (SituacaoPrograma.NAO_ATENDIDA): 'Indeferimentos',
                (SituacaoPrograma.NAO_LOCALIZADA): 'Não Localizadas',
                (SituacaoPrograma.PRE_SELECIONADA): 'Novas Elegíveis do Cadunico',
                (SituacaoPrograma.INDICADA_SERVICO): 'Indicações',
                (SituacaoPrograma.REMOVIDA): 'Desligamentos',
        ]

        ResultadoEstatistica historico = estatisticaService.getEstatisticaHistoricoFamilias(mapaAcoes.keySet().toList(), nomeRegional, idServico, ano);

        List labels = historico.periodos.collect { Date periodo -> periodo.format('MMM') }.toList();
//        List backgroundColor = ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"];

        int max = 0;
        List datasets = [];
        int i = 0;
        historico.acoes.each {
            max = Math.max(max, historico.get(it).max());
            Map dataset = [
    //                label: "cestas",
                    data: historico.get(it), //.collect{ it + new Random().nextInt(300)},
                    fill: false,
                    label: mapaAcoes.get(it)+" = ${historico.get(it).sum()}",
                    backgroundColor: "rgba(${cores.size() >= i-1 ? cores.values().getAt(i) : '0,0,0' }, 0.2)",
                    borderColor: "rgba(${cores.size() >= i-1 ? cores.values().getAt(i) : '0,0,0' }, 1)",
                    borderWidth: 1,
                    //inicialmente exibe apenas as familias inseridas
                    hidden: ! it.equals(SituacaoPrograma.INSERIDA),
                    datalabels: [
                            display: true,
                            color: "rgba(${cores.size() >= i-1 ? cores.values().getAt(i) : '0,0,0' }, 1)",
                    ],
            ]
            datasets << dataset;
            i++;
        }
        log.debug("max $max");
        
        Map chartDefinition = [
                    type: 'line',
                    data: [
                            labels: labels,
                            datasets: datasets,
                    ],
                    options: [
                            legend: [ display: true, ],
                        title: [
                            display: true,
                            text: tituloGrafico,
                            fontSize: 15,
                        ],
                        scales: [
                            yAxes: [[
                                ticks: [
                                        beginAtZero:true,
                                        fontSize: 13,
//                                        suggestedMax: max,
                                ]
                            ]]
                        ],
                        plugins: [
                            datalabels: [
                                display: 'auto',
                                anchor: 'end',
                                align: 'top',
                                offset: 1,
                                font: [
                                  size: 13,
                                ]
                            ]
                        ]
                    ]
        ];

        //depende de quem chamou:
        withFormat {
            //chamada ajax esperando um resultado json
            json { return render(chartDefinition as JSON) }
            //metodo chamado diretamente de outra action do controller
            '*' { return groovy.json.JsonOutput.prettyPrint((chartDefinition as JSON).toString()) }
        }
    }

    def obtemFamiliasAtendidas(String nomeRegional, Integer ano, Integer mes) {

        String tituloGrafico = 'Famílias Atendidas'+completaTituloGrafico(nomeRegional, null, ano, mes);

        Map<String, Integer> concessoes = estatisticaService.getEstatisticaFamiliasAtendidas(nomeRegional, ano, mes);

        List labels = concessoes.keySet().toList();

        Map dataset = [
                data: concessoes.values().toList(),
                //cores: tantas quanto os labels
                backgroundColor: cores.values().collect {"rgba($it, 0.5)"} ,
                borderColor: cores.values().collect {"rgba($it, 0.5)"} ,
        ]
        
        Map chartDefinition = [
                type: 'doughnut',
                data: [
                        labels: labels,
                        datasets: [dataset],
                ],
                options: [
                        aspectRatio: 1.5,
                        title: [
                                display: true,
                                text: tituloGrafico,
                                fontSize: 15,
                        ],
                        plugins: [
                            datalabels: [
                                font: [
                                    size: 10,
                                ]
                            ],
                            centertext: [
                                    text: concessoes.values().sum() ?: "0",
                                    sidePadding: 50 //Default 20 (as a percentage)
                            ],
                        ]
                ]
        ]
        //depende de quem chamou:
        withFormat {
            //chamada ajax esperando um resultado json
            json { return render(chartDefinition as JSON) }
            //metodo chamado diretamente de outra action do controller
            '*' { return groovy.json.JsonOutput.prettyPrint((chartDefinition as JSON).toString()) }
        }
    }

    def obtemFamiliasAtual(String nomeRegional, Long idServico) {

        String tituloGrafico = 'Familias hoje'+completaTituloGrafico(nomeRegional, idServico, null, null)

        //Inicializacao das situacoes (SituacaoPrograma) previstos na resposta
        Map<SituacaoPrograma, String> mapaSituacoes = [
                (SituacaoPrograma.INSERIDA): 'Inseridas no Programa',
                (SituacaoPrograma.NAO_ATENDIDA): 'Indeferidas pelo Serviço',
                (SituacaoPrograma.INSERCAO_RECUSADA_GESTAO): 'Indeferidas pela Gestão',
                (SituacaoPrograma.NAO_LOCALIZADA): 'Não Localizadas',
                (SituacaoPrograma.PRE_SELECIONADA): 'Não Analizadas',
                (SituacaoPrograma.REMOVIDA): 'Desligadas'
        ]

        //busca no banco de dados os resultados a serem apresentados em um mapa contendo as situacoes como chave e as quantidades totais como valor
        Map<SituacaoPrograma, Integer> familias = estatisticaService.getEstatisticaFamiliasAtual(
                mapaSituacoes.keySet().toList(),
                nomeRegional,
                idServico
        );

        //constroi os labels do grafico aa partir das chaves (situacoes) do mapa
        List labels = familias.keySet().collect {
            //traduz a Enum de situacoes para os descritivos definidos logo acima
            mapaSituacoes.get(it)
        };

        //constroi os valores do grafico aa partir dos valores (quantidades) do mapa
        Map dataset = [
                data: familias.values().toList(), //.collect { it + new Random().nextInt(18) },
                //cores: tantas quanto os labels
                backgroundColor: cores.values().collect {"rgba($it, 0.5)"} ,
                borderColor: cores.values().collect {"rgba($it, 0.5)"} ,
        ]

        //definicao completa do grafico (Chart) no padrao do ChartJs
        Map chartDefinition = [
                type: 'doughnut',
                data: [
                        labels: labels,
                        datasets: [dataset],
                ],
                options: [
                        aspectRatio: 1.5,
                        title: [
                                display: true,
                                text: tituloGrafico,
                                fontSize: 15,
                        ],
                        plugins: [
                            datalabels: [
                                font: [
                                    size: 10,
                                ]
                            ],
                            centertext: [
                                    text: familias.values().sum() ?: "0",
    //                            color: '#36A2EB', //Default black
    //                            fontStyle: 'Helvetica', //Default Arial
                                    sidePadding: 50 //Default 20 (as a percentage)
                            ]
                        ],
                ]
        ]

        //depende de quem chamou:
        withFormat {
            //chamada ajax esperando um resultado json
            json { return render(chartDefinition as JSON) }
            //metodo chamado diretamente de outra action do controller
            '*' { return groovy.json.JsonOutput.prettyPrint((chartDefinition as JSON).toString()) }
        }
    }

    def exemplo() {
        List labels = ["Africa", "Asia", "Europe", "Latin America", "North America"];
        List backgroundColor = ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"];
        List data = [2478,5267,734,784,433];

        Map dataset = [
//                label: "populacao",
                backgroundColor: backgroundColor,
                data: data
        ]
        List datasets = [dataset]
        Map dados = [:]
        dados.labels = labels
        dados.datasets = datasets
        request.jsonData = groovy.json.JsonOutput.prettyPrint((dados as JSON).toString())
        render view: 'index'
    }

    public static Map<Integer, String> mapaMeses() {
        int i=1;
        return new java.text.DateFormatSymbols().getShortMonths().take(12)
                .collectEntries { [(i++): it] };
    }
}
