package com.ics.ics_hc_offline.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.dto.InicioDTO;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.util.ArrayList;

public class LstViewGenericaInicio extends ArrayAdapter<InicioDTO> {
    private Context CONTEXT;
    private String ACTIVITY_INST;
    private ArrayList<InicioDTO> VALUES;
    private Resources RES;
    private int CURRENT_FIRST_VISIBLE_ITEM;
    private int CURRENT_VISIBLE_ITEM_COUNT;
    private int CURRENT_SCROLLSTATE;

    /*************  CustomAdapter Constructor *****************/
    public LstViewGenericaInicio(Context context, String activityInst, ArrayList<InicioDTO> values, Resources res) {

        super(context, R.layout.lista_generica_incio_layout, values);

        /********** Take passed values **********/
        this.CONTEXT = context;
        this.ACTIVITY_INST = activityInst;
        this.VALUES = values;
        this.RES = res;

        /*********** Layout inflator to call external xml layout () ***********/
        //inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    /****** Depends upon data size called for each row , Create each ListView row *****/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ListaInicioHolder holder = null;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) ((Activity) CONTEXT)
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lista_generica_incio_layout,
                    parent, false);

            /************ Set Model values in Holder elements ***********/

            holder = new ListaInicioHolder();

            holder.txtvCodigoExpediente = (TextView) rowView
                    .findViewById(R.id.txtvCodigoExpediente);
            holder.txtvNomPaciente = (TextView) rowView
                    .findViewById(R.id.txtvNomPaciente);
            holder.txtvNomMedico = (TextView) rowView
                    .findViewById(R.id.txtvNomMedico);
            holder.txtvEstado = (TextView) rowView
                    .findViewById(R.id.txtvEstado);
            rowView.setTag(holder);
        } else {
            // Get holder
            holder = (ListaInicioHolder) rowView.getTag();
        }

        holder.txtvCodigoExpediente.setText("" + ((InicioDTO) this.VALUES.get(position)).getCodExpediente());
        holder.txtvNomPaciente.setText(((InicioDTO) this.VALUES.get(position)).getNomPaciente());
        holder.txtvEstado.setText(((InicioDTO) this.VALUES.get(position)).getDescripcion());
        if(!StringUtils.isNullOrEmpty(((InicioDTO) this.VALUES.get(position)).getNombreMedico())) {
            holder.txtvNomMedico.setText(new StringBuffer().append(
                    ((InicioDTO) this.VALUES.get(position)).getNombreMedico()).toString());
            /*holder.txtvNomMedico.setText(new StringBuffer().append("Médico: ").append(
                    ((InicioDTO) this.VALUES.get(position)).getNombreMedico()).toString());*/
        } else {
            holder.txtvNomMedico.setText("");
        }

        return rowView;
    }

    static class ListaInicioHolder
    {
        TextView txtvCodigoExpediente;
        TextView txtvNomPaciente;
        TextView txtvEstado;
        TextView txtvNomMedico;
    }
}