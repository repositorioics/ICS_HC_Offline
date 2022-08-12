package com.ics.ics_hc_offline.consulta;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.diagnostiscosactivities.DiagnosticoActivity;
import com.ics.ics_hc_offline.diagnostiscosactivities.DiagnosticoProximaCitaActivity;
import com.ics.ics_hc_offline.diagnostiscosactivities.ExamenHistoriaActivity;
import com.ics.ics_hc_offline.diagnostiscosactivities.TratamientoActivity;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.TemplatePDF;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CDiagnosticoTabFragment extends Fragment {
    public Integer  SEC_HOJA_CONSULTA ;
    public View mRootView;
    public static Bundle BUNDLE;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private HojaConsultaDBAdapter mDbAdapter;
    TemplatePDF templatePDF;

    public CDiagnosticoTabFragment(Bundle bundle) {
        BUNDLE = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.consulta_diagnostico_tab_layout, container, false);

        this.mRootView = rootView;
        inicializarControles(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbAdapter = new HojaConsultaDBAdapter(view.getContext(),false,false);
        mDbAdapter.open();
        Integer codExpediente = Integer.valueOf(BUNDLE.getString("codExpediente"));
        String estudios = mDbAdapter.obtenerEstudiosParticipante(codExpediente);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SEC_HOJA_CONSULTA = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);


        ImageButton ibtVisorDiagnostico = (ImageButton) view.findViewById(R.id.ibtVisorDiagnostico);
        ibtVisorDiagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarRegistro(SEC_HOJA_CONSULTA);
            }
        });

        ((EditText)view.findViewById(R.id.edtxNombrePaciente)).setText(String.valueOf(BUNDLE.getString("nombrePaciente")));
        ((EditText)view.findViewById(R.id.edtxEstudioParticipanteD)).setText(estudios);
        ((EditText)view.findViewById(R.id.edtxCodigoSintomaD)).setText(String.valueOf(BUNDLE.getString("codExpediente")));
        String fechaConsulta = BUNDLE.getString("fechaConsulta");
        try {
            Date date = df.parse(fechaConsulta);
            df = new SimpleDateFormat("dd/MM/yyyy");
            String result = df.format(date);
            ((EditText)view.findViewById(R.id.edtxtFechaSintomaD)).setText(result);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String hora = df.format(calendar.getTime());
            ((EditText)view.findViewById(R.id.edtxtHoraSintomaD)).setText(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String fConsulta = df.format(fechaConsulta);
        //((EditText)view.findViewById(R.id.edtxtFechaSintoma)).setText(fConsulta);

        ((EditText)view.findViewById(R.id.edtxtSexoSintomaD)).setText(String.valueOf(BUNDLE.getString("sexo")));

        String fechaNac = BUNDLE.getString("fechaNac");
        long milliSeconds= Long.parseLong(fechaNac);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliSeconds);
        String edad = DateUtils.obtenerEdad(calendar);
        ((EditText)view.findViewById(R.id.edtxtEdadSintomaD)).setText(edad);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        //Número de expediente fisico con la fecha de nacimiento
        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
        String expedienteFisico = numExpFis.format(cal.getTime());
        ((EditText)view.findViewById(R.id.edtxtExpedienteSintomaD)).setText(expedienteFisico);

        /*((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintomaD)).setText(String.valueOf(HOJACONSULTA.getPesoKg()));
        ((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintomaD)).setText(String.valueOf(HOJACONSULTA.getTallaCm()));
        ((EditText)getActivity().findViewById(R.id.edtxtTempCSintomaD)).setText(String.valueOf(HOJACONSULTA.getTemperaturac()));*/

        ((EditText)view.findViewById(R.id.edtxtPesoKgSintomaD)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)view.findViewById(R.id.edtxtTallaCmSintomaD)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)view.findViewById(R.id.edtxtTempCSintomaD)).setText(String.valueOf(BUNDLE.getString("temperatura")));
        marcarSeccionesCompletadas();
        /*if (HOJACONSULTA != null) {
            activarDiagnostico(HOJACONSULTA);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);
        ((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintomaD)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintomaD)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)getActivity().findViewById(R.id.edtxtTempCSintomaD)).setText(String.valueOf(BUNDLE.getString("temperatura")));
        marcarSeccionesCompletadas();
        //activarDiagnostico(HOJACONSULTA);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void buscarRegistro(int secHojaConsulta) {
        mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(), false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        templatePDF = new TemplatePDF(mRootView.getContext().getApplicationContext());
        templatePDF.openDocument();
        templatePDF.generateHojaConsultaPdf(HOJACONSULTA);
        templatePDF.closeDocument();
        printPdf();
    }

    public void printPdf() {
        try {
            File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
            if (arch.exists()) {
                Uri uri = FileProvider.getUriForFile(mRootView.getContext(), mRootView.getContext().getApplicationContext().getPackageName() + ".provider", arch);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                //intent.setAction(StorageManager.ACTION_CLEAR_APP_CACHE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //error si se quita esto
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mRootView.getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ActivityNotFoundException e) {
            Log.e("appViewPdf", e.toString());
        }
    }

    public void inicializarControles(View rootView) {
        rootView.findViewById(R.id.btndianosticoHistExamenFisico)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ExamenHistoriaActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btndianosticotratamiento)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TratamientoActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btndianosticodiag)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        startActivity(intent);
                    }
                });

        rootView.findViewById(R.id.btndianosticoproxcita)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoProximaCitaActivity.class);
                        intent.putExtra("secHojaConsulta", BUNDLE.getString("secHojaConsulta"));
                        startActivity(intent);
                    }
                });
    }

    /*
    * Metodo para activar el boton de Diagnostico
    * */
    /*public void activarDiagnostico(HojaConsultaOffLineDTO hojaConsulta) {
        if (hojaConsulta != null) {
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
                    && SeccionesDiagnosticos.tratamientoPlanesCompletado(hojaConsulta)) {
                getActivity().findViewById(R.id.btndianosticodiag).setEnabled(true);
            }
        }
    }*/

    /***
     * Metodo para cambiar el color de fondo de los botones de las secciones.
     */
    public void marcarSeccionesCompletadas() {
        validarSecciones();
    }

    public void validarSecciones() {
        if (HOJACONSULTA != null) {
            if (SeccionesDiagnosticos.historiaExamenCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btndianosticoHistExamenFisico);
                Button button = (Button)getActivity().findViewById(R.id.btndianosticoHistExamenFisico);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesDiagnosticos.tratamientoPlanesCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btndianosticotratamiento);
                Button button = (Button)getActivity().findViewById(R.id.btndianosticotratamiento);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesDiagnosticos.diagnosticosCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btndianosticodiag);
                Button button = (Button)getActivity().findViewById(R.id.btndianosticodiag);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
            if (SeccionesDiagnosticos.diagnosticoProximaCitaCompletado(HOJACONSULTA)) {
                cambiarColorBotonesSecciones(R.id.btndianosticoproxcita);
                Button button = (Button)getActivity().findViewById(R.id.btndianosticoproxcita);
                button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
            }
        }
    }

    private void cambiarColorBotonesSecciones(int idView) {
        mRootView.findViewById(idView).setBackgroundColor(getActivity().getResources().
                getColor(R.color.color_bg_button_verde_completado));
    }
}
