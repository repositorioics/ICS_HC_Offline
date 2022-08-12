package com.ics.ics_hc_offline.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.dto.ExpedienteDTO;

import java.util.ArrayList;

public class LstViewGenericaExp extends ArrayAdapter<ExpedienteDTO> {
    private Context CONTEXT;
    private Object ACTIVITY_INST;
    private ArrayList<ExpedienteDTO> VALUES;
    private Resources RES;
    private int CURRENT_FIRST_VISIBLE_ITEM;
    private int CURRENT_VISIBLE_ITEM_COUNT;
    private int CURRENT_SCROLLSTATE;

    public LstViewGenericaExp(Context context, String activityInst, ArrayList<ExpedienteDTO> values, Resources res) {
        super(context, R.layout.lista_generica_exp_layout, values);

        /********** Take passed values **********/
        this.CONTEXT = context;
        this.ACTIVITY_INST = activityInst;
        this.VALUES = values;
        this.RES = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ListaExpedienteHolder holder = null;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) ((Activity) CONTEXT)
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lista_generica_exp_layout,
                    parent, false);

            /************ Set Model values in Holder elements ***********/

            holder = new ListaExpedienteHolder();

            holder.txtvNumHojaConsulta = (TextView) rowView
                    .findViewById(R.id.txtvNumHojaConsulta);
            holder.txtvFecha = (TextView) rowView
                    .findViewById(R.id.txtvFecha);
            holder.txtvHora = (TextView) rowView
                    .findViewById(R.id.txtvHora);
            holder.txtvNomMedico = (TextView) rowView
                    .findViewById(R.id.txtvNomMedico);
            holder.txtvEstado = (TextView) rowView
                    .findViewById(R.id.txtvEstado);
            rowView.setTag(holder);
        } else {
            // Get holder
            holder = (ListaExpedienteHolder) rowView.getTag();
        }

        holder.txtvNumHojaConsulta.setText("" + ((ExpedienteDTO) this.VALUES.get(position)).getNumHojaConsulta());
        holder.txtvFecha.setText(((ExpedienteDTO) this.VALUES.get(position)).getFechaCierre());
        holder.txtvHora.setText(((ExpedienteDTO) this.VALUES.get(position)).getHoraCierre());
        holder.txtvNomMedico.setText(((ExpedienteDTO) this.VALUES.get(position)).getNombre());
        holder.txtvEstado.setText(((ExpedienteDTO) this.VALUES.get(position)).getDescripcion());

        if (position % 2 == 1) {
            rowView.setBackgroundColor(android.graphics.Color.rgb(222,231,209));
        } else {
            rowView.setBackgroundColor(android.graphics.Color.rgb(239,243,234));
        }

        return rowView;
    }

    static class ListaExpedienteHolder
    {
        TextView txtvNumHojaConsulta;
        TextView txtvFecha;
        TextView txtvHora;
        TextView txtvNomMedico;
        TextView txtvEstado;
    }
}
