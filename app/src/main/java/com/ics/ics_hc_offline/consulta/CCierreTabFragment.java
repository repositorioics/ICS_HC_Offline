package com.ics.ics_hc_offline.consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ics.ics_hc_offline.MainActivity;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.diagnostiscosfragment.DiagnosticoFragment;
import com.ics.ics_hc_offline.dto.GrillaCierreDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.enfermeria.ListaEnfermeria;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.sintomasfragment.CabezaSintomaFragment;
import com.ics.ics_hc_offline.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.zip.Inflater;

public class CCierreTabFragment extends Fragment {
    public Integer secHojaConsulta ;
    public View mRootView;
    public static Bundle BUNDLE;
    private Context CONTEXT;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private HojaConsultaDBAdapter mDbAdapter;
    private ProgressDialog progress;
    public CCierreTabFragment(Bundle bundle) {
        BUNDLE = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.consulta_cierre_tab_layout, container, false);
        CONTEXT = rootView.getContext();
        this.mRootView = rootView;
        progress = new ProgressDialog(CONTEXT);
        secHojaConsulta = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
        inicializarControles(rootView);
        cargarGrilla(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbAdapter = new HojaConsultaDBAdapter(view.getContext(),false,false);
        mDbAdapter.open();

        Integer codExpediente = Integer.valueOf(BUNDLE.getString("codExpediente"));
        String estudios = mDbAdapter.obtenerEstudiosParticipante(codExpediente);
        progress = new ProgressDialog(CONTEXT);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        secHojaConsulta = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        ((EditText)view.findViewById(R.id.edtxNombrePaciente)).setText(String.valueOf(BUNDLE.getString("nombrePaciente")));
        ((EditText)view.findViewById(R.id.edtxEstudioParticipanteC)).setText(estudios);
        ((EditText)view.findViewById(R.id.edtxCodigoSintomaC)).setText(String.valueOf(BUNDLE.getString("codExpediente")));
        String fechaConsulta = BUNDLE.getString("fechaConsulta");
        try {
            Date date = df.parse(fechaConsulta);
            df = new SimpleDateFormat("dd/MM/yyyy");
            String result = df.format(date);
            ((EditText)view.findViewById(R.id.edtxtFechaSintomaC)).setText(result);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String hora = df.format(calendar.getTime());
            ((EditText)view.findViewById(R.id.edtxtHoraSintomaC)).setText(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((EditText)view.findViewById(R.id.edtxtSexoSintomaC)).setText(String.valueOf(BUNDLE.getString("sexo")));

        String fechaNac = BUNDLE.getString("fechaNac");
        long milliSeconds= Long.parseLong(fechaNac);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliSeconds);
        String edad = DateUtils.obtenerEdad(calendar);
        ((EditText)view.findViewById(R.id.edtxtEdadSintomaC)).setText(edad);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        //Número de expediente fisico con la fecha de nacimiento
        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
        String expedienteFisico = numExpFis.format(cal.getTime());
        ((EditText)view.findViewById(R.id.edtxtExpedienteSintomaC)).setText(expedienteFisico);

        ((EditText)view.findViewById(R.id.edtxtPesoKgSintomaC)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)view.findViewById(R.id.edtxtTallaCmSintomaC)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)view.findViewById(R.id.edtxtTempCSintomaC)).setText(String.valueOf(BUNDLE.getString("temperatura")));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintomaC)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintomaC)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)getActivity().findViewById(R.id.edtxtTempCSintomaC)).setText(String.valueOf(BUNDLE.getString("temperatura")));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void inicializarControles(final View rootView) {
        rootView.findViewById(R.id.imgBtnCerrarHoja).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                //rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);
                                //rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                                //rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                                //boolean cievalidarCierre();
                                try {
                                    if (!validarCierre()) {
                                        Toast.makeText(getActivity(), "Existen Casillas sin marcar, Favor Verificar", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    llamarProcesoCierre(rootView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_cerrar_hoja), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaDialogClickListener);
            }
        });

        rootView.findViewById(R.id.btnSalirCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarSalirHojaServicio();
            }
        });

        /*rootView.findViewById(R.id.imgBtnCambioTurno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                //Desbilitar el boton para cerrar la hoja de consulta cuando se dio click en cambio turno

                //rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
            }
        });*/

        /*rootView.findViewById(R.id.imgBtnAgregarHoja).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //llamarProcesoAgregarHoja();
                                *//*rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);*//*
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_agregar_hoja), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaDialogClickListener);
            }
        });*/

        /*rootView.findViewById(R.id.btnCancelarCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaCancelarDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                *//*FragmentManager fm = getActivity().getSupportFragmentManager();
                                CancelacionDialog dlogCancelacion = new CancelacionDialog();
                                dlogCancelacion.setTargetFragment(mCorrienteFragmento, 0);
                                dlogCancelacion.show(fm, "Cancelación");*//*
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_de_cancelar), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaCancelarDialogClickListener);
            }
        });*/

        /*rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //llamarNoAtiendeLlamado();
                *//*rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);*//*
            }
        });*/

        /*if(HOJACONSULTA.getEstado().equals("7")) {
            rootView.findViewById(R.id.imgBtnAgregarHoja).setEnabled(false);
            rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
            rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);
            rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
            rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
        }*/
    }

    public void validarSalirHojaServicio() {
        Fragment listaConsulta = new ListaConsulta();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, listaConsulta);
        transaction.commit();
    }

    public void llamarProcesoCierre(final View rootView) {
        ProgressDialog PD;
        PD = new ProgressDialog(CONTEXT);
        PD.setTitle(getResources().getString(R.string.title_procesando));
        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
        Calendar calendar = Calendar.getInstance();

        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        hojaConsulta.setEstado("7");
        hojaConsulta.setFechaCierre(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        boolean resultado = mDbAdapter.editarHojaConsulta(hojaConsulta);
        if (resultado) {
            Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_SHORT).show();
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    PD.dismiss();
                }
            }.start();
        } else {
            Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }
    }

    public boolean validarCierre() {
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        if (SeccionesSintomas.generalesCompletado(hojaConsulta)
                && SeccionesSintomas.estadoGeneralCompletado(hojaConsulta)
                && SeccionesSintomas.cabezaSintomaCompletado(hojaConsulta)
                && SeccionesSintomas.gargantaSintomasCompleto(hojaConsulta)
                && SeccionesSintomas.respiratorioSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.gastrointestinalSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.deshidratacionSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.renalSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.referenciaSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.osteomuscularSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.cutaneoSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.estadoNutricionalSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.vacunasSintomasCompletado(hojaConsulta)
                && SeccionesSintomas.categoriaSintomasCompletado(hojaConsulta)
                && SeccionesDiagnosticos.historiaExamenCompletado(hojaConsulta)
                && SeccionesDiagnosticos.tratamientoPlanesCompletado(hojaConsulta)
                && SeccionesDiagnosticos.diagnosticosCompletado(hojaConsulta)
                && SeccionesDiagnosticos.diagnosticoProximaCitaCompletado(hojaConsulta)
                && SeccionExamenes.examenesMarcados(hojaConsulta)) {
            return true;
        }
        return false;
    }

    public void cargarGrilla(final View rootView) {
        mDbAdapter = new HojaConsultaDBAdapter(rootView.getContext(),false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        /*Obteniendo el medico*/
        GrillaCierreDTO usuarioMedico = mDbAdapter.getUsuarioGrillaCierre(String.valueOf(HOJACONSULTA.getUsuarioMedico()), "\"%Médico\"");

        /*Obteniendo al enfermer@*/
        GrillaCierreDTO usuarioEnfermeria = mDbAdapter.getUsuarioGrillaCierre(String.valueOf(HOJACONSULTA.getUsuarioEnfermeria()), "\"%Enfermeria\"");

        if (usuarioMedico != null) {
            Calendar hoy = Calendar.getInstance();
            ((TextView) rootView.findViewById(R.id.txtvCargoMedLog)).setText(usuarioMedico.getCargo());
            ((TextView) rootView.findViewById(R.id.txtvNumeroMedLog)).setText(usuarioMedico.getCodigoPersonal());
            ((TextView) rootView.findViewById(R.id.txtvNombreMedLog)).setText(usuarioMedico.getNombre());
            ((TextView) rootView.findViewById(R.id.txtvFechaMedLog)).setText(new SimpleDateFormat("dd/MM/yyyy").format(hoy.getTime()));
            ((TextView) rootView.findViewById(R.id.txtvHoraMedLog)).setText(new SimpleDateFormat("KK:mm a").format(hoy.getTime()));
        }

        if (usuarioEnfermeria != null) {
            ((TextView) rootView.findViewById(R.id.txtvCargoEnferm)).setText(usuarioEnfermeria.getCargo());
            ((TextView) rootView.findViewById(R.id.txtvNumeroEnferm)).setText(usuarioEnfermeria.getCodigoPersonal());
            ((TextView) rootView.findViewById(R.id.txtvNombreEnferm)).setText(usuarioEnfermeria.getNombre());
        }
    }
}
