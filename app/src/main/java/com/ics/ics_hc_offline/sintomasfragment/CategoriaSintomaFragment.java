package com.ics.ics_hc_offline.sintomasfragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
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
import com.ics.ics_hc_offline.tools.DecimalDigitsInputFilter;
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CategoriaSintomaFragment extends Fragment {
    private Context CONTEXT;
    private View VIEW;
    private int secHojaConsulta = 0;
    private String fechaNac;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private boolean CATEGORIA_A_B;
    private String CATEGORIA_SELECCIONADA;
    private String vFueraRango = new String();
    private TextView viewTxtvNSintomaCat1;
    private TextView viewTxtvSSintomaCat1;
    private TextView viewTxtvNSintomaCat2;
    private TextView viewTxtvSSintomaCat2;
    private TextView viewTxtvNSintomaCat3;
    private TextView viewTxtvSSintomaCat3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_categorias_sintoma, container, false);
        CONTEXT = VIEW.getContext();
        inicializarContorles();
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        secHojaConsulta = Integer.parseInt(bundle.getString("secHojaConsulta"));
        fechaNac = bundle.getString("fechaNac");

        CATEGORIA_A_B = false;
        CATEGORIA_SELECCIONADA = null;

        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        Button btnBackCategoria = (Button) view.findViewById(R.id.btnBackCategoria);
        btnBackCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    validarCategoria();
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

        Button btnBackCategoria2 = (Button) view.findViewById(R.id.btnBackCategoria2);
        btnBackCategoria2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validarCampos();
                    validarCategoria();
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

        View catPart1 = VIEW.findViewById(R.id.incCategoriaPart1);

        viewTxtvNSintomaCat1 = (TextView) catPart1.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat1 = (TextView) catPart1.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSSintomaCat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        View catPart2 = VIEW.findViewById(R.id.incCategoriaPart2);

        viewTxtvNSintomaCat2 = (TextView) catPart2.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat2 = (TextView) catPart2.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado2(view, 2);
            }
        });

        viewTxtvSSintomaCat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado2(view, 1);
            }
        });

        View catPart3 = VIEW.findViewById(R.id.incCategoriaPart3);

        viewTxtvNSintomaCat3 = (TextView) catPart3.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat3 = (TextView) catPart3.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado3(view, 2);
            }
        });

        viewTxtvSSintomaCat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado3(view, 1);
            }
        });
        cargarDatos();
    }

    public void regresar() {
        getActivity().finish();
    }

    public void inicializarContorles() {
        /*Categoria*/
        View.OnClickListener onClickedCategoria = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCategoria(view);
            }
        };

        VIEW.findViewById(R.id.chkbCategoriaA)
                .setOnClickListener(onClickedCategoria);
        VIEW.findViewById(R.id.chkbCategoriaB)
                .setOnClickListener(onClickedCategoria);
        VIEW.findViewById(R.id.chkbCategoriaC)
                .setOnClickListener(onClickedCategoria);
        VIEW.findViewById(R.id.chkbCategoriaD)
                .setOnClickListener(onClickedCategoria);
        VIEW.findViewById(R.id.chkbCategoriaNA)
                .setOnClickListener(onClickedCategoria);

        /*Cambio Categoria*/
        View.OnClickListener onClickedCambioCat = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCCSiNo(view);
            }
        };

        VIEW.findViewById(R.id.chkbCambioCategoriaSi)
                .setOnClickListener(onClickedCambioCat);
        VIEW.findViewById(R.id.chkbCambioCategoriaNo)
                .setOnClickListener(onClickedCambioCat);

        /*Manifestaciones Hemorragica*/
        View.OnClickListener onClickedManifHemor = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedMAH(view);
            }
        };

        VIEW.findViewById(R.id.chkbMAHSSintoma)
                .setOnClickListener(onClickedManifHemor);
        VIEW.findViewById(R.id.chkbMAHNSintoma)
                .setOnClickListener(onClickedManifHemor);

        /*Prueba Torniquete Positiva*/
        View.OnClickListener onClickedPruebaTornPosi = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPTP(view);
            }
        };

        VIEW.findViewById(R.id.chkbPTPSSintoma)
                .setOnClickListener(onClickedPruebaTornPosi);
        VIEW.findViewById(R.id.chkbPTPNSintoma)
                .setOnClickListener(onClickedPruebaTornPosi);

        /*Petequias 10 PT*/
        View.OnClickListener onClickedPetequias10 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPTQ(view);
            }
        };

        VIEW.findViewById(R.id.chkbPTQSSintoma)
                .setOnClickListener(onClickedPetequias10);
        VIEW.findViewById(R.id.chkbPTQNSintoma)
                .setOnClickListener(onClickedPetequias10);

        /*Petequias 20 PT*/
        View.OnClickListener onClickedPetequias20 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPTT(view);
            }
        };

        VIEW.findViewById(R.id.chkbPTTSSintoma)
                .setOnClickListener(onClickedPetequias20);
        VIEW.findViewById(R.id.chkbPTTNSintoma)
                .setOnClickListener(onClickedPetequias20);

        /*Piel y Extremidades Frias*/
        View.OnClickListener onClickedPielExtreFrias = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPEF(view);
            }
        };

        VIEW.findViewById(R.id.chkbPEFSSintoma)
                .setOnClickListener(onClickedPielExtreFrias);
        VIEW.findViewById(R.id.chkbPEFNSintoma)
                .setOnClickListener(onClickedPielExtreFrias);

        /*Palidez en Extremidades*/
        View.OnClickListener onClickedPalidezExtre = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPLE(view);
            }
        };

        VIEW.findViewById(R.id.chkbPLESSintoma)
                .setOnClickListener(onClickedPalidezExtre);
        VIEW.findViewById(R.id.chkbPLENSintoma)
                .setOnClickListener(onClickedPalidezExtre);

        /*Epitaxis*/
        View.OnClickListener onClickedEpitaxis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedEPI(view);
            }
        };

        VIEW.findViewById(R.id.chkbEPISSintoma)
                .setOnClickListener(onClickedEpitaxis);
        VIEW.findViewById(R.id.chkbEPINSintoma)
                .setOnClickListener(onClickedEpitaxis);

        /*Gingivorragia*/
        View.OnClickListener onClickedGingivorragia = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedGNG(view);
            }
        };

        VIEW.findViewById(R.id.chkbGNGSSintoma)
                .setOnClickListener(onClickedGingivorragia);
        VIEW.findViewById(R.id.chkbGNGNSintoma)
                .setOnClickListener(onClickedGingivorragia);

        /*Petequias Espontaneas*/
        View.OnClickListener onClickedPTQEspontaneas = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedPTS(view);
            }
        };

        VIEW.findViewById(R.id.chkbPTSSSintoma)
                .setOnClickListener(onClickedPTQEspontaneas);
        VIEW.findViewById(R.id.chkbPTSNSintoma)
                .setOnClickListener(onClickedPTQEspontaneas);

        /*Llenado Capilar 2 Seg*/
        View.OnClickListener onClickedLlenadoCapilar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedLLC(view);
            }
        };

        VIEW.findViewById(R.id.chkbLLCSSintoma)
                .setOnClickListener(onClickedLlenadoCapilar);
        VIEW.findViewById(R.id.chkbLLCNSintoma)
                .setOnClickListener(onClickedLlenadoCapilar);

        /*Cianosis*/
        View.OnClickListener onClickedCianosis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedCAI(view);
            }
        };

        VIEW.findViewById(R.id.chkbCAISSintoma)
                .setOnClickListener(onClickedCianosis);
        VIEW.findViewById(R.id.chkbCAINSintoma)
                .setOnClickListener(onClickedCianosis);

        /*Hipermenorrea*/
        View.OnClickListener onClickedHipermenorrea = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHPE(view);
            }
        };

        VIEW.findViewById(R.id.chkbHPESSintoma)
                .setOnClickListener(onClickedHipermenorrea);
        VIEW.findViewById(R.id.chkbHPENSintoma)
                .setOnClickListener(onClickedHipermenorrea);

        /*Hematemesis*/
        View.OnClickListener onClickedHematemesis = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHMT(view);
            }
        };

        VIEW.findViewById(R.id.chkbHMTSSintoma)
                .setOnClickListener(onClickedHematemesis);
        VIEW.findViewById(R.id.chkbHMTNSintoma)
                .setOnClickListener(onClickedHematemesis);

        /*Melena*/
        View.OnClickListener onClickedMelena = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedMLN(view);
            }
        };

        VIEW.findViewById(R.id.chkbMLNSSintoma)
                .setOnClickListener(onClickedMelena);
        VIEW.findViewById(R.id.chkbMLNNSintoma)
                .setOnClickListener(onClickedMelena);

        /*Hemoconcentracion*/
        View.OnClickListener onClickedHemoconcentracion = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHMC(view);
            }
        };

        VIEW.findViewById(R.id.chkbHMCSSintoma)
                .setOnClickListener(onClickedHemoconcentracion);
        VIEW.findViewById(R.id.chkbHMCNSintoma)
                .setOnClickListener(onClickedHemoconcentracion);
        VIEW.findViewById(R.id.chkbHMCDSintoma)
                .setOnClickListener(onClickedHemoconcentracion);

        /*Hospitalizado*/
        View.OnClickListener onClickedHospitalizado = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedHSH(view);
            }
        };

        VIEW.findViewById(R.id.chkbHSHSSintoma)
                .setOnClickListener(onClickedHospitalizado);
        VIEW.findViewById(R.id.chkbHSHNSintoma)
                .setOnClickListener(onClickedHospitalizado);

        /*Transfusion Sangre*/
        View.OnClickListener onClickedTransfusionSangre = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedRTS(view);
            }
        };

        VIEW.findViewById(R.id.chkbRTSSSintoma)
                .setOnClickListener(onClickedTransfusionSangre);
        VIEW.findViewById(R.id.chkbRTSNSintoma)
                .setOnClickListener(onClickedTransfusionSangre);

        /*Tomando Medicamento*/
        View.OnClickListener onClickedTomandoMedicamento = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTMM(view);
            }
        };

        VIEW.findViewById(R.id.chkbTMMSSintoma)
                .setOnClickListener(onClickedTomandoMedicamento);
        VIEW.findViewById(R.id.chkbTMMNSintoma)
                .setOnClickListener(onClickedTomandoMedicamento);

        /*Medicamento Distinto*/
        View.OnClickListener onClickedMedicamentoDistinto = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedTMD(view);
            }
        };

        VIEW.findViewById(R.id.chkbTMDSSintoma)
                .setOnClickListener(onClickedMedicamentoDistinto);
        VIEW.findViewById(R.id.chkbTMDNSintoma)
                .setOnClickListener(onClickedMedicamentoDistinto);

        /*Fecha*/
        ((EditText)VIEW.findViewById(R.id.dpFECC)).setKeyListener(null);

        VIEW.findViewById(R.id.dpFECC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFecha(v);
            }
        });

        /*Linfocitos Atipicos*/
        ((EditText) VIEW.findViewById(R.id.dpFECC)).setKeyListener(null);

        ((EditText) VIEW.findViewById(R.id.edtxtLNF)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
    }

    public void onChkboxClickedCategoria(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbCategoriaA:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    CATEGORIA_A_B = true;
                    CATEGORIA_SELECCIONADA = "A";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbCategoriaB:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    CATEGORIA_A_B = true;
                    CATEGORIA_SELECCIONADA = "B";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbCategoriaC:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    CATEGORIA_A_B = false;
                    CATEGORIA_SELECCIONADA = "C";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkbCategoriaD:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    CATEGORIA_A_B = false;
                    CATEGORIA_SELECCIONADA = "D";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkbCategoriaNA:
                if (checked){
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    CATEGORIA_A_B = false;
                    CATEGORIA_SELECCIONADA = "NA";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    public void onChkboxClickedCCSiNo(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCambioCategoriaSi), VIEW.findViewById(R.id.chkbCambioCategoriaNo),
                view);
    }

    /************************* Categoria Part 1 ***********************************************************************************/

    public void onChkboxClickedMAH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbMAHSSintoma), VIEW.findViewById(R.id.chkbMAHNSintoma),
                view);
    }

    public void onChkboxClickedPTP(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPTPSSintoma), VIEW.findViewById(R.id.chkbPTPNSintoma),
                view);
    }

    public void onChkboxClickedPTQ(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPTQSSintoma), VIEW.findViewById(R.id.chkbPTQNSintoma),
                view);
    }

    public void onChkboxClickedPTT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPTTSSintoma), VIEW.findViewById(R.id.chkbPTTNSintoma),
                view);
    }

    public void onChkboxClickedPEF(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPEFSSintoma), VIEW.findViewById(R.id.chkbPEFNSintoma),
                view);
    }

    public void onChkboxClickedPLE(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPLESSintoma), VIEW.findViewById(R.id.chkbPLENSintoma),
                view);
    }

    /************************* Categoria Part 2 ***********************************************************************************/

    public void onChkboxClickedEPI(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbEPISSintoma), VIEW.findViewById(R.id.chkbEPINSintoma),
                view);
    }

    public void onChkboxClickedGNG(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbGNGSSintoma), VIEW.findViewById(R.id.chkbGNGNSintoma),
                view);
    }

    public void onChkboxClickedPTS(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbPTSSSintoma), VIEW.findViewById(R.id.chkbPTSNSintoma),
                view);
    }

    public void onChkboxClickedLLC(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbLLCSSintoma), VIEW.findViewById(R.id.chkbLLCNSintoma),
                view);
    }

    public void onChkboxClickedCAI(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbCAISSintoma), VIEW.findViewById(R.id.chkbCAINSintoma),
                view);
    }

    /************************* Categoria Part 3 ***********************************************************************************/

    public void onChkboxClickedHPE(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHPESSintoma), VIEW.findViewById(R.id.chkbHPENSintoma),
                VIEW.findViewById(R.id.chkbHPEDSintoma), view);
    }

    public void onChkboxClickedHMT(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHMTSSintoma), VIEW.findViewById(R.id.chkbHMTNSintoma),
                view);
    }

    public void onChkboxClickedMLN(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbMLNSSintoma), VIEW.findViewById(R.id.chkbMLNNSintoma),
                view);
    }

    public void onChkboxClickedHMC(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHMCSSintoma), VIEW.findViewById(R.id.chkbHMCNSintoma),
                VIEW.findViewById(R.id.chkbHMCDSintoma), view);
        if(view.getId() == R.id.chkbHMCSSintoma){
            VIEW.findViewById(R.id.edtxtHMC).setEnabled(true);
        } else{
            ((EditText)VIEW.findViewById(R.id.edtxtHMC)).setText("");
            VIEW.findViewById(R.id.edtxtHMC).setEnabled(false);
        }
    }

    /************************* Categoria Part 4 ***********************************************************************************/

    public void onChkboxClickedHSH(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbHSHSSintoma), VIEW.findViewById(R.id.chkbHSHNSintoma),
                view);
        if(view.getId() == R.id.chkbHSHSSintoma) {
            VIEW.findViewById(R.id.edtxtSIH).setEnabled(true);
        } else {
            VIEW.findViewById(R.id.edtxtSIH).setEnabled(false);
            ((EditText)VIEW.findViewById(R.id.edtxtSIH)).setText("");
        }
    }

    public void onChkboxClickedRTS(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbRTSSSintoma), VIEW.findViewById(R.id.chkbRTSNSintoma),
                view);
        if(view.getId() == R.id.chkbRTSSSintoma) {
            VIEW.findViewById(R.id.edtxtSIR).setEnabled(true);
        } else {
            VIEW.findViewById(R.id.edtxtSIR).setEnabled(false);
            ((EditText)VIEW.findViewById(R.id.edtxtSIR)).setText("");
        }
    }

    public void onChkboxClickedTMM(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbTMMSSintoma), VIEW.findViewById(R.id.chkbTMMNSintoma),
                view);
        if(view.getId() == R.id.chkbTMMSSintoma) {
            VIEW.findViewById(R.id.edtxtSIT).setEnabled(true);
        } else {
            VIEW.findViewById(R.id.edtxtSIT).setEnabled(false);
            ((EditText)VIEW.findViewById(R.id.edtxtSIT)).setText("");
        }
    }

    public void onChkboxClickedTMD(View view) {
        AndroidUtils.controlarCheckBoxGroup(VIEW.findViewById(R.id.chkbTMDSSintoma), VIEW.findViewById(R.id.chkbTMDNSintoma),
                view);
        if(view.getId() == R.id.chkbTMDSSintoma) {
            VIEW.findViewById(R.id.edtxtSIU).setEnabled(true);
        } else {
            VIEW.findViewById(R.id.edtxtSIU).setEnabled(false);
            ((EditText)VIEW.findViewById(R.id.edtxtSIU)).setText("");
        }
    }

    public void showDatePickerDialogFecha(View view) {
        obtenerFecha();
    }

    public void obtenerFecha() {
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
                    ((EditText)VIEW.findViewById(R.id.dpFECC)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) VIEW.findViewById(R.id.dpFECC)).getError() != null) {
                        ((EditText) VIEW.findViewById(R.id.dpFECC)).setError(null);
                        ((EditText) VIEW.findViewById(R.id.dpFECC)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.calendar, 0);
                    }
                    ((EditText) VIEW.findViewById(R.id.dpFECC)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        },year, month, day);
        recogerFecha.show();
    }

    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbMAHSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTPSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTQSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTTSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPEFSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPLESSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbMAHSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPTPSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPTQSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPTTSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPEFSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPLESSintoma)).setChecked(true);

                            ((CheckBox) VIEW.findViewById(R.id.chkbMAHNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTPNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTQNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTTNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPEFNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPLENSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbMAHNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPTPNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPTQNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPTTNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPEFNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPLENSintoma)).setChecked(true);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void SintomaMarcado2(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbEPISSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbGNGSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTSSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLLCSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCAISSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbEPISSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbGNGSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbPTSSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbLLCSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbCAISSintoma)).setChecked(true);

                            ((CheckBox) VIEW.findViewById(R.id.chkbEPINSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbGNGNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbPTSNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbLLCNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbCAINSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbEPINSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbGNGNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbPTSNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbLLCNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbCAINSintoma)).setChecked(true);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));

            MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void SintomaMarcado3(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) VIEW.findViewById(R.id.chkbHPESSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHMTSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMLNSSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHMCSSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbHPESSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbHMTSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) VIEW.findViewById(R.id.chkbMLNSSintoma)).setChecked(true);
                            if(valor==1) {
                                ((CheckBox) VIEW.findViewById(R.id.chkbHMCSSintoma)).setChecked(true);
                                VIEW.findViewById(R.id.edtxtHMC).setEnabled(true);
                            }

                            ((CheckBox) VIEW.findViewById(R.id.chkbHPENSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHMTNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbMLNNSintoma)).setChecked(false);
                            ((CheckBox) VIEW.findViewById(R.id.chkbHMCNSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbHPENSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbHMTNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) VIEW.findViewById(R.id.chkbMLNNSintoma)).setChecked(true);
                            if(valor==2) {
                                ((CheckBox) VIEW.findViewById(R.id.chkbHMCNSintoma)).setChecked(true);
                                ((EditText)VIEW.findViewById(R.id.edtxtHMC)).setText("");
                                VIEW.findViewById(R.id.edtxtHMC).setEnabled(false);
                            }

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public boolean validarCampos() throws Exception {
        if(((CheckBox)VIEW.findViewById(R.id.chkbHSHSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtSIH)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)VIEW.findViewById(R.id.chkbRTSSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtSIR)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)VIEW.findViewById(R.id.chkbTMMSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtSIT)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)VIEW.findViewById(R.id.chkbTMDSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtSIU)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbMAHSSintoma), VIEW.findViewById(R.id.chkbMAHNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPTPSSintoma), VIEW.findViewById(R.id.chkbPTPNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPTQSSintoma), VIEW.findViewById(R.id.chkbPTQNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPTTSSintoma), VIEW.findViewById(R.id.chkbPTTNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPEFSSintoma), VIEW.findViewById(R.id.chkbPEFNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPLESSintoma), VIEW.findViewById(R.id.chkbPLENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbEPISSintoma), VIEW.findViewById(R.id.chkbEPINSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbGNGSSintoma), VIEW.findViewById(R.id.chkbGNGNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbPTSSSintoma), VIEW.findViewById(R.id.chkbPTSNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbLLCSSintoma), VIEW.findViewById(R.id.chkbLLCNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCAISSintoma), VIEW.findViewById(R.id.chkbCAINSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHPESSintoma), VIEW.findViewById(R.id.chkbHPENSintoma),
                VIEW.findViewById(R.id.chkbHPEDSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHMTSSintoma), VIEW.findViewById(R.id.chkbHMTNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbMLNSSintoma), VIEW.findViewById(R.id.chkbMLNNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));

        }else if (((CheckBox) VIEW.findViewById(R.id.chkbHMCSSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtHMC)).getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_hemoconcentracion));

        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbCategoriaA), VIEW.findViewById(R.id.chkbCategoriaB),
                VIEW.findViewById(R.id.chkbCategoriaC), VIEW.findViewById(R.id.chkbCategoriaD), VIEW.findViewById(R.id.chkbCategoriaNA))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbHSHSSintoma), VIEW.findViewById(R.id.chkbHSHNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbRTSSSintoma), VIEW.findViewById(R.id.chkbRTSNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbTMMSSintoma), VIEW.findViewById(R.id.chkbTMMNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(VIEW.findViewById(R.id.chkbTMDSSintoma), VIEW.findViewById(R.id.chkbTMDNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }

        if(!TextUtils.isEmpty(((EditText) VIEW.findViewById(R.id.dpFECC)).getText())) {
            Calendar fechaCategoria = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            fechaCategoria.setTime(sdf.parse(((EditText) VIEW.findViewById(R.id.dpFECC)).getText().toString()));

            long milliSeconds= Long.parseLong(fechaNac);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliSeconds);

            if (fechaCategoria.before(calendar)) {
                throw new Exception(getString(R.string.msj_fecha_menor_fecha_nacimiento));
            }
        }

        int cont = 0;
        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtSaturacion)).getText().toString())) {
            if (!estaEnRango(0, 100, ((EditText) VIEW.findViewById(R.id.edtxtSaturacion)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_saturacion_o2));
                cont++;
            }
        }

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtHMC)).getText().toString())) {
            if (!estaEnRango(0, 75, ((EditText) VIEW.findViewById(R.id.edtxtHMC)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_hemoconcentracion));
                cont++;
            }
        }

        if (cont > 0){
            throw new Exception(getString(R.string.msj_aviso_control_cambios1,vFueraRango));

        }
        return true;
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
        hojaConsulta.setSaturaciono2((!StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtSaturacion)).getText().toString()))
                ? Integer.parseInt(((EditText)VIEW.findViewById(R.id.edtxtSaturacion)).getText().toString()) : -1);

        hojaConsulta.setCategoria((getCategoriaSeleccionada() == null && ((CheckBox)VIEW.findViewById(R.id.chkbCategoriaNA)).isChecked())
                ? "NA" : getCategoriaSeleccionada());

        if(((CheckBox)VIEW.findViewById(R.id.chkbCambioCategoriaSi)).isChecked() || ((CheckBox)VIEW.findViewById(R.id.chkbCambioCategoriaNo)).isChecked()) {
            hojaConsulta.setCambioCategoria(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCambioCategoriaSi),
                    VIEW.findViewById(R.id.chkbCambioCategoriaNo))));
        }

        if(((CheckBox)VIEW.findViewById(R.id.chkbHSHSSintoma)).isChecked() || ((CheckBox)VIEW.findViewById(R.id.chkbHSHNSintoma)).isChecked()) {
            hojaConsulta.setHospitalizado(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHSHSSintoma),
                    VIEW.findViewById(R.id.chkbHSHNSintoma))));
        }

        hojaConsulta.setHospitalizadoEspecificar(((CheckBox)VIEW.findViewById(R.id.chkbHSHSSintoma)).isChecked()
                ? ((EditText)VIEW.findViewById(R.id.edtxtSIH)).getText().toString() : "");

        if(((CheckBox)VIEW.findViewById(R.id.chkbRTSSSintoma)).isChecked() || ((CheckBox)VIEW.findViewById(R.id.chkbRTSNSintoma)).isChecked()) {
            hojaConsulta.setTransfusionSangre(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbRTSSSintoma),
                    VIEW.findViewById(R.id.chkbRTSNSintoma))));
        }

        hojaConsulta.setTransfusionEspecificar(((CheckBox) VIEW.findViewById(R.id.chkbRTSSSintoma)).isChecked()
                ? ((EditText) VIEW.findViewById(R.id.edtxtSIR)).getText().toString() : "");

        if(((CheckBox)VIEW.findViewById(R.id.chkbTMMSSintoma)).isChecked() || ((CheckBox)VIEW.findViewById(R.id.chkbTMMNSintoma)).isChecked()) {
            hojaConsulta.setTomandoMedicamento(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbTMMSSintoma),
                    VIEW.findViewById(R.id.chkbTMMNSintoma))));
        }

        hojaConsulta.setMedicamentoEspecificar(((CheckBox) VIEW.findViewById(R.id.chkbTMMSSintoma)).isChecked()
                ? ((EditText) VIEW.findViewById(R.id.edtxtSIT)).getText().toString() : "");

        if(((CheckBox)VIEW.findViewById(R.id.chkbTMDSSintoma)).isChecked() || ((CheckBox)VIEW.findViewById(R.id.chkbTMDNSintoma)).isChecked()) {
            hojaConsulta.setMedicamentoDistinto(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbTMDSSintoma),
                    VIEW.findViewById(R.id.chkbTMDNSintoma))));
        }

        hojaConsulta.setMedicamentoDistEspecificar(((CheckBox) VIEW.findViewById(R.id.chkbTMDSSintoma)).isChecked()
                ? ((EditText) VIEW.findViewById(R.id.edtxtSIU)).getText().toString() : "");

        hojaConsulta.setManifestacionHemorragica(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbMAHSSintoma),
                VIEW.findViewById(R.id.chkbMAHNSintoma))));

        hojaConsulta.setPruebaTorniquetePositiva(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPTPSSintoma),
                VIEW.findViewById(R.id.chkbPTPNSintoma))));

        hojaConsulta.setPetequia10Pt(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPTQSSintoma),
                VIEW.findViewById(R.id.chkbPTQNSintoma))));

        hojaConsulta.setPetequia20Pt(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPTTSSintoma),
                VIEW.findViewById(R.id.chkbPTTNSintoma))));

        hojaConsulta.setPielExtremidadesFrias(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPEFSSintoma),
                VIEW.findViewById(R.id.chkbPEFNSintoma))));

        hojaConsulta.setPalidezEnExtremidades(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPLESSintoma),
                VIEW.findViewById(R.id.chkbPLENSintoma))));

        hojaConsulta.setEpistaxis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbEPISSintoma),
                VIEW.findViewById(R.id.chkbEPINSintoma))));

        hojaConsulta.setGingivorragia(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbGNGSSintoma),
                VIEW.findViewById(R.id.chkbGNGNSintoma))));

        hojaConsulta.setPetequiasEspontaneas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbPTSSSintoma),
                VIEW.findViewById(R.id.chkbPTSNSintoma))));

        hojaConsulta.setLlenadoCapilar2seg(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbLLCSSintoma),
                VIEW.findViewById(R.id.chkbLLCNSintoma))));

        hojaConsulta.setCianosis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbCAISSintoma),
                VIEW.findViewById(R.id.chkbCAINSintoma))));

        if(!StringUtils.isNullOrEmpty(((EditText)VIEW.findViewById(R.id.edtxtLNF)).getText().toString())){
            hojaConsulta.setLinfocitosaAtipicos(Double.parseDouble(((EditText) VIEW.findViewById(R.id.edtxtLNF)).getText().toString()));
        }


        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.dpFECC)).getText().toString())){
            String fechaLinfocitos = ((EditText) VIEW.findViewById(R.id.dpFECC)).getText().toString();
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fechaLinfocitos);
                String formattedDate = targetFormat.format(date);
                hojaConsulta.setFechaLinfocitos(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            hojaConsulta.setFechaLinfocitos("");
        }
        //HOJACONSULTA.setFechaLinfocitos(((EditText)VIEW.findViewById(R.id.dpFECC)).getText().toString());

        hojaConsulta.setHipermenorrea(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHPESSintoma),
                VIEW.findViewById(R.id.chkbHPENSintoma), VIEW.findViewById(R.id.chkbHPEDSintoma))));

        hojaConsulta.setHematemesis(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHMTSSintoma),
                VIEW.findViewById(R.id.chkbHMTNSintoma))));

        hojaConsulta.setMelena(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbMLNSSintoma),
                VIEW.findViewById(R.id.chkbMLNNSintoma))));

        hojaConsulta.setHemoconc(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(VIEW.findViewById(R.id.chkbHMCSSintoma),
                VIEW.findViewById(R.id.chkbHMCNSintoma), VIEW.findViewById(R.id.chkbHMCDSintoma))));

        if(!StringUtils.isNullOrEmpty(((EditText) VIEW.findViewById(R.id.edtxtHMC)).getText().toString())) {
            hojaConsulta.setHemoconcentracion(Short.parseShort(((EditText)VIEW.findViewById(R.id.edtxtHMC)).getText().toString()));
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
        ((EditText)VIEW.findViewById(R.id.edtxtSaturacion)).setText(((HOJACONSULTA.getSaturaciono2() > 0)
                ? HOJACONSULTA.getSaturaciono2().toString() : null));

        if(!StringUtils.isNullOrEmpty(HOJACONSULTA.getCategoria())) {
            if(HOJACONSULTA.getCategoria().trim().compareTo("A") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).setChecked(true);
                CATEGORIA_A_B = true;
                CATEGORIA_SELECCIONADA = "A";
            } else if(HOJACONSULTA.getCategoria().trim().compareTo("B") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).setChecked(true);
                CATEGORIA_A_B = true;
                CATEGORIA_SELECCIONADA = "B";
            } else if(HOJACONSULTA.getCategoria().trim().compareTo("C") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).setChecked(true);
                CATEGORIA_A_B = false;
                CATEGORIA_SELECCIONADA = "C";
            } else if(HOJACONSULTA.getCategoria().trim().compareTo("D") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).setChecked(true);
                CATEGORIA_A_B = false;
                CATEGORIA_SELECCIONADA = "D";
            } else if(HOJACONSULTA.getCategoria().trim().compareTo("NA") == 0) {
                ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).setChecked(true);
                CATEGORIA_A_B = false;
                CATEGORIA_SELECCIONADA = "NA";
            }
        }

        if(HOJACONSULTA.getCambioCategoria() != null && !HOJACONSULTA.getCambioCategoria().toString().isEmpty()) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCambioCategoriaSi),
                    VIEW.findViewById(R.id.chkbCambioCategoriaNo), ((HOJACONSULTA.getCambioCategoria() != null)
                            ? HOJACONSULTA.getCambioCategoria().charAt(0) : '4'));
        }

        if(HOJACONSULTA.getHospitalizado() != null && !HOJACONSULTA.getHospitalizado().toString().isEmpty()) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHSHSSintoma),
                    VIEW.findViewById(R.id.chkbHSHNSintoma), ((HOJACONSULTA.getHospitalizado() != null)
                            ? HOJACONSULTA.getHospitalizado().charAt(0) : '4'));
        }

        if(HOJACONSULTA.getHospitalizado() != null
                && HOJACONSULTA.getHospitalizado().charAt(0) == '0') {
            VIEW.findViewById(R.id.edtxtSIH).setEnabled(true);
            ((EditText) VIEW.findViewById(R.id.edtxtSIH)).setText(HOJACONSULTA.getHospitalizadoEspecificar());
        }

        if(HOJACONSULTA.getTransfusionSangre() != null && !HOJACONSULTA.getTransfusionSangre().toString().isEmpty()) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbRTSSSintoma),
                    VIEW.findViewById(R.id.chkbRTSNSintoma), ((HOJACONSULTA.getTransfusionSangre() != null)
                            ? HOJACONSULTA.getTransfusionSangre().charAt(0) : '4'));
        }

        if(HOJACONSULTA.getTransfusionSangre() != null
                && HOJACONSULTA.getTransfusionSangre().charAt(0) == '0') {
            VIEW.findViewById(R.id.edtxtSIR).setEnabled(true);
            ((EditText) VIEW.findViewById(R.id.edtxtSIR)).setText(HOJACONSULTA.getTransfusionEspecificar());
        }

        if(HOJACONSULTA.getTomandoMedicamento() != null && !HOJACONSULTA.getTomandoMedicamento().toString().isEmpty()) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbTMMSSintoma),
                    VIEW.findViewById(R.id.chkbTMMNSintoma), ((HOJACONSULTA.getTomandoMedicamento() != null)
                            ? HOJACONSULTA.getTomandoMedicamento().charAt(0) : '4'));
        }

        if(HOJACONSULTA.getTomandoMedicamento() != null
                && HOJACONSULTA.getTomandoMedicamento().charAt(0) == '0') {
            VIEW.findViewById(R.id.edtxtSIT).setEnabled(true);
            ((EditText) VIEW.findViewById(R.id.edtxtSIT)).setText(HOJACONSULTA.getMedicamentoEspecificar());
        }

        if(HOJACONSULTA.getMedicamentoDistinto() != null && !HOJACONSULTA.getMedicamentoDistinto().toString().isEmpty()) {
            AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbTMDSSintoma),
                    VIEW.findViewById(R.id.chkbTMDNSintoma), ((HOJACONSULTA.getMedicamentoDistinto() != null)
                            ? HOJACONSULTA.getMedicamentoDistinto().charAt(0) : '4'));
        }

        if(HOJACONSULTA.getMedicamentoDistinto() != null
                && HOJACONSULTA.getMedicamentoDistinto().charAt(0) == '0') {
            VIEW.findViewById(R.id.edtxtSIU).setEnabled(true);
            ((EditText) VIEW.findViewById(R.id.edtxtSIU)).setText(HOJACONSULTA.getMedicamentoDistEspecificar());
        }

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbMAHSSintoma),
                VIEW.findViewById(R.id.chkbMAHNSintoma), ((HOJACONSULTA.getManifestacionHemorragica() != null)
                        ? HOJACONSULTA.getManifestacionHemorragica().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPTPSSintoma),
                VIEW.findViewById(R.id.chkbPTPNSintoma), ((HOJACONSULTA.getPruebaTorniquetePositiva() != null)
                        ? HOJACONSULTA.getPruebaTorniquetePositiva().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPTQSSintoma),
                VIEW.findViewById(R.id.chkbPTQNSintoma), ((HOJACONSULTA.getPetequia10Pt() != null)
                        ? HOJACONSULTA.getPetequia10Pt().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPTTSSintoma),
                VIEW.findViewById(R.id.chkbPTTNSintoma), ((HOJACONSULTA.getPetequia20Pt() != null)
                        ? HOJACONSULTA.getPetequia20Pt().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPEFSSintoma),
                VIEW.findViewById(R.id.chkbPEFNSintoma), ((HOJACONSULTA.getPielExtremidadesFrias() != null)
                        ? HOJACONSULTA.getPielExtremidadesFrias().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPLESSintoma),
                VIEW.findViewById(R.id.chkbPLENSintoma), ((HOJACONSULTA.getPalidezEnExtremidades() != null)
                        ? HOJACONSULTA.getPalidezEnExtremidades().charAt(0) : '4'));
        //Part 2
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbEPISSintoma),
                VIEW.findViewById(R.id.chkbEPINSintoma), ((HOJACONSULTA.getEpistaxis() != null)
                        ? HOJACONSULTA.getEpistaxis().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbGNGSSintoma),
                VIEW.findViewById(R.id.chkbGNGNSintoma), ((HOJACONSULTA.getGingivorragia() != null)
                        ? HOJACONSULTA.getGingivorragia().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbPTSSSintoma),
                VIEW.findViewById(R.id.chkbPTSNSintoma), ((HOJACONSULTA.getPetequiasEspontaneas() != null)
                        ? HOJACONSULTA.getPetequiasEspontaneas().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbLLCSSintoma),
                VIEW.findViewById(R.id.chkbLLCNSintoma), ((HOJACONSULTA.getLlenadoCapilar2seg() != null)
                        ? HOJACONSULTA.getLlenadoCapilar2seg().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbCAISSintoma),
                VIEW.findViewById(R.id.chkbCAINSintoma), ((HOJACONSULTA.getCianosis() != null)
                        ? HOJACONSULTA.getCianosis().charAt(0) : '4'));

        ((EditText) VIEW.findViewById(R.id.edtxtLNF)).setText(((HOJACONSULTA.getLinfocitosaAtipicos() > 0)
                ? HOJACONSULTA.getLinfocitosaAtipicos().toString() : null));

        //((EditText) VIEW.findViewById(R.id.dpFECC)).setText(HOJACONSULTA.getFechaLinfocitos());
        if (!StringUtils.isNullOrEmpty(HOJACONSULTA.getFechaLinfocitos())) {
            String fechaLinfocitos = HOJACONSULTA.getFechaLinfocitos();
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(fechaLinfocitos);
                String formattedDate = targetFormat.format(date);
                ((EditText) VIEW.findViewById(R.id.dpFECC)).setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Part 3
        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHPESSintoma),
                VIEW.findViewById(R.id.chkbHPENSintoma), VIEW.findViewById(R.id.chkbHPEDSintoma),
                ((HOJACONSULTA.getHipermenorrea() != null)
                        ? HOJACONSULTA.getHipermenorrea().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHMTSSintoma),
                VIEW.findViewById(R.id.chkbHMTNSintoma), ((HOJACONSULTA.getHematemesis() != null)
                        ? HOJACONSULTA.getHematemesis().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbMLNSSintoma),
                VIEW.findViewById(R.id.chkbMLNNSintoma), ((HOJACONSULTA.getMelena() != null)
                        ? HOJACONSULTA.getMelena().charAt(0) : '4'));

        AndroidUtils.establecerCheckboxGuardado(VIEW.findViewById(R.id.chkbHMCSSintoma),
                VIEW.findViewById(R.id.chkbHMCNSintoma), VIEW.findViewById(R.id.chkbHMCDSintoma),
                ((HOJACONSULTA.getHemoconc() != null)
                        ? HOJACONSULTA.getHemoconc().charAt(0) : '4'));

        ((EditText) VIEW.findViewById(R.id.edtxtHMC)).setText(((HOJACONSULTA.getHemoconcentracion() > 0)
                ? HOJACONSULTA.getHemoconcentracion().toString() : null));
    }

    public String getCategoriaSeleccionada() {
        return CATEGORIA_SELECCIONADA;
    }

    public void validarCategoria() throws Exception {
        boolean perteneceEstDengue = false;
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        if (hojaConsulta != null) {
            if (hojaConsulta.getEstudiosParticipantes() != null) {
                String estudiosParticipantes = hojaConsulta.getEstudiosParticipantes();
                String[] estPart;
                estPart = estudiosParticipantes.split(",");
                for (int i = 0; i < estPart.length; i++) {
                    if (estPart[i].trim().equals("Dengue")) {
                        perteneceEstDengue = true;
                    }
                }
                boolean cat_NA = ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaNA)).isChecked();
                boolean cat_D = ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaD)).isChecked();
                boolean cat_A = ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaA)).isChecked();
                boolean cat_B = ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaB)).isChecked();
                boolean cat_C = ((CheckBox) VIEW.findViewById(R.id.chkbCategoriaC)).isChecked();
                if (perteneceEstDengue && cat_NA) {
                    throw new Exception(getString(R.string.PACIENTE_NO_PUEDE_SER_CATEGORIA_NA));
                }
                if (perteneceEstDengue && cat_D && hojaConsulta.getFif() != null && hojaConsulta.getFif().equals("")) {
                    throw new Exception(getString(R.string.PACIENTE_CON_EST_DENGUE_FIF_Y_CATEGORIA_D));
                }
                if ((!perteneceEstDengue && cat_A) ||
                        (!perteneceEstDengue && cat_B) ||
                        (!perteneceEstDengue && cat_C) ||
                        (!perteneceEstDengue && cat_D)) {
                    throw new Exception(getString(R.string.PACIENTE_NO_PUEDE_SER_CATEGORIA_ABCD));
                }
            }
        }
    }
}
