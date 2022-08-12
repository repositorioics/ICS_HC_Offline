package com.ics.ics_hc_offline.dto;

public class DiagnosticoDTO {
    private int secDiagnostico;
    private String codigoDignostico;
    private String diagnostico;
    private char estado;

    public int getSecDiagnostico() {
        return secDiagnostico;
    }

    public void setSecDiagnostico(int secDiagnostico) {
        this.secDiagnostico = secDiagnostico;
    }

    public String getCodigoDignostico() {
        return codigoDignostico;
    }

    public void setCodigoDignostico(String codigoDignostico) {
        this.codigoDignostico = codigoDignostico;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    @Override
    public String toString(){
        return this.diagnostico;

    }
}
