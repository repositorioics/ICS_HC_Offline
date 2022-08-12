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

public class GargantaSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNGGSintoma;
    private TextView viewTxtvSGGSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_garganta_sintoma, container, false);
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

        Button btnGargantaGSintomas = (Button) view.findViewById(R.id.btnGargantaGSintomas);
        btnGargantaGSintomas.setOnClickListener(new View.OnClickListener() {
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

        viewTxtvNGGSintoma = (TextView) VIEW.findViewById(R.id.txtvNGGSintoma);
        viewTxtvSGGSintoma = (TextView) VIEW.findViewById(R.id.txtvSGGSintoma);

        viewTxtvNGGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSGGSintoma.setOnClickListener(new View.OnClickListener() {
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
        /*Eritema*/
        View.OnClickListener onClickedEritema = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedERTGG(view);
            }
        };

        VIEW.findViewById(R.id.chkbERTSGGSintoma)
                .setOnClickListener(onClickedEritema);
        VIEW.findViewById(R.id.chkbERTNGGSintoma)
                .setOnClickListener(onClickedEritema);

        /*Dolor de Garganta*/
        View.OnClickListener onClickedDolorGarganta = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDGGGG(view);
            }
        };

        VIEW.findViewById(R.id.chkbDGGSGGSintoma)
                .setOnClickListener(onClickedDolorGarganta);
        VIEW.findViewById(R.id.chkbDGGNGGSintoma)
                .setOnClickListener(onClickedDolorGarganta);
        VIEW.findViewById(R.id.chkbDGGDGGSintoma)
                .setOnClickListener(onClickedDolorGarganta);

        /*Adenopatias Cevicales*/
        View.OnClickListener onClickedAdenoPatiasCerv = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedADCGG(view);
            }
        };

        VIEW.findViewById(R.id.chkbADCSGGSintoma)
                .setOnClickListener(onClickedAdenoPatiasCerv);
        VIEW.findViewById(R.id.chkbADCNGGSintoma)
                .setOnClickListener(onClickedAdenoPatiasCerv);

        /*Exudado*/
        View.OnClickListener onClickedExudado = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEXDGG(view);
            }
        };

        VIEW.findViewById(R.id.chkbEXDSGGSintoma)
                .setOnClickListener(onClickedExudado);
        VIEW.findViewById(R.id.chkbEXDNGGSintoma)
                .setOnClickListener(onClickedExudado);

        /*Petequias en Mucosa*/
        View.OnClickListener onClickedPetequiasMucosa = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPIMGG(view);
            }
        };

        VIEW.findViewById(R.id.chkbPIMSGGSintoma)
                .setOnClickListener(onClickedPetequiasMucosa);
        VIEW.findViewById(R.id.chkbPIMNGGSintoma)
                .setOnClickListener(onClickedPetequiasMucosa);
    }

    public void onChkboxClickedERTGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbERTSGGSintoma), VIEW.findViewById(R.id.chkbERTNGGSintoma),
                view);
    }

    public void onChkboxClickedDGGGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDGGSGGSintoma), VIEW.findViewById(R.id.chkbDGGNGGSintoma),
                VIEW.findViewById(R.id.chkbDGGDGGSintoma), view);
    }

    public void onChkboxClickedADCGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbADCSGGSintoma), VIEW.findViewById(R.id.chkbADCNGGSintoma),
                view);
    }

    public void onChkboxClickedEXDGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEXDSGGSintoma), VIEW.findViewById(R.id.chkbEXDNGGSintoma),
                view);
    }

    public void onChkboxClickedPIMGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPIMSGGSintoma), VIEW.findViewById(R.id.chkbPIMNGGSintoma),
                view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbERTNGGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDGGNGGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbADCNGGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEXDNGGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPIMNGGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbERTSGGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDGGSGGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbADCSGGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEXDSGGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPIMSGGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDGGDGGSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_garganta_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_garganta_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbERTSGGSintoma), VIEW.findViewById(R.id.chkbERTNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDGGSGGSintoma), VIEW.findViewById(R.id.chkbDGGNGGSintoma),
                VIEW.findViewById(R.id.chkbDGGDGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbADCSGGSintoma), VIEW.findViewById(R.id.chkbADCNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEXDSGGSintoma), VIEW.findViewById(R.id.chkbEXDNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPIMSGGSintoma), VIEW.findViewById(R.id.chkbPIMNGGSintoma))) {
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
        hojaConsulta.setEritema(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbERTSGGSintoma),
                VIEW.findViewById(R.id.chkbERTNGGSintoma))));

        hojaConsulta.setDolorGarganta(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDGGSGGSintoma),
                VIEW.findViewById(R.id.chkbDGGNGGSintoma), VIEW.findViewById(R.id.chkbDGGNGGSintoma))));

        hojaConsulta.setAdenopatiasCervicales(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbADCSGGSintoma),
                VIEW.findViewById(R.id.chkbADCNGGSintoma))));

        hojaConsulta.setExudado(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEXDSGGSintoma),
                VIEW.findViewById(R.id.chkbEXDNGGSintoma))));

        hojaConsulta.setPetequiasMucosa(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPIMSGGSintoma),
                VIEW.findViewById(R.id.chkbPIMNGGSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbERTSGGSintoma),
                VIEW.findViewById(R.id.chkbERTNGGSintoma), ((HOJACONSULTA.getEritema() != null)
                        ? HOJACONSULTA.getEritema().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDGGSGGSintoma),
                VIEW.findViewById(R.id.chkbDGGNGGSintoma), VIEW.findViewById(R.id.chkbDGGNGGSintoma),
                ((HOJACONSULTA.getDolorGarganta() != null) ? HOJACONSULTA.getDolorGarganta().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbADCSGGSintoma),
                VIEW.findViewById(R.id.chkbADCNGGSintoma), ((HOJACONSULTA.getAdenopatiasCervicales() != null)
                        ? HOJACONSULTA.getAdenopatiasCervicales().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEXDSGGSintoma),
                VIEW.findViewById(R.id.chkbEXDNGGSintoma), ((HOJACONSULTA.getExudado() != null)
                        ? HOJACONSULTA.getExudado().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPIMSGGSintoma),
                VIEW.findViewById(R.id.chkbPIMNGGSintoma), ((HOJACONSULTA.getPetequiasMucosa() != null)
                        ? HOJACONSULTA.getPetequiasMucosa().charAt(0) : '4'));
    }
}
