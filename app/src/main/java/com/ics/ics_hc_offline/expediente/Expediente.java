package com.ics.ics_hc_offline.expediente;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.databinding.FragmentHomeBinding;
import com.ics.ics_hc_offline.dto.ExpedienteDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.InicioDTO;
import com.ics.ics_hc_offline.helper.LstViewGenericaExp;
import com.ics.ics_hc_offline.helper.LstViewGenericaInicio;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.tools.ImpresionPdfDialog;
import com.ics.ics_hc_offline.utils.PdfHojaConsulta;
import com.ics.ics_hc_offline.utils.StringUtils;
import com.ics.ics_hc_offline.utils.TemplatePDF;

import net.sqlcipher.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Expediente extends Fragment implements AdapterView.OnItemClickListener {
    ArrayList<ExpedienteDTO> ARRAY_DATOS_EXPEDIENTE;
    String CODIGO_EXPEDIENTE;
    private View VIEW;
    private ListView LST_LISTA_EXP;
    private LstViewGenericaExp LSTV_ADAPTER_EXP;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    private int SEC_HOJA_CONSULTA;
    TemplatePDF templatePDF;
    ProgressDialog PD;
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        VIEW = inflater.inflate(R.layout.activity_expediente_busqueda, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton btnBuscarExp = (ImageButton) getActivity().findViewById(R.id.ibtBuscarExp);
        btnBuscarExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtxBuscarExpediente= (EditText)getActivity().findViewById(R.id.edtxBuscarExpediente);
                if (StringUtils.isNumeric(edtxBuscarExpediente.getText().toString())) {
                    CODIGO_EXPEDIENTE = edtxBuscarExpediente.getText().toString();
                    cargarExpediente(CODIGO_EXPEDIENTE);
                }
                else {
                    MensajesHelper.mostrarMensajeInfo(getActivity(),
                            "Ingrese un código de expediente valido", getResources().getString(
                                    R.string.app_name), null);
                }

            }
        });
    }

    public void cargarExpediente(String codExpediente) {
        HojaConsultaDBAdapter mDbAdapter = new HojaConsultaDBAdapter(VIEW.getContext(),false,false);
        mDbAdapter.open();
        ArrayList<ExpedienteDTO> listaExpedienteConsulta = mDbAdapter.getListaConsultaExpediente(codExpediente);
        if (listaExpedienteConsulta.size() <= 0) {
            Toast.makeText(getActivity(), "No se encontro información", Toast.LENGTH_LONG).show();
        }
        LST_LISTA_EXP = (ListView)  VIEW.findViewById(R.id.lstvHojaConsultaExp);
        //ArrayAdapter<InicioDTO> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        LSTV_ADAPTER_EXP = new LstViewGenericaExp(VIEW.getContext(), "EXPEDIENTE", listaExpedienteConsulta, getResources());
        LST_LISTA_EXP.setAdapter(LSTV_ADAPTER_EXP);
        LST_LISTA_EXP.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ExpedienteDTO expediente = LSTV_ADAPTER_EXP.getItem(position);
        SEC_HOJA_CONSULTA = expediente.getSecHojaConsulta();
        /*ImpresionPdfDialog DlogBuscar=new ImpresionPdfDialog();
        DlogBuscar.show(getChildFragmentManager(), "Seleccionar");*/
        showAlertDialogReimpresionHC();
    }

    /*Metodo para hacer la seleccion de la impresora*/
    private void showAlertDialogReimpresionHC() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VIEW.getContext());
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("Visualizar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PD = new ProgressDialog(VIEW.getContext());
                        PD.setTitle(getResources().getString(R.string.title_procesando));
                        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                        PD.setCancelable(false);
                        PD.setIndeterminate(true);
                        PD.show();
                        onClickPDF(SEC_HOJA_CONSULTA);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PD.dismiss();
                        dialog.cancel();
                    }
                })
                //Set your icon here
                .setTitle("PDF")
                .setIcon(R.drawable.pdf64);
        AlertDialog alert = builder.create();
        alert.show();//showing the dialog
    }

    public void onClickPDF(Integer secHojaConsulta) {
        ActivityCompat.requestPermissions((Activity) VIEW.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        mDbAdapter = new HojaConsultaDBAdapter(VIEW.getContext(), false,false);
        mDbAdapter.open();
        HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + secHojaConsulta + "'", null);
        templatePDF = new TemplatePDF(VIEW.getContext().getApplicationContext());
        templatePDF.openDocument();
        templatePDF.generateHojaConsultaPdf(HOJACONSULTA);
        templatePDF.closeDocument();
        try {
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    printPdf();
                }
            }.start();
        } catch (Exception e) {
            PD.dismiss();
            Log.e("onClickPDF", e.toString());
        }

    }

    public void pdfView() {
        templatePDF.viewPdf();
    }

    public void printPdf() {
        try {
            File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
            if (arch.exists()) {
                Uri uri = FileProvider.getUriForFile(VIEW.getContext(), VIEW.getContext().getApplicationContext().getPackageName() + ".provider", arch);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                //intent.setAction(StorageManager.ACTION_CLEAR_APP_CACHE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //error si se quita esto
                try {
                    startActivity(intent);
                    PD.dismiss();
                } catch (ActivityNotFoundException e) {
                    PD.dismiss();
                    Toast.makeText(VIEW.getContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ActivityNotFoundException e) {
            PD.dismiss();
            Log.e("appViewPdf", e.toString());
        }
    }

    private ArrayList<String[]> getClients() {
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1","Santiago","Carballo"});
        return rows;
    }
}
