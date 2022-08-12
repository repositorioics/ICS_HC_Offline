package com.ics.ics_hc_offline.sintomasfragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacunasSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNENSintoma;
    private TextView viewTxtvSENSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_vacuna_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        Button btnVacunasGSintomas = (Button) view.findViewById(R.id.btnVacunasGSintomas);
        btnVacunasGSintomas.setOnClickListener(new View.OnClickListener() {
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

        viewTxtvNENSintoma = (TextView) VIEW.findViewById(R.id.txtvNENSintoma);
        viewTxtvSENSintoma = (TextView) VIEW.findViewById(R.id.txtvSENSintoma);

        viewTxtvNENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });
        cargarDatos();
    }

    public void regresar() {
        getActivity().finish();
    }

    public void inicializarContorles() {
        /*Lactancia Materna*/
        View.OnClickListener onClickedLactanciaMaterna = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLTMEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbLTMSENSintoma)
                .setOnClickListener(onClickedLactanciaMaterna);
        VIEW.findViewById(R.id.chkbLTMNENSintoma)
                .setOnClickListener(onClickedLactanciaMaterna);
        VIEW.findViewById(R.id.chkbLTMDENSintoma)
                .setOnClickListener(onClickedLactanciaMaterna);

        /*Vacunas Completas*/
        View.OnClickListener onClickedVacunasCompletas = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedVCCEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbVCCSENSintoma)
                .setOnClickListener(onClickedVacunasCompletas);
        VIEW.findViewById(R.id.chkbVCCNENSintoma)
                .setOnClickListener(onClickedVacunasCompletas);
        VIEW.findViewById(R.id.chkbVCCDENSintoma)
                .setOnClickListener(onClickedVacunasCompletas);

        /*Vacuna Influenza*/
        View.OnClickListener onClickedVacunaInfluenza = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedVCIEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbVCISENSintoma)
                .setOnClickListener(onClickedVacunaInfluenza);
        VIEW.findViewById(R.id.chkbVCINENSintoma)
                .setOnClickListener(onClickedVacunaInfluenza);
        VIEW.findViewById(R.id.chkbVCIDENSintoma)
                .setOnClickListener(onClickedVacunaInfluenza);

        VIEW.findViewById(R.id.dpFCV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFechaVacuna(v);
            }
        });
    }

    public void showDatePickerDialogFechaVacuna(View view) {
        obtenerFechaVacuna();
    }

    public void onChkboxClickedLTMEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLTMSENSintoma), VIEW.findViewById(R.id.chkbLTMNENSintoma),
                VIEW.findViewById(R.id.chkbLTMDENSintoma), view);
    }

    public void onChkboxClickedVCCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbVCCSENSintoma), VIEW.findViewById(R.id.chkbVCCNENSintoma),
                VIEW.findViewById(R.id.chkbVCCDENSintoma), view);
    }

    public void onChkboxClickedVCIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbVCISENSintoma), VIEW.findViewById(R.id.chkbVCINENSintoma),
                VIEW.findViewById(R.id.chkbVCIDENSintoma), view);

        if(((CheckBox)VIEW.findViewById(R.id.chkbVCISENSintoma)).isChecked()) {
            VIEW.findViewById(R.id.txtvFCVENSintoma).setVisibility(View.VISIBLE);
            VIEW.findViewById(R.id.dpFCV).setVisibility(View.VISIBLE);
        } else {
            VIEW.findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
            ((EditText)VIEW.findViewById(R.id.dpFCV)).setText("");
            VIEW.findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
        }
    }

    public void obtenerFechaVacuna() {
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
                    ((EditText)VIEW.findViewById(R.id.dpFCV)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpFCV)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpFCV)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpFCV)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpFCV)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbLTMSENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVCCSENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVCISENSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbLTMSENSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbVCCSENSintoma)).setChecked(true);
                            if(valor==1) {
                                ((CheckBox) VIEW.findViewById(R.id.chkbVCISENSintoma)).setChecked(true);
                                VIEW.findViewById(R.id.txtvFCVENSintoma).setVisibility(View.VISIBLE);
                                VIEW.findViewById(R.id.dpFCV).setVisibility(View.VISIBLE);
                            }



                            ((CheckBox) VIEW.findViewById(R.id.chkbLTMNENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVCCNENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVCINENSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbLTMNENSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbVCCNENSintoma)).setChecked(true);
                            if(valor==2) {
                                ((CheckBox) VIEW.findViewById(R.id.chkbVCINENSintoma)).setChecked(true);
                                VIEW.findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
                                ((EditText) VIEW.findViewById(R.id.dpFCV)).setText("");
                                VIEW.findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
                            }


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_vacunas_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_vacunas_sintomas));


            MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        EditText dpFCV = (EditText) VIEW.findViewById(R.id.dpFCV);
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLTMSENSintoma), VIEW.findViewById(R.id.chkbLTMNENSintoma),
                VIEW.findViewById(R.id.chkbLTMDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbVCCSENSintoma), VIEW.findViewById(R.id.chkbVCCNENSintoma),
                VIEW.findViewById(R.id.chkbVCCDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbVCISENSintoma), VIEW.findViewById(R.id.chkbVCINENSintoma),
                VIEW.findViewById(R.id.chkbVCIDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (((CheckBox) VIEW.findViewById(R.id.chkbVCISENSintoma)).isChecked() && StringUtils.isNullOrEmpty(dpFCV.getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_fecha_vacuna));
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
        hojaConsulta.setLactanciaMaterna(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLTMSENSintoma),
                VIEW.findViewById(R.id.chkbLTMNENSintoma), VIEW.findViewById(R.id.chkbLTMDENSintoma))));

        hojaConsulta.setVacunasCompletas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbVCCSENSintoma),
                VIEW.findViewById(R.id.chkbVCCNENSintoma), VIEW.findViewById(R.id.chkbVCCDENSintoma))));

        hojaConsulta.setVacunaInfluenza(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbVCISENSintoma),
                VIEW.findViewById(R.id.chkbVCINENSintoma), VIEW.findViewById(R.id.chkbVCIDENSintoma))));

        if(StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpFCV)).getText().toString())){
            hojaConsulta.setFechaVacuna("");
        }else {
            //HOJACONSULTA.setFechaVacuna(((EditText) VIEW.findViewById(R.id.dpFCV)).getText().toString());
            String fechaVacuna = ((EditText) VIEW.findViewById(R.id.dpFCV)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fechaVacuna);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setFechaVacuna(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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

    public void cargarDatos() {
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLTMSENSintoma),
                VIEW.findViewById(R.id.chkbLTMNENSintoma), VIEW.findViewById(R.id.chkbLTMDENSintoma),
                ((HOJACONSULTA.getLactanciaMaterna() != null) ? HOJACONSULTA.getLactanciaMaterna().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbVCCSENSintoma),
                VIEW.findViewById(R.id.chkbVCCNENSintoma), VIEW.findViewById(R.id.chkbVCCDENSintoma),
                ((HOJACONSULTA.getVacunasCompletas() != null) ? HOJACONSULTA.getVacunasCompletas().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbVCISENSintoma),
                VIEW.findViewById(R.id.chkbVCINENSintoma), VIEW.findViewById(R.id.chkbVCIDENSintoma),
                ((HOJACONSULTA.getVacunaInfluenza() != null) ? HOJACONSULTA.getVacunaInfluenza().charAt(0) : '4'));

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getFechaVacuna())) {
            //((EditText) VIEW.findViewById(R.id.dpFCV)).setText((HOJACONSULTA.getFechaVacuna()));
            String fechaVacuna = HOJACONSULTA.getFechaVacuna();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fechaVacuna);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpFCV)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!((CheckBox)VIEW.findViewById(R.id.chkbVCISENSintoma)).isChecked()) {
            VIEW.findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
            VIEW.findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
        }
    }
}
