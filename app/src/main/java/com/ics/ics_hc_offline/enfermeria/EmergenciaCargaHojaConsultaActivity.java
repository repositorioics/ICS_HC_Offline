package com.ics.ics_hc_offline.enfermeria;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ics.ics_hc_offline.CssfvApp;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.databinding.FragmentHomeBinding;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.InicioDTO;
import com.ics.ics_hc_offline.dto.PacienteDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.ui.home.HomeViewModel;
import com.ics.ics_hc_offline.utils.Constants;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class EmergenciaCargaHojaConsultaActivity extends Fragment {
    private FragmentHomeBinding binding;
    public int COD_EXPEDIENTE = 0;
    public Context CONTEXT;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View emergencia = inflater.inflate(R.layout.fragment_emergencia_carga_hoja_consulta_layout, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        configureBtnGuardar(emergencia);
        configureImageButton(emergencia);
        return emergencia;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.CONTEXT = view.getContext();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void configureBtnGuardar(View view) {
        /***
         * Metodo que se ejecuta en el evento onClick del boton guardar.
         */
        Button button = (Button) view.findViewById(R.id.btnGuardarHojaConsulta);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtxCodigoExpediente = (EditText) view.findViewById(R.id.edtxtCodigoExpediente);
                String valor = edtxCodigoExpediente.getText().toString();
                if (!StringUtils.isNullOrEmpty(valor)) {
                    COD_EXPEDIENTE = Integer.valueOf(valor);
                }
                if (COD_EXPEDIENTE > 0) {
                    DialogInterface.OnClickListener preguntaGuardarDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //progress.dismiss();
                                    //Enviar a guardar
                                    crearConsulta();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(getActivity(), getResources().getString(R.string.msj_aviso_crear_hoja_consulta),
                            getResources().getString(R.string.title_estudio_sostenible), preguntaGuardarDialogClickListener);
                }else{
                    MensajesHelper.mostrarMensajeInfo(getActivity(),
                            getResources().getString(
                                    R.string.msj_realizar_busqueda_para_guardar), getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        });
    }

    private void configureImageButton(View view) {
        // TODO Auto-generated method stub
        ImageButton btnBusquedaLupa = (ImageButton) view.findViewById(R.id.imgBotonBusquedaLupa);
        btnBusquedaLupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarCodigoPaciente(view)) {
                    HojaConsultaDBAdapter mDbAdapter = new HojaConsultaDBAdapter(binding.getRoot().getContext(),false,false);
                    mDbAdapter.open();
                    try {
                        EditText edtxCodigoExpediente = (EditText) view.findViewById(R.id.edtxtCodigoExpediente);
                        String codigoExpediente = edtxCodigoExpediente.getText().toString();

                        PacienteDTO paciente = mDbAdapter.getParticipante(MainDBConstants.codExpediente  + "='" + codigoExpediente + "'", null);
                        if (paciente == null) {
                            MensajesHelper.mostrarMensajeInfo(getActivity(),
                                    getResources().getString(
                                            R.string.msj_no_se_encontro_codigo_paciente), getResources().getString(
                                            R.string.title_estudio_sostenible), null);

                            return;
                        } else {
                            if (paciente.getEstado() == '1') {
                                Toast.makeText(getActivity(), "Paciente en estado retirado!", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                EditText edtxtNombrePaciente = (EditText) view.findViewById(R.id.edtxtNombrePaciente);
                                edtxtNombrePaciente.setText(paciente.getNomPaciente());
                            }
                        }

                    } catch(SQLException e){
                    }
                    catch(Exception e){
                    }
                    finally{
                        mDbAdapter.close();
                    }
                }
            }
        });
    }

    /***
     * Metodo que se ejecuta en el evento onClick del boton guardar.
     */

    private boolean verificarCodigoPaciente (View view) {
        EditText edtxCodigoExpediente = (EditText) view.findViewById(R.id.edtxtCodigoExpediente);

        if (StringUtils.isNullOrEmpty(edtxCodigoExpediente.getText().toString())) {
            MensajesHelper.mostrarMensajeInfo(getActivity(),
                    getResources().getString(
                            R.string.msj_no_se_encontro_codigo_paciente), getResources().getString(
                            R.string.title_estudio_sostenible), null);

            return false;
        }
        return true;
    }

    /*
    * Metodo para enviar el participante a consulta.
    * */
    private void crearConsulta() {
        ProgressDialog PD;
        PD = new ProgressDialog(CONTEXT);
        PD.setTitle(getResources().getString(R.string.title_procesando));
        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
        HojaConsultaDBAdapter mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();

        //String filtro = MainDBConstants.codExpediente + "='" + COD_EXPEDIENTE + "'";
        //String consulta = "Inicial";
        //String filtroConsulta = MainDBConstants.consulta + "='" + consulta + "'";00
        boolean exisHojaConsulta = mDbAdapter.validarSiExisteUnaHojaActiva(COD_EXPEDIENTE, null);
        if (exisHojaConsulta) {
            Toast toast = Toast.makeText(CONTEXT.getApplicationContext(), "Ya existe una hoja de consulta para ese codigo", Toast.LENGTH_LONG);
            toast.show();
            PD.dismiss();
        } else {
            boolean result = mDbAdapter.createNewHojaConsulta(COD_EXPEDIENTE, CONTEXT);
            if (result) {
                Toast toast = Toast.makeText(CONTEXT.getApplicationContext(), R.string.success, Toast.LENGTH_LONG);
                toast.show();
                PD.dismiss();
            } else {
                Toast t = Toast.makeText(CONTEXT.getApplicationContext(),
                        getString(R.string.error, "Al crear la hoja de consulta"),
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                t.show();
                PD.dismiss();
            }
        }
    }
}
