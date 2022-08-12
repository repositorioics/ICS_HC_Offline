package com.ics.ics_hc_offline.dto;

public class ErrorDTO {
    private Long codigoError;
    private String mensajeError;

    public Long getCodigoError() {
        return codigoError;
    }
    public void setCodigoError(Long codigoError) {
        this.codigoError = codigoError;
    }
    public String getMensajeError() {
        return mensajeError;
    }
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
