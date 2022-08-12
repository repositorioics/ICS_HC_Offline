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

public class DeshidratacionSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNDHSintoma;
    private TextView viewTxtvSDHSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_deshidratacion_sintoma, container, false);
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

        Button btnDeshidratacionGSintomas = (Button) view.findViewById(R.id.btnDeshidratacionGSintomas);
        btnDeshidratacionGSintomas.setOnClickListener(new View.OnClickListener() {
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

        viewTxtvNDHSintoma = (TextView) VIEW.findViewById(R.id.txtvNDHSintoma);
        viewTxtvSDHSintoma = (TextView) VIEW.findViewById(R.id.txtvSDHSintoma);

        viewTxtvNDHSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSDHSintoma.setOnClickListener(new View.OnClickListener() {
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
        /*Lengua y Mucosa Seca*/
        View.OnClickListener onClickedLenguaMucosaSeca = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLMSDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbLMSSDHSintoma)
                .setOnClickListener(onClickedLenguaMucosaSeca);
        VIEW.findViewById(R.id.chkbLMSNDHSintoma)
                .setOnClickListener(onClickedLenguaMucosaSeca);

        /*Pliegue Cutaneo*/
        View.OnClickListener onClickedPliegeueCutaneo = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPGCDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbPGCSDHSintoma)
                .setOnClickListener(onClickedPliegeueCutaneo);
        VIEW.findViewById(R.id.chkbPGCNDHSintoma)
                .setOnClickListener(onClickedPliegeueCutaneo);

        /*Orina Reducida*/
        View.OnClickListener onClickedOrinaReducida = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedORRDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbORRSDHSintoma)
                .setOnClickListener(onClickedOrinaReducida);
        VIEW.findViewById(R.id.chkbORRNDHSintoma)
                .setOnClickListener(onClickedOrinaReducida);

        /*Bebe Avido con Sed*/
        View.OnClickListener onClickedBebeAvidoSed = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedBASDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbBASSDHSintoma)
                .setOnClickListener(onClickedBebeAvidoSed);
        VIEW.findViewById(R.id.chkbBASNDHSintoma)
                .setOnClickListener(onClickedBebeAvidoSed);

        /*Ojos Hundidos*/
        View.OnClickListener onClickedOjosHundidos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedOJHDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbOJHSDHSintoma)
                .setOnClickListener(onClickedOjosHundidos);
        VIEW.findViewById(R.id.chkbOJHNDHSintoma)
                .setOnClickListener(onClickedOjosHundidos);

        /*Fontanela Hundida*/
        View.OnClickListener onClickedFontanelaHundida = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedFTHDH(view);
            }
        };

        VIEW.findViewById(R.id.chkbFTHSDHSintoma)
                .setOnClickListener(onClickedFontanelaHundida);
        VIEW.findViewById(R.id.chkbFTHNDHSintoma)
                .setOnClickListener(onClickedFontanelaHundida);

    }

    public void onChkboxClickedLMSDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLMSSDHSintoma), VIEW.findViewById(R.id.chkbLMSNDHSintoma), view);
    }

    public void onChkboxClickedPGCDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPGCSDHSintoma), VIEW.findViewById(R.id.chkbPGCNDHSintoma), view);
    }

    public void onChkboxClickedORRDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbORRSDHSintoma), VIEW.findViewById(R.id.chkbORRNDHSintoma),
                VIEW.findViewById(R.id.chkbORRDDHSintoma), view);
    }

    public void onChkboxClickedBASDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbBASSDHSintoma), VIEW.findViewById(R.id.chkbBASNDHSintoma),
                view);
    }

    public void onChkboxClickedOJHDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbOJHSDHSintoma), VIEW.findViewById(R.id.chkbOJHNDHSintoma), view);
    }

    public void onChkboxClickedFTHDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbFTHSDHSintoma), VIEW.findViewById(R.id.chkbFTHNDHSintoma),
                view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbLMSNDHSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPGCNDHSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbORRNDHSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbBASNDHSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOJHNDHSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFTHNDHSintoma)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbLMSSDHSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPGCSDHSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbORRSDHSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbBASSDHSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOJHSDHSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFTHSDHSintoma)).setChecked(valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbORRDDHSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_deshid_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_deshid_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLMSSDHSintoma), VIEW.findViewById(R.id.chkbLMSNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPGCSDHSintoma), VIEW.findViewById(R.id.chkbPGCNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbORRSDHSintoma), VIEW.findViewById(R.id.chkbORRNDHSintoma), VIEW.findViewById(R.id.chkbORRDDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbBASSDHSintoma), VIEW.findViewById(R.id.chkbBASNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbOJHSDHSintoma), VIEW.findViewById(R.id.chkbOJHNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbFTHSDHSintoma), VIEW.findViewById(R.id.chkbFTHNDHSintoma))) {
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
        hojaConsulta.setLenguaMucosasSecas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLMSSDHSintoma), VIEW.findViewById(R.id.chkbLMSNDHSintoma))));

        hojaConsulta.setPliegueCutaneo(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPGCSDHSintoma), VIEW.findViewById(R.id.chkbPGCNDHSintoma))));

        hojaConsulta.setOrinaReducida(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbORRSDHSintoma), VIEW.findViewById(R.id.chkbORRNDHSintoma),
                VIEW.findViewById(R.id.chkbORRDDHSintoma))));

        hojaConsulta.setBebeConSed(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbBASSDHSintoma), VIEW.findViewById(R.id.chkbBASNDHSintoma))));

        hojaConsulta.setOjosHundidos(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbOJHSDHSintoma), VIEW.findViewById(R.id.chkbOJHNDHSintoma)).toString());

        hojaConsulta.setFontanelaHundida(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbFTHSDHSintoma), VIEW.findViewById(R.id.chkbFTHNDHSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLMSSDHSintoma), VIEW.findViewById(R.id.chkbLMSNDHSintoma),
                ((HOJACONSULTA.getLenguaMucosasSecas() != null)
                        ? HOJACONSULTA.getLenguaMucosasSecas().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPGCSDHSintoma), VIEW.findViewById(R.id.chkbPGCNDHSintoma),
                ((HOJACONSULTA.getPliegueCutaneo() != null)
                        ? HOJACONSULTA.getPliegueCutaneo().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbORRSDHSintoma), VIEW.findViewById(R.id.chkbORRNDHSintoma),
                VIEW.findViewById(R.id.chkbORRDDHSintoma),
                ((HOJACONSULTA.getOrinaReducida() != null)
                        ? HOJACONSULTA.getOrinaReducida().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbBASSDHSintoma), VIEW.findViewById(R.id.chkbBASNDHSintoma),
                ((HOJACONSULTA.getBebeConSed() != null)
                        ? HOJACONSULTA.getBebeConSed().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbOJHSDHSintoma),
                VIEW.findViewById(R.id.chkbOJHNDHSintoma),
                ((HOJACONSULTA.getOjosHundidos() != null)
                        ? HOJACONSULTA.getOjosHundidos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbFTHSDHSintoma), VIEW.findViewById(R.id.chkbFTHNDHSintoma),
                ((HOJACONSULTA.getFontanelaHundida() != null)
                        ? HOJACONSULTA.getFontanelaHundida().charAt(0) : '4'));
    }
}
