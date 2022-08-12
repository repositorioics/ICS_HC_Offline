package com.ics.ics_hc_offline.sintomasfragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.CatalogoDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.tools.DecimalDigitsInputFilter;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class EstadoNutriSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private double pesoKg;
    private double tallaCmPaciente;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    ArrayAdapter<CatalogoDTO> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_estado_nutri_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));

        pesoKg = Double.parseDouble(bundle.getString("pesoKg"));
        tallaCmPaciente = Double.parseDouble(bundle.getString("tallaCm"));

        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        Button btnEstadoNutriGSintomas = (Button) view.findViewById(R.id.btnEstadoNutriGSintomas);
        btnEstadoNutriGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    guardarDatos();
                } catch (Exception e) {
                    if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
                    } else {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), getString(R.string.msj_error_no_controlado), getString(R.string.title_estudio_sostenible), null);
                    }
                    e.printStackTrace();
                }
            }
        });
        cargarDatos();
    }

    @Override
    public void onResume() {
        super.onResume();
        double imc = (pesoKg > 0 && tallaCmPaciente > 0) ? pesoKg / Math.pow((tallaCmPaciente / 100.00), 2) : 00.00;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00", otherSymbols);

        String result = (imc > 0) ? df.format(imc) : "0.00";

        ((EditText) VIEW.findViewById(R.id.edtxtimc)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)});
        EditText text = (EditText) VIEW.findViewById(R.id.edtxtimc);
        text.setText(result);
    }

    public void regresar() {
        getActivity().finish();
    }

    public void inicializarContorles() {};

    private void llenarSpinnerEstdoNutricional() {
        ArrayList<CatalogoDTO> estadosNutriconales = new ArrayList<CatalogoDTO>();
        estadosNutriconales.add(new CatalogoDTO(0, "Seleccione"));
        estadosNutriconales.add(new CatalogoDTO(1, "Obeso"));
        estadosNutriconales.add(new CatalogoDTO(2, "Sobrepeso"));
        estadosNutriconales.add(new CatalogoDTO(3, "Sospecha de Problema"));
        estadosNutriconales.add(new CatalogoDTO(4, "Normal"));
        estadosNutriconales.add(new CatalogoDTO(5, "Bajo Peso"));
        estadosNutriconales.add(new CatalogoDTO(6, "Bajo Peso Severo"));
        estadosNutriconales.add(new CatalogoDTO(7, "Desconocido"));
        Spinner spnEstadoNutri = (Spinner) VIEW.findViewById(R.id.spnEstadoNutri);
        adapter = new ArrayAdapter<CatalogoDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  estadosNutriconales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEstadoNutri.setAdapter(adapter);
    }

    public void validarCampos() throws Exception {
        if(((Spinner) VIEW.findViewById(R.id.spnEstadoNutri)).getSelectedItem() == null ||
                ((CatalogoDTO)((Spinner) VIEW.findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() == 0) {
            throw new Exception(getString(R.string.msj_completar_informacion) + ", debe seleccionar un estado nutricional.");
        }

        // Aumento en el rango imc valorAnterior = 50, valorActual = 70
        if (!estaEnRango(5, 70, ((EditText) VIEW.findViewById(R.id.edtxtimc)).getText().toString())) {
            throw new Exception(getString(R.string.msj_aviso_control_cambios2, getString(R.string.label_imc)));
        }
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    public void guardarDatos() {
        ProgressDialog PD;
        PD = new ProgressDialog(CONTEXT);
        PD.setTitle(getResources().getString(R.string.title_procesando));
        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        hojaConsulta.setImc(Double.parseDouble(((EditText)VIEW.findViewById(R.id.edtxtimc)).getText().toString()));

        hojaConsulta.setObeso(String.valueOf(obtenerValor(1)));

        hojaConsulta.setSobrepeso(String.valueOf(obtenerValor(2)));

        hojaConsulta.setSospechaProblema(String.valueOf(obtenerValor(3)));

        hojaConsulta.setNormal(String.valueOf(obtenerValor(4)));

        hojaConsulta.setBajoPeso(String.valueOf(obtenerValor(5)));

        hojaConsulta.setBajoPesoSevero(String.valueOf(obtenerValor(6)));

        boolean resultado = mDbAdapter.editarHojaConsulta(hojaConsulta);
        if (resultado) {
            Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_SHORT).show();
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    PD.dismiss();
                    regresar(); // finish Activity
                }
            }.start();
        } else {
            Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }
    }

    private char obtenerValor(int id) {
        return  (((CatalogoDTO)((Spinner) VIEW.findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() == id) ? '0' :
                (((CatalogoDTO)((Spinner) VIEW.findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() != 7) ? '1' :'2';
    }

    public void cargarDatos() {
        llenarSpinnerEstdoNutricional();

        if(HOJACONSULTA.getImc() != null) {
            ((EditText) VIEW.findViewById(R.id.edtxtimc)).setText(HOJACONSULTA.getImc().toString());
        }

        if(HOJACONSULTA.getObeso() != null &&
                HOJACONSULTA.getObeso().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(1)));
        } else if(HOJACONSULTA.getSobrepeso() != null &&
                HOJACONSULTA.getSobrepeso().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(2)));
        } else if(HOJACONSULTA.getSospechaProblema() != null &&
                HOJACONSULTA.getSospechaProblema().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(3)));
        } else if(HOJACONSULTA.getNormal() != null &&
                HOJACONSULTA.getNormal().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(4)));
        } else if(HOJACONSULTA.getBajoPeso() != null &&
                HOJACONSULTA.getBajoPeso().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(5)));
        } else if(HOJACONSULTA.getBajoPesoSevero() != null &&
                HOJACONSULTA.getBajoPesoSevero().compareTo("0") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(6)));
        } else if(HOJACONSULTA.getObeso() != null &&
                HOJACONSULTA.getObeso().compareTo("2") == 0) {
            ((Spinner)VIEW.findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(7)));
        }
    }
}
