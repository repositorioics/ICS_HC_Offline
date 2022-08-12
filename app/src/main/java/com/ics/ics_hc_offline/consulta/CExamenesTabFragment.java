package com.ics.ics_hc_offline.consulta;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;
import com.ics.ics_hc_offline.utils.TemplatePDF;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CExamenesTabFragment extends Fragment {

    public Integer  SEC_HOJA_CONSULTA ;
    public View mRootView;
    public static Bundle BUNDLE;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private TextView viewTxtvSExamen;
    private TextView viewTxtvNExamen;
    TemplatePDF templatePDF;

    public CExamenesTabFragment(Bundle bundle) {
        BUNDLE = bundle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.consulta_examenes_tab_layout, container, false);

        this.mRootView = rootView;
        inicializarContorles(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbAdapter = new HojaConsultaDBAdapter(view.getContext(),false,false);
        mDbAdapter.open();
        Integer codExpediente = Integer.valueOf(BUNDLE.getString("codExpediente"));
        String estudios = mDbAdapter.obtenerEstudiosParticipante(codExpediente);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SEC_HOJA_CONSULTA = Integer.parseInt(BUNDLE.getString("secHojaConsulta"));
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);

        ((EditText)view.findViewById(R.id.edtxNombrePaciente)).setText(String.valueOf(BUNDLE.getString("nombrePaciente")));
        ((EditText)view.findViewById(R.id.edtxEstudioParticipante)).setText(estudios);
        ((EditText)view.findViewById(R.id.edtxCodigoExamenes)).setText(String.valueOf(BUNDLE.getString("codExpediente")));
        String fechaConsulta = BUNDLE.getString("fechaConsulta");
        try {
            Date date = df.parse(fechaConsulta);
            df = new SimpleDateFormat("dd/MM/yyyy");
            String result = df.format(date);
            ((EditText)view.findViewById(R.id.edtxtFechaExamen)).setText(result);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String hora = df.format(calendar.getTime());
            ((EditText)view.findViewById(R.id.edtxtHoraExamen)).setText(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String fConsulta = df.format(fechaConsulta);
        //((EditText)view.findViewById(R.id.edtxtFechaSintoma)).setText(fConsulta);

        ((EditText)view.findViewById(R.id.edtxtSexoExamen)).setText(String.valueOf(BUNDLE.getString("sexo")));

        String fechaNac = BUNDLE.getString("fechaNac");
        long milliSeconds= Long.parseLong(fechaNac);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliSeconds);
        String edad = DateUtils.obtenerEdad(calendar);
        ((EditText)view.findViewById(R.id.edtxtEdadExamen)).setText(edad);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        //Número de expediente fisico con la fecha de nacimiento
        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
        String expedienteFisico = numExpFis.format(cal.getTime());
        ((EditText)view.findViewById(R.id.edtxtExpedienteExamen)).setText(expedienteFisico);

        //((EditText)getActivity().findViewById(R.id.edtxtPesoKgExamen)).setText(String.valueOf(HOJACONSULTA.getPesoKg()));
        //((EditText)getActivity().findViewById(R.id.edtxtTallaCmExamen)).setText(String.valueOf(HOJACONSULTA.getTallaCm()));
        //((EditText)getActivity().findViewById(R.id.edtxtTempCExamen)).setText(String.valueOf(HOJACONSULTA.getTemperaturac()));

        ((EditText)view.findViewById(R.id.edtxtPesoKgExamen)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)view.findViewById(R.id.edtxtTallaCmExamen)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)view.findViewById(R.id.edtxtTempCExamen)).setText(String.valueOf(BUNDLE.getString("temperatura")));

        Button btnEnviarOrdLab = (Button) view.findViewById(R.id.btnEnviarOrdLab);
        btnEnviarOrdLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_btnEnviarOrdLab();
            }
        });

        ImageButton ibtVisorDiagnostico = (ImageButton) view.findViewById(R.id.ibtVisorDiagnostico);
        ibtVisorDiagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarRegistro(SEC_HOJA_CONSULTA);
            }
        });

        View examPart1 = view.findViewById(R.id.incExamenParte1);

        viewTxtvSExamen = (TextView) examPart1.findViewById(R.id.txtvSExamen);
        viewTxtvNExamen = (TextView) examPart1.findViewById(R.id.txtvNExamen);

        viewTxtvSExamen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        viewTxtvNExamen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });
        cargarDatos();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((EditText)getActivity().findViewById(R.id.edtxtPesoKgExamen)).setText(String.valueOf(BUNDLE.getString("pesoKg")));
        ((EditText)getActivity().findViewById(R.id.edtxtTallaCmExamen)).setText(String.valueOf(BUNDLE.getString("tallaCm")));
        ((EditText)getActivity().findViewById(R.id.edtxtTempCExamen)).setText(String.valueOf(BUNDLE.getString("temperatura")));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void buscarRegistro(int secHojaConsulta) {
        mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(), false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        templatePDF = new TemplatePDF(mRootView.getContext().getApplicationContext());
        templatePDF.openDocument();
        templatePDF.generateHojaConsultaPdf(HOJACONSULTA);
        templatePDF.closeDocument();
        printPdf();
    }

    public void printPdf() {
        try {
            File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
            if (arch.exists()) {
                Uri uri = FileProvider.getUriForFile(mRootView.getContext(), mRootView.getContext().getApplicationContext().getPackageName() + ".provider", arch);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                //intent.setAction(StorageManager.ACTION_CLEAR_APP_CACHE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //error si se quita esto
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mRootView.getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ActivityNotFoundException e) {
            Log.e("appViewPdf", e.toString());
        }
    }

    public void inicializarContorles(View rootView) {

        View.OnClickListener onClickedBHC = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedBHC(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSBHCExamen)
                .setOnClickListener(onClickedBHC);

        rootView.findViewById(R.id.chkbNBHCExamen)
                .setOnClickListener(onClickedBHC);


        View.OnClickListener onClickedDengue = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedSDengue(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSSerologiaDengueExamen)
                .setOnClickListener(onClickedDengue);

        rootView.findViewById(R.id.chkbNSerologiaDengueExamen)
                .setOnClickListener(onClickedDengue);

        View.OnClickListener onClickedChick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedSChick(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSSerologiaChickExamen)
                .setOnClickListener(onClickedChick);

        rootView.findViewById(R.id.chkbNSerologiaChickExamen)
                .setOnClickListener(onClickedChick);

        View.OnClickListener onClickedGotaGruesa = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedGotaGruesa(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSGotaGruesaExamen)
                .setOnClickListener(onClickedGotaGruesa);

        rootView.findViewById(R.id.chkbNGotaGruesaExamen)
                .setOnClickListener(onClickedGotaGruesa);

        View.OnClickListener onClickedExtendidoPeriferico = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedExtendidoPeriferico(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSExtendidoPerExamen)
                .setOnClickListener(onClickedExtendidoPeriferico);

        rootView.findViewById(R.id.chkbNExtendidoPerExamen)
                .setOnClickListener(onClickedExtendidoPeriferico);

        View.OnClickListener onClickedAST = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedAST(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSAST)
                .setOnClickListener(onClickedAST);

        rootView.findViewById(R.id.chkbNAST)
                .setOnClickListener(onClickedAST);

        View.OnClickListener onClickedEGO = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedEGO(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSEGOExamen)
                .setOnClickListener(onClickedEGO);

        rootView.findViewById(R.id.chkbNEGOExamen)
                .setOnClickListener(onClickedEGO);

        View.OnClickListener onClickedEGH = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedEGH(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSEGHExamen)
                .setOnClickListener(onClickedEGH);

        rootView.findViewById(R.id.chkbNEGHExamen)
                .setOnClickListener(onClickedEGH);

        View.OnClickListener onClickedCitologiaFecal = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedCitologiaFecal(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSCitologiaFecalExamen)
                .setOnClickListener(onClickedCitologiaFecal);

        rootView.findViewById(R.id.chkbNCitologiaFecalExamen)
                .setOnClickListener(onClickedCitologiaFecal);

        View.OnClickListener onClickedFactorReu = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedFactorReumatoide(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSFactorReuExamen)
                .setOnClickListener(onClickedFactorReu);

        rootView.findViewById(R.id.chkbNFactorReuExamen)
                .setOnClickListener(onClickedFactorReu);

        View.OnClickListener onClickedAlbumina = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedAlbumina(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSAlbuminaExamen)
                .setOnClickListener(onClickedAlbumina);

        rootView.findViewById(R.id.chkbNAlbuminaExamen)
                .setOnClickListener(onClickedAlbumina);

        View.OnClickListener onClickedBilirrubinas = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedBilrirubinas(view);
            }
        };

        rootView.findViewById(R.id.chkbSBilirrubinasExamen)
                .setOnClickListener(onClickedBilirrubinas);

        rootView.findViewById(R.id.chkbNBilirrubinasExamen)
                .setOnClickListener(onClickedBilirrubinas);

        View.OnClickListener onClickedCPK = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedCPK(view);
            }
        };

        rootView.findViewById(R.id.chkbSCPKExamen)
                .setOnClickListener(onClickedCPK);

        rootView.findViewById(R.id.chkbNCPKExamen)
                .setOnClickListener(onClickedCPK);

        View.OnClickListener onClickedColesterol = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedColesterol(view);
            }
        };

        rootView.findViewById(R.id.chkbSColesterolExamen)
                .setOnClickListener(onClickedColesterol);

        rootView.findViewById(R.id.chkbNColesterolExamen)
                .setOnClickListener(onClickedColesterol);

        View.OnClickListener onClickedInfluenza = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedInfluenza(view);
            }
        };

        rootView.findViewById(R.id.chkbSInfluenzaExamen)
                .setOnClickListener(onClickedInfluenza);

        rootView.findViewById(R.id.chkbNInfluenzaExamen)
                .setOnClickListener(onClickedInfluenza);

        View.OnClickListener onClickedOtro = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedOtro(view);
            }
        };

        rootView.findViewById(R.id.chkbSOtroExamen)
                .setOnClickListener(onClickedOtro);

        rootView.findViewById(R.id.chkbNOtroExamen)
                .setOnClickListener(onClickedOtro);

        /*View.OnClickListener onClickBtnEnvio = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_btnEnviarOrdLab();
            }
        };

        rootView.findViewById(R.id.btnEnviarOrdLab).setOnClickListener(onClickBtnEnvio);*/
    }

    //------------------------- Primer Grupo de CheckBox---------------------------

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedBHC(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbNBHCExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedSDengue(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedSChick(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedGotaGruesa(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedExtendidoPeriferico(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedAST(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST),
                view);
    }

    //-------------------------Segundo Grupo de CheckBox---------------------------
    public void onChkboxClickedEGO(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen),
                view);
    }

    public void onChkboxClickedEGH(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen),
                view);
    }

    public void onChkboxClickedCitologiaFecal(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen),
                view);
    }

    public void onChkboxClickedFactorReumatoide(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen),
                view);
    }

    public void onChkboxClickedAlbumina(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen),
                view);
    }

    //-------------------------Tercer Grupo de CheckBox---------------------------
    public void onChkboxClickedBilrirubinas(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen),
                view);
    }

    public void onChkboxClickedCPK(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen),
                view);
    }

    public void onChkboxClickedColesterol(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen),
                view);
    }

    public void onChkboxClickedInfluenza(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen),
                view);
    }

    public void onChkboxClickedOtro(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen),
                view);

        if(view.getId() == R.id.chkbSOtroExamen){
            getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.VISIBLE);
        }else{
            ((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).setText("");
            getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.INVISIBLE);
        }
    }

    public void onClick_btnEnviarOrdLab() {
        DialogInterface.OnClickListener preguntaEnviarOrdLab = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        validacionCheckBoxExamenes();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(getContext(),
                getResources().getString(R.string.msj_esta_seguro_de_guardar_examenes),getResources().getString(
                        R.string.title_estudio_sostenible), preguntaEnviarOrdLab);
    }

    public void validacionCheckBoxExamenes() {
        if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbNBHCExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen))) {
            presentaMensajeOtroExamen();
            return;
        }
        if(((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).isChecked()) {
            if(StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).getText().toString())) {
                MensajesHelper.mostrarMensajeInfo(getContext(),
                        getResources().getString(
                                R.string.msj_otro_examen_sin_texto), getResources().getString(
                                R.string.title_estudio_sostenible), null);
                return;
            }
        }
        guardarDatos();
    }

    public void presentaMensaje() {
        MensajesHelper.mostrarMensajeInfo(getContext(),
                getResources().getString(
                        R.string.msj_casillas_sin_marcar),getResources().getString(
                        R.string.title_estudio_sostenible), null);
    }

    public void presentaMensajeOtroExamen() {
        MensajesHelper.mostrarMensajeInfo(getContext(),
                getResources().getString(
                        R.string.msj_falta_marcar_otro_examen_lab),getResources().getString(
                        R.string.title_estudio_sostenible), null);
    }

    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSAST)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen)).setChecked(false);
                            //((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).setChecked(false);

                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSAST)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen)).setChecked(true);

                            ((CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNAST)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen)).setChecked(false);

                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNAST)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen)).setChecked(true);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(getActivity(),
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(getActivity(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void guardarDatos() {
        ProgressDialog PD;
        PD = new ProgressDialog(mRootView.getContext());
        PD.setTitle(getResources().getString(R.string.title_procesando));
        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
        mDbAdapter = new HojaConsultaDBAdapter(mRootView.getContext(), false,false);
        mDbAdapter.open();
        HojaConsultaOffLineDTO hojaConsulta = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + SEC_HOJA_CONSULTA + "'", null);
        hojaConsulta.setBhc(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbSBHCExamen))));
        hojaConsulta.setSerologiaDengue(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen))));
        hojaConsulta.setSerologiaChik(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen))));
        hojaConsulta.setGotaGruesa(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen))));
        hojaConsulta.setExtendidoPeriferico(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen))));
        hojaConsulta.setEgo(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen))));
        hojaConsulta.setEgh(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen))));
        hojaConsulta.setCitologiaFecal(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen))));
        hojaConsulta.setFactorReumatoideo(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen))));
        hojaConsulta.setAlbumina(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen))));
        hojaConsulta.setAstAlt(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST))));
        //hojaConsulta.setAstAlt(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSASTALTExamen), getActivity().findViewById(R.id.chkbNASTALTExamen)));
        hojaConsulta.setBilirrubinas(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen))));
        hojaConsulta.setCpk(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen))));
        hojaConsulta.setColesterol(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen))));
        hojaConsulta.setInfluenza(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen))));
        hojaConsulta.setOel(String.valueOf(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen))));
        hojaConsulta.setOtroExamenLab(((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).getText().toString());
        hojaConsulta.setEstado("4");
        if (hojaConsulta.getEgo().equals("0") || hojaConsulta.getEgh().equals("0")
            || hojaConsulta.getCitologiaFecal().equals("0") || hojaConsulta.getExtendidoPeriferico().equals("0")
            || hojaConsulta.getGotaGruesa().equals("0") || hojaConsulta.getSerologiaDengue().equals("0")
            || hojaConsulta.getSerologiaChik().equals("0") || hojaConsulta.getInfluenza().equals("0")) {

            Calendar fechaOrdenLab = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

            hojaConsulta.setFechaOrdenLaboratorio(sdf.format(fechaOrdenLab.getTime()));
        } else {
            hojaConsulta.setFechaOrdenLaboratorio("");
        }

        boolean resultado = mDbAdapter.editarHojaConsulta(hojaConsulta);
        if (resultado) {
            Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_SHORT).show();
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    PD.dismiss();
                }
            }.start();
        } else {
            Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }
    }

    public void cargarDatos() {
        if (HOJACONSULTA != null) {
            if (HOJACONSULTA.getBhc() != null) {
                if (HOJACONSULTA.getBhc().compareTo("0") == 0) {
                    CheckBox chkbSBHCExamen = (CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen);
                    chkbSBHCExamen.setChecked(true);
                } else {
                    CheckBox chkbSBHCExamen = (CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen);
                    chkbSBHCExamen.setChecked(true);

                }
            }
            if (HOJACONSULTA.getSerologiaDengue() != null) {
                if (HOJACONSULTA.getSerologiaDengue().compareTo("0") == 0) {
                    CheckBox chkbSSerologiaDengueExamen = (CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen);
                    chkbSSerologiaDengueExamen.setChecked(true);
                } else {
                    CheckBox chkbNSerologiaDengueExamen = (CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen);
                    chkbNSerologiaDengueExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getSerologiaChik() != null) {
                if (HOJACONSULTA.getSerologiaChik().compareTo("0") == 0) {
                    CheckBox chkbSSerologiaChickExamen = (CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen);
                    chkbSSerologiaChickExamen.setChecked(true);

                } else {
                    CheckBox chkbNSerologiaChickExamen = (CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen);
                    chkbNSerologiaChickExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getGotaGruesa() != null) {
                if (HOJACONSULTA.getGotaGruesa().compareTo("0") == 0) {
                    CheckBox chkbSGotaGruesaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen);
                    chkbSGotaGruesaExamen.setChecked(true);
                } else {
                    CheckBox chkbNGotaGruesaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen);
                    chkbNGotaGruesaExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getExtendidoPeriferico() != null) {
                if (HOJACONSULTA.getExtendidoPeriferico().compareTo("0") == 0) {
                    CheckBox chkbSExtendidoPerExamen = (CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen);
                    chkbSExtendidoPerExamen.setChecked(true);
                } else {
                    CheckBox chkbNExtendidoPerExamen = (CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen);
                    chkbNExtendidoPerExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getEgo() != null) {
                if (HOJACONSULTA.getEgo().compareTo("0") == 0) {
                    CheckBox chkbSEGOExamen = (CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen);
                    chkbSEGOExamen.setChecked(true);
                } else {
                    CheckBox chkbNEGOExamen = (CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen);
                    chkbNEGOExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getEgh() != null) {
                if (HOJACONSULTA.getEgh().compareTo("0") == 0) {
                    CheckBox chkbSEGHExamen = (CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen);
                    chkbSEGHExamen.setChecked(true);
                } else {
                    CheckBox chkbNEGHExamen = (CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen);
                    chkbNEGHExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getCitologiaFecal() != null) {
                if (HOJACONSULTA.getCitologiaFecal().compareTo("0") == 0) {
                    CheckBox chkbSCitologiaFecalExamen = (CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen);
                    chkbSCitologiaFecalExamen.setChecked(true);
                } else {
                    CheckBox chkbNCitologiaFecalExamen = (CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen);
                    chkbNCitologiaFecalExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getFactorReumatoideo() != null) {
                if (HOJACONSULTA.getFactorReumatoideo().compareTo("0") == 0) {
                    CheckBox chkbSFactorReuExamen = (CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen);
                    chkbSFactorReuExamen.setChecked(true);
                } else {
                    CheckBox chkbNFactorReuExamen = (CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen);
                    chkbNFactorReuExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getAlbumina() != null) {
                if (HOJACONSULTA.getAlbumina().compareTo("0") == 0) {
                    CheckBox chkbSAlbuminaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen);
                    chkbSAlbuminaExamen.setChecked(true);
                } else {
                    CheckBox chkbNAlbuminaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen);
                    chkbNAlbuminaExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getAstAlt() != null) {
                if (HOJACONSULTA.getAstAlt().compareTo("0") == 0) {
                    CheckBox chkbSAST = (CheckBox) getActivity().findViewById(R.id.chkbSAST);
                    chkbSAST.setChecked(true);
                } else {
                    CheckBox chkbNAST = (CheckBox) getActivity().findViewById(R.id.chkbNAST);
                    chkbNAST.setChecked(true);
                }
            }
            //================
            if (HOJACONSULTA.getBilirrubinas() != null) {
                if (HOJACONSULTA.getBilirrubinas().compareTo("0") == 0) {
                    CheckBox chkbSBilirrubinasExamen = (CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen);
                    chkbSBilirrubinasExamen.setChecked(true);
                } else {
                    CheckBox chkbNBilirrubinasExamen = (CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen);
                    chkbNBilirrubinasExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getCpk() != null) {
                if (HOJACONSULTA.getCpk().compareTo("0") == 0) {
                    CheckBox chkbSCPKExamen = (CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen);
                    chkbSCPKExamen.setChecked(true);
                } else {
                    CheckBox chkbNCPKExamen = (CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen);
                    chkbNCPKExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getColesterol() != null) {
                if (HOJACONSULTA.getColesterol().compareTo("0") == 0) {
                    CheckBox chkbSColesterolExamen = (CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen);
                    chkbSColesterolExamen.setChecked(true);
                } else {
                    CheckBox chkbNColesterolExamen = (CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen);
                    chkbNColesterolExamen.setChecked(true);
                }
            }
            if (HOJACONSULTA.getInfluenza() != null) {
                if (HOJACONSULTA.getInfluenza().compareTo("0") == 0) {
                    CheckBox chkbSInfluenzaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen);
                    chkbSInfluenzaExamen.setChecked(true);
                } else {
                    CheckBox chkbNInfluenzaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen);
                    chkbNInfluenzaExamen.setChecked(true);
                }
            }

            //Verificando si otra examen de laboratorio está marcado
            if (HOJACONSULTA.getOel() != null) {
                if (HOJACONSULTA.getOel().compareTo("0") == 0) {
                    CheckBox chkbSOtroExamen = (CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen);
                    chkbSOtroExamen.setChecked(true);

                    if ((!StringUtils.isNullOrEmpty(HOJACONSULTA.getOtroExamenLab()))) {
                        EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                        edtxtOtroExamenLab.setVisibility(View.VISIBLE);
                        ((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).setText(HOJACONSULTA.getOtroExamenLab());
                    }

                } else {
                    CheckBox chkbNOtroExamen = (CheckBox) getActivity().findViewById(R.id.chkbNOtroExamen);
                    chkbNOtroExamen.setChecked(true);
                    EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                    edtxtOtroExamenLab.setText("");
                    edtxtOtroExamenLab.setVisibility(View.INVISIBLE);
                }
            } else {
                EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                edtxtOtroExamenLab.setText("");
                edtxtOtroExamenLab.setVisibility(View.INVISIBLE);
            }
        }
    }
}
