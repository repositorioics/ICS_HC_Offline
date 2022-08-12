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

public class OsteomuscularSintomasFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvNSintoma;
    private TextView viewTxtvSSintoma;
    private TextView viewTxtvDSintoma;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_osteomuscular_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);

        Button btnOsteomuscularGSintomas = (Button) view.findViewById(R.id.btnOsteomuscularGSintomas);
        btnOsteomuscularGSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    validarArtralgia();
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

        viewTxtvNSintoma = (TextView) VIEW.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintoma = (TextView) VIEW.findViewById(R.id.txtvSSintoma);
        viewTxtvDSintoma = (TextView) VIEW.findViewById(R.id.txtvDSintoma);

        viewTxtvNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        viewTxtvDSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 3);
            }
        });
        cargarDatos();
    }

    public void regresar() {
        getActivity().finish();
    }

    public void inicializarContorles() {
        /*Artralgia*/
        View.OnClickListener onClickedArtralgia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedARTOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbARTSOTSintoma)
                .setOnClickListener(onClickedArtralgia);
        VIEW.findViewById(R.id.chkbARTNOTSintoma)
                .setOnClickListener(onClickedArtralgia);
        VIEW.findViewById(R.id.chkbARTDOTSintoma)
                .setOnClickListener(onClickedArtralgia);

        /*Mialgia*/
        View.OnClickListener onClickedMialgia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedMIAOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbMIASOTSintoma)
                .setOnClickListener(onClickedMialgia);
        VIEW.findViewById(R.id.chkbMIANOTSintoma)
                .setOnClickListener(onClickedMialgia);
        VIEW.findViewById(R.id.chkbMIADOTSintoma)
                .setOnClickListener(onClickedMialgia);

        /*Lumbalgia*/
        View.OnClickListener onClickedLumbalgia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLUMOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbLUMSOTSintoma)
                .setOnClickListener(onClickedLumbalgia);
        VIEW.findViewById(R.id.chkbLUMNOTSintoma)
                .setOnClickListener(onClickedLumbalgia);
        VIEW.findViewById(R.id.chkbLUMDOTSintoma)
                .setOnClickListener(onClickedLumbalgia);

        /*Dolor Cuello*/
        View.OnClickListener onClickedDolorCuello = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedDOCOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbDOCSOTSintoma)
                .setOnClickListener(onClickedDolorCuello);
        VIEW.findViewById(R.id.chkbDOCNOTSintoma)
                .setOnClickListener(onClickedDolorCuello);
        VIEW.findViewById(R.id.chkbDOCDOTSintoma)
                .setOnClickListener(onClickedDolorCuello);

        /*Tenosinovitis*/
        View.OnClickListener onClickedTenosinovitis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTNSOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbTNSSOTSintoma)
                .setOnClickListener(onClickedTenosinovitis);
        VIEW.findViewById(R.id.chkbTNSNOTSintoma)
                .setOnClickListener(onClickedTenosinovitis);
        VIEW.findViewById(R.id.chkbTNSDOTSintoma)
                .setOnClickListener(onClickedTenosinovitis);

        /*Artralgia Proximal*/
        View.OnClickListener onClickedArtProximal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedARPOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbARPSOTSintoma)
                .setOnClickListener(onClickedArtProximal);
        VIEW.findViewById(R.id.chkbARPNOTSintoma)
                .setOnClickListener(onClickedArtProximal);
        VIEW.findViewById(R.id.chkbARPDOTSintoma)
                .setOnClickListener(onClickedArtProximal);

        /*Artralgia Distal*/
        View.OnClickListener onClickedArtDistal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedARDOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbARDSOTSintoma)
                .setOnClickListener(onClickedArtDistal);
        VIEW.findViewById(R.id.chkbARDNOTSintoma)
                .setOnClickListener(onClickedArtDistal);
        VIEW.findViewById(R.id.chkbARDDOTSintoma)
                .setOnClickListener(onClickedArtDistal);

        /*Conjuntuvitis*/
        View.OnClickListener onClickedConjuntivitis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCNJOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbCNJSOTSintoma)
                .setOnClickListener(onClickedConjuntivitis);
        VIEW.findViewById(R.id.chkbCNJNOTSintoma)
                .setOnClickListener(onClickedConjuntivitis);
        VIEW.findViewById(R.id.chkbCNJDOTSintoma)
                .setOnClickListener(onClickedConjuntivitis);

        /*Edema Nuñecas*/
        View.OnClickListener onClickedEdemaMunecas = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEDMOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEDMSOTSintoma)
                .setOnClickListener(onClickedEdemaMunecas);
        VIEW.findViewById(R.id.chkbEDMNOTSintoma)
                .setOnClickListener(onClickedEdemaMunecas);
        VIEW.findViewById(R.id.chkbEDMDOTSintoma)
                .setOnClickListener(onClickedEdemaMunecas);

        /*Edema Codos*/
        View.OnClickListener onClickedEdemaCodos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEDCOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEDCSOTSintoma)
                .setOnClickListener(onClickedEdemaCodos);
        VIEW.findViewById(R.id.chkbEDCNOTSintoma)
                .setOnClickListener(onClickedEdemaCodos);
        VIEW.findViewById(R.id.chkbEDCDOTSintoma)
                .setOnClickListener(onClickedEdemaCodos);

        /*Edema Hombros*/
        View.OnClickListener onClickedEdemaHombros = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEDHOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEDHSOTSintoma)
                .setOnClickListener(onClickedEdemaHombros);
        VIEW.findViewById(R.id.chkbEDHNOTSintoma)
                .setOnClickListener(onClickedEdemaHombros);
        VIEW.findViewById(R.id.chkbEDHDOTSintoma)
                .setOnClickListener(onClickedEdemaHombros);

        /*Edema Rodillas*/
        View.OnClickListener onClickedEdemaRodillas = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEDROT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEDRSOTSintoma)
                .setOnClickListener(onClickedEdemaRodillas);
        VIEW.findViewById(R.id.chkbEDRNOTSintoma)
                .setOnClickListener(onClickedEdemaRodillas);
        VIEW.findViewById(R.id.chkbEDRDOTSintoma)
                .setOnClickListener(onClickedEdemaRodillas);

        /*Edema Tobillos*/
        View.OnClickListener onClickedEdemaTobillos = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEDTOT(view);
            }
        };

        VIEW.findViewById(R.id.chkbEDTSOTSintoma)
                .setOnClickListener(onClickedEdemaTobillos);
        VIEW.findViewById(R.id.chkbEDTNOTSintoma)
                .setOnClickListener(onClickedEdemaTobillos);
        VIEW.findViewById(R.id.chkbEDTDOTSintoma)
                .setOnClickListener(onClickedEdemaTobillos);
    }

    public void onChkboxClickedARTOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbARTSOTSintoma), VIEW.findViewById(R.id.chkbARTNOTSintoma),
                VIEW.findViewById(R.id.chkbARTDOTSintoma), view);
    }

    public void onChkboxClickedMIAOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbMIASOTSintoma), VIEW.findViewById(R.id.chkbMIANOTSintoma),
                VIEW.findViewById(R.id.chkbMIADOTSintoma), view);
    }

    public void onChkboxClickedLUMOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLUMSOTSintoma), VIEW.findViewById(R.id.chkbLUMNOTSintoma),
                VIEW.findViewById(R.id.chkbLUMDOTSintoma), view);
    }

    public void onChkboxClickedDOCOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbDOCSOTSintoma), VIEW.findViewById(R.id.chkbDOCNOTSintoma),
                VIEW.findViewById(R.id.chkbDOCDOTSintoma), view);
    }

    public void onChkboxClickedTNSOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbTNSSOTSintoma), VIEW.findViewById(R.id.chkbTNSNOTSintoma),
                VIEW.findViewById(R.id.chkbTNSDOTSintoma), view);
    }

    public void onChkboxClickedARPOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbARPSOTSintoma), VIEW.findViewById(R.id.chkbARPNOTSintoma),
                VIEW.findViewById(R.id.chkbARPDOTSintoma), view);
    }

    public void onChkboxClickedARDOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbARDSOTSintoma), VIEW.findViewById(R.id.chkbARDNOTSintoma),
                VIEW.findViewById(R.id.chkbARDDOTSintoma), view);
    }

    public void onChkboxClickedCNJOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCNJSOTSintoma), VIEW.findViewById(R.id.chkbCNJNOTSintoma),
                VIEW.findViewById(R.id.chkbCNJDOTSintoma), view);
    }

    public void onChkboxClickedEDMOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEDMSOTSintoma), VIEW.findViewById(R.id.chkbEDMNOTSintoma),
                VIEW.findViewById(R.id.chkbEDMDOTSintoma), view);
    }

    public void onChkboxClickedEDCOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEDCSOTSintoma), VIEW.findViewById(R.id.chkbEDCNOTSintoma),
                VIEW.findViewById(R.id.chkbEDCDOTSintoma), view);
    }

    public void onChkboxClickedEDHOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEDHSOTSintoma), VIEW.findViewById(R.id.chkbEDHNOTSintoma),
                VIEW.findViewById(R.id.chkbEDHDOTSintoma), view);
    }

    public void onChkboxClickedEDROT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEDRSOTSintoma), VIEW.findViewById(R.id.chkbEDRNOTSintoma),
                VIEW.findViewById(R.id.chkbEDRDOTSintoma), view);
    }

    public void onChkboxClickedEDTOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEDTSOTSintoma), VIEW.findViewById(R.id.chkbEDTNOTSintoma),
                VIEW.findViewById(R.id.chkbEDTDOTSintoma), view);
    }

    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbARTSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARTNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARTDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMIASOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMIANOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMIADOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLUMSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLUMNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLUMDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDOCSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDOCNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbDOCDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbTNSSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbTNSNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbTNSDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARPSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARPNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARPDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARDSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARDNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbARDDOTSintoma)).setChecked(false);

                            ((CheckBox) VIEW.findViewById(R.id.chkbCNJSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDMSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDCSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDHSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDRSOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDTSOTSintoma)).setChecked(false);

                            ((CheckBox) VIEW.findViewById(R.id.chkbCNJNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDMNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDCNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDHNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDRNOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDTNOTSintoma)).setChecked(false);

                            ((CheckBox) VIEW.findViewById(R.id.chkbCNJDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDMDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDCDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDHDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDRDOTSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbEDTDOTSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbARTSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbMIASOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbLUMSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbDOCSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbTNSSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbARPSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbARDSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbCNJSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEDMSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEDCSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEDHSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEDRSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEDTSOTSintoma)).setChecked(true);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbARTNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbMIANOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbLUMNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbDOCNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbTNSNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbARPNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbCNJNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEDMNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEDCNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEDHNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEDRNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEDTNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbARDNOTSintoma)).setChecked(true);

                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbARTDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbMIADOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbLUMDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbDOCDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbTNSDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbARPDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbARDDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbCNJDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbEDMDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbEDCDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbEDHDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbEDRDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) VIEW.findViewById(R.id.chkbEDTDOTSintoma)).setChecked(true);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_osteomuscular_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_osteomuscular_sintomas));
            if(valor==3) mensaje = String.format(getResources().getString(R.string.msg_change_desc), getResources().getString(R.string.boton_osteomuscular_sintomas));


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
        if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbARTSOTSintoma), VIEW.findViewById(R.id.chkbARTNOTSintoma),
                VIEW.findViewById(R.id.chkbARTDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbMIASOTSintoma), VIEW.findViewById(R.id.chkbMIANOTSintoma),
                VIEW.findViewById(R.id.chkbMIADOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLUMSOTSintoma), VIEW.findViewById(R.id.chkbLUMNOTSintoma),
                VIEW.findViewById(R.id.chkbLUMDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbDOCSOTSintoma), VIEW.findViewById(R.id.chkbDOCNOTSintoma),
                VIEW.findViewById(R.id.chkbDOCDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbTNSSOTSintoma), VIEW.findViewById(R.id.chkbTNSNOTSintoma),
                VIEW.findViewById(R.id.chkbTNSDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbARPSOTSintoma), VIEW.findViewById(R.id.chkbARPNOTSintoma),
                VIEW.findViewById(R.id.chkbARPDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbARDSOTSintoma), VIEW.findViewById(R.id.chkbARDNOTSintoma),
                VIEW.findViewById(R.id.chkbARDDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCNJSOTSintoma), VIEW.findViewById(R.id.chkbCNJNOTSintoma),
                VIEW.findViewById(R.id.chkbCNJDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEDMSOTSintoma), VIEW.findViewById(R.id.chkbEDMNOTSintoma),
                VIEW.findViewById(R.id.chkbEDMDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEDCSOTSintoma), VIEW.findViewById(R.id.chkbEDCNOTSintoma),
                VIEW.findViewById(R.id.chkbEDCDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEDHSOTSintoma), VIEW.findViewById(R.id.chkbEDHNOTSintoma),
                VIEW.findViewById(R.id.chkbEDHDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEDRSOTSintoma), VIEW.findViewById(R.id.chkbEDRNOTSintoma),
                VIEW.findViewById(R.id.chkbEDRDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEDTSOTSintoma), VIEW.findViewById(R.id.chkbEDTNOTSintoma),
                VIEW.findViewById(R.id.chkbEDTDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public void validarArtralgia() throws Exception {
        boolean existeArtralgia = ((CheckBox) VIEW.findViewById(R.id.chkbARTSOTSintoma)).isChecked();
        boolean artralgiaProximal = ((CheckBox) VIEW.findViewById(R.id.chkbARPSOTSintoma)).isChecked();
        boolean artralgiaDistal = ((CheckBox) VIEW.findViewById(R.id.chkbARDSOTSintoma)).isChecked();
        if (existeArtralgia) {
            if (!artralgiaProximal && !artralgiaDistal) {
                throw new Exception("Tiene que marcar Artralgia Proximal ó Artralgia Distal, " +
                        " debido a que el paciente presenta una Artralgia");
            }
        }
        if (artralgiaProximal || artralgiaDistal) {
            if (!existeArtralgia) {
                throw new Exception("Para poder guardar Artralgia Proximal ó Artralgia Distal, " +
                        " tiene que marcar Artralgia Si");
            }
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
        hojaConsulta.setAltralgia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbARTSOTSintoma), VIEW.findViewById(R.id.chkbARTNOTSintoma),
                VIEW.findViewById(R.id.chkbARTDOTSintoma))));

        hojaConsulta.setMialgia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbMIASOTSintoma), VIEW.findViewById(R.id.chkbMIANOTSintoma),
                VIEW.findViewById(R.id.chkbMIADOTSintoma))));

        hojaConsulta.setLumbalgia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLUMSOTSintoma), VIEW.findViewById(R.id.chkbLUMNOTSintoma),
                VIEW.findViewById(R.id.chkbLUMDOTSintoma))));

        hojaConsulta.setDolorCuello(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbDOCSOTSintoma), VIEW.findViewById(R.id.chkbDOCNOTSintoma),
                VIEW.findViewById(R.id.chkbDOCDOTSintoma))));

        hojaConsulta.setTenosinovitis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbTNSSOTSintoma), VIEW.findViewById(R.id.chkbTNSNOTSintoma),
                VIEW.findViewById(R.id.chkbTNSDOTSintoma))));

        hojaConsulta.setArtralgiaProximal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbARPSOTSintoma), VIEW.findViewById(R.id.chkbARPNOTSintoma),
                VIEW.findViewById(R.id.chkbARPDOTSintoma))));

        hojaConsulta.setArtralgiaDistal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbARDSOTSintoma), VIEW.findViewById(R.id.chkbARDNOTSintoma),
                VIEW.findViewById(R.id.chkbARDDOTSintoma))));

        hojaConsulta.setConjuntivitis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCNJSOTSintoma), VIEW.findViewById(R.id.chkbCNJNOTSintoma),
                VIEW.findViewById(R.id.chkbCNJDOTSintoma))));

        hojaConsulta.setEdemaMunecas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEDMSOTSintoma), VIEW.findViewById(R.id.chkbEDMNOTSintoma),
                VIEW.findViewById(R.id.chkbEDMDOTSintoma))));

        hojaConsulta.setEdemaCodos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEDCSOTSintoma), VIEW.findViewById(R.id.chkbEDCNOTSintoma),
                VIEW.findViewById(R.id.chkbEDCDOTSintoma))));

        hojaConsulta.setEdemaHombros(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEDHSOTSintoma), VIEW.findViewById(R.id.chkbEDHNOTSintoma),
                VIEW.findViewById(R.id.chkbEDHDOTSintoma))));

        hojaConsulta.setEdemaRodillas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEDRSOTSintoma), VIEW.findViewById(R.id.chkbEDRNOTSintoma),
                VIEW.findViewById(R.id.chkbEDRDOTSintoma))));

        hojaConsulta.setEdemaTobillos(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEDTSOTSintoma), VIEW.findViewById(R.id.chkbEDTNOTSintoma),
                VIEW.findViewById(R.id.chkbEDTDOTSintoma))));

        boolean resultado = mDbAdapter.editarHojaConsulta(hojaConsulta);
        if (resultado) { ;
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
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbARTSOTSintoma), VIEW.findViewById(R.id.chkbARTNOTSintoma),
                VIEW.findViewById(R.id.chkbARTDOTSintoma), ((HOJACONSULTA.getAltralgia() != null)
                        ? HOJACONSULTA.getAltralgia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbMIASOTSintoma), VIEW.findViewById(R.id.chkbMIANOTSintoma),
                VIEW.findViewById(R.id.chkbMIADOTSintoma), ((HOJACONSULTA.getMialgia() != null)
                        ? HOJACONSULTA.getMialgia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLUMSOTSintoma), VIEW.findViewById(R.id.chkbLUMNOTSintoma),
                VIEW.findViewById(R.id.chkbLUMDOTSintoma), ((HOJACONSULTA.getLumbalgia() != null)
                        ? HOJACONSULTA.getLumbalgia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbDOCSOTSintoma), VIEW.findViewById(R.id.chkbDOCNOTSintoma),
                VIEW.findViewById(R.id.chkbDOCDOTSintoma), ((HOJACONSULTA.getDolorCuello() != null)
                        ? HOJACONSULTA.getDolorCuello().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbTNSSOTSintoma), VIEW.findViewById(R.id.chkbTNSNOTSintoma),
                VIEW.findViewById(R.id.chkbTNSDOTSintoma), ((HOJACONSULTA.getTenosinovitis() != null)
                        ? HOJACONSULTA.getTenosinovitis().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbARPSOTSintoma), VIEW.findViewById(R.id.chkbARPNOTSintoma),
                VIEW.findViewById(R.id.chkbARPDOTSintoma), ((HOJACONSULTA.getArtralgiaProximal() != null)
                        ? HOJACONSULTA.getArtralgiaProximal().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbARDSOTSintoma), VIEW.findViewById(R.id.chkbARDNOTSintoma),
                VIEW.findViewById(R.id.chkbARDDOTSintoma), ((HOJACONSULTA.getArtralgiaDistal() != null)
                        ? HOJACONSULTA.getArtralgiaDistal().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCNJSOTSintoma), VIEW.findViewById(R.id.chkbCNJNOTSintoma),
                VIEW.findViewById(R.id.chkbCNJDOTSintoma), ((HOJACONSULTA.getConjuntivitis() != null)
                        ? HOJACONSULTA.getConjuntivitis().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEDMSOTSintoma), VIEW.findViewById(R.id.chkbEDMNOTSintoma),
                VIEW.findViewById(R.id.chkbEDMDOTSintoma), ((HOJACONSULTA.getEdemaMunecas() != null)
                        ? HOJACONSULTA.getEdemaMunecas().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEDCSOTSintoma), VIEW.findViewById(R.id.chkbEDCNOTSintoma),
                VIEW.findViewById(R.id.chkbEDCDOTSintoma), ((HOJACONSULTA.getEdemaCodos() != null)
                        ? HOJACONSULTA.getEdemaCodos().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEDHSOTSintoma), VIEW.findViewById(R.id.chkbEDHNOTSintoma),
                VIEW.findViewById(R.id.chkbEDHDOTSintoma), ((HOJACONSULTA.getEdemaHombros() != null)
                        ? HOJACONSULTA.getEdemaHombros().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEDRSOTSintoma), VIEW.findViewById(R.id.chkbEDRNOTSintoma),
                VIEW.findViewById(R.id.chkbEDRDOTSintoma), ((HOJACONSULTA.getEdemaRodillas() != null)
                        ? HOJACONSULTA.getEdemaRodillas().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEDTSOTSintoma), VIEW.findViewById(R.id.chkbEDTNOTSintoma),
                VIEW.findViewById(R.id.chkbEDTDOTSintoma), ((HOJACONSULTA.getEdemaTobillos() != null)
                        ? HOJACONSULTA.getEdemaTobillos().charAt(0) : '4'));
    }
}
