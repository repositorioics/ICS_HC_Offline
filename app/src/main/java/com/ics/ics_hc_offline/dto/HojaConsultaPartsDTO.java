package com.ics.ics_hc_offline.dto;

public class HojaConsultaPartsDTO {
    private int secHojaConsulta;
    private int codExpediente;
    private int numHojaConsulta;
    private String estado;
    private String fechaConsulta;
    private int usuarioEnfermeria;
    private int usuarioMedico;
    private String fis;
    private String fif;

    public int getSecHojaConsulta() {
        return secHojaConsulta;
    }

    public void setSecHojaConsulta(int secHojaConsulta) {
        this.secHojaConsulta = secHojaConsulta;
    }

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(String fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public int getUsuarioEnfermeria() {
        return usuarioEnfermeria;
    }

    public void setUsuarioEnfermeria(int usuarioEnfermeria) {
        this.usuarioEnfermeria = usuarioEnfermeria;
    }

    public int getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(int usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public String getFis() {
        return fis;
    }

    public void setFis(String fis) {
        this.fis = fis;
    }

    public String getFif() {
        return fif;
    }

    public void setFif(String fif) {
        this.fif = fif;
    }
}
