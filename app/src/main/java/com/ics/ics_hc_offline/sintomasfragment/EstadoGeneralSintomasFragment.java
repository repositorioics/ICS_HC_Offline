package com.ics.ics_hc_offline.sintomasfragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.ics.ics_hc_offline.consulta.CSintomasTabFragment;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.sintomasactivities.EstadoGeneralSintomasActivity;
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

public class EstadoGeneralSintomasFragment extends Fragment {

    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNEGSintoma;
    private TextView viewTxtvSEGSintoma;

    public EstadoGeneralSintomasFragment() {}

    public static EstadoGeneralSintomasFragment newInstance() {
        return new EstadoGeneralSintomasFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VIEW = inflater.inflate(R.layout.activity_estado_general_sintoma, container, false);
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

        viewTxtvNEGSintoma = (TextView) VIEW.findViewById(R.id.txtvNEGSintoma);
        viewTxtvSEGSintoma = (TextView) VIEW.findViewById(R.id.txtvSEGSintoma);

        viewTxtvNEGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSEGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });

        Button btnEstadoGeneralGSintomas = (Button) view.findViewById(R.id.btnEstadoGeneralGSintomas);
        btnEstadoGeneralGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    validarFiebre();
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
        cargarDatos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void regresar() {
        getActivity().finish();
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkbFiebreNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAstnNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMalEstNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPerdConsNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbInqIrriNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbConvulNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHipoNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLetarNEGSintoma)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFiebreSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAstnSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMalEstSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPerdConsSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbInqIrriSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbConvulSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHipoSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLetarSEGSintoma)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkbFiebreDEGSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbAstnDEGSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_estado_general_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_estado_general_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo para inicializar todos los controles de interfaz de la pantalla.
     */
    public void inicializarContorles() {

        /*Fiebre*/
        View.OnClickListener onClickedFiebre = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedFiebreEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbFiebreSEGSintoma)
                .setOnClickListener(onClickedFiebre);
        VIEW.findViewById(R.id.chkbFiebreNEGSintoma)
                .setOnClickListener(onClickedFiebre);
        VIEW.findViewById(R.id.chkbFiebreDEGSintoma)
                .setOnClickListener(onClickedFiebre);

        /*Astenia*/
        View.OnClickListener onClickedAstenia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAstnEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbAstnSEGSintoma)
                .setOnClickListener(onClickedAstenia);
        VIEW.findViewById(R.id.chkbAstnNEGSintoma)
                .setOnClickListener(onClickedAstenia);
        VIEW.findViewById(R.id.chkbAstnDEGSintoma)
                .setOnClickListener(onClickedAstenia);

        /*Anormalmente Somnoliento*/
        View.OnClickListener onClickedAnormalSomnoliento = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAnormSomEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma)
                .setOnClickListener(onClickedAnormalSomnoliento);
        VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma)
                .setOnClickListener(onClickedAnormalSomnoliento);

        /*Mal estado General*/
        View.OnClickListener onClickedMalEstadoGeneral = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedMalEstGEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbMalEstSEGSintoma)
                .setOnClickListener(onClickedMalEstadoGeneral);
        VIEW.findViewById(R.id.chkbMalEstNEGSintoma)
                .setOnClickListener(onClickedMalEstadoGeneral);

        /*Perdida Consciencia*/
        View.OnClickListener onClickedPerdidaConsciencia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPerdConsEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbPerdConsSEGSintoma)
                .setOnClickListener(onClickedPerdidaConsciencia);
        VIEW.findViewById(R.id.chkbPerdConsNEGSintoma)
                .setOnClickListener(onClickedPerdidaConsciencia);

        /*Inquite Irritable*/
        View.OnClickListener onClickedInquietoIrritable = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedInqIrriEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbInqIrriSEGSintoma)
                .setOnClickListener(onClickedInquietoIrritable);
        VIEW.findViewById(R.id.chkbInqIrriNEGSintoma)
                .setOnClickListener(onClickedInquietoIrritable);

        /*Convulsiones*/
        View.OnClickListener onClickedConvulsiones = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedConvulEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbConvulSEGSintoma)
                .setOnClickListener(onClickedConvulsiones);
        VIEW.findViewById(R.id.chkbConvulNEGSintoma)
                .setOnClickListener(onClickedConvulsiones);

        /*Hiportemia*/
        View.OnClickListener onClickedHipotermia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHipoEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbHipoSEGSintoma)
                .setOnClickListener(onClickedHipotermia);
        VIEW.findViewById(R.id.chkbHipoNEGSintoma)
                .setOnClickListener(onClickedHipotermia);

        /*Letargia*/
        View.OnClickListener onClickedLetargia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLetarEG(view);
            }
        };
        VIEW.findViewById(R.id.chkbLetarSEGSintoma)
                .setOnClickListener(onClickedLetargia);
        VIEW.findViewById(R.id.chkbLetarNEGSintoma)
                .setOnClickListener(onClickedLetargia);
    }

    public void onChkboxClickedFiebreEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbFiebreSEGSintoma), VIEW.findViewById(R.id.chkbFiebreNEGSintoma),
                VIEW.findViewById(R.id.chkbFiebreDEGSintoma), view);
    }

    public void onChkboxClickedAstnEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbAstnSEGSintoma), VIEW.findViewById(R.id.chkbAstnNEGSintoma),
                VIEW.findViewById(R.id.chkbAstnDEGSintoma), view);
    }

    public void onChkboxClickedAnormSomEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma), VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma),
                view);
    }

    public void onChkboxClickedMalEstGEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbMalEstSEGSintoma), VIEW.findViewById(R.id.chkbMalEstNEGSintoma),
                view);
    }

    public void onChkboxClickedPerdConsEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPerdConsSEGSintoma), VIEW.findViewById(R.id.chkbPerdConsNEGSintoma),
                view);
    }

    public void onChkboxClickedInqIrriEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbInqIrriSEGSintoma), VIEW.findViewById(R.id.chkbInqIrriNEGSintoma),
                view);
    }

    public void onChkboxClickedConvulEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbConvulSEGSintoma), VIEW.findViewById(R.id.chkbConvulNEGSintoma),
                view);
    }

    public void onChkboxClickedHipoEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHipoSEGSintoma), VIEW.findViewById(R.id.chkbHipoNEGSintoma),
                view);
    }

    public void onChkboxClickedLetarEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLetarSEGSintoma), VIEW.findViewById(R.id.chkbLetarNEGSintoma),
                view);
    }

    /***
     * Metodo para validar los campos requeridos
     * @throws Exception
     */
    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbFiebreSEGSintoma), VIEW.findViewById(R.id.chkbFiebreNEGSintoma),
                VIEW.findViewById(R.id.chkbFiebreDEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbAstnSEGSintoma), VIEW.findViewById(R.id.chkbAstnNEGSintoma),
                VIEW.findViewById(R.id.chkbAstnDEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma), VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbMalEstSEGSintoma), VIEW.findViewById(R.id.chkbMalEstNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPerdConsSEGSintoma), VIEW.findViewById(R.id.chkbPerdConsNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbInqIrriSEGSintoma), VIEW.findViewById(R.id.chkbInqIrriNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbConvulSEGSintoma), VIEW.findViewById(R.id.chkbConvulNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHipoSEGSintoma), VIEW.findViewById(R.id.chkbHipoNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLetarSEGSintoma), VIEW.findViewById(R.id.chkbLetarNEGSintoma))) {
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
        hojaConsulta.setFiebre(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbFiebreSEGSintoma), VIEW.findViewById(R.id.chkbFiebreNEGSintoma),
                VIEW.findViewById(R.id.chkbFiebreDEGSintoma))));

        hojaConsulta.setAstenia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbAstnSEGSintoma), VIEW.findViewById(R.id.chkbAstnNEGSintoma),
                VIEW.findViewById(R.id.chkbAstnDEGSintoma))));

        hojaConsulta.setAsomnoliento(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma), VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma))));

        hojaConsulta.setMalEstado(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbMalEstSEGSintoma), VIEW.findViewById(R.id.chkbMalEstNEGSintoma))));

        hojaConsulta.setPerdidaConsciencia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPerdConsSEGSintoma), VIEW.findViewById(R.id.chkbPerdConsNEGSintoma))));

        hojaConsulta.setInquieto(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbInqIrriSEGSintoma), VIEW.findViewById(R.id.chkbInqIrriNEGSintoma))));

        hojaConsulta.setConvulsiones(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbConvulSEGSintoma), VIEW.findViewById(R.id.chkbConvulNEGSintoma))));

        hojaConsulta.setHipotermia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHipoSEGSintoma), VIEW.findViewById(R.id.chkbHipoNEGSintoma))));

        hojaConsulta.setLetargia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLetarSEGSintoma), VIEW.findViewById(R.id.chkbLetarNEGSintoma))));

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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbFiebreSEGSintoma), VIEW.findViewById(R.id.chkbFiebreNEGSintoma),
                VIEW.findViewById(R.id.chkbFiebreDEGSintoma), ((HOJACONSULTA.getFiebre() != null)
                        ? HOJACONSULTA.getFiebre().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbAstnSEGSintoma), VIEW.findViewById(R.id.chkbAstnNEGSintoma),
                VIEW.findViewById(R.id.chkbAstnDEGSintoma), ((HOJACONSULTA.getAstenia() != null)
                        ? HOJACONSULTA.getAstenia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbAnormSomnSEGSintoma),
                VIEW.findViewById(R.id.chkbAnormSomnNEGSintoma), ((HOJACONSULTA.getAsomnoliento() != null)
                        ? HOJACONSULTA.getAsomnoliento().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbMalEstSEGSintoma),
                VIEW.findViewById(R.id.chkbMalEstNEGSintoma), ((HOJACONSULTA.getMalEstado() != null)
                        ? HOJACONSULTA.getMalEstado().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPerdConsSEGSintoma),
                VIEW.findViewById(R.id.chkbPerdConsNEGSintoma), ((HOJACONSULTA.getPerdidaConsciencia() != null)
                        ? HOJACONSULTA.getPerdidaConsciencia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbInqIrriSEGSintoma),
                VIEW.findViewById(R.id.chkbInqIrriNEGSintoma), ((HOJACONSULTA.getInquieto() != null)
                        ? HOJACONSULTA.getInquieto().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbConvulSEGSintoma),
                VIEW.findViewById(R.id.chkbConvulNEGSintoma), ((HOJACONSULTA.getConvulsiones() != null)
                        ? HOJACONSULTA.getInquieto().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHipoSEGSintoma),
                VIEW.findViewById(R.id.chkbHipoNEGSintoma), ((HOJACONSULTA.getHipotermia() != null)
                        ? HOJACONSULTA.getHipotermia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLetarSEGSintoma),
                VIEW.findViewById(R.id.chkbLetarNEGSintoma), ((HOJACONSULTA.getLetargia() != null)
                        ? HOJACONSULTA.getLetargia().charAt(0) : '4'));
    }

    public void validarFiebre() throws Exception {
        if (HOJACONSULTA != null) {
            String fif = HOJACONSULTA.getFif() != null ? HOJACONSULTA.getFif() : null;
            String consulta = HOJACONSULTA.getConsulta() != null ? HOJACONSULTA.getConsulta() : null;
            Double temperaturaMedico = HOJACONSULTA.getTemMedc();

            if (consulta != null) {
                /*Validar si es consulta Inicial*/
                if (consulta.trim().equals("Inicial")) {
                    /*Validar fiebre si presenta fif*/
                    boolean presentaFiebre = ((CheckBox) VIEW.findViewById(R.id.chkbFiebreSEGSintoma)).isChecked();
                    //if (fif != null && !fif.trim().equals("null") && !presentaFiebre) {
                    if (!StringUtils.isNullOrEmpty(fif) && !presentaFiebre) {
                        throw new Exception("Debe marcar fiebre debido a que se ingreso la FIF");
                    }
                }
                /*Validar si es consulta Inicial ó Seguimiento*/
                if (consulta.trim().equals("Inicial") || consulta.trim().equals("Seguimiento")) {
                    /*Validar fiebre con temperatura medico*/
                    if (temperaturaMedico > 0) {
                        boolean presentaFiebre = ((CheckBox) VIEW.findViewById(R.id.chkbFiebreSEGSintoma)).isChecked();
                        double tempMedc = Double.parseDouble(temperaturaMedico.toString());
                        if (tempMedc >= 37.8 && !presentaFiebre) {
                            throw new Exception("Debe marcar fiebre debido a que el paciente presenta una temperatura de: " + tempMedc);
                        }
                    }
                }
            }
        }
    }
}
