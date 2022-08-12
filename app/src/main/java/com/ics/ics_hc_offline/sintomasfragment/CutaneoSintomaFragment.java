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

public class CutaneoSintomaFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNCTSintoma;
    private TextView viewTxtvSCTSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_cutaneo_sintoma, container, false);
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

        Button btnCutaneoGSintomas = (Button) view.findViewById(R.id.btnCutaneoGSintomas);
        btnCutaneoGSintomas.setOnClickListener(new View.OnClickListener() {
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

        viewTxtvNCTSintoma = (TextView) VIEW.findViewById(R.id.txtvNCTSintoma);
        viewTxtvSCTSintoma = (TextView) VIEW.findViewById(R.id.txtvSCTSintoma);


        viewTxtvNCTSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSCTSintoma.setOnClickListener(new View.OnClickListener() {
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
        /*Rash Localizado*/
        View.OnClickListener onClickedRashLocalizado = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRHLCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRHLSCTSintoma)
                .setOnClickListener(onClickedRashLocalizado);
        VIEW.findViewById(R.id.chkbRHLNCTSintoma)
                .setOnClickListener(onClickedRashLocalizado);

        /*Rash Generalizado*/
        View.OnClickListener onClickedRashGeneralizado = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRHGCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRHGSCTSintoma)
                .setOnClickListener(onClickedRashGeneralizado);
        VIEW.findViewById(R.id.chkbRHGNCTSintoma)
                .setOnClickListener(onClickedRashGeneralizado);

        /*Rash Eritematoso*/
        View.OnClickListener onClickedRashEritematoso = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRHECT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRHESCTSintoma)
                .setOnClickListener(onClickedRashEritematoso);
        VIEW.findViewById(R.id.chkbRHENCTSintoma)
                .setOnClickListener(onClickedRashEritematoso);

        /*Rash Macular*/
        View.OnClickListener onClickedRashMacular = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRHMCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRHMSCTSintoma)
                .setOnClickListener(onClickedRashMacular);
        VIEW.findViewById(R.id.chkbRHMNCTSintoma)
                .setOnClickListener(onClickedRashMacular);

        /*Rash Papular*/
        View.OnClickListener onClickedRashPapular = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRHPCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRHPSCTSintoma)
                .setOnClickListener(onClickedRashPapular);
        VIEW.findViewById(R.id.chkbRHPNCTSintoma)
                .setOnClickListener(onClickedRashPapular);

        /*Rash Moteada*/
        View.OnClickListener onClickedRashMoteada = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRSMCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRSMSCTSintoma)
                .setOnClickListener(onClickedRashMoteada);
        VIEW.findViewById(R.id.chkbRSMNCTSintoma)
                .setOnClickListener(onClickedRashMoteada);

        /*Rubor Facial*/
        View.OnClickListener onClickedRuborFacial = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRBFCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbRBFSCTSintoma)
                .setOnClickListener(onClickedRuborFacial);
        VIEW.findViewById(R.id.chkbRBFNCTSintoma)
                .setOnClickListener(onClickedRuborFacial);

        /*Equimosis*/
        View.OnClickListener onClickedEquimosis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEQMCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEQMSCTSintoma)
                .setOnClickListener(onClickedEquimosis);
        VIEW.findViewById(R.id.chkbEQMNCTSintoma)
                .setOnClickListener(onClickedEquimosis);

        /*Cianosis Cetral*/
        View.OnClickListener onClickedCianosisCentral = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCNCCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbCNCSCTSintoma)
                .setOnClickListener(onClickedCianosisCentral);
        VIEW.findViewById(R.id.chkbCNCNCTSintoma)
                .setOnClickListener(onClickedCianosisCentral);

        /*Ictericia*/
        View.OnClickListener onClickedIctericia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedITCCT(view);
            }
        };

        VIEW.findViewById(R.id.chkbITCSCTSintoma)
                .setOnClickListener(onClickedIctericia);
        VIEW.findViewById(R.id.chkbITCNCTSintoma)
                .setOnClickListener(onClickedIctericia);
    }

    public void onChkboxClickedRHLCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRHLSCTSintoma), VIEW.findViewById(R.id.chkbRHLNCTSintoma), view);
    }

    public void onChkboxClickedRHGCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRHGSCTSintoma), VIEW.findViewById(R.id.chkbRHGNCTSintoma),
                view);
    }

    public void onChkboxClickedRHECT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRHESCTSintoma), VIEW.findViewById(R.id.chkbRHENCTSintoma),
                view);
    }

    public void onChkboxClickedRHMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRHMSCTSintoma), VIEW.findViewById(R.id.chkbRHMNCTSintoma),
                view);
    }

    public void onChkboxClickedRHPCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRHPSCTSintoma), VIEW.findViewById(R.id.chkbRHPNCTSintoma), view);
    }

    public void onChkboxClickedRSMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRSMSCTSintoma), VIEW.findViewById(R.id.chkbRSMNCTSintoma),
                view);
    }

    public void onChkboxClickedRBFCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRBFSCTSintoma), VIEW.findViewById(R.id.chkbRBFNCTSintoma),
                view);
    }


    public void onChkboxClickedEQMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEQMSCTSintoma), VIEW.findViewById(R.id.chkbEQMNCTSintoma),
                view);
    }

    public void onChkboxClickedCNCCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCNCSCTSintoma), VIEW.findViewById(R.id.chkbCNCNCTSintoma),
                view);
    }

    public void onChkboxClickedITCCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbITCSCTSintoma), VIEW.findViewById(R.id.chkbITCNCTSintoma),
                view);
    }

    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbRHLSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHGSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHESCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHMSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHPSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRSMSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRBFSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEQMSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCNCSCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbITCSCTSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRHLSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRHGSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRHESCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRHMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRHPSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRSMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbRBFSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEQMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbCNCSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbITCSCTSintoma)).setChecked(true);

                            ((CheckBox) VIEW.findViewById(R.id.chkbRHLNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHGNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHENCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHMNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRHPNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRSMNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRBFNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEQMNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCNCNCTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbITCNCTSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRHLNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRHGNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRHENCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRHMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRHPNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRSMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbRBFNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEQMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbCNCNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbITCNCTSintoma)).setChecked(true);


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_cutaneo_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_cutaneo_sintomas));
            if(valor==3) mensaje = String.format(getResources().getString(R.string.msg_change_desc), getResources().getString(R.string.boton_cutaneo_sintomas));


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
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRHLSCTSintoma), VIEW.findViewById(R.id.chkbRHLNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRHGSCTSintoma), VIEW.findViewById(R.id.chkbRHGNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRHESCTSintoma), VIEW.findViewById(R.id.chkbRHENCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRHMSCTSintoma), VIEW.findViewById(R.id.chkbRHMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRHPSCTSintoma), VIEW.findViewById(R.id.chkbRHPNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRSMSCTSintoma), VIEW.findViewById(R.id.chkbRSMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRBFSCTSintoma), VIEW.findViewById(R.id.chkbRBFNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEQMSCTSintoma), VIEW.findViewById(R.id.chkbEQMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCNCSCTSintoma), VIEW.findViewById(R.id.chkbCNCNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbITCSCTSintoma), VIEW.findViewById(R.id.chkbITCNCTSintoma))) {
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
        hojaConsulta.setRahsLocalizado(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRHLSCTSintoma), VIEW.findViewById(R.id.chkbRHLNCTSintoma))));

        hojaConsulta.setRahsGeneralizado(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRHGSCTSintoma), VIEW.findViewById(R.id.chkbRHGNCTSintoma))));

        hojaConsulta.setRashEritematoso(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRHESCTSintoma), VIEW.findViewById(R.id.chkbRHENCTSintoma))));

        hojaConsulta.setRahsMacular(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRHMSCTSintoma), VIEW.findViewById(R.id.chkbRHMNCTSintoma))));

        hojaConsulta.setRashPapular(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRHPSCTSintoma), VIEW.findViewById(R.id.chkbRHPNCTSintoma))));

        hojaConsulta.setRahsMoteada(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRSMSCTSintoma), VIEW.findViewById(R.id.chkbRSMNCTSintoma))));

        hojaConsulta.setRuborFacial(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRBFSCTSintoma), VIEW.findViewById(R.id.chkbRBFNCTSintoma))));

        hojaConsulta.setEquimosis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEQMSCTSintoma), VIEW.findViewById(R.id.chkbEQMNCTSintoma))));

        hojaConsulta.setCianosisCentral(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCNCSCTSintoma), VIEW.findViewById(R.id.chkbCNCNCTSintoma))));

        hojaConsulta.setIctericia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbITCSCTSintoma), VIEW.findViewById(R.id.chkbITCNCTSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRHLSCTSintoma), VIEW.findViewById(R.id.chkbRHLNCTSintoma),
                ((HOJACONSULTA.getRahsLocalizado() != null) ? HOJACONSULTA.getRahsLocalizado().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRHGSCTSintoma), VIEW.findViewById(R.id.chkbRHGNCTSintoma),
                ((HOJACONSULTA.getRahsGeneralizado() != null) ? HOJACONSULTA.getRahsGeneralizado().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRHESCTSintoma), VIEW.findViewById(R.id.chkbRHENCTSintoma),
                ((HOJACONSULTA.getRashEritematoso() != null) ? HOJACONSULTA.getRashEritematoso().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRHMSCTSintoma), VIEW.findViewById(R.id.chkbRHMNCTSintoma),
                ((HOJACONSULTA.getRahsMacular() != null) ? HOJACONSULTA.getRahsMacular().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRHPSCTSintoma), VIEW.findViewById(R.id.chkbRHPNCTSintoma),
                ((HOJACONSULTA.getRashPapular() != null) ? HOJACONSULTA.getRashPapular().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRSMSCTSintoma), VIEW.findViewById(R.id.chkbRSMNCTSintoma),
                ((HOJACONSULTA.getRahsMoteada() != null) ? HOJACONSULTA.getRahsMoteada().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRBFSCTSintoma), VIEW.findViewById(R.id.chkbRBFNCTSintoma),
                ((HOJACONSULTA.getRuborFacial() != null) ? HOJACONSULTA.getRuborFacial().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEQMSCTSintoma), VIEW.findViewById(R.id.chkbEQMNCTSintoma),
                ((HOJACONSULTA.getEquimosis() != null) ? HOJACONSULTA.getEquimosis().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCNCSCTSintoma), VIEW.findViewById(R.id.chkbCNCNCTSintoma),
                ((HOJACONSULTA.getCianosisCentral() != null) ? HOJACONSULTA.getCianosisCentral().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbITCSCTSintoma), VIEW.findViewById(R.id.chkbITCNCTSintoma),
                ((HOJACONSULTA.getIctericia() != null) ? HOJACONSULTA.getIctericia().charAt(0) : '4'));
    }
}
