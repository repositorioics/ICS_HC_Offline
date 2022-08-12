package com.ics.ics_hc_offline.diagnostiscosfragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticoFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private ProgressDialog progress;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;

    ArrayAdapter<DiagnosticoDTO> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        VIEW = inflater.inflate(R.layout.activity_diagnostico, container, false);
        CONTEXT = VIEW.getContext();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        //inicializarContorles();
        cargarDiagnoticos();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        progress = new ProgressDialog(CONTEXT);
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        Button btnDiagnostico = (Button) view.findViewById(R.id.btnDiagnostico);
        btnDiagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    validrDiagnosticosSeleccionados(HOJACONSULTA);
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

    }

    public void regresar() {
        getActivity().finish();
    }

    private void cargarDiagnoticos() {
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        List<DiagnosticoDTO> diagnostico = mDbAdapter.getDiagnosticos(null, null);
        if (diagnostico.size() > 0) {
            DiagnosticoDTO diagTO=new DiagnosticoDTO();
            ArrayList<DiagnosticoDTO> diag1= new  ArrayList<DiagnosticoDTO>();
            diagTO.setSecDiagnostico(0);
            diagTO.setDiagnostico("Seleccione Diagnostico");
            diag1.add(diagTO);
            diag1.addAll(diagnostico);

            Spinner spnDialogo1 = (Spinner) VIEW.findViewById(R.id.spnDialogo1);
            Spinner spnDialogo2 = (Spinner) VIEW.findViewById(R.id.spnDialogo2);
            Spinner spnDialogo3 = (Spinner) VIEW.findViewById(R.id.spnDialogo3);
            Spinner spnDialogo4 = (Spinner) VIEW.findViewById(R.id.spnDialogo4);

            adapter = new ArrayAdapter<DiagnosticoDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  diag1) {
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView,parent);
                    ((TextView) v).setGravity(Gravity.LEFT);
                    return v;
                }
            };

            spnDialogo1.setAdapter(adapter);
            spnDialogo3.setAdapter(adapter);
            spnDialogo2.setAdapter(adapter);
            spnDialogo4.setAdapter(adapter);
            /*Cargar datos si existen el la base de datos*/
            cargarDatos(diag1);
        }
    }

    public void guardarDatos() {
        ProgressDialog PD;
        PD = new ProgressDialog(CONTEXT);
        PD.setTitle(getResources().getString(R.string.title_procesando));
        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        Integer diagnostico1 =  ((DiagnosticoDTO) ((Spinner)VIEW.findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico1( Short.parseShort( diagnostico1.toString()));
        Integer diagnostico2 =  ((DiagnosticoDTO) ((Spinner)VIEW.findViewById(R.id.spnDialogo2)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico2(Short.parseShort(diagnostico2.toString()));
        Integer diagnostico3 =  ((DiagnosticoDTO) ((Spinner)VIEW.findViewById(R.id.spnDialogo3)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico3(Short.parseShort(diagnostico3.toString()));
        Integer diagnostico4 =  ((DiagnosticoDTO) ((Spinner)VIEW.findViewById(R.id.spnDialogo4)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico4(Short.parseShort(diagnostico4.toString()));
        hojaConsulta.setOtroDiagnostico(((EditText) VIEW.findViewById(R.id.edtxtOtrosDiagnostico)).getText().toString());

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
            //regresar();
        } else {
            Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }
    }

    public void cargarDatos(ArrayList<DiagnosticoDTO> diag1) {
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        if (diag1.size() > 0) {
            if (hojaConsulta != null) {
                ((EditText) VIEW.findViewById(R.id.edtxtOtrosDiagnostico)).setText((hojaConsulta.getOtroDiagnostico() == null) ? "" : hojaConsulta.getOtroDiagnostico());
                for (DiagnosticoDTO DiagDto : diag1) {
                    if (DiagDto.getSecDiagnostico() == hojaConsulta.getDiagnostico1()) {
                        ((Spinner) VIEW.findViewById(R.id.spnDialogo1)).setSelection(adapter.getPosition(DiagDto));
                    }
                    if (DiagDto.getSecDiagnostico() == hojaConsulta.getDiagnostico2()) {
                        ((Spinner) VIEW.findViewById(R.id.spnDialogo2)).setSelection(adapter.getPosition(DiagDto));
                    }
                    if (DiagDto.getSecDiagnostico() == hojaConsulta.getDiagnostico3()) {
                        ((Spinner) VIEW.findViewById(R.id.spnDialogo3)).setSelection(adapter.getPosition(DiagDto));
                    }
                    if (DiagDto.getSecDiagnostico() == hojaConsulta.getDiagnostico4()) {
                        ((Spinner) VIEW.findViewById(R.id.spnDialogo4)).setSelection(adapter.getPosition(DiagDto));
                    }
                }
            }
        }
        verificarAlertasDiagnosticos(hojaConsulta);
    }

    public void validarCampos() throws Exception {
        Integer diagnosticoId1 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId2 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo2)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId3 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo3)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId4 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo4)).getSelectedItem()).getSecDiagnostico();
        String otroDiagnostico = ((EditText) VIEW.findViewById(R.id.edtxtOtrosDiagnostico)).getText().toString();

        if(((Spinner) VIEW.findViewById(R.id.spnDialogo1)).getSelectedItem() == null ||
                ((DiagnosticoDTO)((Spinner) VIEW.findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico() == 0) {
            throw new Exception(getString(R.string.msj_completar_informacion) + ", debe seleccionar un valor en el primer Diagnostico.");
        }

        if ((diagnosticoId1 == 50 || diagnosticoId2 == 50 || diagnosticoId3 == 50 || diagnosticoId4 == 50)
                &&(StringUtils.isNullOrEmpty(otroDiagnostico))) {
            throw new Exception(getString(R.string.msj_completar_informacion) + ", debe ingresar el otro Diagnostico.");
        }

    }

    public void validrDiagnosticosSeleccionados(HojaConsultaOffLineDTO hojaConsulta) throws Exception {

        Integer diagnosticoId1 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId2 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo2)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId3 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo3)).getSelectedItem()).getSecDiagnostico();
        Integer diagnosticoId4 =  ((DiagnosticoDTO) ((Spinner) VIEW.findViewById(R.id.spnDialogo4)).getSelectedItem()).getSecDiagnostico();

        if (hojaConsulta.getCategoria() != null) {
            if (hojaConsulta.getCategoria().trim().equals("A")) {
                if (diagnosticoId1 == 57 || diagnosticoId2 == 57 || diagnosticoId3 == 57 || diagnosticoId4 == 57) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: SINDROME FEBRIL AGUDO SIN FOCO APARENTE");
                }
            }
            if (hojaConsulta.getCategoria().trim().equals("B") || hojaConsulta.getCategoria().trim().equals("C")) {
                if (diagnosticoId1 == 94 || diagnosticoId2 == 94 || diagnosticoId3 == 94 || diagnosticoId4 == 94) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: DENGUE SIN SIGNOS DE ALARMA");
                }
                if (diagnosticoId1 == 95 || diagnosticoId2 == 95 || diagnosticoId3 == 95 || diagnosticoId4 == 95) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: DENGUE CON SIGNOS DE ALARMA");
                }
                if (diagnosticoId1 == 96 || diagnosticoId2 == 96 || diagnosticoId3 == 96 || diagnosticoId4 == 96) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: DENGUE GRAVE");
                }
                if (diagnosticoId1 == 97 || diagnosticoId2 == 97 || diagnosticoId3 == 97 || diagnosticoId4 == 97) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: CHOQUE INICIAL");
                }
                if (diagnosticoId1 == 98 || diagnosticoId2 == 98 || diagnosticoId3 == 98 || diagnosticoId4 == 98) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: CHOQUE HIPOTENSIVO");
                }
                if (diagnosticoId1 == 99 || diagnosticoId2 == 99 || diagnosticoId3 == 99 || diagnosticoId4 == 99) {
                    throw new Exception("No coincide categoria con diagnostico, Categoria: " + hojaConsulta.getCategoria().trim() +
                            ", Diagnostico: DENGUE CONFIRMADO POR PCR");
                }
            }
        }
    }

    public  void verificarAlertasDiagnosticos(HojaConsultaOffLineDTO hojaConsulta) {
        if (hojaConsulta !=  null) {
            String categoria = hojaConsulta.getCategoria();
            String serologiaDengue = hojaConsulta.getSerologiaDengue();
            String eti = hojaConsulta.getEti();
            /*Obteniendo los valores que tiene los text view que muestran los diagnosticos*/
            String valueDiag1 = "";
            valueDiag1 = ((TextView) VIEW.findViewById(R.id.txvDiag1)).getText().toString();
            TextView txtvDiag1 = ((TextView) VIEW.findViewById(R.id.txvDiag1));
            TextView txtvDiag2 = ((TextView) VIEW.findViewById(R.id.txvDiag2));
            txtvDiag1.setText("");
            txtvDiag2.setText("");
            if (categoria != null && serologiaDengue != null) {
                if (categoria.trim().equals("C") && serologiaDengue.trim().equals("0")) {
                    //Seleccionar diagnostico de búsqueda activa.
                    if (StringUtils.isNullOrEmpty(valueDiag1)) {
                        txtvDiag1.setText("Marco categoria " + categoria + " + " + "Serologia Dengue" + " --> " + "Seleccionar diagnóstico búsqueda activa");
                    }
                }
            }
            if (eti != null) {
                valueDiag1 = ((TextView) VIEW.findViewById(R.id.txvDiag1)).getText().toString();
                if (eti.trim().equals("0")) {
                    if (StringUtils.isNullOrEmpty(valueDiag1)) {
                        txtvDiag1.setText("Marco Eti " + "--> " + "Seleccionar diagnóstico Eti");
                    } else {
                        txtvDiag2.setText("Marco Eti " + "--> " + "Seleccionar diagnóstico Eti");
                    }
                }
            }
        }
    }
}
