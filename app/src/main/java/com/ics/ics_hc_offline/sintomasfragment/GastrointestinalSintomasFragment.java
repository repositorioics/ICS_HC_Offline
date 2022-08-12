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
import com.ics.ics_hc_offline.utils.StringUtils;

public class GastrointestinalSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private String vFueraRango = "";
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNSintoma;
    private TextView viewTxtvSSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_gastrointestinal_sintoma, container, false);
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

        Button btnGastroinstesGSintomas = (Button) view.findViewById(R.id.btnGastroinstesGSintomas);
        btnGastroinstesGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vFueraRango = "";
                try {
                    validarCampos();
                    guardarDatos();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewTxtvNSintoma = (TextView) VIEW.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintoma = (TextView) VIEW.findViewById(R.id.txtvSSintoma);

        viewTxtvNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });
        cargarDatos();
    }

    public void inicializarContorles() {
        /*Poco Apetito*/
        View.OnClickListener onClickedPocoApetito = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPocoAptGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbPocoAptSGTSintoma)
                .setOnClickListener(onClickedPocoApetito);
        VIEW.findViewById(R.id.chkbPocoAptNGTSintoma)
                .setOnClickListener(onClickedPocoApetito);
        VIEW.findViewById(R.id.chkbPocoAptDGTSintoma)
                .setOnClickListener(onClickedPocoApetito);

        /*Nausea*/
        View.OnClickListener onClickedNausea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedNauseaGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbNauseaSGTSintoma)
                .setOnClickListener(onClickedNausea);
        VIEW.findViewById(R.id.chkbNauseaNGTSintoma)
                .setOnClickListener(onClickedNausea);
        VIEW.findViewById(R.id.chkbNauseaDGTSintoma)
                .setOnClickListener(onClickedNausea);

        /*Dificultad para Alimentarse*/
        View.OnClickListener onClickedDifiAlimentarse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDAGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDASGTSintoma)
                .setOnClickListener(onClickedDifiAlimentarse);
        VIEW.findViewById(R.id.chkbDANGTSintoma)
                .setOnClickListener(onClickedDifiAlimentarse);
        VIEW.findViewById(R.id.chkbDADGTSintoma)
                .setOnClickListener(onClickedDifiAlimentarse);

        /*Vomito*/
        View.OnClickListener onClickedVomito = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedVomGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbVomSGTSintoma)
                .setOnClickListener(onClickedVomito);
        VIEW.findViewById(R.id.chkbVomNGTSintoma)
                .setOnClickListener(onClickedVomito);
        VIEW.findViewById(R.id.chkbVomDGTSintoma)
                .setOnClickListener(onClickedVomito);

        /*Diarrea*/
        View.OnClickListener onClickedDiarrea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDiaGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDiaSGTSintoma)
                .setOnClickListener(onClickedDiarrea);
        VIEW.findViewById(R.id.chkbDiaNGTSintoma)
                .setOnClickListener(onClickedDiarrea);
        VIEW.findViewById(R.id.chkbDiaDGTSintoma)
                .setOnClickListener(onClickedDiarrea);

        /*Diarrea con Sangre*/
        View.OnClickListener onClickedDiarreaSangre = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDiaSGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDiaSSGTSintoma)
                .setOnClickListener(onClickedDiarreaSangre);
        VIEW.findViewById(R.id.chkbDiaSNGTSintoma)
                .setOnClickListener(onClickedDiarreaSangre);
        VIEW.findViewById(R.id.chkbDiaSDGTSintoma)
                .setOnClickListener(onClickedDiarreaSangre);

        /*Estreñimiento*/
        View.OnClickListener onClickedEstrenimiento = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEstGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEstSGTSintoma)
                .setOnClickListener(onClickedEstrenimiento);
        VIEW.findViewById(R.id.chkbEstNGTSintoma)
                .setOnClickListener(onClickedEstrenimiento);
        VIEW.findViewById(R.id.chkbEstDGTSintoma)
                .setOnClickListener(onClickedEstrenimiento);

        /*Dolor Abdominal Intermitente*/
        View.OnClickListener onClickedDolorAbInt = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDaiGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDaiSGTSintoma)
                .setOnClickListener(onClickedDolorAbInt);
        VIEW.findViewById(R.id.chkbDaiNGTSintoma)
                .setOnClickListener(onClickedDolorAbInt);
        VIEW.findViewById(R.id.chkbDaiDGTSintoma)
                .setOnClickListener(onClickedDolorAbInt);

        /*Dolor Abdominal Continuo*/
        View.OnClickListener onClickedDolorAbCont = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDacGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDacSGTSintoma)
                .setOnClickListener(onClickedDolorAbCont);
        VIEW.findViewById(R.id.chkbDacNGTSintoma)
                .setOnClickListener(onClickedDolorAbCont);
        VIEW.findViewById(R.id.chkbDacDGTSintoma)
                .setOnClickListener(onClickedDolorAbCont);

        /*Epigastralgia*/
        View.OnClickListener onClickedEpigastralgia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEpiGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEpiSGTSintoma)
                .setOnClickListener(onClickedEpigastralgia);
        VIEW.findViewById(R.id.chkbEpiNGTSintoma)
                .setOnClickListener(onClickedEpigastralgia);
        VIEW.findViewById(R.id.chkbEpiDGTSintoma)
                .setOnClickListener(onClickedEpigastralgia);

        /*Intolerancia via Oral*/
        View.OnClickListener onClickedIntViaOral = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedInvGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbInvSGTSintoma)
                .setOnClickListener(onClickedIntViaOral);
        VIEW.findViewById(R.id.chkbInvNGTSintoma)
                .setOnClickListener(onClickedIntViaOral);
        VIEW.findViewById(R.id.chkbInvDGTSintoma)
                .setOnClickListener(onClickedIntViaOral);

        /*Distencion Abdominal*/
        View.OnClickListener onClickedDistAbdominal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDABGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDABSGTSintoma)
                .setOnClickListener(onClickedDistAbdominal);
        VIEW.findViewById(R.id.chkbDABNGTSintoma)
                .setOnClickListener(onClickedDistAbdominal);
        VIEW.findViewById(R.id.chkbDABDGTSintoma)
                .setOnClickListener(onClickedDistAbdominal);

        /*Hepatomegalia*/
        View.OnClickListener onClickedHepatomegalia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHepaGT(view);
            }
        };

        VIEW.findViewById(R.id.chkbHepaSGTSintoma)
                .setOnClickListener(onClickedHepatomegalia);
        VIEW.findViewById(R.id.chkbHepaNGTSintoma)
                .setOnClickListener(onClickedHepatomegalia);
    }

    public void onChkboxClickedPocoAptGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPocoAptSGTSintoma), VIEW.findViewById(R.id.chkbPocoAptNGTSintoma),
                VIEW.findViewById(R.id.chkbPocoAptDGTSintoma), view);
    }

    public void onChkboxClickedNauseaGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbNauseaSGTSintoma), VIEW.findViewById(R.id.chkbNauseaNGTSintoma),
                VIEW.findViewById(R.id.chkbNauseaDGTSintoma), view);
    }

    public void onChkboxClickedDAGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDASGTSintoma), VIEW.findViewById(R.id.chkbDANGTSintoma),
                VIEW.findViewById(R.id.chkbDADGTSintoma), view);
    }

    public void onChkboxClickedVomGT(View view) {

        switch (view.getId()) {
            case R.id.chkbVomSGTSintoma :
                ((CheckBox)VIEW.findViewById(R.id.chkbVomSGTSintoma)).setChecked(true);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomNGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                VIEW.findViewById(R.id.edtxtVm12h).setVisibility(View.VISIBLE);
                break;
            case R.id.chkbVomNGTSintoma :
                ((CheckBox)VIEW.findViewById(R.id.chkbVomSGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomNGTSintoma)).setChecked(true);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                VIEW.findViewById(R.id.edtxtVm12h).setVisibility(View.GONE);
                ((EditText)VIEW.findViewById(R.id.edtxtVm12h)).setText("");
                break;
            case R.id.chkbVomDGTSintoma :
                ((CheckBox)VIEW.findViewById(R.id.chkbVomSGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomNGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbVomDGTSintoma)).setChecked(true);
                VIEW.findViewById(R.id.edtxtVm12h).setVisibility(View.GONE);
                ((EditText)VIEW.findViewById(R.id.edtxtVm12h)).setText("");
                break;
        }
    }

    public void onChkboxClickedDiaGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDiaSGTSintoma), VIEW.findViewById(R.id.chkbDiaNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaDGTSintoma), view);
    }

    public void onChkboxClickedDiaSGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDiaSSGTSintoma), VIEW.findViewById(R.id.chkbDiaSNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaSDGTSintoma), view);
    }

    public void onChkboxClickedEstGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEstSGTSintoma), VIEW.findViewById(R.id.chkbEstNGTSintoma),
                VIEW.findViewById(R.id.chkbEstDGTSintoma), view);
    }

    public void onChkboxClickedDaiGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDaiSGTSintoma), VIEW.findViewById(R.id.chkbDaiNGTSintoma),
                VIEW.findViewById(R.id.chkbDaiDGTSintoma), view);
    }

    public void onChkboxClickedDacGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDacSGTSintoma), VIEW.findViewById(R.id.chkbDacNGTSintoma),
                VIEW.findViewById(R.id.chkbDacDGTSintoma), view);
    }

    public void onChkboxClickedEpiGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEpiSGTSintoma), VIEW.findViewById(R.id.chkbEpiNGTSintoma),
                VIEW.findViewById(R.id.chkbEpiDGTSintoma), view);
    }

    public void onChkboxClickedInvGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbInvSGTSintoma), VIEW.findViewById(R.id.chkbInvNGTSintoma),
                VIEW.findViewById(R.id.chkbInvDGTSintoma), view);
    }

    public void onChkboxClickedDABGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDABSGTSintoma), VIEW.findViewById(R.id.chkbDABNGTSintoma),
                VIEW.findViewById(R.id.chkbDABDGTSintoma), view);
    }

    public void onChkboxClickedHepaGT(View view) {

        switch (view.getId()) {
            case R.id.chkbHepaSGTSintoma :
                ((CheckBox)VIEW.findViewById(R.id.chkbHepaNGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbHepaSGTSintoma)).setChecked(true);
                VIEW.findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.VISIBLE);
                break;
            case R.id.chkbHepaNGTSintoma :
                ((CheckBox)VIEW.findViewById(R.id.chkbHepaSGTSintoma)).setChecked(false);
                ((CheckBox)VIEW.findViewById(R.id.chkbHepaNGTSintoma)).setChecked(true);
                VIEW.findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.GONE);
                ((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).setText("");
                break;
        }
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbPocoAptNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNauseaNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDANGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVomNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaSNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEstNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDaiNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDacNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEpiNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbInvNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDABNGTSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHepaNGTSintoma)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbPocoAptSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNauseaSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDASGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVomSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaSSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEstSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDaiSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDacSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEpiSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbInvSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDABSGTSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHepaSGTSintoma)).setChecked(valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbPocoAptDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNauseaDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDADGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDiaSDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEstDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDaiDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDacDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEpiDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbInvDGTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDABDGTSintoma)).setChecked(false);

                            if (valor) {
                                VIEW.findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.VISIBLE);
                                VIEW.findViewById(R.id.edtxtVm12h).setVisibility(View.VISIBLE);
                            } else {
                                VIEW.findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.GONE);
                                ((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).setText("");
                                VIEW.findViewById(R.id.edtxtVm12h).setVisibility(View.GONE);
                                ((EditText)VIEW.findViewById(R.id.edtxtVm12h)).setText("");
                            }

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_gastro_intestinal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_gastro_intestinal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        EditText edtxtHepaCmSintoma = (EditText) VIEW.findViewById(R.id.edtxtHepaCmSintoma);
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPocoAptSGTSintoma), VIEW.findViewById(R.id.chkbPocoAptNGTSintoma),
                VIEW.findViewById(R.id.chkbPocoAptDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbNauseaSGTSintoma), VIEW.findViewById(R.id.chkbNauseaNGTSintoma),
                VIEW.findViewById(R.id.chkbNauseaDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));

        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDASGTSintoma), VIEW.findViewById(R.id.chkbDANGTSintoma),
                VIEW.findViewById(R.id.chkbDADGTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbVomSGTSintoma), VIEW.findViewById(R.id.chkbVomNGTSintoma),
                VIEW.findViewById(R.id.chkbVomDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDiaSGTSintoma), VIEW.findViewById(R.id.chkbDiaNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDiaSSGTSintoma), VIEW.findViewById(R.id.chkbDiaSNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaSDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEstSGTSintoma), VIEW.findViewById(R.id.chkbEstNGTSintoma),
                VIEW.findViewById(R.id.chkbEstDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDaiSGTSintoma), VIEW.findViewById(R.id.chkbDaiNGTSintoma),
                VIEW.findViewById(R.id.chkbDaiDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDacSGTSintoma), VIEW.findViewById(R.id.chkbDacNGTSintoma),
                VIEW.findViewById(R.id.chkbDacDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEpiSGTSintoma), VIEW.findViewById(R.id.chkbEpiNGTSintoma),
                VIEW.findViewById(R.id.chkbEpiDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbInvSGTSintoma), VIEW.findViewById(R.id.chkbInvNGTSintoma),
                VIEW.findViewById(R.id.chkbInvDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDABSGTSintoma), VIEW.findViewById(R.id.chkbDABNGTSintoma),
                VIEW.findViewById(R.id.chkbDABDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHepaSGTSintoma), VIEW.findViewById(R.id.chkbHepaNGTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbHepaSGTSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(edtxtHepaCmSintoma.getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (((CheckBox) VIEW.findViewById(R.id.chkbVomSGTSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtVm12h)).getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }

        //validando los rangos
        EditText edtVM12 = ((EditText)VIEW.findViewById(R.id.edtxtVm12h));
        EditText edtHepa = ((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma));

        int cont = 0;
        if(edtVM12.getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(edtVM12.getText().toString())
                && !estaEnRango(0, 24, edtVM12.getText().toString())){
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_vomito_ultima_12_horas));
            cont++;
        }

        if(edtHepa.getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(edtHepa.getText().toString())
                && !estaEnRango(0, 5, edtHepa.getText().toString())){
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_hepatomegalia_cm));
            cont++;
        }

        if (cont > 0){
            throw new Exception(getString(R.string.msj_aviso_control_cambios1, vFueraRango));
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
        hojaConsulta.setPocoApetito(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPocoAptSGTSintoma), VIEW.findViewById(R.id.chkbPocoAptNGTSintoma),
                VIEW.findViewById(R.id.chkbPocoAptDGTSintoma))));

        hojaConsulta.setNausea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbNauseaSGTSintoma), VIEW.findViewById(R.id.chkbNauseaNGTSintoma),
                VIEW.findViewById(R.id.chkbNauseaDGTSintoma))));

        hojaConsulta.setDificultadAlimentarse(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDASGTSintoma), VIEW.findViewById(R.id.chkbDANGTSintoma),
                VIEW.findViewById(R.id.chkbDADGTSintoma))));

        hojaConsulta.setVomito12horas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbVomSGTSintoma), VIEW.findViewById(R.id.chkbVomNGTSintoma),
                VIEW.findViewById(R.id.chkbVomDGTSintoma))));

        if(((CheckBox)VIEW.findViewById(R.id.chkbVomSGTSintoma)).isChecked() &&
                !StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtVm12h)).getText().toString())) {
            hojaConsulta.setVomito12h(Short.parseShort(((EditText)VIEW.findViewById(R.id.edtxtVm12h)).getText().toString()));
        } else {
            hojaConsulta.setVomito12h(null);
        }

        hojaConsulta.setDiarrea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDiaSGTSintoma), VIEW.findViewById(R.id.chkbDiaNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaDGTSintoma))));

        hojaConsulta.setDiarreaSangre(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDiaSSGTSintoma), VIEW.findViewById(R.id.chkbDiaSNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaSDGTSintoma))));

        hojaConsulta.setEstrenimiento(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEstSGTSintoma), VIEW.findViewById(R.id.chkbEstNGTSintoma),
                VIEW.findViewById(R.id.chkbEstDGTSintoma))));

        hojaConsulta.setDolorAbIntermitente(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDaiSGTSintoma), VIEW.findViewById(R.id.chkbDaiNGTSintoma),
                VIEW.findViewById(R.id.chkbDaiDGTSintoma))));

        hojaConsulta.setDolorAbContinuo(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDacSGTSintoma), VIEW.findViewById(R.id.chkbDacNGTSintoma),
                VIEW.findViewById(R.id.chkbDacDGTSintoma))));

        hojaConsulta.setEpigastralgia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEpiSGTSintoma), VIEW.findViewById(R.id.chkbEpiNGTSintoma),
                VIEW.findViewById(R.id.chkbEpiDGTSintoma))));

        hojaConsulta.setIntoleranciaOral(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbInvSGTSintoma), VIEW.findViewById(R.id.chkbInvNGTSintoma),
                VIEW.findViewById(R.id.chkbInvDGTSintoma))));

        hojaConsulta.setDistensionAbdominal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDABSGTSintoma), VIEW.findViewById(R.id.chkbDABNGTSintoma),
                VIEW.findViewById(R.id.chkbDABDGTSintoma))));

        hojaConsulta.setHepatomegalia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHepaSGTSintoma), VIEW.findViewById(R.id.chkbHepaNGTSintoma))));

        if(((CheckBox)VIEW.findViewById(R.id.chkbHepaSGTSintoma)).isChecked() &&
                !StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).getText().toString())) {
            hojaConsulta.setHepatomegaliaCm(Double.parseDouble(((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).getText().toString()));
        } else
            hojaConsulta.setHepatomegaliaCm(null);

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

    public void regresar() {
        getActivity().finish();
    }

    public void cargarDatos() {
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPocoAptSGTSintoma), VIEW.findViewById(R.id.chkbPocoAptNGTSintoma),
                VIEW.findViewById(R.id.chkbPocoAptDGTSintoma), ((HOJACONSULTA.getPocoApetito() != null)
                        ? HOJACONSULTA.getPocoApetito().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbNauseaSGTSintoma), VIEW.findViewById(R.id.chkbNauseaNGTSintoma),
                VIEW.findViewById(R.id.chkbNauseaDGTSintoma), ((HOJACONSULTA.getNausea() != null)
                        ? HOJACONSULTA.getNausea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDASGTSintoma), VIEW.findViewById(R.id.chkbDANGTSintoma),
                VIEW.findViewById(R.id.chkbDADGTSintoma), ((HOJACONSULTA.getDificultadAlimentarse() != null)
                        ? HOJACONSULTA.getDificultadAlimentarse().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbVomSGTSintoma), VIEW.findViewById(R.id.chkbVomNGTSintoma),
                VIEW.findViewById(R.id.chkbVomDGTSintoma), ((HOJACONSULTA.getVomito12horas() != null)
                        ? HOJACONSULTA.getVomito12horas().charAt(0) : '4'));

        if(HOJACONSULTA.getVomito12h() > 0 &&
                HOJACONSULTA.getVomito12horas().charAt(0) == '0') {
            ((EditText)VIEW.findViewById(R.id.edtxtVm12h)).setVisibility(View.VISIBLE);
            ((EditText)VIEW.findViewById(R.id.edtxtVm12h)).setText(((HOJACONSULTA.getVomito12h() != null)
                    ? HOJACONSULTA.getVomito12h().toString() : ""));
        }

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDiaSGTSintoma), VIEW.findViewById(R.id.chkbDiaNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaDGTSintoma), ((HOJACONSULTA.getDiarrea() != null)
                        ? HOJACONSULTA.getDiarrea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDiaSSGTSintoma), VIEW.findViewById(R.id.chkbDiaSNGTSintoma),
                VIEW.findViewById(R.id.chkbDiaSDGTSintoma), ((HOJACONSULTA.getDiarreaSangre() != null)
                        ? HOJACONSULTA.getDiarreaSangre().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEstSGTSintoma), VIEW.findViewById(R.id.chkbEstNGTSintoma),
                VIEW.findViewById(R.id.chkbEstDGTSintoma), ((HOJACONSULTA.getEstrenimiento() != null)
                        ? HOJACONSULTA.getEstrenimiento().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDaiSGTSintoma), VIEW.findViewById(R.id.chkbDaiNGTSintoma),
                VIEW.findViewById(R.id.chkbDaiDGTSintoma), ((HOJACONSULTA.getDolorAbIntermitente() != null)
                        ? HOJACONSULTA.getDolorAbIntermitente().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDacSGTSintoma), VIEW.findViewById(R.id.chkbDacNGTSintoma),
                VIEW.findViewById(R.id.chkbDacDGTSintoma), ((HOJACONSULTA.getDolorAbContinuo() != null)
                        ? HOJACONSULTA.getDolorAbContinuo().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEpiSGTSintoma), VIEW.findViewById(R.id.chkbEpiNGTSintoma),
                VIEW.findViewById(R.id.chkbEpiDGTSintoma), ((HOJACONSULTA.getEpigastralgia() != null)
                        ? HOJACONSULTA.getEpigastralgia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbInvSGTSintoma), VIEW.findViewById(R.id.chkbInvNGTSintoma),
                VIEW.findViewById(R.id.chkbInvDGTSintoma), ((HOJACONSULTA.getIntoleranciaOral() != null)
                        ? HOJACONSULTA.getIntoleranciaOral().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDABSGTSintoma), VIEW.findViewById(R.id.chkbDABNGTSintoma),
                VIEW.findViewById(R.id.chkbDABDGTSintoma), ((HOJACONSULTA.getDistensionAbdominal() != null)
                        ? HOJACONSULTA.getDistensionAbdominal().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHepaSGTSintoma), VIEW.findViewById(R.id.chkbHepaNGTSintoma),
                ((HOJACONSULTA.getHepatomegalia() != null)
                        ? HOJACONSULTA.getHepatomegalia().charAt(0) : '4'));

        if(HOJACONSULTA.getHepatomegalia() != null &&
                HOJACONSULTA.getHepatomegalia().charAt(0) == '0') {
            ((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).setVisibility(View.VISIBLE);
            ((EditText)VIEW.findViewById(R.id.edtxtHepaCmSintoma)).setText(((HOJACONSULTA.getHepatomegaliaCm() != null)
                    ? HOJACONSULTA.getHepatomegaliaCm().toString() : ""));
        }
    }

}
