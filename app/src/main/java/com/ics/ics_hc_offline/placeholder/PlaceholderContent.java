package com.ics.ics_hc_offline.placeholder;

import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.InicioDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();
    public static final List<PlaceholderItem> LST_ENFERMERIA = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i, 1, "", "","", ""));
        }
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.id, item);
        ITEM_MAP.put(String.valueOf(item.id), item);
    }

    private static PlaceholderItem createPlaceholderItem(int position, int numHojaConsulta, String separador,
                                                         String nomPaciente, String nomMedico, String estado) {
        //return new PlaceholderItem(String.valueOf(position), "Item " + position, makeDetails(position));
        return new PlaceholderItem(String.valueOf(position), numHojaConsulta, separador , nomPaciente, nomMedico, estado);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final int numHojaConsuta;
        public final String separador;
        public final String nomPaciente;
        public final String nomMedico;
        public final String estado;

        public PlaceholderItem(String id, int numHojaConsuta, String separador, String nomPaciente, String nomMedico, String estado) {
            this.id = id;
            this.numHojaConsuta = numHojaConsuta;
            this.separador = separador;
            this.nomPaciente = nomPaciente;
            this.nomMedico = nomMedico;
            this.estado = estado;
        }

        /*public PlaceholderItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }*/

        @Override
        public String toString() {
            return nomPaciente;
        }
    }

    /*public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final String details;

        public PlaceholderItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }*/
}