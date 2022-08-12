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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RespiratorioSintomasFragment extends Fragment {
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
        VIEW = inflater.inflate(R.layout.activity_respiratorio_sintoma, container, false);
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

        Button btnRespiratorioGSintomas = (Button) view.findViewById(R.id.btnRespiratorioGSintomas);
        btnRespiratorioGSintomas.setOnClickListener(new View.OnClickListener() {
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

        ((EditText)VIEW.findViewById(R.id.dpNVF)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        cargarDatos();
    }

    public void inicializarContorles() {
        /*Tos*/
        View.OnClickListener onClickedTos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTOSEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbTOSSENSintoma)
                .setOnClickListener(onClickedTos);
        VIEW.findViewById(R.id.chkbTOSNENSintoma)
                .setOnClickListener(onClickedTos);
        VIEW.findViewById(R.id.chkbTOSDENSintoma)
                .setOnClickListener(onClickedTos);

        /*Rinorrea*/
        View.OnClickListener onClickedRinorrea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRNREN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRNRSENSintoma)
                .setOnClickListener(onClickedRinorrea);
        VIEW.findViewById(R.id.chkbRNRNENSintoma)
                .setOnClickListener(onClickedRinorrea);
        VIEW.findViewById(R.id.chkbRNRDENSintoma)
                .setOnClickListener(onClickedRinorrea);

        /*Congestion Nasal*/
        View.OnClickListener onClickedCongestionNasal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCGNEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbCGNSENSintoma)
                .setOnClickListener(onClickedCongestionNasal);
        VIEW.findViewById(R.id.chkbCGNNENSintoma)
                .setOnClickListener(onClickedCongestionNasal);
        VIEW.findViewById(R.id.chkbCGNDENSintoma)
                .setOnClickListener(onClickedCongestionNasal);

        /*Otalgia*/
        View.OnClickListener onClickedOtalgia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedOTGEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbOTGSENSintoma)
                .setOnClickListener(onClickedOtalgia);
        VIEW.findViewById(R.id.chkbOTGNENSintoma)
                .setOnClickListener(onClickedOtalgia);
        VIEW.findViewById(R.id.chkbOTGDENSintoma)
                .setOnClickListener(onClickedOtalgia);

        /*Aleteo Nasal*/
        View.OnClickListener onClickedAleteoNasal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedALNEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbALNSENSintoma)
                .setOnClickListener(onClickedAleteoNasal);
        VIEW.findViewById(R.id.chkbALNNENSintoma)
                .setOnClickListener(onClickedAleteoNasal);

        /*Apnea*/
        View.OnClickListener onClickedApnea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAPNEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbAPNSENSintoma)
                .setOnClickListener(onClickedApnea);
        VIEW.findViewById(R.id.chkbAPNNENSintoma)
                .setOnClickListener(onClickedApnea);

        /*Respiracion Rapida*/
        View.OnClickListener onClickedRespiracionRapida = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRSREN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRSRSENSintoma)
                .setOnClickListener(onClickedRespiracionRapida);
        VIEW.findViewById(R.id.chkbRSRNENSintoma)
                .setOnClickListener(onClickedRespiracionRapida);

        /*Quejido Espiratorio*/
        View.OnClickListener onClickedQuejidoEspiratorio = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedQJEEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbQJESENSintoma)
                .setOnClickListener(onClickedQuejidoEspiratorio);
        VIEW.findViewById(R.id.chkbQJENENSintoma)
                .setOnClickListener(onClickedQuejidoEspiratorio);

        /*Estridor Reposo*/
        View.OnClickListener onClickedEstridorReposo = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedESREN(view);
            }
        };

        VIEW.findViewById(R.id.chkbESRSENSintoma)
                .setOnClickListener(onClickedEstridorReposo);
        VIEW.findViewById(R.id.chkbESRNENSintoma)
                .setOnClickListener(onClickedEstridorReposo);

        /*Tiraje Subcostal*/
        View.OnClickListener onClickedTirajeSubCostal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTISEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbTISSENSintoma)
                .setOnClickListener(onClickedTirajeSubCostal);
        VIEW.findViewById(R.id.chkbTISNENSintoma)
                .setOnClickListener(onClickedTirajeSubCostal);

        /*Sibilancias*/
        View.OnClickListener onClickedSibilancias = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSIBEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbSIBSENSintoma)
                .setOnClickListener(onClickedSibilancias);
        VIEW.findViewById(R.id.chkbSIBNENSintoma)
                .setOnClickListener(onClickedSibilancias);

        /*Crepitos*/
        View.OnClickListener onClickedCrepitos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCEPEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbCEPSENSintoma)
                .setOnClickListener(onClickedCrepitos);
        VIEW.findViewById(R.id.chkbCEPNENSintoma)
                .setOnClickListener(onClickedCrepitos);

        /*Roncos*/
        View.OnClickListener onClickedRoncos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRNCEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbRNCSENSintoma)
                .setOnClickListener(onClickedRoncos);
        VIEW.findViewById(R.id.chkbRNCNENSintoma)
                .setOnClickListener(onClickedRoncos);

        /*Otra Fif*/
        View.OnClickListener onClickedOtraFif = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedOTFEN(view);
            }
        };

        VIEW.findViewById(R.id.chkbOTFSENSintoma)
                .setOnClickListener(onClickedOtraFif);
        VIEW.findViewById(R.id.chkbOTFNENSintoma)
                .setOnClickListener(onClickedOtraFif);
    }

    public void onChkboxClickedTOSEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbTOSSENSintoma), VIEW.findViewById(R.id.chkbTOSNENSintoma),
                VIEW.findViewById(R.id.chkbTOSDENSintoma), view);
    }

    public void onChkboxClickedRNREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRNRSENSintoma), VIEW.findViewById(R.id.chkbRNRNENSintoma),
                VIEW.findViewById(R.id.chkbRNRDENSintoma), view);
    }

    public void onChkboxClickedCGNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCGNSENSintoma), VIEW.findViewById(R.id.chkbCGNNENSintoma),
                VIEW.findViewById(R.id.chkbCGNDENSintoma), view);
    }

    public void onChkboxClickedOTGEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbOTGSENSintoma), VIEW.findViewById(R.id.chkbOTGNENSintoma),
                VIEW.findViewById(R.id.chkbOTGDENSintoma), view);
    }

    public void onChkboxClickedALNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbALNSENSintoma), VIEW.findViewById(R.id.chkbALNNENSintoma),
                view);
    }

    public void onChkboxClickedAPNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbAPNSENSintoma), VIEW.findViewById(R.id.chkbAPNNENSintoma),
                view);
    }

    public void onChkboxClickedRSREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRSRSENSintoma), VIEW.findViewById(R.id.chkbRSRNENSintoma),
                view);
    }

    public void onChkboxClickedQJEEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbQJESENSintoma), VIEW.findViewById(R.id.chkbQJENENSintoma),
                view);
    }

    public void onChkboxClickedESREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbESRSENSintoma), VIEW.findViewById(R.id.chkbESRNENSintoma),
                view);
    }

    public void onChkboxClickedTISEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbTISSENSintoma), VIEW.findViewById(R.id.chkbTISNENSintoma),
                view);
    }

    public void onChkboxClickedSIBEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbSIBSENSintoma), VIEW.findViewById(R.id.chkbSIBNENSintoma),
                view);
    }

    public void onChkboxClickedCEPEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCEPSENSintoma), VIEW.findViewById(R.id.chkbCEPNENSintoma),
                view);
    }

    public void onChkboxClickedRNCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRNCSENSintoma), VIEW.findViewById(R.id.chkbRNCNENSintoma),
                view);
    }

    public void onChkboxClickedOTFEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbOTFSENSintoma), VIEW.findViewById(R.id.chkbOTFNENSintoma),
                view);

        if(((CheckBox) VIEW.findViewById(R.id.chkbOTFSENSintoma)).isChecked()){
            VIEW.findViewById(R.id.txtvNVFENSintoma).setVisibility(View.VISIBLE);
            VIEW.findViewById(R.id.dpNVF).setVisibility(View.VISIBLE);

        }else{
            VIEW.findViewById(R.id.txtvNVFENSintoma).setVisibility(View.INVISIBLE);
            EditText etNF = ((EditText) VIEW.findViewById(R.id.dpNVF));
            etNF.setText(null);
            etNF.setVisibility(View.INVISIBLE);
        }
    }

    public void showDatePickerDialog() {
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
                    ((EditText)VIEW.findViewById(R.id.dpNVF)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpNVF)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpNVF)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpNVF)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpNVF)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbTOSNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRNRNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCGNNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOTGNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbALNNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAPNNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRSRNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbQJENENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbESRNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbTISNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbSIBNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCEPNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRNCNENSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOTFNENSintoma)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbTOSSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRNRSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCGNSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOTGSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbALNSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAPNSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRSRSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbQJESENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbESRSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbTISSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbSIBSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCEPSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRNCSENSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOTFSENSintoma)).setChecked(valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkbTOSDENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbRNRDENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCGNDENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbOTGDENSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_respiratorio_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_respiratorio_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        EditText dpNVF = (EditText) VIEW.findViewById(R.id.dpNVF);
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbTOSSENSintoma), VIEW.findViewById(R.id.chkbTOSNENSintoma),
                VIEW.findViewById(R.id.chkbTOSDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRNRSENSintoma), VIEW.findViewById(R.id.chkbRNRNENSintoma),
                VIEW.findViewById(R.id.chkbRNRDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCGNSENSintoma), VIEW.findViewById(R.id.chkbCGNNENSintoma),
                VIEW.findViewById(R.id.chkbCGNDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbOTGSENSintoma), VIEW.findViewById(R.id.chkbOTGNENSintoma),
                VIEW.findViewById(R.id.chkbOTGDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbALNSENSintoma), VIEW.findViewById(R.id.chkbALNNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbAPNSENSintoma), VIEW.findViewById(R.id.chkbAPNNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRSRSENSintoma), VIEW.findViewById(R.id.chkbRSRNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbQJESENSintoma), VIEW.findViewById(R.id.chkbQJENENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbESRSENSintoma), VIEW.findViewById(R.id.chkbESRNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbTISSENSintoma), VIEW.findViewById(R.id.chkbTISNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbSIBSENSintoma), VIEW.findViewById(R.id.chkbSIBNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCEPSENSintoma), VIEW.findViewById(R.id.chkbCEPNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRNCSENSintoma), VIEW.findViewById(R.id.chkbRNCNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) VIEW.findViewById(R.id.chkbOTFSENSintoma)).isChecked() && !((CheckBox) VIEW.findViewById(R.id.chkbOTFNENSintoma)).isChecked()) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (((CheckBox) VIEW.findViewById(R.id.chkbOTFSENSintoma)).isChecked() && StringUtils.isNullOrEmpty(dpNVF.getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_nueva_fif));
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
        hojaConsulta.setTos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbTOSSENSintoma),
                VIEW.findViewById(R.id.chkbTOSNENSintoma), VIEW.findViewById(R.id.chkbTOSDENSintoma))));

        hojaConsulta.setRinorrea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRNRSENSintoma),
                VIEW.findViewById(R.id.chkbRNRNENSintoma), VIEW.findViewById(R.id.chkbRNRNENSintoma))));

        hojaConsulta.setCongestionNasal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCGNSENSintoma),
                VIEW.findViewById(R.id.chkbCGNNENSintoma), VIEW.findViewById(R.id.chkbCGNDENSintoma))));

        hojaConsulta.setOtalgia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbOTGSENSintoma),
                VIEW.findViewById(R.id.chkbOTGNENSintoma), VIEW.findViewById(R.id.chkbOTGDENSintoma))));

        hojaConsulta.setAleteoNasal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbALNSENSintoma),
                VIEW.findViewById(R.id.chkbALNNENSintoma))));

        hojaConsulta.setApnea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbAPNSENSintoma),
                VIEW.findViewById(R.id.chkbAPNNENSintoma))));

        hojaConsulta.setRespiracionRapida(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRSRSENSintoma),
                VIEW.findViewById(R.id.chkbRSRNENSintoma))));

        hojaConsulta.setQuejidoEspiratorio(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbQJESENSintoma),
                VIEW.findViewById(R.id.chkbQJENENSintoma))));

        hojaConsulta.setEstiradorReposo(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbESRSENSintoma),
                VIEW.findViewById(R.id.chkbESRNENSintoma))));

        hojaConsulta.setTirajeSubcostal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbTISSENSintoma),
                VIEW.findViewById(R.id.chkbTISNENSintoma))));

        hojaConsulta.setSibilancias(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbSIBSENSintoma),
                VIEW.findViewById(R.id.chkbSIBNENSintoma))));

        hojaConsulta.setCrepitos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCEPSENSintoma),
                VIEW.findViewById(R.id.chkbCEPNENSintoma))));

        hojaConsulta.setRoncos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRNCSENSintoma),
                VIEW.findViewById(R.id.chkbRNCNENSintoma))));

        hojaConsulta.setOtraFif(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbOTFSENSintoma),
                VIEW.findViewById(R.id.chkbOTFNENSintoma))));

        if (VIEW.findViewById(R.id.dpNVF).getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpNVF)).getText().toString())) {
            hojaConsulta.setNuevaFif(((EditText) VIEW.findViewById(R.id.dpNVF)).getText().toString());
        } else {
            hojaConsulta.setNuevaFif(null);
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

    public void regresar() {
        getActivity().finish();
    }

    public void cargarDatos() {
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbTOSSENSintoma),
                VIEW.findViewById(R.id.chkbTOSNENSintoma), VIEW.findViewById(R.id.chkbTOSDENSintoma),
                ((HOJACONSULTA.getTos() != null) ? HOJACONSULTA.getTos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRNRSENSintoma),
                VIEW.findViewById(R.id.chkbRNRNENSintoma), VIEW.findViewById(R.id.chkbRNRNENSintoma),
                ((HOJACONSULTA.getRinorrea() != null) ? HOJACONSULTA.getRinorrea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCGNSENSintoma),
                VIEW.findViewById(R.id.chkbCGNNENSintoma), VIEW.findViewById(R.id.chkbCGNDENSintoma),
                ((HOJACONSULTA.getCongestionNasal() != null) ? HOJACONSULTA.getCongestionNasal().charAt(0)
                        : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbOTGSENSintoma),
                VIEW.findViewById(R.id.chkbOTGNENSintoma), VIEW.findViewById(R.id.chkbOTGDENSintoma),
                ((HOJACONSULTA.getOtalgia() != null) ? HOJACONSULTA.getOtalgia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbALNSENSintoma),
                VIEW.findViewById(R.id.chkbALNNENSintoma),
                ((HOJACONSULTA.getAleteoNasal() != null) ? HOJACONSULTA.getAleteoNasal().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbAPNSENSintoma),
                VIEW.findViewById(R.id.chkbAPNNENSintoma), ((HOJACONSULTA.getApnea() != null)
                        ? HOJACONSULTA.getApnea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRSRSENSintoma),
                VIEW.findViewById(R.id.chkbRSRNENSintoma), ((HOJACONSULTA.getRespiracionRapida() != null)
                        ? HOJACONSULTA.getRespiracionRapida().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbQJESENSintoma),
                VIEW.findViewById(R.id.chkbQJENENSintoma), ((HOJACONSULTA.getQuejidoEspiratorio() != null)
                        ? HOJACONSULTA.getQuejidoEspiratorio().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbESRSENSintoma),
                VIEW.findViewById(R.id.chkbESRNENSintoma), ((HOJACONSULTA.getEstiradorReposo() != null)
                        ? HOJACONSULTA.getEstiradorReposo().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbTISSENSintoma),
                VIEW.findViewById(R.id.chkbTISNENSintoma), ((HOJACONSULTA.getTirajeSubcostal() != null)
                        ? HOJACONSULTA.getTirajeSubcostal().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbSIBSENSintoma),
                VIEW.findViewById(R.id.chkbSIBNENSintoma), ((HOJACONSULTA.getSibilancias() != null)
                        ? HOJACONSULTA.getSibilancias().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCEPSENSintoma),
                VIEW.findViewById(R.id.chkbCEPNENSintoma), ((HOJACONSULTA.getCrepitos() != null)
                        ? HOJACONSULTA.getCrepitos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRNCSENSintoma),
                VIEW.findViewById(R.id.chkbRNCNENSintoma), ((HOJACONSULTA.getRoncos() != null)
                        ? HOJACONSULTA.getRoncos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbOTFSENSintoma),
                VIEW.findViewById(R.id.chkbOTFNENSintoma), ((HOJACONSULTA.getOtraFif() != null)
                        ? HOJACONSULTA.getOtraFif().charAt(0) : '4'));

        if( !StringUtils.isNullOrEmpty(HOJACONSULTA.getNuevaFif()) ) {
            ((EditText) VIEW.findViewById(R.id.dpNVF)).setText((HOJACONSULTA.getNuevaFif()));
        }

        if(!((CheckBox) VIEW.findViewById(R.id.chkbOTFSENSintoma)).isChecked()){
            VIEW.findViewById(R.id.txtvNVFENSintoma).setVisibility(View.INVISIBLE);
            VIEW.findViewById(R.id.dpNVF).setVisibility(View.INVISIBLE);
        }
    }
}
