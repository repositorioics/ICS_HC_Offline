package com.ics.ics_hc_offline.consulta;

import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.utils.StringUtils;

public class SeccionesDiagnosticos {
    public static boolean historiaExamenCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getHistoriaExamenFisico())) {
            result = true;
        }
        return result;
    }

    public static boolean tratamientoPlanesCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getAcetaminofen())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAsa())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getIbuprofen())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPenicilina())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAmoxicilina())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDicloxacilina())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getOtro())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getFurazolidona())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getMetronidazolTinidazol())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAlbendazolMebendazol())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSulfatoFerroso())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSueroOral())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSulfatoZinc())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getLiquidosIv())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPrednisona())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHidrocortisonaIv())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSalbutamol())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getOseltamivir())) {
            result = true;
        }
        return result;
    }

    public static boolean diagnosticosCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (hojaConsulta.getDiagnostico1() > 0) {
            result = true;
        }
        return result;
    }
    public static boolean diagnosticoProximaCitaCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (hojaConsulta.getProximaCita() != null) {
            result = true;
        }
        return result;
    }
}
