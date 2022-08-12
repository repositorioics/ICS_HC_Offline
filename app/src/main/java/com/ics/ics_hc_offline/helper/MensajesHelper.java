package com.ics.ics_hc_offline.helper;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.ics.ics_hc_offline.R;


public class MensajesHelper {

	public static final void mostrarMensajeError(Context context, String mensaje, String titulo, OnClickListener onClik){
		CustomBuilder dlgAlert  = new CustomBuilder(context);
		dlgAlert.setMessage(mensaje);
		dlgAlert.setTitle(titulo);
		dlgAlert.setPositiveButton("OK", onClik);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
	
	public static final void mostrarMensajeInfo(Context context, String mensaje, String titulo, OnClickListener onClik){
		CustomBuilder dlgAlert  = new CustomBuilder(context);
		dlgAlert.setMessage(mensaje);
		dlgAlert.setTitle(titulo);
		dlgAlert.setIcon(R.drawable.alert_info);
		dlgAlert.setPositiveButton("OK", onClik);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
	
	public static final void mostrarMensajeYesNo(Context context, String mensaje, String titulo, OnClickListener onClik){
		CustomBuilder dlgAlert  = new CustomBuilder(context);
		dlgAlert.setMessage(mensaje);
		dlgAlert.setTitle(titulo);
		dlgAlert.setIcon(R.drawable.alert_question);
		dlgAlert.setPositiveButton("Si", onClik);
		dlgAlert.setNegativeButton("No", onClik);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
	
	public static final void mostrarMensajeOk(Context context, String mensaje, String titulo, OnClickListener onClik){
		CustomBuilder dlgAlert  = new CustomBuilder(context);
		dlgAlert.setMessage(mensaje);
		dlgAlert.setTitle(titulo);
		dlgAlert.setIcon(R.drawable.alert_ok);
		dlgAlert.setPositiveButton("OK", onClik);
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}
}
