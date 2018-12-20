package org.apoiasuas

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.SituacaoPrograma
import org.apoiasuas.redeSocioAssistencial.Acesso
import org.apoiasuas.util.Credencial
import org.apoiasuas.util.StringUtils

class CustomizadaTagLib {

    static defaultEncodeAs = [taglib: 'raw'];

    final static String TAMANHO_ICONE = '40px';

    def emoticon = { attrs, body ->
        out << body() << (attrs.happy == 'true' ? " :-)" : " :-(")
    }


/*
        <asset:image style="width: 50px; height: 50px" title="${'idoso'}" src="idoso.png"/>
        <asset:image style="width: 50px; height: 50px" title="${'idoso'}" src="crianca.png"/>
        <asset:image style="width: 50px; height: 50px" title="${'idoso'}" src="pcd.png"/>
        <asset:image style="width: 50px; height: 50px" title="${'idoso'}" src="monoparental.png"/>
        <asset:image style="width: 50px; height: 50px" title="${'idoso'}" src="gestante.png"/>
*/

/**
 * Exibe icones para cada tipo de vulnerabilidade da familia
 *
 * @attr familia REQUIRED
 */
    def vulnerabilidades = { attrs, body ->

        //Traduzindo propriedades de ItemMenuDTO para os atributos da tag
        if (! attrs.containsKey("familia"))
            throw new RuntimeException("Atributo familia obrigatorio na tag vulnerabilidades")
        Familia familia = attrs.remove("familia");

        if (familia.gestante)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'gestante(s)'}", src: 'gestante.png');
        if (familia.nutriz)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'nutriz'}", src: 'nutriz.png');
        if (familia.criancaPequena)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'criança(s) de 0 a 5 anos'}", src: 'crianca_pequena.png');
        if (familia.crianca)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'criança(s) de 6 a 11 anos'}", src: 'crianca.png');
        if (familia.adolescente)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'adolescente(s) de 12 a 17 anos'}", src: 'adolescente.png');
        if (familia.pcd)
        out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                title:"${'pessoa(s) com deficiência'}", src: 'pcd.png');
        if (familia.idoso)
            out << asset.image(style:"width: $TAMANHO_ICONE; height: $TAMANHO_ICONE",
                    title:"${'idoso(s)'}", src: 'idoso.png');
    }


    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoTexto = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        if (attrs.value in Date)
            attrs.value = ((Date)attrs.value).format("dd/MM/yyyy");
        out << g.campoEdicao(template: '/layouts/campoEdicao',
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            g.textField(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoCheckbox = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        if (attrs.value in Date)
            attrs.value = ((Date)attrs.value).format("dd/MM/yyyy");
        out << g.campoEdicao(template: '/layouts/campoEdicao',
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                name: attrs.name, titulo: "", helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            out << g.checkBox(attrs);
            out << " "+attrs.titulo;
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoSelect = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        out << g.campoEdicao(template: '/layouts/campoEdicao',
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            g.select(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoMemo = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        attrs.classesDiv = 'tamanho-memo';
        if (! attrs.rows)
            attrs.rows = 8;

        out << g.campoEdicao(template: '/layouts/campoEdicao', beanCamposEdicao: attrs.beanCamposEdicao,
                quebraLinha: attrs.quebraLinha, name: attrs.name, titulo: attrs.titulo,
                helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
                obrigatorio: attrs.obrigatorio) {
            g.textArea(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicao = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);

        if (! attrs.rows)
            attrs.rows = 8;
        out << render(template: '/layouts/campoEdicao',
                model: [beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                        name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
                        obrigatorio: attrs.obrigatorio]) {
            raw(body())
        }
    }

    private void comumEdicao(Map attrs) {
        attrs.beanCamposEdicao = attrs.beanCamposEdicao ?: request['beanCamposEdicao'] ?: null;
        attrs.value = attrs.value ?: attrs.beanCamposEdicao?.(attrs?.name);
    }

    /**
     * @attr conteudo atalho para passar um conteúdo sem ter que implementar um corpo na tag (este último, no entanto, tem prevalência quando presente)
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr titulo string a ser exibida como titulo do campo
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr escondeVazio default TRUE - Indica se o campo deve ser ignorado na rendereização quando o conteúdo passado é vazio
     * @attr classeCss css para estilização do elemento que agrupa as informações deste campo
     */
    def campoExibicao = { attrs, body ->
        String escondeVazio = attrs.remove('escondeVazio') ?: 'true';

        Object conteudo = attrs.conteudo;
        if (conteudo && conteudo instanceof Date)
            conteudo = g.formatDate(date: conteudo)?.toString()
        else if (conteudo) {
            conteudo = conteudo?.toString();
        }

        //Substitui o escape padrao do Grails por um customizado que interpreta \n como <br>
        def newBody = body() ?: conteudo ? StringUtils.toHtml(conteudo) : null
        if (escondeVazio.toBoolean() && ! newBody)
            return;

        out << render(template: '/layouts/campoExibicao', model: attrs) { newBody }
    }

    /**
     * @attr familia REQUIRED
     */
    def acoesFamilia = { attrs, body ->
        Familia familia = attrs.familia;
        Acesso acesso = session.credencial.acesso

        out << g.form(name: 'formAcoesFamilia') {
            out << '\n ' + g.hiddenField(name: 'id', value: familia.id);
            out << '\n ' + g.hiddenField(name: 'version', value: familia.version);

            //acoes para os servicos da ponta:
            if (acesso == Acesso.ENCAMINHAMENTO && session.credencial.servicoSistema.id == familia.servicoSistemaSeguranca.id) {
                if (familia.situacao in [SituacaoPrograma.PRE_SELECIONADA, SituacaoPrograma.INSERCAO_LIBERADA, SituacaoPrograma.NAO_ATENDIDA, SituacaoPrograma.NAO_LOCALIZADA])
                    out << '\n ' + g.actionSubmit(value: "Inserir no programa", action:'inserir', class: 'botao-acao btn-default btn');
                if (familia.situacao != SituacaoPrograma.NAO_ATENDIDA)
                    out << '\n ' + g.actionSubmit(value: "Não será atendida atualmente", action:'naoAtender', class: 'botao-acao btn-default btn');
                if (familia.situacao != SituacaoPrograma.NAO_LOCALIZADA)
                    out << '\n ' + g.actionSubmit(value: "Não foi localizada", action:'naoLocalizada', class: 'botao-acao btn-default btn');
                if (familia.situacao == SituacaoPrograma.INDICADA_SERVICO)
                    out << '\n ' + g.actionSubmit(value: "Excluir", action:'excluir', class: 'botao-acao btn-default btn');
            }

            //acoes para a gestao:
            if (acesso == Acesso.GESTAO) {
                if (familia.situacao == SituacaoPrograma.INDICADA_SERVICO) {
                    out << '\n ' + g.actionSubmit(value: "Liberar inserção no programa", action: 'liberarInsercao', class: 'botao-acao btn-default btn');
                    out << '\n ' + g.actionSubmit(value: "Não será atendida atualmente", action:'naoAtender', class: 'botao-acao btn-default btn');
                }
                if (familia.situacao in [SituacaoPrograma.INSERCAO_LIBERADA, SituacaoPrograma.INSERIDA])
                    out << '\n ' + g.actionSubmit(value: "Registrar concessão", action:'registrarConcessao', class: 'botao-acao btn-default btn');
            }

            //acoes para o banco de alimentos:
            if (acesso == Acesso.ATENDIMENTO) {
                if (familia.situacao == SituacaoPrograma.INSERIDA)
                    out << '\n ' + g.actionSubmit(value: "Registrar concessão", action:'registrarConcessao', class: 'botao-acao btn-default btn');
            }
            out << '\n ';
        }
//        if (acesso == Acesso.ENCAMINHAMENTO && familia.situacao != SituacaoPrograma.INSERIDA)
//            out << form

    }

    /**
     * @attr not
     * @attr acessos REQUIRED
     */
    def temAcesso = { attrs, body ->
        boolean not = false;
        if (attrs.containsKey('not'))
            not = attrs.not == true;
        List<Acesso> acessos = []
        if (attrs.containsKey('acessos')) {
            if (attrs.acessos instanceof Acesso)
                acessos = [attrs.acessos]
            else
                acessos = attrs.acessos
        }
        if (not)
            acessos = Acesso.values().removeAll(acessos);
        Credencial credencial = session.credencial;
        if (! credencial?.acesso)
            return;

        if (acessos.contains(credencial.acesso)) {
            out << body();
        }
    }

/*
    def nulo = {
        out << withTag(name: 'form', attrs: [name: 'formAcoesFamilia']) {
            out << body()
        }

        out << g.campoEdicao(template: '/servico/campoEdicao', plugin: Modulos.NUCLEO, beanCamposEdicao: attrs.beanCamposEdicao,
                quebraLinha: attrs.quebraLinha, name: attrs.name, titulo: attrs.titulo,
                helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
                obrigatorio: attrs.obrigatorio) {
            g.textArea(attrs);
        }

        out << g.render(attrs, body);

        //Eh preciso buscar a tag original antes de executa-la, pois ela foi sobrescrita
        FormTagLib original = grailsAttributes.applicationContext.getBean(FormTagLib.name)
        original.select.call(attrs)

        out << render(template: '/layouts/campoExibicao', model: attrs) { newBody }
    }
*/

}
