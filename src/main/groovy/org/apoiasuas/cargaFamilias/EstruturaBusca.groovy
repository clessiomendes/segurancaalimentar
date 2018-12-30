package org.apoiasuas.cargaFamilias

import com.opencsv.exceptions.CsvException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by clessio on 26/12/2018.
 */
class EstruturaBusca {
    final static Logger log = LoggerFactory.getLogger(CargaFamilias.class);

    private final static int ESTIMATIVA_FAMILIAS_POR_CARGA = 5000;
    private final static int ESTIMATIVA_CIDADAOS_POR_CARGA = ESTIMATIVA_FAMILIAS_POR_CARGA * 3;

//    public List<CsvException> erros = [] as ArrayList
    public List<CidadaoCarga> erros = [] as ArrayList
    public int familiasNovas = 0;
    public int familiasAtualizadas = 0;
    public int familiasSemGeo = 0;
    public int familiasComErro = 0;
    public int familiasSincronizadas = 0;
    public Map<String, List<CidadaoCarga>> codigoFamiliar_membrosDaFamilia = new HashMap(ESTIMATIVA_FAMILIAS_POR_CARGA)
    public Map<String, List<CidadaoCarga>> nisMembro_membrosDaFamilia = new HashMap(ESTIMATIVA_CIDADAOS_POR_CARGA)

//    public EstruturaBusca() {
//        codigoFamiliar_membrosFamilia
//        nis_membrosFamilia = [:] as HashMap
//    }
    public void logResultados() {
        log.info("Familias novas ${familiasNovas}, atualizadas ${familiasAtualizadas}, sem georeferenciamento ${familiasSemGeo}, indicações vinculadas ao cad unico ${familiasSincronizadas}, com erro ${familiasComErro}");
        erros.sort{it.linha}.each {
//            System.err.println("Erro linha ${it.linha}, NIS ${it.nis}: ${it.erro} ")
            log.info("Erro linha ${it.linha}, NIS RF ${it.nis}: ${it.erro} ")
        }

    }
}
