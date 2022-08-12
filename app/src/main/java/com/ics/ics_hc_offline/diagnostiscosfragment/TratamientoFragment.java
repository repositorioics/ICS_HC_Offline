package com.ics.ics_hc_offline.diagnostiscosfragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class TratamientoFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private ProgressDialog progress;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNo;
    private TextView viewTxtvSi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_diagnostico_tratamiento, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        progress = new ProgressDialog(CONTEXT);
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        EditText edtxtTratamientoPeso = (EditText) VIEW.findViewById(R.id.edtxtTratamientoPeso);
        EditText edtxtTratamientoTalla = (EditText) VIEW.findViewById(R.id.edtxtTratamientoTalla);
        edtxtTratamientoPeso.setText(String.valueOf(HOJACONSULTA.getPesoKg()));
        edtxtTratamientoTalla.setText(String.valueOf(HOJACONSULTA.getTallaCm()));

        Button btnTratamiento = (Button) VIEW.findViewById(R.id.btnTratamiento);
        btnTratamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    guardarDatos();
                } catch (Exception e) {
                    progress.dismiss();
                    if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
                    } else {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), getString(R.string.msj_error_no_controlado), getString(R.string.title_estudio_sostenible), null);
                    }
                    e.printStackTrace();
                }
            }
        });

        Button btnTratamiento2 = (Button) VIEW.findViewById(R.id.btnTratamiento2);
        btnTratamiento2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    guardarDatos();
                } catch (Exception e) {
                    progress.dismiss();
                    if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
                    } else {
                        MensajesHelper.mostrarMensajeInfo(VIEW.getContext(), getString(R.string.msj_error_no_controlado), getString(R.string.title_estudio_sostenible), null);
                    }
                    e.printStackTrace();
                }
            }
        });

        viewTxtvNo = (TextView) VIEW.findViewById(R.id.txtNo);
        viewTxtvSi = (TextView) VIEW.findViewById(R.id.txtvSi);

        viewTxtvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });
        scrolleablePlanes();
        cargarDatos();
    }

    public void regresar() {
        getActivity().finish();
    }

    // funcion para hacer el texto escroleable
    @SuppressLint("ClickableViewAccessibility")
    public void scrolleablePlanes() {
        EditText edtxtPlanes = (EditText) VIEW.findViewById(R.id.edtxtPlanes);
        edtxtPlanes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == R.id.edtxtPlanes) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
    }

    public void inicializarContorles() {
        //Acetaminofen
        View.OnClickListener onClickedAcetaminofen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAcetaminofen(view);
            }
        };

        VIEW.findViewById(R.id.chkIbAcetamenofenSi)
                .setOnClickListener(onClickedAcetaminofen);
        VIEW.findViewById(R.id.chkIbAcetamenofenNo)
                .setOnClickListener(onClickedAcetaminofen);

        //ASA
        View.OnClickListener onClickedAsa = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAsa(view);
            }
        };

        VIEW.findViewById(R.id.chkASASi)
                .setOnClickListener(onClickedAsa);
        VIEW.findViewById(R.id.chkASANo)
                .setOnClickListener(onClickedAsa);

        //Ibuprofen
        View.OnClickListener onClickedIbuprofen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedIbuprofen(view);
            }
        };

        VIEW.findViewById(R.id.chkIbuProfenSi)
                .setOnClickListener(onClickedIbuprofen);
        VIEW.findViewById(R.id.chkIbuprofenNo)
                .setOnClickListener(onClickedIbuprofen);

        //Penicilina
        View.OnClickListener onClickedPenicilina = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPenicilina(view);
            }
        };

        VIEW.findViewById(R.id.chkPenicilinaSi)
                .setOnClickListener(onClickedPenicilina);
        VIEW.findViewById(R.id.chkPenicilinaNo)
                .setOnClickListener(onClickedPenicilina);

        //Amoxicilina
        View.OnClickListener onClickedAmoxicilina = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAmoxicilina(view);
            }
        };

        VIEW.findViewById(R.id.chkAmoxicilinaSi)
                .setOnClickListener(onClickedAmoxicilina);
        VIEW.findViewById(R.id.chkAmoxicilinaNo)
                .setOnClickListener(onClickedAmoxicilina);

        //Dicloxacilina
        View.OnClickListener onClickedDicloxacilina = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDicloxacilina(view);
            }
        };

        VIEW.findViewById(R.id.chkDicloxacilinaSi)
                .setOnClickListener(onClickedDicloxacilina);
        VIEW.findViewById(R.id.chkDicloxacilinaNo)
                .setOnClickListener(onClickedDicloxacilina);

        //Otro
        View.OnClickListener onClickedOtro = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedOtro(view);
            }
        };

        VIEW.findViewById(R.id.chkOtrosSi)
                .setOnClickListener(onClickedOtro);
        VIEW.findViewById(R.id.chkOtrosNo)
                .setOnClickListener(onClickedOtro);

        //Furazolidona
        View.OnClickListener onClickedFurazolindona = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedFurazolidona(view);
            }
        };

        VIEW.findViewById(R.id.chkFurazolidonaSi)
                .setOnClickListener(onClickedFurazolindona);
        VIEW.findViewById(R.id.chkFurazolidonaNo)
                .setOnClickListener(onClickedFurazolindona);

        //Metronidazol/Tinidazol
        View.OnClickListener onClickedMetronidazol = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedMetronidazol(view);
            }
        };

        VIEW.findViewById(R.id.chkMetroTinidazolSi)
                .setOnClickListener(onClickedMetronidazol);
        VIEW.findViewById(R.id.chkMetroTinidazolNo)
                .setOnClickListener(onClickedMetronidazol);

        //Albendazol/Mebendazol
        View.OnClickListener onClickedAlbendazol = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedAlbendazol(view);
            }
        };

        VIEW.findViewById(R.id.chkAlbendazolMebendazolSi)
                .setOnClickListener(onClickedAlbendazol);
        VIEW.findViewById(R.id.chkAlbendazolMebendazolNo)
                .setOnClickListener(onClickedAlbendazol);

        //Sulfato Ferroso
        View.OnClickListener onClickedSulfatoFerroso = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSulfatoFerroso(view);
            }
        };

        VIEW.findViewById(R.id.chkSulfatoFerrosoSi)
                .setOnClickListener(onClickedSulfatoFerroso);
        VIEW.findViewById(R.id.chkSulfatoFerrosoNo)
                .setOnClickListener(onClickedSulfatoFerroso);

        //Suero Oral
        View.OnClickListener onClickedSueroOral = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSueroOral(view);
            }
        };

        VIEW.findViewById(R.id.chkSueroOralSi)
                .setOnClickListener(onClickedSueroOral);
        VIEW.findViewById(R.id.chkSueroOralNo)
                .setOnClickListener(onClickedSueroOral);

        //Sulfato de Zinc
        View.OnClickListener onClickedSulfatoZinc = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSulfatoZinc(view);
            }
        };

        VIEW.findViewById(R.id.chkSulfatoZincSi)
                .setOnClickListener(onClickedSulfatoZinc);
        VIEW.findViewById(R.id.chkSulfatoZincNo)
                .setOnClickListener(onClickedSulfatoZinc);

        //Liquidos IV
        View.OnClickListener onClickedLiquidosIv = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLiquidosIV(view);
            }
        };

        VIEW.findViewById(R.id.chkLiquidosIVSi)
                .setOnClickListener(onClickedLiquidosIv);
        VIEW.findViewById(R.id.chkLiquidosIVNo)
                .setOnClickListener(onClickedLiquidosIv);

        //Prednisona
        View.OnClickListener onClickedPrednisona = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPrednisona(view);
            }
        };

        VIEW.findViewById(R.id.chkPrednisonaSi)
                .setOnClickListener(onClickedPrednisona);
        VIEW.findViewById(R.id.chkPrednisonaNo)
                .setOnClickListener(onClickedPrednisona);

        //Hidrocortisona
        View.OnClickListener onClickedHidrocortisona = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHidrocortisona(view);
            }
        };

        VIEW.findViewById(R.id.chkHidrocortisonaSi)
                .setOnClickListener(onClickedHidrocortisona);
        VIEW.findViewById(R.id.chkHidrocortisonaNo)
                .setOnClickListener(onClickedHidrocortisona);

        //Salbutamol
        View.OnClickListener onClickedSalbutamol = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedSalbutamol(view);
            }
        };

        VIEW.findViewById(R.id.chkSalutamolSi)
                .setOnClickListener(onClickedSalbutamol);
        VIEW.findViewById(R.id.chkSalutamolNo)
                .setOnClickListener(onClickedSalbutamol);

        //Oseltamivir
        View.OnClickListener onClickedOseltamivir = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedOseltamivir(view);
            }
        };

        VIEW.findViewById(R.id.chkOseltamivirSi)
                .setOnClickListener(onClickedOseltamivir);
        VIEW.findViewById(R.id.chkOseltamivirNo)
                .setOnClickListener(onClickedOseltamivir);
    }

    public void onChkboxClickedAcetaminofen(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkIbAcetamenofenSi),
                VIEW.findViewById(R.id.chkIbAcetamenofenNo), view);
    }

    public void onChkboxClickedAsa(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkASASi),
                VIEW.findViewById(R.id.chkASANo), view);
    }

    public void onChkboxClickedIbuprofen(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkIbuProfenSi),
                VIEW.findViewById(R.id.chkIbuprofenNo), view);
    }

    public void onChkboxClickedPenicilina(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkPenicilinaSi),
                VIEW.findViewById(R.id.chkPenicilinaNo), view);
    }

    public void onChkboxClickedAmoxicilina(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkAmoxicilinaSi),
                VIEW.findViewById(R.id.chkAmoxicilinaNo), view);
    }

    public void onChkboxClickedDicloxacilina(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkDicloxacilinaSi),
                VIEW.findViewById(R.id.chkDicloxacilinaNo), view);
    }

    public void onChkboxClickedOtro(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkOtrosSi),
                VIEW.findViewById(R.id.chkOtrosNo), view);
        if(view.getId() == R.id.chkOtrosSi) {
            getActivity().findViewById(R.id.edtxtOtro).setEnabled(true);
        } else {
            ((EditText) getActivity().findViewById(R.id.edtxtOtro)).setText("");
            getActivity().findViewById(R.id.edtxtOtro).setEnabled(false);
        }
    }

    public void onChkboxClickedFurazolidona(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkFurazolidonaSi),
                VIEW.findViewById(R.id.chkFurazolidonaNo), view);
    }

    public void onChkboxClickedMetronidazol(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkMetroTinidazolSi),
                VIEW.findViewById(R.id.chkMetroTinidazolNo), view);
    }

    public void onChkboxClickedAlbendazol(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkAlbendazolMebendazolSi),
                VIEW.findViewById(R.id.chkAlbendazolMebendazolNo), view);
    }

    public void onChkboxClickedSulfatoFerroso(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkSulfatoFerrosoSi),
                VIEW.findViewById(R.id.chkSulfatoFerrosoNo), view);
    }

    public void onChkboxClickedSueroOral(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkSueroOralSi),
                VIEW.findViewById(R.id.chkSueroOralNo), view);
    }

    public void onChkboxClickedSulfatoZinc(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkSulfatoZincSi),
                VIEW.findViewById(R.id.chkSulfatoZincNo), view);
    }

    public void onChkboxClickedLiquidosIV(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkLiquidosIVSi),
                VIEW.findViewById(R.id.chkLiquidosIVNo), view);
    }

    public void onChkboxClickedPrednisona(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkPrednisonaSi),
                VIEW.findViewById(R.id.chkPrednisonaNo), view);
    }

    public void onChkboxClickedHidrocortisona(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkHidrocortisonaSi),
                VIEW.findViewById(R.id.chkHidrocortisonaNo), view);
    }

    public void onChkboxClickedSalbutamol(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkSalutamolSi),
                VIEW.findViewById(R.id.chkSalutamolNo), view);
    }

    public void onChkboxClickedOseltamivir(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkOseltamivirSi),
                VIEW.findViewById(R.id.chkOseltamivirNo), view);
    }

    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) VIEW.findViewById(R.id.chkIbAcetamenofenNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkASANo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkIbuprofenNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkPenicilinaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkAmoxicilinaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkDicloxacilinaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkOtrosNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkFurazolidonaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkMetroTinidazolNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkAlbendazolMebendazolNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSulfatoFerrosoNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSueroOralNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSulfatoZincNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkLiquidosIVNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkPrednisonaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkHidrocortisonaNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSalutamolNo)).setChecked(!valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkOseltamivirNo)).setChecked(!valor);

                            ((CheckBox) VIEW.findViewById(R.id.chkIbAcetamenofenSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkASASi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkIbuProfenSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkPenicilinaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkAmoxicilinaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkDicloxacilinaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkOtrosSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkFurazolidonaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkMetroTinidazolSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkAlbendazolMebendazolSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSulfatoFerrosoSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSueroOralSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSulfatoZincSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkLiquidosIVSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkPrednisonaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkHidrocortisonaSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkSalutamolSi)).setChecked(valor);
                            ((CheckBox) VIEW.findViewById(R.id.chkOseltamivirSi)).setChecked(valor);

                            if(((CheckBox) VIEW.findViewById(R.id.chkOtrosSi)).isChecked()){
                                VIEW.findViewById(R.id.edtxtOtro).setEnabled(true);
                            }
                            else {
                                ((EditText) VIEW.findViewById(R.id.edtxtOtro)).setText("");
                                VIEW.findViewById(R.id.edtxtOtro).setEnabled(false);
                            }

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_diagnostico_Tratamiento)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_diagnostico_Tratamiento)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkIbAcetamenofenSi), VIEW.findViewById(R.id.chkIbAcetamenofenNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkASASi), VIEW.findViewById(R.id.chkASANo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkIbuProfenSi), VIEW.findViewById(R.id.chkIbuprofenNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkPenicilinaSi), VIEW.findViewById(R.id.chkPenicilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkAmoxicilinaSi), VIEW.findViewById(R.id.chkAmoxicilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkDicloxacilinaSi), VIEW.findViewById(R.id.chkDicloxacilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkOtrosSi), VIEW.findViewById(R.id.chkOtrosNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkFurazolidonaSi), VIEW.findViewById(R.id.chkFurazolidonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkMetroTinidazolSi), VIEW.findViewById(R.id.chkMetroTinidazolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkAlbendazolMebendazolSi), VIEW.findViewById(R.id.chkAlbendazolMebendazolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkSulfatoFerrosoSi), VIEW.findViewById(R.id.chkSulfatoFerrosoNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkSueroOralSi), VIEW.findViewById(R.id.chkSueroOralNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkSulfatoZincSi), VIEW.findViewById(R.id.chkSulfatoZincNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkLiquidosIVSi), VIEW.findViewById(R.id.chkLiquidosIVNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkPrednisonaSi), VIEW.findViewById(R.id.chkPrednisonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkHidrocortisonaSi), VIEW.findViewById(R.id.chkHidrocortisonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkSalutamolSi), VIEW.findViewById(R.id.chkSalutamolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkOseltamivirSi), VIEW.findViewById(R.id.chkOseltamivirNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(((CheckBox) VIEW.findViewById(R.id.chkOtrosSi)).isChecked() && StringUtils.isNullOrEmpty( ((EditText) VIEW.findViewById(R.id.edtxtOtro)).getText().toString()) ){
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtPlanes)).getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_los_planes));
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
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        hojaConsulta.setAcetaminofen(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkIbAcetamenofenSi), VIEW.findViewById(R.id.chkIbAcetamenofenNo))));
        hojaConsulta.setAsa(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkASASi), VIEW.findViewById(R.id.chkASANo))));
        hojaConsulta.setIbuprofen(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkIbuProfenSi), VIEW.findViewById(R.id.chkIbuprofenNo))));
        hojaConsulta.setPenicilina(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkPenicilinaSi), VIEW.findViewById(R.id.chkPenicilinaNo))));
        hojaConsulta.setAmoxicilina(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkAmoxicilinaSi), VIEW.findViewById(R.id.chkAmoxicilinaNo))));
        hojaConsulta.setDicloxacilina(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkDicloxacilinaSi), VIEW.findViewById(R.id.chkDicloxacilinaNo))));
        hojaConsulta.setOtro(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkOtrosSi), VIEW.findViewById(R.id.chkOtrosNo))));
        hojaConsulta.setOtroAntibiotico(((EditText) VIEW.findViewById(R.id.edtxtOtro)).getText().toString()) ;
        hojaConsulta.setFurazolidona(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkFurazolidonaSi), VIEW.findViewById(R.id.chkFurazolidonaNo))));
        hojaConsulta.setMetronidazolTinidazol(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkMetroTinidazolSi), VIEW.findViewById(R.id.chkMetroTinidazolNo))));
        hojaConsulta.setAlbendazolMebendazol(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkAlbendazolMebendazolSi), VIEW.findViewById(R.id.chkAlbendazolMebendazolNo))));
        hojaConsulta.setSulfatoFerroso(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkSulfatoFerrosoSi), VIEW.findViewById(R.id.chkSulfatoFerrosoNo))));
        hojaConsulta.setSueroOral(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkSueroOralSi), VIEW.findViewById(R.id.chkSueroOralNo))));
        hojaConsulta.setSulfatoZinc(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkSulfatoZincSi), VIEW.findViewById(R.id.chkSulfatoZincNo))));
        hojaConsulta.setLiquidosIv(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkLiquidosIVSi), VIEW.findViewById(R.id.chkLiquidosIVNo))));
        hojaConsulta.setPrednisona(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkPrednisonaSi), VIEW.findViewById(R.id.chkPrednisonaNo))));
        hojaConsulta.setHidrocortisonaIv(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkHidrocortisonaSi), VIEW.findViewById(R.id.chkHidrocortisonaNo))));
        hojaConsulta.setSalbutamol(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkSalutamolSi), VIEW.findViewById(R.id.chkSalutamolNo))));
        hojaConsulta.setOseltamivir(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkOseltamivirSi), VIEW.findViewById(R.id.chkOseltamivirNo))));
        String planes = ((EditText) VIEW.findViewById(R.id.edtxtPlanes)).getText().toString();
        if (StringUtils.isNullOrEmpty(planes)) {
            Toast.makeText(getActivity(), "Intente nuevamente", Toast.LENGTH_LONG).show();
            return;
        } else {
            hojaConsulta.setPlanes(planes);
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
        if (HOJACONSULTA != null) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkIbAcetamenofenSi), VIEW.findViewById(R.id.chkIbAcetamenofenNo),
                    ((HOJACONSULTA.getAcetaminofen() != null)
                            ? HOJACONSULTA.getAcetaminofen().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkASASi), VIEW.findViewById(R.id.chkASANo),
                    ((HOJACONSULTA.getAsa() != null)
                            ? HOJACONSULTA.getAsa().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkIbuProfenSi), VIEW.findViewById(R.id.chkIbuprofenNo),
                    ((HOJACONSULTA.getIbuprofen() != null)
                            ? HOJACONSULTA.getIbuprofen().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkPenicilinaSi), VIEW.findViewById(R.id.chkPenicilinaNo),
                    ((HOJACONSULTA.getPenicilina() != null)
                            ? HOJACONSULTA.getPenicilina().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkAmoxicilinaSi), VIEW.findViewById(R.id.chkAmoxicilinaNo),
                    ((HOJACONSULTA.getAmoxicilina() != null)
                            ? HOJACONSULTA.getAmoxicilina().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkDicloxacilinaSi), VIEW.findViewById(R.id.chkDicloxacilinaNo),
                    ((HOJACONSULTA.getDicloxacilina() != null)
                            ? HOJACONSULTA.getDicloxacilina().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkOtrosSi), VIEW.findViewById(R.id.chkOtrosNo),
                    ((HOJACONSULTA.getOtro() != null)
                            ? HOJACONSULTA.getOtro().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkFurazolidonaSi), VIEW.findViewById(R.id.chkFurazolidonaNo),
                    ((HOJACONSULTA.getFurazolidona() != null)
                            ? HOJACONSULTA.getFurazolidona().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkMetroTinidazolSi), VIEW.findViewById(R.id.chkMetroTinidazolNo),
                    ((HOJACONSULTA.getMetronidazolTinidazol() != null)
                            ? HOJACONSULTA.getMetronidazolTinidazol().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkAlbendazolMebendazolSi), VIEW.findViewById(R.id.chkAlbendazolMebendazolNo),
                    ((HOJACONSULTA.getAlbendazolMebendazol() != null)
                            ? HOJACONSULTA.getAlbendazolMebendazol().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkSueroOralSi), VIEW.findViewById(R.id.chkSueroOralNo),
                    ((HOJACONSULTA.getSueroOral() != null)
                            ? HOJACONSULTA.getSueroOral().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkSulfatoFerrosoSi), VIEW.findViewById(R.id.chkSulfatoFerrosoNo),
                    ((HOJACONSULTA.getSulfatoFerroso() != null)
                            ? HOJACONSULTA.getSulfatoFerroso().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkSulfatoZincSi), VIEW.findViewById(R.id.chkSulfatoZincNo),
                    ((HOJACONSULTA.getSulfatoZinc() != null)
                            ? HOJACONSULTA.getSulfatoZinc().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkLiquidosIVSi), VIEW.findViewById(R.id.chkLiquidosIVNo),
                    ((HOJACONSULTA.getLiquidosIv() != null)
                            ? HOJACONSULTA.getLiquidosIv().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkPrednisonaSi), VIEW.findViewById(R.id.chkPrednisonaNo),
                    ((HOJACONSULTA.getPrednisona() != null)
                            ? HOJACONSULTA.getPrednisona().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkHidrocortisonaSi), VIEW.findViewById(R.id.chkHidrocortisonaNo),
                    ((HOJACONSULTA.getHidrocortisonaIv() != null)
                            ? HOJACONSULTA.getHidrocortisonaIv().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkSalutamolSi), VIEW.findViewById(R.id.chkSalutamolNo),
                    ((HOJACONSULTA.getSalbutamol() != null)
                            ? HOJACONSULTA.getSalbutamol().charAt(0) : '4'));

            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkOseltamivirSi), VIEW.findViewById(R.id.chkOseltamivirNo),
                    ((HOJACONSULTA.getOseltamivir() != null)
                            ? HOJACONSULTA.getOseltamivir().charAt(0) : '4'));

            if(((CheckBox) VIEW.findViewById(R.id.chkOtrosNo)).isChecked()) {
                ((EditText) VIEW.findViewById(R.id.edtxtOtro)).setText("");
                VIEW.findViewById(R.id.edtxtOtro).setEnabled(false);
            }else {
                ((EditText) VIEW.findViewById(R.id.edtxtOtro)).setText(HOJACONSULTA.getOtroAntibiotico());
            }
            ((EditText)VIEW.findViewById(R.id.edtxtPlanes)).setText(HOJACONSULTA.getPlanes());
        }
    }
}
