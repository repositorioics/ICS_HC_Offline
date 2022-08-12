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

public class CabezaSintomaFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNCBSintoma;
    private TextView viewTxtvSCBSintoma;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_cabeza_sintoma, container, false);
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

        Button btnCabezaGSintomas = (Button) view.findViewById(R.id.btnCabezaGSintomas);
        btnCabezaGSintomas.setOnClickListener(new View.OnClickListener() {
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

        viewTxtvNCBSintoma = (TextView) VIEW.findViewById(R.id.txtvNCBSintoma);
        viewTxtvSCBSintoma = (TextView) VIEW.findViewById(R.id.txtvSCBSintoma);

        viewTxtvNCBSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSCBSintoma.setOnClickListener(new View.OnClickListener() {
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
        /*Cefalea*/
        View.OnClickListener onClickedCefalea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCAFCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbCAFSCBSintoma)
                .setOnClickListener(onClickedCefalea);
        VIEW.findViewById(R.id.chkbCAFNCBSintoma)
                .setOnClickListener(onClickedCefalea);
        VIEW.findViewById(R.id.chkbCAFDCBSintoma)
                .setOnClickListener(onClickedCefalea);

        /*Rigidez de cuello*/
        View.OnClickListener onClickedRigidezCuello = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRGCCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbRGCSCBSintoma)
                .setOnClickListener(onClickedRigidezCuello);
        VIEW.findViewById(R.id.chkbRGCNCBSintoma)
                .setOnClickListener(onClickedRigidezCuello);

        /*Inyeccion conjuntival*/
        View.OnClickListener onClickedInyeccionConjun = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedINCCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbINCSCBSintoma)
                .setOnClickListener(onClickedInyeccionConjun);
        VIEW.findViewById(R.id.chkbINCNCBSintoma)
                .setOnClickListener(onClickedInyeccionConjun);

        /*Hemorragia suconjuntival*/
        View.OnClickListener onClickedHemorragiaSubConjun = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHSCCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbHSCSCBSintoma)
                .setOnClickListener(onClickedHemorragiaSubConjun);
        VIEW.findViewById(R.id.chkbHSCNCBSintoma)
                .setOnClickListener(onClickedHemorragiaSubConjun);

        /*Dolor Retrocular*/
        View.OnClickListener onClickedDolorRetrocular = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDROCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbDROSCBSintoma)
                .setOnClickListener(onClickedDolorRetrocular);
        VIEW.findViewById(R.id.chkbDRONCBSintoma)
                .setOnClickListener(onClickedDolorRetrocular);
        VIEW.findViewById(R.id.chkbDRODCBSintoma)
                .setOnClickListener(onClickedDolorRetrocular);

        /*Fontanela Abombada*/
        View.OnClickListener onClickedFontanelaAbomb = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedFTACB(view);
            }
        };

        VIEW.findViewById(R.id.chkbFTASCBSintoma)
                .setOnClickListener(onClickedFontanelaAbomb);
        VIEW.findViewById(R.id.chkbFTANCBSintoma)
                .setOnClickListener(onClickedFontanelaAbomb);

        /*Ictericia Conjuntival*/
        View.OnClickListener onClickedIctericiaConjun = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedITCCB(view);
            }
        };

        VIEW.findViewById(R.id.chkbITCSCBSintoma)
                .setOnClickListener(onClickedIctericiaConjun);
        VIEW.findViewById(R.id.chkbITCNCBSintoma)
                .setOnClickListener(onClickedIctericiaConjun);
    }

    public void onChkboxClickedCAFCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCAFSCBSintoma), VIEW.findViewById(R.id.chkbCAFNCBSintoma),
               VIEW.findViewById(R.id.chkbCAFDCBSintoma), view);
    }

    public void onChkboxClickedRGCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRGCSCBSintoma), VIEW.findViewById(R.id.chkbRGCNCBSintoma), view);
    }

    public void onChkboxClickedINCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbINCSCBSintoma), VIEW.findViewById(R.id.chkbINCNCBSintoma),
                view);
    }

    public void onChkboxClickedHSCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHSCSCBSintoma), VIEW.findViewById(R.id.chkbHSCNCBSintoma),
                view);
    }

    public void onChkboxClickedDROCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDROSCBSintoma), VIEW.findViewById(R.id.chkbDRONCBSintoma),
                VIEW.findViewById(R.id.chkbDRODCBSintoma), view);
    }

    public void onChkboxClickedFTACB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbFTASCBSintoma), VIEW.findViewById(R.id.chkbFTANCBSintoma),
                view);
    }

    public void onChkboxClickedITCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbITCSCBSintoma), VIEW.findViewById(R.id.chkbITCNCBSintoma),
                view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbCAFNCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRGCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbINCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHSCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDRONCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFTANCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbITCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCAFSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRGCSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbINCSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHSCSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDROSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFTASCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbITCSCBSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCAFDCBSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDRODCBSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_cabez_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_cabez_sintomas)), getResources().getString(
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
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCAFSCBSintoma), VIEW.findViewById(R.id.chkbCAFNCBSintoma),
                VIEW.findViewById(R.id.chkbCAFDCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRGCSCBSintoma), VIEW.findViewById(R.id.chkbRGCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbINCSCBSintoma), VIEW.findViewById(R.id.chkbINCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHSCSCBSintoma), VIEW.findViewById(R.id.chkbHSCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDROSCBSintoma), VIEW.findViewById(R.id.chkbDRONCBSintoma),
                VIEW.findViewById(R.id.chkbDRODCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbFTASCBSintoma), VIEW.findViewById(R.id.chkbFTANCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbITCSCBSintoma), VIEW.findViewById(R.id.chkbITCNCBSintoma))) {
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
        hojaConsulta.setCefalea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCAFSCBSintoma), VIEW.findViewById(R.id.chkbCAFNCBSintoma),
                VIEW.findViewById(R.id.chkbCAFDCBSintoma))));

        hojaConsulta.setRigidezCuello(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRGCSCBSintoma), VIEW.findViewById(R.id.chkbRGCNCBSintoma))));

        hojaConsulta.setInyeccionConjuntival(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbINCSCBSintoma), VIEW.findViewById(R.id.chkbINCNCBSintoma))));

        hojaConsulta.setHemorragiaSuconjuntival(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHSCSCBSintoma), VIEW.findViewById(R.id.chkbHSCNCBSintoma))));

        hojaConsulta.setDolorRetroocular(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDROSCBSintoma), VIEW.findViewById(R.id.chkbDRONCBSintoma),
                VIEW.findViewById(R.id.chkbDRODCBSintoma))));

        hojaConsulta.setFontanelaAbombada(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbFTASCBSintoma), VIEW.findViewById(R.id.chkbFTANCBSintoma))));

        hojaConsulta.setIctericiaConuntival(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbITCSCBSintoma), VIEW.findViewById(R.id.chkbITCNCBSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCAFSCBSintoma), VIEW.findViewById(R.id.chkbCAFNCBSintoma),
                VIEW.findViewById(R.id.chkbCAFDCBSintoma), ((HOJACONSULTA.getCefalea() != null)
                        ? HOJACONSULTA.getCefalea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRGCSCBSintoma), VIEW.findViewById(R.id.chkbRGCNCBSintoma),
                ((HOJACONSULTA.getRigidezCuello() != null)
                        ? HOJACONSULTA.getRigidezCuello().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbINCSCBSintoma), VIEW.findViewById(R.id.chkbINCNCBSintoma),
                ((HOJACONSULTA.getInyeccionConjuntival() != null)
                        ? HOJACONSULTA.getInyeccionConjuntival().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHSCSCBSintoma), VIEW.findViewById(R.id.chkbHSCNCBSintoma),
                ((HOJACONSULTA.getHemorragiaSuconjuntival() != null)
                        ? HOJACONSULTA.getHemorragiaSuconjuntival().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDROSCBSintoma), VIEW.findViewById(R.id.chkbDRONCBSintoma),
                VIEW.findViewById(R.id.chkbDRODCBSintoma), ((HOJACONSULTA.getDolorRetroocular() != null)
                        ? HOJACONSULTA.getDolorRetroocular().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbFTASCBSintoma), VIEW.findViewById(R.id.chkbFTANCBSintoma),
                ((HOJACONSULTA.getFontanelaAbombada() != null)
                        ? HOJACONSULTA.getFontanelaAbombada().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbITCSCBSintoma), VIEW.findViewById(R.id.chkbITCNCBSintoma),
                ((HOJACONSULTA.getIctericiaConuntival() != null)
                        ? HOJACONSULTA.getIctericiaConuntival().charAt(0) : '4'));
    }
}
