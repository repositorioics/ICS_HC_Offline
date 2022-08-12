package com.ics.ics_hc_offline.sintomasfragment;

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
import com.ics.ics_hc_offline.utils.StringUtils;
import com.ics.ics_hc_offline.utils.UtilHojaConsulta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReferenciaSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNENSintoma;
    private TextView viewTxtvSENSintoma;
    private String MENSAJE_MATRIZ = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_referencia_sintoma, container, false);
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

        Button btnReferenciaGSintomas = (Button) view.findViewById(R.id.btnReferenciaGSintomas);
        btnReferenciaGSintomas.setOnClickListener(new View.OnClickListener() {
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
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });
        cargarDatos();
    }

    public void regresar() {
        getActivity().finish();
    }

    public void inicializarContorles() {
        /*Interconsulta Pediatrica*/
        View.OnClickListener onClickedInterconsultaPediatrica = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedINPEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbINPSENSintoma)
                .setOnClickListener(onClickedInterconsultaPediatrica);
        VIEW.findViewById(R.id.chkbINPNENSintoma)
                .setOnClickListener(onClickedInterconsultaPediatrica);

        /*Referencia Hospital*/
        View.OnClickListener onClickedRefHospital = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRFHEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRFHSENSintoma)
                .setOnClickListener(onClickedRefHospital);
        VIEW.findViewById(R.id.chkbRFHNENSintoma)
                .setOnClickListener(onClickedRefHospital);

        /*Referencia Dengue*/
        View.OnClickListener onClickedRefDengue = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRFDEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRFDSENSintoma)
                .setOnClickListener(onClickedRefDengue);
        VIEW.findViewById(R.id.chkbRFDNENSintoma)
                .setOnClickListener(onClickedRefDengue);

        /*Referencia por IRAG*/
        View.OnClickListener onClickedRefIRAG = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRFIEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRFISENSintoma)
                .setOnClickListener(onClickedRefIRAG);
        VIEW.findViewById(R.id.chkbRFINENSintoma)
                .setOnClickListener(onClickedRefIRAG);

        /*Referencia por CHICK*/
        View.OnClickListener onClickedRefCHICK = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRFCEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRFCSENSintoma)
                .setOnClickListener(onClickedRefCHICK);
        VIEW.findViewById(R.id.chkbRFCNENSintoma)
                .setOnClickListener(onClickedRefCHICK);

        /*ETI*/
        View.OnClickListener onClickedETI = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedETIEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbETISENSintoma)
                .setOnClickListener(onClickedETI);
        VIEW.findViewById(R.id.chkbETINENSintoma)
                .setOnClickListener(onClickedETI);

        /*IRAG*/
        View.OnClickListener onClickedIRAG = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedIRAEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbIRASENSintoma)
                .setOnClickListener(onClickedIRAG);
        VIEW.findViewById(R.id.chkbIRANENSintoma)
                .setOnClickListener(onClickedIRAG);

        /*Neumonia*/
        View.OnClickListener onClickedNeumonia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedNEUEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbNEUSENSintoma)
                .setOnClickListener(onClickedNeumonia);
        VIEW.findViewById(R.id.chkbNEUNENSintoma)
                .setOnClickListener(onClickedNeumonia);

        /*CV*/
        View.OnClickListener onClickedCV = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkcoxClickedCV(view);
            }
        };

        VIEW.findViewById(R.id.chkbCOVIDSSintoma)
                .setOnClickListener(onClickedCV);
        VIEW.findViewById(R.id.chkbCOVIDNSintoma)
                .setOnClickListener(onClickedCV);
    }

    public void onChkboxClickedINPEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbINPSENSintoma), VIEW.findViewById(R.id.chkbINPNENSintoma), view);
    }

    public void onChkboxClickedRFHEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRFHSENSintoma), VIEW.findViewById(R.id.chkbRFHNENSintoma),
                view);
    }

    public void onChkboxClickedRFDEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRFDSENSintoma), VIEW.findViewById(R.id.chkbRFDNENSintoma),
                view);
        boolean referenciaDengue = ((CheckBox) VIEW.findViewById(R.id.chkbRFDSENSintoma)).isChecked();
        if (referenciaDengue) {
            String mensaje = "Recuerde marcar las casillas que son de gravedad";
            MensajesHelper.mostrarMensajeInfo(this.CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    null);
        }
    }

    public void onChkboxClickedRFIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRFISENSintoma), VIEW.findViewById(R.id.chkbRFINENSintoma),
                view);
    }

    public void onChkboxClickedRFCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRFCSENSintoma), VIEW.findViewById(R.id.chkbRFCNENSintoma), view);
    }

    public void onChkboxClickedETIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbETISENSintoma), VIEW.findViewById(R.id.chkbETINENSintoma),
                view);
    }

    public void onChkboxClickedIRAEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbIRASENSintoma), VIEW.findViewById(R.id.chkbIRANENSintoma),
                view);
    }


    public void onChkboxClickedNEUEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbNEUSENSintoma), VIEW.findViewById(R.id.chkbNEUNENSintoma),
                view);
    }

    public void onChkcoxClickedCV(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCOVIDSSintoma), VIEW.findViewById(R.id.chkbCOVIDNSintoma),
                view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbINPNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFHNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFDNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFINENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFCNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbETINENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbIRANENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNEUNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCOVIDNSintoma)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbINPSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFHSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFDSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFISENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRFCSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbETISENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbIRASENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNEUSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCOVIDSSintoma)).setChecked(valor);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_referencia_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_referencia_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbINPSENSintoma), VIEW.findViewById(R.id.chkbINPNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRFHSENSintoma), VIEW.findViewById(R.id.chkbRFHNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRFDSENSintoma), VIEW.findViewById(R.id.chkbRFDNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRFISENSintoma), VIEW.findViewById(R.id.chkbRFINENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRFCSENSintoma), VIEW.findViewById(R.id.chkbRFCNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbETISENSintoma), VIEW.findViewById(R.id.chkbETINENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbIRASENSintoma), VIEW.findViewById(R.id.chkbIRANENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbNEUSENSintoma), VIEW.findViewById(R.id.chkbNEUNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCOVIDSSintoma), VIEW.findViewById(R.id.chkbCOVIDNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
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
        hojaConsulta.setInterconsultaPediatrica(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbINPSENSintoma), VIEW.findViewById(R.id.chkbINPNENSintoma))));

        hojaConsulta.setReferenciaHospital(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRFHSENSintoma), VIEW.findViewById(R.id.chkbRFHNENSintoma))));

        hojaConsulta.setReferenciaDengue(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRFDSENSintoma), VIEW.findViewById(R.id.chkbRFDNENSintoma))));

        hojaConsulta.setReferenciaIrag(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRFISENSintoma), VIEW.findViewById(R.id.chkbRFINENSintoma))));

        hojaConsulta.setReferenciaChik(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRFCSENSintoma), VIEW.findViewById(R.id.chkbRFCNENSintoma))));

        hojaConsulta.setEti(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbETISENSintoma), VIEW.findViewById(R.id.chkbETINENSintoma))));

        hojaConsulta.setIrag(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbIRASENSintoma), VIEW.findViewById(R.id.chkbIRANENSintoma))));

        hojaConsulta.setNeumonia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbNEUSENSintoma), VIEW.findViewById(R.id.chkbNEUNENSintoma))));

        hojaConsulta.setcV(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCOVIDSSintoma), VIEW.findViewById(R.id.chkbCOVIDNSintoma))));

        boolean resultado = mDbAdapter.editarHojaConsulta(hojaConsulta);
        if (resultado) {
            Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_LONG).show();
            obtenerMensajesAlertaCriteriosEti();
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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbINPSENSintoma), VIEW.findViewById(R.id.chkbINPNENSintoma),
                ((HOJACONSULTA.getInterconsultaPediatrica() != null)
                        ? HOJACONSULTA.getInterconsultaPediatrica().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRFHSENSintoma), VIEW.findViewById(R.id.chkbRFHNENSintoma),
                ((HOJACONSULTA.getReferenciaHospital() != null)
                        ? HOJACONSULTA.getReferenciaHospital().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRFDSENSintoma), VIEW.findViewById(R.id.chkbRFDNENSintoma),
                ((HOJACONSULTA.getReferenciaDengue() != null)
                        ? HOJACONSULTA.getReferenciaDengue().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRFISENSintoma), VIEW.findViewById(R.id.chkbRFINENSintoma),
                ((HOJACONSULTA.getReferenciaIrag() != null)
                        ? HOJACONSULTA.getReferenciaIrag().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRFCSENSintoma), VIEW.findViewById(R.id.chkbRFCNENSintoma),
                ((HOJACONSULTA.getReferenciaChik() != null)
                        ? HOJACONSULTA.getReferenciaChik().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbETISENSintoma), VIEW.findViewById(R.id.chkbETINENSintoma),
                ((HOJACONSULTA.getEti() != null) ? HOJACONSULTA.getEti().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbIRASENSintoma), VIEW.findViewById(R.id.chkbIRANENSintoma),
                ((HOJACONSULTA.getIrag() != null) ? HOJACONSULTA.getIrag().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbNEUSENSintoma), VIEW.findViewById(R.id.chkbNEUNENSintoma),
                ((HOJACONSULTA.getNeumonia() != null) ? HOJACONSULTA.getNeumonia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCOVIDSSintoma), VIEW.findViewById(R.id.chkbCOVIDNSintoma),
                ((HOJACONSULTA.getcV() != null) ? HOJACONSULTA.getcV().charAt(0) : '4'));
    }

    public void obtenerMensajesAlertaCriteriosEti() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
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
                MensajesHelper.mostrarMensajeInfo(CONTEXT, MENSAJE_MATRIZ,
                        getResources().getString(R.string.title_estudio_sostenible), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
