package com.ics.ics_hc_offline.dto;

public class EstadosHojasDTO {
    private int secEstado;
    private int codigo;
    private String descripcion;
    private String estado;
    private String orden;

    public int getSecEstado() {
        return secEstado;
    }

    public void setSecEstado(int secEstado) {
        this.secEstado = secEstado;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}
