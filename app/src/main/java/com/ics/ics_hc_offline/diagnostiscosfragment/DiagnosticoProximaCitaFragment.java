package com.ics.ics_hc_offline.diagnostiscosfragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.common.collect.Collections2;
import com.google.common.base.Predicate;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.tools.DialogBusquedaGenerica;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiagnosticoProximaCitaFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private ProgressDialog progress;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private ArrayAdapter<EscuelaPacienteDTO> adapter;
    private String vFueraRango = new String();
    private ArrayList<EscuelaPacienteDTO> mEscuelasPaciente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        VIEW = inflater.inflate(R.layout.activity_diagnostico_proximacita, container, false);
        CONTEXT = VIEW.getContext();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        inicializarContorles();
        cargarListaEscuela();
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
        ImageButton imgBusquedaColegio = (ImageButton) view.findViewById(R.id.imgBusquedaColegio);
        imgBusquedaColegio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaColegio();
            }
        });

        Button btnProximaCita = (Button) view.findViewById(R.id.btnProximaCita);
        btnProximaCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    guardarDatos();
                } catch (Exception e) {
                    progress.dismiss();
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

    public void inicializarContorles() {
        //Calendar
        VIEW.findViewById(R.id.ibtCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogProximaCita();
            }
        });
        ((EditText) VIEW.findViewById(R.id.dpProximaCita)).setKeyListener(null);

        /*Horario Clases*/
        View.OnClickListener onClickedHorarioClases = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHorario(view);
            }
        };

        VIEW.findViewById(R.id.chkAM)
                .setOnClickListener(onClickedHorarioClases);
        VIEW.findViewById(R.id.chkPm)
                .setOnClickListener(onClickedHorarioClases);
        VIEW.findViewById(R.id.chkNA)
                .setOnClickListener(onClickedHorarioClases);
    }

    public void onChkboxClickedHorario(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkAM:
                if (checked) {
                    ((CheckBox) VIEW.findViewById(R.id.chkPm)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkNA)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkPm:
                if (checked) {
                    ((CheckBox) VIEW.findViewById(R.id.chkAM)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkNA)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkNA:
                if (checked) {
                    ((CheckBox) VIEW.findViewById(R.id.chkAM)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkPm)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

        }
    }

    public void busquedaColegio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
        builder.setTitle("Buscar");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.activity_dialog_buscar_generico, null, false);
        final EditText search = (EditText) mView.findViewById(R.id.etxtBuscar);
        builder.setView(mView)
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        Toast.makeText(CONTEXT, "Buscando el colegio: " + search.getText(), Toast.LENGTH_LONG).show();
                        String busqueda = (search.getText() != null) ? search.getText().toString() : null;
                        filtrarSpinnerEscuelaPaciente(busqueda);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void cargarListaEscuela() {
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        List<EscuelaPacienteDTO> paramLstEscuela = mDbAdapter.getEscuelas(null, null);
        if (paramLstEscuela.size() > 0) {
            EscuelaPacienteDTO escuelaPaciente = new EscuelaPacienteDTO();
            ArrayList<EscuelaPacienteDTO> lstEscuelas= new ArrayList<>();
            escuelaPaciente.setCodEscuela((short) 0);
            escuelaPaciente.setDescripcion("Seleccione Colegio");
            lstEscuelas.add(escuelaPaciente);
            lstEscuelas.addAll(paramLstEscuela);
            Spinner spnEscuelas = (Spinner) VIEW.findViewById(R.id.spnColegio);
            adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  lstEscuelas);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEscuelas.setAdapter(adapter);
            this.mEscuelasPaciente = lstEscuelas;

            cargarDatosProximaCita(lstEscuelas);
        }
    }

    public void showDatePickerDialogProximaCita() {
        obtenerFechaProximaCita();
    }

    public void obtenerFechaProximaCita() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorIgualFechaActual(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()))){
                    ((EditText)getActivity().findViewById(R.id.dpProximaCita)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).setError(null);
                } else {
                    ((EditText)getActivity().findViewById(R.id.dpProximaCita)).setError(getString(R.string.msj_fecha_menor_hoy));
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).setText("");
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).requestFocus();
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void filtrarSpinnerEscuelaPaciente(final String busqueda) {
        ArrayList<EscuelaPacienteDTO> nuevaEscuelasPaciente = this.mEscuelasPaciente;
        if(!StringUtils.isNullOrEmpty(busqueda)) {
            nuevaEscuelasPaciente = new ArrayList<>(Collections2.filter(this.mEscuelasPaciente, new Predicate<EscuelaPacienteDTO>() {
                @Override
                public boolean apply(EscuelaPacienteDTO escuelaPaciente) {
                    return escuelaPaciente.getDescripcion().toLowerCase().contains(busqueda.toLowerCase());
                }
            }));
        }

        adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  nuevaEscuelasPaciente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) VIEW.findViewById(R.id.spnColegio)).setAdapter(adapter);
    }

    public void validarCampos() throws Exception {
        if (((EditText) VIEW.findViewById(R.id.dpProximaCita)).getText().toString().length() == 0) {
            throw new Exception(getString(R.string.msj_debe_ingresar_proxima_cita));
        } else {
            // Aumento en los meses de la proxima cita valorAnterior = 2, valorActual = 3
            Calendar fechaMesesDespues = GregorianCalendar.getInstance();
            fechaMesesDespues.add(Calendar.MONTH, 3);
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            EditText dpProximaCita = (EditText) VIEW.findViewById(R.id.dpProximaCita);
            Date fechaProximaCita = formateador.parse(dpProximaCita.getText().toString());

            if (fechaProximaCita.after(fechaMesesDespues.getTime())) {
                throw new Exception(getString(R.string.msj_la_fecha_menor_dos_meses));
            }
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtTelEme)).getText().toString())) {
            if (!validarTelefRegex(((EditText) VIEW.findViewById(R.id.edtxtTelEme)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_TelefonoEmergencia));
            }
        }

    }

    private boolean validarTelefRegex(String valor) {
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher matcher = pattern.matcher(valor);
        return matcher.matches();
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
        String horario;
        if(((CheckBox) VIEW.findViewById(R.id.chkAM)).isChecked())
            horario="M";
        else if (((CheckBox) VIEW.findViewById(R.id.chkPm)).isChecked())
            horario="V";
        else
            horario="N";

        hojaConsulta.setHorarioClases(horario);
        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtTelEme)).getText().toString())) {
            hojaConsulta.setTelef(Long.parseLong(((EditText) VIEW.findViewById(R.id.edtxtTelEme)).getText().toString()));
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpProximaCita)).getText().toString())){
            String proximaCita = ((EditText) VIEW.findViewById(R.id.dpProximaCita)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(proximaCita);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setProximaCita(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Intente nuevamente", Toast.LENGTH_LONG).show();
            return;
        }
        int codEscuela =  ((EscuelaPacienteDTO) ((Spinner) VIEW.findViewById(R.id.spnColegio)).getSelectedItem()).getCodEscuela();
        if(codEscuela > 0)
            hojaConsulta.setColegio(String.valueOf(codEscuela));

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

    public void cargarDatosProximaCita(ArrayList<EscuelaPacienteDTO> escuelaPaciente) {
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        if (escuelaPaciente.size() > 0) {
            if (hojaConsulta != null) {
                if (!StringUtils.isNullOrEmpty(hojaConsulta.getProximaCita())) {
                    //((EditText) VIEW.findViewById(R.id.dpFif)).setText(String.valueOf(HOJACONSULTA.getFif()));
                    String proximaCita = hojaConsulta.getProximaCita();
                    try {
                        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        Date date = null;
                        date = originalFormat.parse(proximaCita);
                        String formattedDate = targetFormat.format(date);
                        ((EditText) VIEW.findViewById(R.id.dpProximaCita)).setText(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(hojaConsulta.getTelef() != null) {
                    if (hojaConsulta.getTelef() > 0) {
                        ((EditText)VIEW.findViewById(R.id.edtxtTelEme)).setText(String.valueOf(hojaConsulta.getTelef()));
                    } else {
                        ((EditText)VIEW.findViewById(R.id.edtxtTelEme)).setText("");
                    }
                }

                if (hojaConsulta.getHorarioClases() != null
                        && hojaConsulta.getHorarioClases().toString().trim().compareTo("M") == 0) {
                    ((CheckBox) VIEW.findViewById(R.id.chkAM)).setChecked(true);
                    ((CheckBox) VIEW.findViewById(R.id.chkPm)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkNA)).setChecked(false);

                } else if (hojaConsulta.getHorarioClases() != null
                        && hojaConsulta.getHorarioClases().toString().trim().compareTo("V") == 0) {
                    ((CheckBox) VIEW.findViewById(R.id.chkPm)).setChecked(true);
                    ((CheckBox) VIEW.findViewById(R.id.chkAM)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkNA)).setChecked(false);

                } else if(hojaConsulta.getHorarioClases() != null
                        && hojaConsulta.getHorarioClases().toString().trim().compareTo("N") == 0) {
                    ((CheckBox) VIEW.findViewById(R.id.chkNA)).setChecked(true);
                    ((CheckBox) VIEW.findViewById(R.id.chkPm)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkAM)).setChecked(false);
                }

                if(!StringUtils.isNullOrEmpty(hojaConsulta.getColegio())) {
                    EscuelaPacienteDTO colegioEncontrado = new EscuelaPacienteDTO();
                    for(int i = 0; i < adapter.getCount(); i++) {
                        colegioEncontrado = adapter.getItem(i);
                        if(colegioEncontrado.getCodEscuela() ==  Integer.parseInt(hojaConsulta.getColegio().trim())) {
                            break;
                        }
                    }
                    int posicion = adapter.getPosition(colegioEncontrado);
                    ((Spinner) VIEW.findViewById(R.id.spnColegio)).setSelection(posicion);
                }
            }
        }
    }
}
