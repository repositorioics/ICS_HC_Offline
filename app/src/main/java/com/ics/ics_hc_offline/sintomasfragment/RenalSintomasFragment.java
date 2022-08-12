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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.utils.AndroidUtils;

public class RenalSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNRNSintoma;
    private TextView viewTxtvSRNSintoma;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_renal_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        Button btnRenalGSintomas = (Button) view.findViewById(R.id.btnRenalGSintomas);
        btnRenalGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    guardarDatos();
                }catch (Exception e) {
                    if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
                    } else {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), getString(R.string.msj_error_no_controlado), getString(R.string.title_estudio_sostenible), null);
                    }
                    e.printStackTrace();
                }
            }
        });

        viewTxtvNRNSintoma = (TextView) VIEW.findViewById(R.id.txtvNRNSintoma);
        viewTxtvSRNSintoma = (TextView) VIEW.findViewById(R.id.txtvSRNSintoma);

        viewTxtvNRNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSRNSintoma.setOnClickListener(new View.OnClickListener() {
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
        /*Sintomas Urinarios*/
        View.OnClickListener onClickedSintomasUrinarios = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSNURN(view);
            }
        };

        VIEW.findViewById(R.id.chkbSNUSRNSintoma)
                .setOnClickListener(onClickedSintomasUrinarios);
        VIEW.findViewById(R.id.chkbSNUNRNSintoma)
                .setOnClickListener(onClickedSintomasUrinarios);
        VIEW.findViewById(R.id.chkbSNUDRNSintoma)
                .setOnClickListener(onClickedSintomasUrinarios);

        /*Leucocituria >=10 x campo*/
        View.OnClickListener onClickedLeucocituria = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLEURN(view);
            }
        };

        VIEW.findViewById(R.id.chkbLEUSRNSintoma)
                .setOnClickListener(onClickedLeucocituria);
        VIEW.findViewById(R.id.chkbLEUNRNSintoma)
                .setOnClickListener(onClickedLeucocituria);
        VIEW.findViewById(R.id.chkbLEUDRNSintoma)
                .setOnClickListener(onClickedLeucocituria);

        /*Nitritos*/
        View.OnClickListener onClickedNitritos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedNITRN(view);
            }
        };

        VIEW.findViewById(R.id.chkbNITSRNSintoma)
                .setOnClickListener(onClickedNitritos);
        VIEW.findViewById(R.id.chkbNITNRNSintoma)
                .setOnClickListener(onClickedNitritos);
        VIEW.findViewById(R.id.chkbNITDRNSintoma)
                .setOnClickListener(onClickedNitritos);

        /*Eritrocitos >=6 x campo*/
        View.OnClickListener onClickedEritrocitos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedERTRN(view);
            }
        };

        VIEW.findViewById(R.id.chkbERTSRNSintoma)
                .setOnClickListener(onClickedEritrocitos);
        VIEW.findViewById(R.id.chkbERTNRNSintoma)
                .setOnClickListener(onClickedEritrocitos);
        VIEW.findViewById(R.id.chkbERTDRNSintoma)
                .setOnClickListener(onClickedEritrocitos);

        /*Bilirrubinuria*/
        View.OnClickListener onClickedBilirrubinuria = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedBLRRN(view);
            }
        };

        VIEW.findViewById(R.id.chkbBLRSRNSintoma)
                .setOnClickListener(onClickedBilirrubinuria);
        VIEW.findViewById(R.id.chkbBLRNRNSintoma)
                .setOnClickListener(onClickedBilirrubinuria);
        VIEW.findViewById(R.id.chkbBLRDRNSintoma)
                .setOnClickListener(onClickedBilirrubinuria);
    }

    public void onChkboxClickedSNURN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbSNUSRNSintoma), VIEW.findViewById(R.id.chkbSNUNRNSintoma),
                VIEW.findViewById(R.id.chkbSNUDRNSintoma), view);
    }

    public void onChkboxClickedLEURN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLEUSRNSintoma), VIEW.findViewById(R.id.chkbLEUNRNSintoma),
                VIEW.findViewById(R.id.chkbLEUDRNSintoma), view);
    }

    public void onChkboxClickedNITRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbNITSRNSintoma), VIEW.findViewById(R.id.chkbNITNRNSintoma),
                VIEW.findViewById(R.id.chkbNITDRNSintoma), view);
    }

    public void onChkboxClickedERTRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbERTSRNSintoma), VIEW.findViewById(R.id.chkbERTNRNSintoma),
                VIEW.findViewById(R.id.chkbERTDRNSintoma), view);
    }

    public void onChkboxClickedBLRRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbBLRSRNSintoma), VIEW.findViewById(R.id.chkbBLRNRNSintoma),
                VIEW.findViewById(R.id.chkbBLRDRNSintoma), view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbSNUNRNSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLEUNRNSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNITNRNSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbERTNRNSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbBLRNRNSintoma)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbSNUSRNSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLEUSRNSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNITSRNSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbERTSRNSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbBLRSRNSintoma)).setChecked(valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbSNUDRNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLEUDRNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbNITDRNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbERTDRNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbBLRDRNSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_renal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_renal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo para validar los campos requeridos
     * @throws Exception
     */
    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbSNUSRNSintoma), VIEW.findViewById(R.id.chkbSNUNRNSintoma),
                VIEW.findViewById(R.id.chkbSNUDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLEUSRNSintoma), VIEW.findViewById(R.id.chkbLEUNRNSintoma),
                VIEW.findViewById(R.id.chkbLEUDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbNITSRNSintoma), VIEW.findViewById(R.id.chkbNITNRNSintoma),
                VIEW.findViewById(R.id.chkbNITDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbERTSRNSintoma), VIEW.findViewById(R.id.chkbERTNRNSintoma),
                VIEW.findViewById(R.id.chkbERTDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbBLRSRNSintoma), VIEW.findViewById(R.id.chkbBLRNRNSintoma),
                VIEW.findViewById(R.id.chkbBLRDRNSintoma))) {
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
        hojaConsulta.setSintomasUrinarios(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbSNUSRNSintoma),
                VIEW.findViewById(R.id.chkbSNUNRNSintoma), VIEW.findViewById(R.id.chkbSNUDRNSintoma))));

        hojaConsulta.setLeucocituria(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLEUSRNSintoma),
                VIEW.findViewById(R.id.chkbLEUNRNSintoma), VIEW.findViewById(R.id.chkbLEUNRNSintoma))));

        hojaConsulta.setNitritos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbNITSRNSintoma),
                VIEW.findViewById(R.id.chkbNITNRNSintoma), VIEW.findViewById(R.id.chkbNITDRNSintoma))));

        hojaConsulta.setEritrocitos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbERTSRNSintoma),
                VIEW.findViewById(R.id.chkbERTNRNSintoma), VIEW.findViewById(R.id.chkbERTDRNSintoma))));

        hojaConsulta.setBilirrubinuria(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbBLRSRNSintoma),
                VIEW.findViewById(R.id.chkbBLRNRNSintoma), VIEW.findViewById(R.id.chkbBLRDRNSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbSNUSRNSintoma),
                VIEW.findViewById(R.id.chkbSNUNRNSintoma), VIEW.findViewById(R.id.chkbSNUDRNSintoma),
                ((HOJACONSULTA.getSintomasUrinarios() != null)
                        ? HOJACONSULTA.getSintomasUrinarios().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLEUSRNSintoma),
                VIEW.findViewById(R.id.chkbLEUNRNSintoma), VIEW.findViewById(R.id.chkbLEUDRNSintoma),
                ((HOJACONSULTA.getLeucocituria() != null)
                        ? HOJACONSULTA.getLeucocituria().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbNITSRNSintoma),
                VIEW.findViewById(R.id.chkbNITNRNSintoma), VIEW.findViewById(R.id.chkbNITDRNSintoma),
                ((HOJACONSULTA.getNitritos() != null)
                        ? HOJACONSULTA.getNitritos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbERTSRNSintoma),
                VIEW.findViewById(R.id.chkbERTNRNSintoma), VIEW.findViewById(R.id.chkbERTDRNSintoma),
                ((HOJACONSULTA.getEritrocitos() != null)
                        ? HOJACONSULTA.getEritrocitos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbBLRSRNSintoma),
                VIEW.findViewById(R.id.chkbBLRNRNSintoma), VIEW.findViewById(R.id.chkbBLRDRNSintoma),
                ((HOJACONSULTA.getBilirrubinuria() != null)
                        ? HOJACONSULTA.getBilirrubinuria().charAt(0) : '4'));
    }
}
