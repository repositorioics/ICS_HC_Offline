package com.ics.ics_hc_offline.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtils {
    public static String obtenerEdad(Calendar fechaNacimiento) {

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR);
        int month = (age)*12 + today.get(Calendar.MONTH) - fechaNacimiento.get(Calendar.MONTH);

        if(today.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.get(Calendar.DAY_OF_MONTH)){
            month = month - 1;
        }

        if(month == 0) {
            Long tDias = (today.getTimeInMillis() - fechaNacimiento.getTimeInMillis())  / (1000 * 60 * 60 * 24);
            return new StringBuffer().append(tDias).append(" dias").toString();

        }
        else if(age == 0) {
            age = today.get(Calendar.MONTH) - fechaNacimiento.get(Calendar.MONTH);
            if(age == 0) {
                age = today.get(Calendar.DAY_OF_MONTH) - fechaNacimiento.get(Calendar.DAY_OF_MONTH);
                return new StringBuffer().append(age).append(" dias").toString();
            }else {
                int diaFechaActual = today.get(Calendar.DAY_OF_MONTH);
                int diaFechaNac = fechaNacimiento.get(Calendar.DAY_OF_MONTH);
                if (diaFechaActual < diaFechaNac) {
                    age = age - 1;
                    return new StringBuffer().append(age).append(" meses").toString();
                } else {
                    return new StringBuffer().append(age).append(" meses").toString();
                }

            }
        } else if (month > 0 && month < 12) {
            return new StringBuffer().append(month).append(" meses").toString();

        }else {
            if (today.get(Calendar.MONTH) < fechaNacimiento.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == fechaNacimiento.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.get(Calendar.DAY_OF_MONTH)) {
                age--;
            }
            return new StringBuffer().append(age).append(" años").toString();
        }
    }

    public static boolean esMayorFechaHoy(Calendar fechaSeleccionada) {
        Calendar today = Calendar.getInstance();

        if (fechaSeleccionada.after(today)) {
            return true;
        }
        return false;
    }

    /**
     * Función para validar si la hora es menor o igual que la hora actual siempre y cuando la fecha sea la actual
     *
     * @param fechaSeleccionada debe ser en formato dd/MM/yyyy
     * @param hora valor a validar en formato hh:mm:AM o hh:mm:PM (12 hrs)
     * @return si la hora es mayor que la actual retorna verdadero, falso para los demás casos
     */
    public static boolean esMayorHoraActual(String fechaSeleccionada, String hora) {
        try {
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date selDate = dateFormat.parse(fechaSeleccionada);

            //si la fecha seleccionada es igual a la fecha actual, se valida la hora
            if (dateFormat.format(selDate).equals(dateFormat.format(today))) {

                DateFormat hourFormat = new SimpleDateFormat("HH:mm");
                String hNow = hourFormat.format(today);
                String[] valoresH = hora.split(":");
                String[] valoresActH = hNow.split(":");

                if (valoresH.length < 3) {
                    return true;
                } else if (valoresH[2].equalsIgnoreCase("PM")) {
                    valoresH[0] = String.valueOf(Integer.parseInt(valoresH[0]) + 12);
                }

                if (Integer.parseInt(valoresH[0]) < Integer.parseInt(valoresActH[0]) ||
                        (Integer.parseInt(valoresH[0]) == Integer.parseInt(valoresActH[0]) && Integer.parseInt(valoresH[1]) <= Integer.parseInt(valoresActH[1]))) {
                    return false;
                }

                return true;

            } else if (selDate.before(today)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Indica si la hora es mayor que la actual
     *
     * @param hora hora a validar en formato hh:mm:AM o hh:mm:PM (12 hrs)
     * @return verdadero si la hora es mayor que la actual
     */
    public static boolean esMayorHoraActual(String hora) {
        Date today = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm");
        String hNow = hourFormat.format(today);
        String[] valoresH = hora.split(":");
        String[] valoresActH = hNow.split(":");

        if (valoresH.length < 3) {
            return true;
        } else if (valoresH[2].equalsIgnoreCase("PM")) {
            valoresH[0] = String.valueOf(Integer.parseInt(valoresH[0]) + 12);
        }

        if (Integer.parseInt(valoresH[0]) < Integer.parseInt(valoresActH[0]) ||
                (Integer.parseInt(valoresH[0]) == Integer.parseInt(valoresActH[0]) && Integer.parseInt(valoresH[1]) <= Integer.parseInt(valoresActH[1]))) {
            return false;
        }

        return true;
    }

    public static boolean esMayorHoraActual(int hora, int minutos) {
        Calendar horaSeleccion = Calendar.getInstance();
        horaSeleccion.set(Calendar.HOUR_OF_DAY, hora);
        horaSeleccion.set(Calendar.MINUTE, minutos);

        Calendar hoy = Calendar.getInstance();

        if(horaSeleccion.after(hoy)) {
            return true;
        }

        return false;
    }

    /**
     * Indica si la fecha es mayor o igual que la fecha actual
     *
     * @param fechaSeleccionada fecha a validar en formato dd/MM/yyyy
     * @return verdadero si la fecha es mayor o igual que la actual
     */
    public static boolean esMayorIgualFechaActual(String fechaSeleccionada) {
        try {
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date selDate = dateFormat.parse(fechaSeleccionada);

            //si la fecha seleccionada es igual a la fecha actual, se valida la hora
            if (dateFormat.format(selDate).equals(dateFormat.format(today)) || selDate.after(today)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;

        }
    }
}
