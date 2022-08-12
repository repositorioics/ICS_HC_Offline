package com.ics.ics_hc_offline.consulta;

import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;

public class SeccionExamenes {
    public static boolean examenesMarcados(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if( hojaConsulta.getBhc() != null
                && hojaConsulta.getSerologiaDengue() != null
                && hojaConsulta.getSerologiaChik() != null
                && hojaConsulta.getGotaGruesa() != null
                && hojaConsulta.getExtendidoPeriferico() != null
                && hojaConsulta.getEgo() != null
                && hojaConsulta.getEgh() != null
                && hojaConsulta.getCitologiaFecal() != null
                && hojaConsulta.getFactorReumatoideo() != null
                && hojaConsulta.getAlbumina() != null
                && hojaConsulta.getAstAlt() != null
                && hojaConsulta.getBilirrubinas() != null
                && hojaConsulta.getCpk() != null
                && hojaConsulta.getColesterol() != null
                && hojaConsulta.getInfluenza() != null) {
            result = true;
        }
        return  result;
    }
}
