package com.ics.ics_hc_offline.sintomasfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.CssfvApp;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.tools.DecimalDigitsInputFilter;
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GeneralesSintomasFragment extends Fragment {

    private Context CONTEXT;
    private View VIEW;
    private String vFueraRango = "";
    private int secHojaConsulta = 0;
    private Boolean mAmPm = null;
    private String fechaConsulta;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private static HojaConsultaOffLineDTO HC = null;

    public GeneralesSintomasFragment() {
        // Required empty public constructor
    }

    public static GeneralesSintomasFragment newInstance() {
        return new GeneralesSintomasFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_generales_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        fechaConsulta = bundle.getString("fechaConsulta");
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        Button btnGeneralesGSintomas = (Button) view.findViewById(R.id.btnGeneralesGSintomas);
        btnGeneralesGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFueraRango = "";
                try {
                    validarCampoRequerido();
                    if (diffDayFConsultaAndFif() && verificarFisFifConsultaAnterior()) {
                        alertDialogConsutaAnteriorYUltDiaFiebre();
                    } else if (diffDayFConsultaAndFif()) {
                        alertDialogUltDiaFiebre();
                    } else if (verificarFisFifConsultaAnterior()) {
                        alertDialogConsutaAnterior();
                    } else {
                        guardarDatos();
                    }
                     /*if (diffDayFConsultaAndFif()) {
                        alertDialogUltDiaFiebre();
                    }  else {
                        guardarDatos();
                    }*/
                    /*if (diffDayFConsultaAndFif()) {
                        alertDialogUltDiaFiebre();
                    } else {

                    }*/
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

    public void regresar() {
        getActivity().finish();
    }

    /***
     * Metodo para inicializar todos los controles de interfaz de la pantalla.
     */
    public void inicializarContorles() {
        ((EditText) VIEW.findViewById(R.id.edtxtTempMedGeneralesSint)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});

        VIEW.findViewById(R.id.dpFis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFis(v);
            }
        });

        ((EditText)VIEW.findViewById(R.id.dpFis)).setKeyListener(null);

        VIEW.findViewById(R.id.dpFif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFif(v);
            }
        });

        ((EditText)VIEW.findViewById(R.id.dpFif)).setKeyListener(null);

        VIEW.findViewById(R.id.dpUltmFiebGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFib(v);
            }
        });

        ((EditText)VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setKeyListener(null);

        VIEW.findViewById(R.id.dpUltmDosGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogDosis(v);
            }
        });

        ((EditText)VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setKeyListener(null);

        VIEW.findViewById(R.id.edtxtHoraGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogHora(v);
            }
        });

        ((EditText)VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).setKeyListener(null);

        /*Lugar de atencion*/
        View.OnClickListener onClickedLugarAtencion = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLugarAtencion(view);
            }
        };
        VIEW.findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)
                .setOnClickListener(onClickedLugarAtencion);
        VIEW.findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)
                .setOnClickListener(onClickedLugarAtencion);

        /*Cosulta*/
        View.OnClickListener onClickedConsulta = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedConsulta(view);
            }
        };

        VIEW.findViewById(R.id.chkbConsultaInicialGeneralesSint)
                .setOnClickListener(onClickedConsulta);
        VIEW.findViewById(R.id.chkbConsultaSeguimGeneralesSint)
                .setOnClickListener(onClickedConsulta);
        VIEW.findViewById(R.id.chkbConsultaConvGeneralesSint)
                .setOnClickListener(onClickedConsulta);

        /*Seguimiento Chick*/

        View.OnClickListener onClickedSegChk = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSegChick(view);
            }
        };

        VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)
                .setOnClickListener(onClickedSegChk);
        VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)
                .setOnClickListener(onClickedSegChk);
        VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)
                .setOnClickListener(onClickedSegChk);
        VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)
                .setOnClickListener(onClickedSegChk);
        VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)
                .setOnClickListener(onClickedSegChk);

        /*Turno*/
        View.OnClickListener onClickedTurno = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTurno(view);
            }
        };
        VIEW.findViewById(R.id.chkbRegularGeneralesSint)
                .setOnClickListener(onClickedTurno);
        VIEW.findViewById(R.id.chkbNocheGeneralesSint)
                .setOnClickListener(onClickedTurno);
        VIEW.findViewById(R.id.chkbFindeGeneralesSint)
                .setOnClickListener(onClickedTurno);

        /* AM/PM */
        View.OnClickListener onClickAmPm = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAmPm(view);
            }
        };
        VIEW.findViewById(R.id.chkbAMUltFGeneralesSint)
                .setOnClickListener(onClickAmPm);
        VIEW.findViewById(R.id.chkbPMUltFGeneralesSint)
                .setOnClickListener(onClickAmPm);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedLugarAtencion(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint),
                VIEW.findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint),
                view);
    }

    @SuppressLint("NonConstantResourceId")
    public void onChkboxClickedConsulta(View view) {
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
        mDbAdapter.open();
        HC = mDbAdapter.getHojaConsulta(MainDBConstants.codExpediente  + "='" + HOJACONSULTA.getCodExpediente() + "'" + " AND estado = '7'", "fechaConsulta DESC LIMIT 1");
        // Check which checkbox was clicked
        boolean checked = ((CheckBox) view).isChecked();
        String formattedDateFis = "";
        String formattedDateFif = "";
        switch (view.getId()) {
            case R.id.chkbConsultaSeguimGeneralesSint:
            case R.id.chkbConsultaConvGeneralesSint:
                if (checked) {
                    if (HC != null) {
                        if (!StringUtils.isNullOrEmpty(HC.getFis())) {
                            String fis = HC.getFis();
                            try {
                                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                Date date = null;
                                date = originalFormat.parse(fis);
                                formattedDateFis = targetFormat.format(date);
                                //((EditText) VIEW.findViewById(R.id.dpFis)).setText(formattedDateFis);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!StringUtils.isNullOrEmpty(HC.getFif())) {
                            //((EditText) VIEW.findViewById(R.id.dpFif)).setText(String.valueOf(HOJACONSULTA.getFif()));
                            String fif = HC.getFif();
                            try {
                                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                Date date = null;
                                date = originalFormat.parse(fif);
                                formattedDateFif = targetFormat.format(date);
                                //((EditText) VIEW.findViewById(R.id.dpFif)).setText(formattedDateFif);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
        if (!StringUtils.isNullOrEmpty(formattedDateFis) || !StringUtils.isNullOrEmpty(formattedDateFif)) {
            asignarFisFifConsultaAnterior(formattedDateFis, formattedDateFif);
        }
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbConsultaInicialGeneralesSint),
                VIEW.findViewById(R.id.chkbConsultaSeguimGeneralesSint),
                VIEW.findViewById(R.id.chkbConsultaConvGeneralesSint), view);
    }
    @SuppressLint("NonConstantResourceId")
    public void onChkboxClickedSegChick(View view) {
    // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbSegChk1GeneralesSint:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                break;
            case R.id.chkbSegChk2GeneralesSint:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                break;
            case R.id.chkbSegChk3GeneralesSint:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                break;
            case R.id.chkbSegChk4GeneralesSint:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                break;
            case R.id.chkbSegChk5GeneralesSint:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                }
                break;
        }
    }

    public void onChkboxClickedTurno(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRegularGeneralesSint),
                VIEW.findViewById(R.id.chkbNocheGeneralesSint),
                VIEW.findViewById(R.id.chkbFindeGeneralesSint), view);
    }

    public void onChkboxClickedAmPm(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbAMUltFGeneralesSint),
                VIEW.findViewById(R.id.chkbPMUltFGeneralesSint),
                view);
    }

    public void showDatePickerDialogFis(View view) {
        obtenerFis();
    }

    public void showDatePickerDialogFif(View view) {
        obtenerFif();
    }

    public void showDatePickerDialogFib(View view) {
        obtenerUltimiDiaFiebre();
    }

    public void showDatePickerDialogDosis(View view) {
        obtenerUltDosisAntipiretico();
    }

    public void showTimePickerDialogHora(View view) {
        obtenerHoraUltDosisAntipiretico();
    }

    public void obtenerFis() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)VIEW.findViewById(R.id.dpFis)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpFis)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpFis)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpFis)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpFis)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void obtenerFif() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)VIEW.findViewById(R.id.dpFif)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpFif)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpFif)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpFif)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpFif)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void obtenerUltimiDiaFiebre() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void obtenerUltDosisAntipiretico() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void obtenerHoraUltDosisAntipiretico() {
        Calendar dateTime = Calendar.getInstance();
        new TimePickerDialog(CONTEXT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                mAmPm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? true :
                        (calendar.get(Calendar.AM_PM) == Calendar.PM) ? false : null;

                if(((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).getError() != null) {
                    ((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).setError(null);
                    ((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.timer, 0);
                }

                ((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).setText(new SimpleDateFormat("KK:mm a").format(calendar.getTime()));
            }
        }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE),
                true).show();
    }

    public void validarCampoRequerido() throws Exception {
        String pas = ((EditText) VIEW.findViewById(R.id.edtxtPASint)).getText().toString();
        String pad = ((EditText) VIEW.findViewById(R.id.edtxtPADint)).getText().toString();
        if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtPASint)).getText().toString())) {
            throw new Exception(getString(R.string.label_pas) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtPADint)).getText().toString())) {
            throw new Exception(getString(R.string.label_pad) + ", " + getString(R.string.msj_completar_informacion));
        }  else if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_fcia_card) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_fcia_resp) + ", " + getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).isChecked() && !((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_lugar_atencion) + ", " + getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) VIEW.findViewById(R.id.chkbConsultaInicialGeneralesSint)).isChecked() && !((CheckBox) VIEW.findViewById(R.id.chkbConsultaSeguimGeneralesSint)).isChecked()
                && !((CheckBox) VIEW.findViewById(R.id.chkbConsultaConvGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_consulta) + ", " + getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) VIEW.findViewById(R.id.chkbRegularGeneralesSint)).isChecked() && !((CheckBox) VIEW.findViewById(R.id.chkbNocheGeneralesSint)).isChecked()
                && !((CheckBox) VIEW.findViewById(R.id.chkbFindeGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_turno) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_temp_med_c) + ", " + getString(R.string.msj_completar_informacion));
        } else if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())) {
            if (StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString())) {
                throw new Exception(getString(R.string.msj_hora_vacia));
            }
        }
        int cont = 0;
        //if (!estaEnRango(55, 135, pas)) { // rangos anteriores
        if (!estaEnRango(55, 220, pas)) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_pas));
            cont++;
        }

        //if (!estaEnRango(35, 100, pad)) { // rangos anteriores
        if (!estaEnRango(35, 160, pad)) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_pad));
            cont++;
        }
        if (!estaEnRango(11, 80, ((EditText) VIEW.findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_fcia_resp));
            cont++;
        }

        if (!estaEnRango(45, 200, ((EditText) VIEW.findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_fcia_card));
            cont++;
        }
        if (!estaEnRango(35.5, 41, ((EditText) VIEW.findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp_med_c));
            cont++;
        }

        if (cont > 0){
            throw new Exception(getString(R.string.msj_aviso_control_cambios1, vFueraRango));
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
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        int mUsuarioLogiado = ((CssfvApp) VIEW.getContext().getApplicationContext()).getInfoSessionWSDTO().getUserId();

        String pas = ((EditText) VIEW.findViewById(R.id.edtxtPASint)).getText().toString();
        String pad = ((EditText) VIEW.findViewById(R.id.edtxtPADint)).getText().toString();
        hojaConsulta.setUsuarioMedico(mUsuarioLogiado);
        hojaConsulta.setPas(Short.parseShort(pas));
        hojaConsulta.setPad(Short.parseShort(pad));
        hojaConsulta.setFciaCard(Integer.parseInt(((EditText) VIEW.findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString()));
        hojaConsulta.setFciaResp(Integer.parseInt(((EditText) VIEW.findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString()));

        if (((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).isChecked())
            hojaConsulta.setLugarAtencion("CS SFV");
        else if (((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).isChecked())
            hojaConsulta.setLugarAtencion("Terreno");

        if (((CheckBox) VIEW.findViewById(R.id.chkbConsultaInicialGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Inicial");
        else if (((CheckBox) VIEW.findViewById(R.id.chkbConsultaConvGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Convaleciente");
        else if (((CheckBox) VIEW.findViewById(R.id.chkbConsultaSeguimGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Seguimiento");

        if (((CheckBox) VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("1");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("2");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("3");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("4");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("5");
        } else{
            hojaConsulta.setSegChick(null);
        }

        if (((CheckBox) VIEW.findViewById(R.id.chkbRegularGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("1");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbNocheGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("2");
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbFindeGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("3");
        }

        hojaConsulta.setTemMedc(Double.parseDouble(((EditText) VIEW.findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString()));

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpFif)).getText().toString())){
            //HOJACONSULTA.setFif(((EditText) VIEW.findViewById(R.id.dpFif)).getText().toString());
            String fif = ((EditText) VIEW.findViewById(R.id.dpFif)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fif);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setFif(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            hojaConsulta.setFif("");
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpFis)).getText().toString())) {
            //HOJACONSULTA.setFis(((EditText) VIEW.findViewById(R.id.dpFis)).getText().toString());
            String fis = ((EditText) VIEW.findViewById(R.id.dpFis)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fis);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setFis(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            hojaConsulta.setFis("");
        }

        if (!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).getText().toString())) {
            //HOJACONSULTA.setUltDiaFiebre(((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).getText().toString());
            String ultDiaFiebre = ((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(ultDiaFiebre);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setUltDiaFiebre(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            hojaConsulta.setUltDiaFiebre("");
        }

        EditText dpUltmFiebGeneralesSint = (EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint);
        String valorUltmFieb = dpUltmFiebGeneralesSint.getText().toString();
        if (valorUltmFieb.trim().equals("")) {
            ((CheckBox) VIEW.findViewById(R.id.chkbPMUltFGeneralesSint)).setChecked(false);
            ((CheckBox) VIEW.findViewById(R.id.chkbAMUltFGeneralesSint)).setChecked(false);
            hojaConsulta.setAmPmUltDiaFiebre("");
            hojaConsulta.setAmPmUltDiaFiebre("");
        } else {
            if(!((CheckBox) VIEW.findViewById(R.id.chkbAMUltFGeneralesSint)).isChecked() &&
                    !((CheckBox) VIEW.findViewById(R.id.chkbPMUltFGeneralesSint)).isChecked()) {
                Toast.makeText(getActivity(), "Error, debe seleccionar AM ó PM", Toast.LENGTH_LONG).show();
                PD.dismiss();
                return;
            } else {
                if(((CheckBox) VIEW.findViewById(R.id.chkbAMUltFGeneralesSint)).isChecked()) {
                    hojaConsulta.setAmPmUltDiaFiebre("AM");
                } else if(((CheckBox) VIEW.findViewById(R.id.chkbPMUltFGeneralesSint)).isChecked()) {
                    hojaConsulta.setAmPmUltDiaFiebre("PM");
                }
            }
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())) {
            //HOJACONSULTA.setUltDosisAntipiretico(((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString());
            String ultDosisAntipiretico = ((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(ultDosisAntipiretico);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setUltDosisAntipiretico(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            hojaConsulta.setUltDosisAntipiretico("");
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString())) {
            hojaConsulta.setHoraUltDosisAntipiretico(((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString());
        }

        if(mAmPm != null && mAmPm.booleanValue() == true) {
            hojaConsulta.setAmPmUltDosisAntipiretico("AM");
        } else if(mAmPm != null && mAmPm.booleanValue() == false) {
            hojaConsulta.setAmPmUltDosisAntipiretico("PM");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar hora = Calendar.getInstance();
        df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        hojaConsulta.setHora(df.format(hora.getTime()));
        hojaConsulta.setEstado("3");

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

    /**
     * Meotod para verificar si el participante tiene mas de 4 días de FIF.
     * Fecha: 07/05/2022 - SC
     * */
    public boolean diffDayFConsultaAndFif() {
        boolean esMayorA4Dias = false;
        try {
            if (!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpFif)).getText().toString())) {
                EditText fif = (EditText) VIEW.findViewById(R.id.dpFif);
                String fifValue = fif.getText().toString();
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

                long milliSeconds= Long.parseLong(fechaConsulta);
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(milliSeconds);

                Date dateFechaConsulta = calendar.getTime();
                String resultFechaConsulta = sdf2.format(dateFechaConsulta);
                Date dateFif = sdf2.parse(fifValue);
                Date dateFConsulta = sdf2.parse(resultFechaConsulta);

                long diffInMillies = Math.abs(dateFConsulta.getTime() - dateFif.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                if (diff >= 4) {
                    esMayorA4Dias = true;
                } else {
                    esMayorA4Dias = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return esMayorA4Dias;
    }

    /**
     * Meotod para verificar la fis y fif de la consulta anterior coinciden con las que se
     * acaban de ingresar.
     * Fecha: 27/10/2022 - SC
     * */
    public boolean verificarFisFifConsultaAnterior() {
        boolean valor = false;
        try {
            if (HC != null) {
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                String fif = ((EditText) VIEW.findViewById(R.id.dpFif)).getText().toString();
                String fis = ((EditText) VIEW.findViewById(R.id.dpFis)).getText().toString();
                if (!StringUtils.isNullOrEmpty(HC.getFis()) && !StringUtils.isNullOrEmpty(fis)) {
                    String fisAnterior = HC.getFis();
                    DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    //DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.ENGLISH);
                    Date date = null;
                    date = originalFormat.parse(fis);
                    Date fecha2 = formato.parse(fisAnterior);
                    if (!date.equals(fecha2)) {
                        valor = true;
                    }
                }
                if (!StringUtils.isNullOrEmpty(HC.getFif()) && !StringUtils.isNullOrEmpty(fif)) {
                    String fifAnterior = HC.getFif();
                    DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date date = null;
                    date = originalFormat.parse(fif);
                    Date fecha2 = formato.parse(fifAnterior);
                    if (!date.equals(fecha2)) {
                        valor = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valor;
    }

    public void cargarDatos() {
        if(HOJACONSULTA.getPas() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtPASint)).setText(String.valueOf(HOJACONSULTA.getPas()));
        }

        if(HOJACONSULTA.getPad() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtPADint)).setText(String.valueOf(HOJACONSULTA.getPad()));
        }

        if (HOJACONSULTA.getFciaResp() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtFciaRespGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getFciaResp()));
        }

        if (HOJACONSULTA.getFciaCard() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtFciaCardGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getFciaCard()));
        }

        if (HOJACONSULTA.getTurno() != null) {
            if (HOJACONSULTA.getTurno().equals("1")) {
                ((CheckBox) VIEW.findViewById(R.id.chkbRegularGeneralesSint)).setChecked(true);
            } else if (HOJACONSULTA.getTurno().equals("2")) {
                ((CheckBox) VIEW.findViewById(R.id.chkbNocheGeneralesSint)).setChecked(true);
            } else {
                ((CheckBox) VIEW.findViewById(R.id.chkbFindeGeneralesSint)).setChecked(true);
            }
        }

        if (HOJACONSULTA.getTemMedc() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtTempMedGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getTemMedc()));
        }

        if (!StringUtils.isNullOrEmpty(HOJACONSULTA.getFif())) {
            //((EditText) VIEW.findViewById(R.id.dpFif)).setText(String.valueOf(HOJACONSULTA.getFif()));
            String fif = HOJACONSULTA.getFif();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fif);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpFif)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getFis())) {
            //((EditText) VIEW.findViewById(R.id.dpFis)).setText(String.valueOf(HOJACONSULTA.getFis()));
            String fis = HOJACONSULTA.getFis();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fis);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpFis)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!StringUtils.isNullOrEmpty(HOJACONSULTA.getUltDiaFiebre())){
            //((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getUltDiaFiebre()));
            String ultDiaFiebre = HOJACONSULTA.getUltDiaFiebre();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(ultDiaFiebre);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpUltmFiebGeneralesSint)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getUltDosisAntipiretico()) ) {
            //((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getUltDosisAntipiretico()));
            String ultDosisAntipiretico = HOJACONSULTA.getUltDosisAntipiretico();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(ultDosisAntipiretico);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpUltmDosGeneralesSint)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getHoraUltDosisAntipiretico())) {
            ((EditText) VIEW.findViewById(R.id.edtxtHoraGeneralesSint)).setText(String.valueOf(HOJACONSULTA.getHoraUltDosisAntipiretico()));
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getLugarAtencion())) {
            if (HOJACONSULTA.getLugarAtencion().compareTo("Terreno") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).setChecked(true);
            } else {
                ((CheckBox) VIEW.findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).setChecked(true);
            }
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getConsulta())) {
            if(HOJACONSULTA.getConsulta().compareTo("Inicial") == 0) {
                ((CheckBox)VIEW.findViewById(R.id.chkbConsultaInicialGeneralesSint)).setChecked(true);
            } else if(HOJACONSULTA.getConsulta().compareTo("Convaleciente") == 0) {
                ((CheckBox)VIEW.findViewById(R.id.chkbConsultaConvGeneralesSint)).setChecked(true);
            } else {
                ((CheckBox)VIEW.findViewById(R.id.chkbConsultaSeguimGeneralesSint)).setChecked(true);
            }
        }

        if(HOJACONSULTA.getSegChick() != null) {
            if(HOJACONSULTA.getSegChick().equals("1")) {
                ((CheckBox)VIEW.findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(true);
            } else if(HOJACONSULTA.getSegChick().equals("2")) {
                ((CheckBox)VIEW.findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(true);
            } else if(HOJACONSULTA.getSegChick().equals("3")) {
                ((CheckBox)VIEW.findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(true);
            } else if(HOJACONSULTA.getSegChick().equals("4")) {
                ((CheckBox)VIEW.findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(true);
            } else {
                ((CheckBox)VIEW.findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(true);
            }
        }

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getAmPmUltDiaFiebre())) {
            if(HOJACONSULTA.getAmPmUltDiaFiebre().compareTo("AM") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbAMUltFGeneralesSint)).setChecked(true);
            } else {
                ((CheckBox) VIEW.findViewById(R.id.chkbPMUltFGeneralesSint)).setChecked(true);
            }
        }
    }

    /**
     * Metodo para mostrar la alerta para la fecha ultimo dia de fiebre
     * Fecha: 07/05/2022 - SC
     * */
    private void alertDialogUltDiaFiebre() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(CONTEXT);
        dialog.setMessage("Recuerde llenar la fecha último día fiebre");
        dialog.setTitle(getResources().getString(R.string.title_estudio_sostenible));
        dialog.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                        guardarDatos();
                    }
                });
        dialog.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CONTEXT,"Se a cancelado el guardado",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    /**
     * Metodo para mostrar la alerta cuando la consulta es de Seguimiento o Convaleciente
     * Y las fis y fif no coincidan con la consulta anterior
     * Fecha: 27/10/2022 - SC
     * */
    private void alertDialogConsutaAnterior() {
        String formattedDateFif = "";
        String formattedDateFis = "";
        if (!StringUtils.isNullOrEmpty(HC.getFif())) {
            String fif = HC.getFif();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fif);
                formattedDateFif = targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!StringUtils.isNullOrEmpty(HC.getFis())) {
            String fis = HC.getFis();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fis);
                formattedDateFis = targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder dialog=new AlertDialog.Builder(CONTEXT);
        String mensaje = "";
        if (!StringUtils.isNullOrEmpty(formattedDateFis) && !StringUtils.isNullOrEmpty(formattedDateFif)) {
            mensaje = "FIS o FIF no coinciden con la consulta anterior, " +
                    "FIS Consulta previa: " + formattedDateFis + " FIF Consulta previa: " + formattedDateFif;
        }
        if (!StringUtils.isNullOrEmpty(formattedDateFis) && StringUtils.isNullOrEmpty(formattedDateFif)) {
            mensaje = "FIS no coincide con la consulta anterior, " +
                    "FIS Consulta previa: " + formattedDateFis;
        }
        if (!StringUtils.isNullOrEmpty(formattedDateFif) && StringUtils.isNullOrEmpty(formattedDateFis)) {
            mensaje = "FIF no coincide con la consulta anterior, " +
                    "FIF Consulta previa: " + formattedDateFif;
        }
        dialog.setMessage(mensaje);
        dialog.setTitle(getResources().getString(R.string.title_estudio_sostenible));
        dialog.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                        guardarDatos();
                    }
                });
        dialog.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CONTEXT,"Se a cancelado el guardado",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    /**
     * Metodo para mostrar la alerta cuando la consulta es de Seguimiento o Convaleciente
     * Y las fis y fif no coincidan con la consulta anterior
     * Fecha: 27/10/2022 - SC
     * */
    private void alertDialogConsutaAnteriorYUltDiaFiebre() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(CONTEXT);
        dialog.setMessage("Recuerde llenar la fecha último día fiebre, " +
                "FIS o FIF no coinciden con la consulta anterior ");
        dialog.setTitle(getResources().getString(R.string.title_estudio_sostenible));
        dialog.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                        guardarDatos();
                    }
                });
        dialog.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CONTEXT,"Se a cancelado el guardado",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    /**
     * Metodo para mostrar la alerta cuando la consulta es de Seguimiento o Convaleciente
     * Y las fis y fif no coincidan con la consulta anterior
     * Fecha: 27/10/2022 - SC
     * */
    private void asignarFisFifConsultaAnterior(String fis, String fif) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(CONTEXT);
        dialog.setMessage("Desea agregar la FIS y la FIF de la consulta anterior?");
        dialog.setTitle(getResources().getString(R.string.title_estudio_sostenible));
        dialog.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (!StringUtils.isNullOrEmpty(fis)) {
                            ((EditText) VIEW.findViewById(R.id.dpFis)).setText(fis);
                        }
                        if (!StringUtils.isNullOrEmpty(fif)) {
                            ((EditText) VIEW.findViewById(R.id.dpFif)).setText(fif);
                        }
                    }
                });
        dialog.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((EditText) VIEW.findViewById(R.id.dpFif)).setText("");
                ((EditText) VIEW.findViewById(R.id.dpFis)).setText("");
                Toast.makeText(CONTEXT,"Se a cancelado la acción",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }
}
