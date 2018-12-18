//abre um espaço entre a legenda e o gráfico para evitar que os rotulos sejam sobrepostos aa legenda
Chart.Legend.prototype.afterFit = function() {
    this.height = this.height + 15;
    console.log('this.height = '+this.height+' + 30;');
};

//Plugin para exibir informacoes no centro do grafico
//https://stackoverflow.com/a/43026361/1916198
Chart.pluginService.register({
    beforeDraw: function (chart) {
        if (chart.config.options.plugins.centertext) {
            //Get ctx from string
            var ctx = chart.chart.ctx;

            //Get options from the center object in options
            var centerConfig = chart.config.options.plugins.centertext;
            var fontStyle = centerConfig.fontStyle || 'Arial';
            var txt = centerConfig.text;
            var color = centerConfig.color || '#000';
            var sidePadding = centerConfig.sidePadding || 20;
            var sidePaddingCalculated = (sidePadding/100) * (chart.innerRadius * 2)
            //Start with a base font of 30px
            ctx.font = "30px " + fontStyle;

            //Get the width of the string and also the width of the element minus 10 to give it 5px side padding
            var stringWidth = ctx.measureText(txt).width;
            var elementWidth = (chart.innerRadius * 2) - sidePaddingCalculated;

            // Find out how much the font can grow in width.
            var widthRatio = elementWidth / stringWidth;
            var newFontSize = Math.floor(30 * widthRatio);
            var elementHeight = (chart.innerRadius * 2);

            // Pick a new font size so it will not be larger than the height of label.
            var fontSizeToUse = Math.min(newFontSize, elementHeight);

            //Set font settings to draw it correctly.
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2);
            var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 2);
            ctx.font = fontSizeToUse+"px " + fontStyle;
            ctx.fillStyle = color;

            //Draw text in center
            ctx.fillText(txt, centerX, centerY);
        }
    }
});

function inicializaComponenteChart(chartDefinition, afterFit) {
    var currentScript = document.currentScript || (function () {
                var scripts = document.getElementsByTagName('script');
                return scripts[scripts.length - 1];
            })();
    var $divCanvas = $('<div><canvas/></div>');
    var $canvas = $divCanvas.find('canvas');
    //$divCanvas.prependTo($(currentScript).parent());
    $divCanvas.appendTo($(currentScript).parent());

/*
    var $btnDownload = $(currentScript).parent().find(".download-button");
    $btnDownload.click(function() {
        donwloadGrafico($canvas[0]);
    })
*/

    return new Chart($canvas, chartDefinition);
}

function donwloadGrafico(btn) {
    var elementos = getElementos(btn);
    var titulo = elementos.chart.options.title.text || 'grafico'
    ReImg.fromCanvas(elementos.canvas).downloadPng(titulo+".png");
/*
    var canvasDataUrl = $canvas[0].toDataURL("image/png", 1.0).replace("image/png", "image/octet-stream");
    var link = document.createElement('a');
    link.download = "grafico.png";
    link.href = canvasDataUrl;
    link.click();
*/
}

function limparFiltros(btn) {
    var elementos = getElementos(btn);
    if (elementos.selectRegional) {
        elementos.selectRegional.selectedIndex = 0;
        $(elementos.selectRegional).change();
    }
    if (elementos.selectServico)
        elementos.selectServico.selectedIndex = 0;
    if (elementos.selectAno)
        elementos.selectAno.selectedIndex = 0;
    if (elementos.selectMes)
        elementos.selectMes.selectedIndex = 0;

    if (elementos.btnRrefresh)
        elementos.btnRrefresh.click();
}

$( document ).ready(function() {
    Chart.defaults.global.defaultFontColor = 'black';
    Chart.defaults.global.devicePixelRatio = 2;
    Chart.defaults.global.legend.labels.fontSize = 13;
    Chart.defaults.global.responsive = false;
    //setTimeout(teste, 1000);

});

function changeSelectRegional(select) {
    var $divChart = $(select).closest('.div-chart');
    var $selectServico = $divChart.find('.select-servico')
    $.getJSON(urlSelectRegional, {regional: select.value}, function(result) {
        $selectServico.find('option').remove();
        $selectServico.prepend("<option selected value=''>Serviço</option>");
        $.each(result, function(key, value){
            $selectServico.append('<option value=' + key + '>' + value + '</option>');
        });
    });
    //$selectServico.val("");
}

/**
 * Retorna uma estrutura contendo os diversos elementos DOM (botoes, divs, objeto chartjs...) associados ao elemento passado como parametro
 * @param filho
 */
function getElementos(filho) {
    var $divChart = $(filho).closest('.div-chart');
    return {
        divChart: $divChart[0],
        chart: $divChart[0].segalimChart,
        canvas: $divChart.find('canvas')[0],
        btnRrefresh: $divChart.find('.refresh-button')[0],
        btnDownload: $divChart.find('.download-button')[0],
        btnClean: $divChart.find('.clean-button')[0],
        selectRegional: $divChart.find('.select-regional')[0],
        selectServico: $divChart.find('.select-servico')[0],
        selectAno: $divChart.find('.select-ano')[0],
        selectMes: $divChart.find('.select-mes')[0],
    };
}

function atualiza(btn, url) {
    var elementos = getElementos(btn);
    var chart = elementos.chart
    $.getJSON(url,
              {
                  nomeRegional: elementos.selectRegional ? elementos.selectRegional.value : "",
                  idServico: elementos.selectServico ? elementos.selectServico.value : "",
                  ano: elementos.selectAno ? elementos.selectAno.value : "",
                  mes: elementos.selectMes ? elementos.selectMes.value : "",
              },
              function( result ) {
                  //atualiza os labels
                  chart.data.labels = result.data.labels;

                  if (chart.data.datasets.length == result.data.datasets.length) {
                      //atualiza os dados (mantendo a visibilidade dos labels de acordo com o que o operador marcou
                      var i = 0;
                      chart.data.datasets.forEach(function() {
                          chart.data.datasets[i].data = result.data.datasets[i].data;
                          if (chart.data.datasets[i].label)
                              chart.data.datasets[i].label = result.data.datasets[i].label;
                          i++;
                      });
                  } else {
                      //atualiza os datasets inteiros
                      chart.data.datasets = result.data.datasets;
                  }
                  //atualiza o total (ignora se nao houver esse elemento no grafico)
                  try {
                      chart.options.plugins.centertext = result.options.plugins.centertext;
                  } catch (e) { }
                  //atualiza o titulo (ignora se nao houver esse elemento no grafico)
                  try {
                      chart.options.title.text = result.options.title.text;
                  } catch (e) { }
                  chart.update();
              })
        .fail(function(erro) {
            alert("Erro ao atualizar os dados do grafico");
            console.log(erro.responseText);
        });
}

function teste() {

}