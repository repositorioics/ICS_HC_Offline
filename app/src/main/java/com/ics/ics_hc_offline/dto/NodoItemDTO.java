package com.ics.ics_hc_offline.dto;

import java.util.ArrayList;
import java.util.List;

public class NodoItemDTO {
    private static final long serialVersionUID = 4135130555568007409L;

    private String etiqueta;
    private String url;
    private int orden;
    private boolean autorizado;
    private List<NodoItemDTO> nodoMenuItems = new ArrayList<NodoItemDTO>();

    public NodoItemDTO() {

    }

    /**
     * Establece el nombre del item del men�
     */
    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
    /**
     * Obtiene el nombre del item del men�
     */
    public String getEtiqueta() {
        return etiqueta;
    }
    /**
     * Establece la localizaci�n del recurso o url dentro de la aplicaci�n a donde
     * se redigir� la navegaci�n al hacer click sobre el item del men�
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * Obtiene la localizaci�n del recurso dentro de la aplicaci�n y que corresponde
     * a la url donde se redigir� la navegaci�n al hacer click sobre el item del men�.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Establece el n�mero de orden asignado al item del men� dentro de un submen� o men� ra�z.
     */
    public void setOrden(int orden) {
        this.orden = orden;
    }
    /**
     * Obtiene el n�mero de orden asignado al item del men� dentro de un submen� o men� raiz.
     */
    public int getOrden() {
        return orden;
    }

    /**
     *
     * @return
     */
    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public List<NodoItemDTO> getNodoMenuItems() {
        return nodoMenuItems;
    }

    public void setNodoMenuItems(List<NodoItemDTO> nodoMenuItems) {
        this.nodoMenuItems = nodoMenuItems;
    }

    @Override
    public String toString() {
        return this.etiqueta;
    }
}
