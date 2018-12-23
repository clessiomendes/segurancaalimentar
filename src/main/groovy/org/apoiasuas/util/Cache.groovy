package org.apoiasuas.util

import org.apoiasuas.redeSocioAssistencial.Acesso
import org.apoiasuas.redeSocioAssistencial.ServicoSistema

import javax.servlet.http.HttpSession

public class Cache {

    static List<ServicoSistema> servicos = null
    static List<ServicoSistema> servicosEncaminhamento = null
    static List<String> regionais = null

    private static void inicializaServicos() {
        servicos = ServicoSistema.findAll().sort{ it.nome };
        servicosEncaminhamento = servicos.findAll { it.acesso == Acesso.ENCAMINHAMENTO } ;
        regionais = servicosEncaminhamento.collect{ it.regional }.unique().findAll{ it }.sort();
    }

    public static List<ServicoSistema> getServicosEncaminhamento(String regional = null) {
        if (servicosEncaminhamento == null)
            inicializaServicos();

        //filtro opcional por regional
        if (regional)
            return servicosEncaminhamento.findAll { it.regional == regional }
        else
            return servicosEncaminhamento;
    }

    public static ServicoSistema getServico(Long id) {
        if (servicos == null)
            inicializaServicos();

        return servicos.find { it.id == id }
    }

    public static ServicoSistema getServico(String email) {
        if (servicos == null)
            inicializaServicos();

        return servicos.find { it.email == email }
    }

    public static List<ServicoSistema> getRegionais() {
        if (regionais == null)
            inicializaServicos();
        return regionais;
    }
}
