package com.ics.ics_hc_offline.utils;

import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;

public class UtilHojaConsulta {
    public static boolean validarCasoETI(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            if((hojaConsulta.getRinorrea() != null
                    && hojaConsulta.getRinorrea().compareTo("0") == 0)
                    || (hojaConsulta.getDolorGarganta() != null
                    && hojaConsulta.getDolorGarganta().compareTo("0") == 0)
                    || (hojaConsulta.getTos() != null
                    && hojaConsulta.getTos().compareTo("0") == 0)) {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {

        }
        return false;
    }
    public static boolean validarCasoIRAG(HojaConsultaOffLineDTO hojaConsulta) {

        try {
            if((hojaConsulta.getEstiradorReposo() != null
                    && hojaConsulta.getEstiradorReposo().compareTo("0") == 0)
                    || (hojaConsulta.getAleteoNasal() != null
                    && hojaConsulta.getAleteoNasal().compareTo("0") == 0)
                    || (hojaConsulta.getApnea() != null
                    && hojaConsulta.getApnea().compareTo("0") == 0)
                    || (hojaConsulta.getQuejidoEspiratorio() != null
                    && hojaConsulta.getQuejidoEspiratorio().compareTo("0") == 0)
                    || (hojaConsulta.getTirajeSubcostal() != null
                    && hojaConsulta.getTirajeSubcostal().compareTo("0") == 0)
                    || (hojaConsulta.getCianosisCentral() != null
                    && hojaConsulta.getCianosisCentral().compareTo("0") == 0)) {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
        }

        return false;
    }

    public static boolean validarCasoNeumonia(HojaConsultaOffLineDTO hojaConsulta) {
        if((hojaConsulta.getTos() != null
                && hojaConsulta.getTos().compareTo("0") == 0)
                && hojaConsulta.getCrepitos().compareTo("0") == 0) {
            return true;
        }
        if(hojaConsulta.getRespiracionRapida() != null
                && hojaConsulta.getRespiracionRapida().compareTo("0") == 0) {
            return true;
        }
        if(hojaConsulta.getCrepitos() != null
                && hojaConsulta.getCrepitos().compareTo("0") == 0) {
            return true;
        }
        return false;
    }
}
