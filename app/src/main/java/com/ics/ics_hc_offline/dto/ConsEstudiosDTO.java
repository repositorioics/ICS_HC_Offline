package com.ics.ics_hc_offline.dto;

public class ConsEstudiosDTO {
    private int secConsEstudios;
    private int codigoExpediente;
    private String codigoConsentimiento;
    private String retirado;

    public int getSecConsEstudios() {
        return secConsEstudios;
    }

    public void setSecConsEstudios(int secConsEstudios) {
        this.secConsEstudios = secConsEstudios;
    }

    public int getCodigoExpediente() {
        return codigoExpediente;
    }

    public void setCodigoExpediente(int codigoExpediente) {
        this.codigoExpediente = codigoExpediente;
    }

    public String getCodigoConsentimiento() {
        return codigoConsentimiento;
    }

    public void setCodigoConsentimiento(String codigoConsentimiento) {
        this.codigoConsentimiento = codigoConsentimiento;
    }

    public String getRetirado() {
        return retirado;
    }

    public void setRetirado(String retirado) {
        this.retirado = retirado;
    }
}
