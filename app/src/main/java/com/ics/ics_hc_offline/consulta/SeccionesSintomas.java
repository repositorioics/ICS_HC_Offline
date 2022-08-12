package com.ics.ics_hc_offline.consulta;

import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.utils.StringUtils;

public class SeccionesSintomas {
    public static boolean generalesCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (hojaConsulta.getPas() != null
                && hojaConsulta.getPad() != null
                && hojaConsulta.getFciaResp() > 0
                && hojaConsulta.getFciaCard() > 0
                && hojaConsulta.getTemMedc() != null) {
            result = true;
        }
        return result;
    }

    public static boolean estadoGeneralCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getFiebre())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getAstenia())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getAsomnoliento())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getMalEstado())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getPerdidaConsciencia())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getInquieto())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getConvulsiones())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getHipotermia())
        && !StringUtils.isNullOrEmpty(hojaConsulta.getLetargia())){
            result = true;
        }
        return result;
    }

    public static boolean cabezaSintomaCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getCefalea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRigidezCuello())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getInyeccionConjuntival())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHemorragiaSuconjuntival())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDolorRetroocular())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getFontanelaAbombada())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getIctericiaConuntival())) {
            result = true;
        }
        return result;
    }

    public static boolean gargantaSintomasCompleto(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getEritema())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDolorGarganta())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAdenopatiasCervicales())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getExudado())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPetequiasMucosa())) {
            result = true;
        }
        return result;
    }

    public static boolean respiratorioSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getTos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRinorrea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getCongestionNasal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getOtalgia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAleteoNasal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getAleteoNasal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getApnea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRespiracionRapida())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getQuejidoEspiratorio())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEstiradorReposo())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getTirajeSubcostal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSibilancias())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getCrepitos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRoncos())) {
            result = true;

        }
        return result;
    }

    public static boolean gastrointestinalSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getPocoApetito())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getNausea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDificultadAlimentarse())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getVomito12horas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDiarrea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDiarreaSangre())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEstrenimiento())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDolorAbIntermitente())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDolorAbContinuo())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEpigastralgia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getIntoleranciaOral())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDistensionAbdominal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHepatomegalia())) {
            result = true;
        }
        return result;
    }

    public static boolean deshidratacionSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getLenguaMucosasSecas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPliegueCutaneo())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getOrinaReducida())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getBebeConSed())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getOjosHundidos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getFontanelaHundida())) {
            result = true;
        }
        return result;
    }

    public static boolean renalSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getSintomasUrinarios())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getLeucocituria())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getNitritos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEritrocitos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getBilirrubinuria())) {
            result = true;
        }
        return result;
    }

    public static boolean referenciaSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getInterconsultaPediatrica())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getReferenciaHospital())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getReferenciaDengue())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getReferenciaIrag())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getReferenciaChik())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEti())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getIrag())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getNeumonia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getcV())) {
            result = true;
        }
        return result;
    }

    public static boolean osteomuscularSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getAltralgia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getMialgia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getLumbalgia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getDolorCuello())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getTenosinovitis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getArtralgiaProximal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getArtralgiaDistal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getConjuntivitis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEdemaMunecas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEdemaCodos())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEdemaHombros())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEdemaRodillas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEdemaTobillos())) {
            result = true;
        }
        return result;
    }

    public static boolean cutaneoSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getRahsLocalizado())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRahsGeneralizado())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRashEritematoso())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRahsMacular())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRashPapular())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRahsMoteada())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRuborFacial())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEquimosis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getCianosisCentral())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getIctericia())) {
            result = true;
        }
        return result;
    }

    public static boolean estadoNutricionalSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getImc().toString())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getObeso())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSobrepeso())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getSospechaProblema())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getNormal())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getRahsMoteada())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getBajoPeso())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getBajoPesoSevero())) {
            result = true;
        }
        return result;
    }

    public static boolean vacunasSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getLactanciaMaterna())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getVacunasCompletas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getVacunaInfluenza())) {
            result = true;
        }
        return result;
    }

    public static boolean categoriaSintomasCompletado(HojaConsultaOffLineDTO hojaConsulta) {
        boolean result = false;
        if (!StringUtils.isNullOrEmpty(hojaConsulta.getSaturaciono2().toString())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getCategoria())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getManifestacionHemorragica())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPetequia10Pt())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPetequia20Pt())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPielExtremidadesFrias())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPalidezEnExtremidades())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getEpistaxis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getGingivorragia())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getPetequiasEspontaneas())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getLlenadoCapilar2seg())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getCianosis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHipermenorrea())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHematemesis())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getMelena())
                && !StringUtils.isNullOrEmpty(hojaConsulta.getHemoconcentracion().toString())) {
            result = true;
        }
        return result;
    }
}
