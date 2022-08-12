package com.ics.ics_hc_offline.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import com.ics.ics_hc_offline.R;

public class DialogBusquedaGenerica extends DialogFragment{
    public interface DialogListener {
        public void onDialogAceptClick(DialogFragment dialog);
    }

    public static DialogBusquedaGenerica newInstance(String titulo) {
        DialogBusquedaGenerica f = new DialogBusquedaGenerica();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        f.setArguments(args);

        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_dialog_buscar_generico, null, false))
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        if (getTargetFragment() != null) {
                            ((DialogListener) getTargetFragment()).onDialogAceptClick(DialogBusquedaGenerica.this);
                        } else {
                            ((DialogListener) getActivity()).onDialogAceptClick(DialogBusquedaGenerica.this);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(getArguments().getString("titulo"));

        return alertDialog;
    }
}
