package com.ics.ics_hc_offline.enfermeria;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.ics.ics_hc_offline.CssfvApp;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.consulta.TabFragment;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.databinding.FragmentHomeBinding;
import com.ics.ics_hc_offline.dto.InicioDTO;
import com.ics.ics_hc_offline.helper.LstViewGenericaInicio;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaEnfermeria extends Fragment implements AdapterView.OnItemClickListener {

    private LstViewGenericaInicio LST_ADAPTER_INICIO;
    private ListView LSTV_LISTA_ENFERMERIA;
    public int M_POSITION;
    private InicioDTO mInicioDto;
    private FragmentHomeBinding binding;
    private View VIEW;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Crear la lista a mostrar
        VIEW = inflater.inflate(R.layout.fragment_lista_enfermeria, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return VIEW;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HojaConsultaDBAdapter mDbAdapter = new HojaConsultaDBAdapter(view.getContext(),false,false);
        mDbAdapter.open();
        ArrayList<InicioDTO> listaEnfermeria = mDbAdapter.getListaEnfermeria();

        LSTV_LISTA_ENFERMERIA = (ListView)  view.findViewById(R.id.lstvListaEnfermeria);
        //ArrayAdapter<InicioDTO> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        LST_ADAPTER_INICIO = new LstViewGenericaInicio(view.getContext(), "ENFERMERIA", listaEnfermeria, getResources());
        LSTV_LISTA_ENFERMERIA.setAdapter(LST_ADAPTER_INICIO);
        LSTV_LISTA_ENFERMERIA.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        InicioDTO inicioDto = LST_ADAPTER_INICIO.getItem(position);
        DialogInterface.OnClickListener confirmacionAtencionPacienteDialogClickListener = new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                enviarPacienteEnfermeria(position);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
        MensajesHelper.mostrarMensajeYesNo(getContext(), String.format(getResources().getString(
                R.string.msj_confirmacion_atencion_consulta_paciente), inicioDto.getNomPaciente()),
                getResources().getString(R.string.title_estudio_sostenible), confirmacionAtencionPacienteDialogClickListener);
        //Toast.makeText(getContext(), "TEST" + " - " + position, Toast.LENGTH_LONG).show();
    }

    /***
     * Metodo que veririfica si el paciente ya esta siendo atendido
     * en el area de enfermeria
     */
    public void enviarPacienteEnfermeria(int mPosition) {
        this.M_POSITION = mPosition;
        mInicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);

        int IdUsuarioLogeado = ((CssfvApp) getContext().getApplicationContext()).getInfoSessionWSDTO().getUserId();
        Integer IdUsuarioEnfermeria = (StringUtils.isNullOrEmpty(mInicioDto.getUsuarioEnfermeria()) ||
                mInicioDto.getUsuarioEnfermeria().compareTo("null") == 0) ? null :
                Integer.valueOf(mInicioDto.getUsuarioEnfermeria());

        if (IdUsuarioEnfermeria != null) {
            if (IdUsuarioEnfermeria > 0) {
                if (IdUsuarioEnfermeria != IdUsuarioLogeado) {
                    MensajesHelper.mostrarMensajeInfo(getContext(),
                            getResources().getString(R.string.msj_paciente_ya_atendido),
                            getResources().getString(R.string.app_name), null);
                } else {
                    goDatosPreclinicos(IdUsuarioEnfermeria);
                }
            } else {
                goDatosPreclinicos(IdUsuarioEnfermeria);
            }
        }
    }

    public void goDatosPreclinicos(Integer IdUsuarioEnfermeria) {
        AsyncTask<Void, Void, Void> TabsFragment = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(VIEW.getContext());
                PD.setTitle(getResources().getString(R.string.title_procesando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                Bundle args = new Bundle();
                mInicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);
                args.putString("secHojaConsulta", String.valueOf(mInicioDto.getSecHojaConsulta()));
                args.putString("nombrePaciente", mInicioDto.getNomPaciente());
                args.putString("codExpediente", String.valueOf(mInicioDto.getCodExpediente()));
                args.putString("fechaNac", mInicioDto.getFechaNac());
                args.putString("sexo", mInicioDto.getSexo());
                args.putString("numHojaConsulta", String.valueOf(mInicioDto.getNumHojaConsulta()));
                args.putString("numOrdenLlegada", String.valueOf(mInicioDto.getNumOrdenLlegada()));
                args.putString("horasv", String.valueOf(mInicioDto.getHorasv()));
                args.putString("usuarioEnfermeria", String.valueOf(IdUsuarioEnfermeria));

                Fragment newFragment = new DatosPreClinicos();
                newFragment.setArguments(args);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.nav_host_fragment_content_main, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        PD.dismiss();
                    }
                }.start();
            }
        };
        TabsFragment.execute((Void[])null);
    }
}
