package com.ics.ics_hc_offline.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ics.ics_hc_offline.R;

public class ImpresionPdfDialog extends DialogFragment {
    public interface DialogImpresionPdfListener {
        public void onClickImpresion();
        public void onClickPDF();
    }

    DialogImpresionPdfListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View myView = inflater.inflate(R.layout.activity_dialog_impresion_pdf, null);

        myView.findViewById(R.id.imgBtnImprimir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getActivity()).onClickImpresion();
                getDialog().dismiss();
            }
        });

        myView.findViewById(R.id.imgBtnPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DialogImpresionPdfListener)getActivity()).onClickPDF();
                getDialog().dismiss();
            }
        });

        builder.setView(myView)
                .setNegativeButton(R.string.boton_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Seleccione una opción");

        return alertDialog;
    }
}
