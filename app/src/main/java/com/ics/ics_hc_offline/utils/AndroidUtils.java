package com.ics.ics_hc_offline.utils;

import android.view.View;
import android.widget.CheckBox;

public class AndroidUtils {
    public static boolean esChkboxsFalse(View view1, View view2){
        return (!((CheckBox) view1).isChecked() && !((CheckBox) view2).isChecked()) ? true : false;
    }

    public static boolean esChkboxsFalse(View view1, View view2, View view3){
        return (!((CheckBox) view1).isChecked() && !((CheckBox) view2).isChecked() && !((CheckBox) view3).isChecked()) ? true : false;
    }


    public static boolean esChkboxsFalse(View view1, View view2, View view3, View view4, View view5){
        return (!((CheckBox) view1).isChecked() && !((CheckBox) view2).isChecked() && !((CheckBox) view3).isChecked()
                && !((CheckBox) view4).isChecked() && !((CheckBox) view5).isChecked()) ? true : false ;
    }

    public static void controlarCheckBoxGroup(View view1, View view2, View viewSeleccionado) {
        if(viewSeleccionado.getId() == view1.getId()) {
            ((CheckBox) view2).setChecked(false);
        } else {
            ((CheckBox) view1).setChecked(false);
        }
    }

    public static void controlarCheckBoxGroup(View view1, View view2, View view3, View viewSeleccionado) {
        if(viewSeleccionado.getId() == view1.getId()) {
            ((CheckBox) view2).setChecked(false);
            ((CheckBox) view3).setChecked(false);
        } else if(viewSeleccionado.getId() == view2.getId()) {
            ((CheckBox) view1).setChecked(false);
            ((CheckBox) view3).setChecked(false);
        } else {
            ((CheckBox) view1).setChecked(false);
            ((CheckBox) view2).setChecked(false);
        }
    }

    /***
     * Metodo para obtener el valor para S y N, en seleccion de chekbox
     *
     * @param viewS, Checkbox para S
     * @param viewN, Check para N
     * @return Character, S = 0, N = 1
     */
    public static Character resultadoGenericoChkbSND(View viewS, View viewN) {
        return (((CheckBox)viewS).isChecked()) ? '0' : '1';
    }

    public static Character resultadoGenericoChk(View view) {
        return (((CheckBox) view).isChecked()) ? '0' : '1';
    }

    /***
     * Metodo para obtener el valor para S, N, D, en seleccion de chekbox
     *
     * @param viewS, Checkbox para S
     * @param viewN, Checkbox para N
     * @param viewD, Checkbox para D
     * @return Character, S = 0, N = 1, D = 2
     */
    public static Character resultadoGenericoChkbSND(View viewS, View viewN, View viewD) {
        return (((CheckBox)viewS).isChecked()) ? '0' : (((CheckBox)viewN).isChecked()) ? '1' : '2';
    }

    public static void establecerCheckboxGuardado(View view1, View view2, char valorGuardado) {

        if(valorGuardado == '0') {
            ((CheckBox) view1).setChecked(true);
        } else if(valorGuardado == '1') {
            ((CheckBox) view2).setChecked(true);
        }
    }

    public static void establecerCheckboxGuardado(View view1, View view2, View view3, char valorGuardado) {
        if(valorGuardado == '0') {
            ((CheckBox) view1).setChecked(true);
        } else if(valorGuardado == '1') {
            ((CheckBox) view2).setChecked(true);
        } else if(valorGuardado == '2') {
            ((CheckBox) view3).setChecked(true);
        }
    }
}
