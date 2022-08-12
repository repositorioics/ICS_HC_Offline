package com.ics.ics_hc_offline.consulta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.sintomasactivities.CabezaSintomaActivity;
import com.ics.ics_hc_offline.sintomasactivities.CategoriaSintomaActivity;
import com.ics.ics_hc_offline.sintomasactivities.CutaneoSintomaActivity;
import com.ics.ics_hc_offline.sintomasactivities.DeshidratacionSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.EstadoGeneralSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.EstadoNutriSintomasAcitvity;
import com.ics.ics_hc_offline.sintomasactivities.GargantaSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.GastrointestinalSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.GeneralesSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.OsteomuscularSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.ReferenciaSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.RenalSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.RespiratorioSintomasActivity;
import com.ics.ics_hc_offline.sintomasactivities.VacunasSintomasActivity;
import com.ics.ics_hc_offline.tools.DecimalDigitsInputFilter;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;
import com.ics.ics_hc_offline.utils.UtilHojaConsulta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CSintomasTabFragment extends Fragment  {

    public Integer  SEC_HOJA_CONSULTA ;
    public View mRootView;
    public static Bundle BUNDLE;
    public static LayoutInflater INFLATER;
    private Dialog mDialog;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private String vFueraRango = new String();
    private String MENSAJE_MATRIZ = null;
    public CSintomasTabFragment(Bundle bundle) {
        BUNDLE = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.consulta_sintomas_tab_layout, container, false);

        this.mRootView = rootView;

        INFLATER = inflater;
        inicializarControles(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,  @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(),false,false);
        mDbAdapter.open();
        Integer codExpediente = Integer.valueOf(BUNDLE.getString("codExpediente"));
        String estudios = mDbAdapter.obtenerEstudiosParticipante(codExpediente);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SEC_HOJA_CONSULTA = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);

        ((EditText)mRootView.findViewById(R.id.edtxNombrePaciente)).setText(String.valueOf(BUNDLE.getString("nombrePaciente")));
        ((EditText)mRootView.findViewById(R.id.edtxEstudioParticipante)).setText(estudios);
        ((EditText)mRootView.findViewById(R.id.edtxCodigoSintoma)).setText(String.valueOf(BUNDLE.getString("codExpediente")));
        String fechaConsulta = BUNDLE.getString("fechaConsulta");
        try {
            Date date = df.parse(fechaConsulta);
            df = new SimpleDateFormat("dd/MM/yyyy");
            String result = df.format(date);
            ((EditText)mRootView.findViewById(R.id.edtxtFechaSintoma)).setText(result);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String hora = df.format(calendar.getTime());
            ((EditText)mRootView.findViewById(R.id.edtxtHoraSintoma)).setText(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String fConsulta = df.format(fechaConsulta);
        //((EditText)view.findViewById(R.id.edtxtFechaSintoma)).setText(fConsulta);

        ((EditText)mRootView.findViewById(R.id.edtxtSexoSintoma)).setText(String.valueOf(BUNDLE.getString("sexo")));

        String fechaNac = BUNDLE.getString("fechaNac");
        long milliSeconds= Long.parseLong(fechaNac);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliSeconds);
        String edad = DateUtils.obtenerEdad(calendar);
        ((EditText)mRootView.findViewById(R.id.edtxtEdadSintoma)).setText(edad);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        //Número de expediente fisico con la fecha de nacimiento
        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
        String expedienteFisico = numExpFis.format(cal.getTime());
        ((EditText)mRootView.findViewById(R.id.edtxtExpedienteSintoma)).setText(expedienteFisico);

        ((EditText)mRootView.findViewById(R.id.edtxtPesoKgSintoma)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)mRootView.findViewById(R.id.edtxtTallaCmSintoma)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)mRootView.findViewById(R.id.edtxtTempCSintoma)).setText(String.valueOf(BUNDLE.getString("temperatura")));
        marcarSeccionesCompletadas();
        //init();
    }

    /*public void init () {
        *//*Creando una tarea asincrona*//*
        AsyncTask<Void, Void, Void> sitomasTabFragment = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(mRootView.getContext());
                PD.setTitle(getResources().getString(R.string.title_procesando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(),false,false);
                mDbAdapter.open();
                Integer codExpediente = Integer.valueOf(BUNDLE.getString("codExpediente"));
                String estudios = mDbAdapter.obtenerEstudiosParticipante(codExpediente);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                SEC_HOJA_CONSULTA = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
                HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);

                ((EditText)mRootView.findViewById(R.id.edtxNombrePaciente)).setText(String.valueOf(BUNDLE.getString("nombrePaciente")));
                ((EditText)mRootView.findViewById(R.id.edtxEstudioParticipante)).setText(estudios);
                ((EditText)mRootView.findViewById(R.id.edtxCodigoSintoma)).setText(String.valueOf(BUNDLE.getString("codExpediente")));
                String fechaConsulta = BUNDLE.getString("fechaConsulta");
                try {
                    Date date = df.parse(fechaConsulta);
                    df = new SimpleDateFormat("dd/MM/yyyy");
                    String result = df.format(date);
                    ((EditText)mRootView.findViewById(R.id.edtxtFechaSintoma)).setText(result);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    String hora = df.format(calendar.getTime());
                    ((EditText)mRootView.findViewById(R.id.edtxtHoraSintoma)).setText(hora);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //String fConsulta = df.format(fechaConsulta);
                //((EditText)view.findViewById(R.id.edtxtFechaSintoma)).setText(fConsulta);

                ((EditText)mRootView.findViewById(R.id.edtxtSexoSintoma)).setText(String.valueOf(BUNDLE.getString("sexo")));

                String fechaNac = BUNDLE.getString("fechaNac");
                long milliSeconds= Long.parseLong(fechaNac);
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(milliSeconds);
                String edad = DateUtils.obtenerEdad(calendar);
                ((EditText)mRootView.findViewById(R.id.edtxtEdadSintoma)).setText(edad);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(milliSeconds);
                //Número de expediente fisico con la fecha de nacimiento
                SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
                String expedienteFisico = numExpFis.format(cal.getTime());
                ((EditText)mRootView.findViewById(R.id.edtxtExpedienteSintoma)).setText(expedienteFisico);

                ((EditText)mRootView.findViewById(R.id.edtxtPesoKgSintoma)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
                ((EditText)mRootView.findViewById(R.id.edtxtTallaCmSintoma)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
                ((EditText)mRootView.findViewById(R.id.edtxtTempCSintoma)).setText(String.valueOf(BUNDLE.getString("temperatura")));

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                marcarSeccionesCompletadas();
                PD.dismiss();
            }
        };
        sitomasTabFragment.execute((Void[])null);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(),false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);
        marcarSeccionesCompletadas();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /***
     * Metodo para inicializar los controles UI.
     * @param rootView, Contenedor principal.
     */
    public void inicializarControles(View rootView) {
        try {
            inicializarPrimerSegmento(rootView);

            rootView.findViewById(R.id.ibtEditPreclinicos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarDatosPreclinicos();
                }
            });

            rootView.findViewById(R.id.ibtMensajeAlerta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    obtenerMensajesAlertaCriteriosEti();
                }
            });
        } catch (NullPointerException np) {
            np.printStackTrace();
        }
    }

    /***
     * Metodo para asociar los eventos con los metodos correspondientes, Primera Parte.
     * @param rootView, Contenedor principal.
     */
    private void inicializarPrimerSegmento(View rootView) {
        rootView.findViewById(R.id.btnGeneralesSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GeneralesSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnEstadoGeneralSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EstadoGeneralSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnCabezaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CabezaSintomaActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnGargantaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GargantaSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnRespiratorioSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), RespiratorioSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnGastroInstesSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), GastrointestinalSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnDeshidartacionSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), DeshidratacionSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnRenalSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), RenalSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnReferenciaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ReferenciaSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnOsteomuscularSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), OsteomuscularSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnCutaneoSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CutaneoSintomaActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnEstadoNutriSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EstadoNutriSintomasAcitvity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        intent.putExtra("pesoKg", BUNDLE.getString("pesoKg"));
                        intent.putExtra("tallaCm", BUNDLE.getString("tallaCm"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnVacunasSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), VacunasSintomasActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btnCategoriaSatImcSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CategoriaSintomaActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        intent.putExtra("fechaConsulta", BUNDLE.getString("fechaConsulta"));
                        intent.putExtra("fechaNac", BUNDLE.getString("fechaNac"));
                        startActivity(intent);
                    }
                });
    }

    /***
     * Metodo que controla el evento de edición de datos preclinicos.
     */
    private void editarDatosPreclinicos() {
        //((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).setEnabled(true);
        getActivity().findViewById(R.id.edtxtPesoKgSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        getActivity().findViewById(R.id.edtxtTallaCmSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        getActivity().findViewById(R.id.edtxtTempCSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    vFueraRango = "";
                    DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    if (validarCamposFueraRango()) {
                                        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);
                                        EditText edtxtPeso = (EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma);
                                        EditText edtxtTalla = (EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma);
                                        EditText edtxtTemp = (EditText) getActivity().findViewById(R.id.edtxtTempCSintoma);
                                        HOJACONSULTA.setPesoKg(Double.valueOf(edtxtPeso.getText().toString()));
                                        HOJACONSULTA.setTallaCm(Double.valueOf(edtxtTalla.getText().toString()));
                                        HOJACONSULTA.setTemperaturac(Double.valueOf(edtxtTemp.getText().toString()));
                                        boolean resultado = mDbAdapter.editarHojaConsulta(HOJACONSULTA);
                                        if (resultado) {
                                            BUNDLE.putString("pesoKg", String.valueOf(edtxtPeso.getText()));
                                            BUNDLE.putString("tallaCm", String.valueOf(edtxtTalla.getText()));
                                            BUNDLE.putString("temperatura", String.valueOf(edtxtTemp.getText()));
                                            getActivity().findViewById(R.id.edtxtPesoKgSintoma).setEnabled(false);
                                            getActivity().findViewById(R.id.edtxtTallaCmSintoma).setEnabled(false);
                                            getActivity().findViewById(R.id.edtxtTempCSintoma).setEnabled(false);
                                            Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
                                            //
                                        }
                                    };
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(getActivity(), String.format(
                            getResources().getString(
                                    R.string.msj_aviso_control_cambios), vFueraRango), getResources().getString(
                            R.string.title_estudio_sostenible), preguntaEnviarDialogClickListener);
                    return true;
                }
                return false;
            }
        };

        //((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).setOnEditorActionListener(onEditorActionListener);
    }

    private boolean validarCamposFueraRango() {
        int cont = 0;
        if (!estaEnRango(1, 200, ((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_peso));
            cont++;

        }

        if (!estaEnRango(20, 200, ((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_talla));
            cont++;
        }

        if (!estaEnRango(34, 42, ((EditText)getActivity().findViewById(R.id.edtxtTempCSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp));
            cont++;
        }

        if (cont >0){
            MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                    getResources().getString(
                            R.string.msj_aviso_control_cambios1), vFueraRango), getResources().getString(
                    R.string.title_estudio_sostenible),null);
            return false;
        }

        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    public void obtenerMensajesAlertaCriteriosEti() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);
            if (HOJACONSULTA.getFiebre() != null && HOJACONSULTA.getFiebre() != null) {
                if (HOJACONSULTA.getFif() != null) {
                    Date fConsulta = new SimpleDateFormat("yyyy-MM-dd HH:mm:s").parse(HOJACONSULTA.getFechaConsulta());
                    Calendar fechaConsulta = new GregorianCalendar();
                    fechaConsulta.setTime(fConsulta);
                    Calendar fechaSintoma = new GregorianCalendar();
                    Date fis = new SimpleDateFormat("yyyy-MM-dd HH:mm:s").parse(HOJACONSULTA.getFis());
                    fechaSintoma.setTime(fis);
                    fechaSintoma.add(Calendar.DAY_OF_MONTH, 1);
                    Date fif = new SimpleDateFormat("yyyy-MM-dd HH:mm:s").parse(HOJACONSULTA.getFif());
                    Calendar fechaInicioFiebre = new GregorianCalendar();
                    fechaInicioFiebre.setTime(fif);
                    Calendar hoy = Calendar.getInstance();
                    int dayEti = -1;
                    int dayFiebre = -1;

                    long difSintoma = fechaConsulta.getTimeInMillis() - fechaSintoma.getTimeInMillis();
                    dayEti = (int) (difSintoma / (1000 * 24 * 60 * 60));

                    long difFiebre = hoy.getTimeInMillis() - fechaInicioFiebre.getTimeInMillis();
                    dayFiebre = (int) (difFiebre / (1000 * 24 * 60 * 60));

                    if (dayEti <= 4) {
                        if (UtilHojaConsulta.validarCasoETI(HOJACONSULTA)) {
                            stringBuilder.append("Reune criterios ETI. Recuerde tomar muestras de influenza").append("\n");
                        }
                    }

                    if (dayFiebre <= 4) {
                        if (UtilHojaConsulta.validarCasoIRAG(HOJACONSULTA)) {
                            stringBuilder.append("Reune criterios IRAG. Recuerde tomar muestras de influenza").append("\n");
                        }
                    }

                    if (UtilHojaConsulta.validarCasoNeumonia(HOJACONSULTA)) {
                        stringBuilder.append("Reune criterios de Neumonia. Recuerde tomar muestras respiratoria").append("\n");
                    }

                    if (dayFiebre >= 5 || dayFiebre == -1) {
                        if (UtilHojaConsulta.validarCasoIRAG(HOJACONSULTA)) {
                            stringBuilder.append("Reune criterios IRAG. Recuerde tomar muestras respiratoria").append("\n");
                        }
                    }
                }
            } else {
                if (UtilHojaConsulta.validarCasoNeumonia(HOJACONSULTA)) {
                    stringBuilder.append("Reune criterios de Neumonia. Recuerde tomar muestras respiratoria").append("\n");
                }

                if (UtilHojaConsulta.validarCasoIRAG(HOJACONSULTA)) {
                    stringBuilder.append("Reune criterios IRAG. Recuerde tomar muestras respiratoria").append("\n");
                }
            }

            MENSAJE_MATRIZ = stringBuilder.toString();
            if (!StringUtils.isNullOrEmpty(MENSAJE_MATRIZ)
                    && !MENSAJE_MATRIZ.startsWith("any")) {
                MensajesHelper.mostrarMensajeInfo(getContext(), MENSAJE_MATRIZ,
                        getResources().getString(R.string.title_estudio_sostenible), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Metodo para cambiar el color de fondo de los botones de las secciones.
     */
    public void marcarSeccionesCompletadas() {
        marcarGenerales();
    }

    public void marcarGenerales() {
        if (HOJACONSULTA != null) {
            if (SeccionesSintomas.generalesCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnGeneralesSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnGeneralesSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.estadoGeneralCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnEstadoGeneralSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnEstadoGeneralSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.cabezaSintomaCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnCabezaSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnCabezaSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.gargantaSintomasCompleto(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnGargantaSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnGargantaSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.respiratorioSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnRespiratorioSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnRespiratorioSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.gastrointestinalSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnGastroInstesSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnGastroInstesSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }

            if (SeccionesSintomas.deshidratacionSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnDeshidartacionSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnDeshidartacionSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.renalSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnRenalSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnRenalSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.referenciaSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnReferenciaSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnReferenciaSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesSintomas.osteomuscularSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnOsteomuscularSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnOsteomuscularSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }

            if (SeccionesSintomas.cutaneoSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnCutaneoSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnCutaneoSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }

            if (SeccionesSintomas.estadoNutricionalSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnEstadoNutriSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnEstadoNutriSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }

            if (SeccionesSintomas.vacunasSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnVacunasSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnVacunasSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }

            if (SeccionesSintomas.categoriaSintomasCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btnCategoriaSatImcSintomas);
                Button button = (Button)getActivity().findViewById(R.id.btnCategoriaSatImcSintomas);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
        }
    }

    private void cambiarColorBotonesSecciones(int idView) {
        mRootView.findViewById(idView).setBackgroundColor(getActivity().getResources().
                getColor(R.color.color_bg_button_verde_completado));
    }
}
