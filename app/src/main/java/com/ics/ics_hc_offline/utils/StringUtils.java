package com.ics.ics_hc_offline.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String BREAK_LINE = "\n";
    public static final String CRETURN_LINE = "\r";

    public static final boolean areEquals(String stringA, String stringB) {
        return changeNullToEmpty(stringA).equals(changeNullToEmpty(stringB));
    }

    public static final boolean areEqualsIgnoreCase(String stringA, String stringB) {
        return changeNullToEmpty(stringA).toLowerCase().equals(changeNullToEmpty(stringB).toLowerCase());
    }

    public static final boolean isNullOrEmpty(String string) {
        return string==null || string.trim().equals(EMPTY_STRING);
    }

    public static final boolean isNotEmpty(String string) {
        return !isNullOrEmpty(string);
    }

    public static final String changeNullOrEmptyToString(String string, String changeString) {
        return isNullOrEmpty(string) ? changeString : string;
    }

    public static final String changeNullToEmpty(String string) {
        return string==null ? EMPTY_STRING : string;
    }

    public static final String trimAndUpperCase(String string) {
        string = changeNullToEmpty(string);
        return string.toUpperCase();
    }

    public static final boolean equalsAny(String string, String ... values) {
        boolean result = false;
        if (string==null || values.length==0) return result;
        for (String value: values) {
            if (string.equals(value)) { result = true; break; }
        }
        return result;
    }

    public static final boolean containsAny(String string, String ... values) {
        boolean result = false;
        if (string==null || values.length==0) return result;
        for (String value: values) {
            if (string.contains(value)) { result = true; break; }
        }
        return result;
    }

    public static final String removeUnprintableChars(String string) {
        if (isNullOrEmpty(string)) return string;
        string = string.replace(BREAK_LINE, EMPTY_STRING);
        string = string.replace(CRETURN_LINE, EMPTY_STRING);
        return string;
    }

    /**
     * Convierte cadena a fecha
     * @param strFecha
     * @return
     */
    public static Date formatoFecha (String strFecha){
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");

        Date fecha = null;
        try {

            fecha = formatoDelTexto.parse(strFecha);

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

        return fecha;
    }

    /**
     * Determina si el par�metro valor es num�rico
     * @param valor
     * @return
     */
    public static boolean valorNumerico(String valor) {
        char[] charValor = valor.toCharArray();
        for (int i = 0; i < valor.length(); i++) {
            if (!(Character.isDigit(charValor[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determina si el par�metro valor es alfab�tico
     * @param valor
     * @return
     */
    public static boolean valorAlfabetico(String valor) {
        valor = valor.replace(" ", "");
        char[] charValor = valor.toCharArray();
        for (int i = 0; i < valor.length(); i++) {
            if (!(Character.isLetter(charValor[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * metodo para validar correo electronico
     * @param correo
     * @return
     */
    public static boolean isEmail(String correo) {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        mat = pat.matcher(correo);
        if (mat.find()) {
            System.out.println("[" + mat.group() + "]");
            return true;
        }else{
            return false;
        }
    }

    public static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

    /**
     * Función para concatenar valores separados por ","
     * @param vOriginal Cadena original
     * @param vConcatenar valor que se agregará a la cadena original
     * @return valores separados por ","
     */
    public static String concatenar(String vOriginal, String vConcatenar){
        if(vConcatenar == null || vConcatenar.trim().length() == 0) {
            return vOriginal;
        }

        if(vOriginal != null && vOriginal.trim().length() > 0){
            vOriginal = vOriginal + ", " + vConcatenar.replace(":", "");
        }else{
            vOriginal = vConcatenar.replace(":", "");
        }

        return vOriginal;
    }
}
