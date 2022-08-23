package com.ics.ics_hc_offline.enfermeria;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.ics.ics_hc_offline.CssfvApp;
import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.consulta.ListaConsulta;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.databinding.FragmentHomeBinding;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.utils.DateUtils;
import com.ics.ics_hc_offline.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DatosPreClinicos extends Fragment {
    private FragmentHomeBinding binding;
    private String vFueraRango = new String();
    private Context CONTEXT;
    private int secHojaConsulta = 0;
    private HojaConsultaDBAdapter mDbAdapter;
    private static HojaConsultaOffLineDTO HOJACONSULTA = null;
    //private static List<UsuarioDTO> USUARIOS = null;
    private ArrayAdapter<UsuarioDTO> adapter;
    private ArrayList<UsuarioDTO> mUsuarios;
    private View VIEW;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        VIEW = inflater.inflate(R.layout.preclinicos_layout, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        CONTEXT = VIEW.getContext();
        onClick_btnEnviar(VIEW);
        onClick_btnCancel(VIEW);
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

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        ((EditText)view.findViewById(R.id.edtxtNombreApellido)).setText(String.valueOf(bundle.getString("nombrePaciente")));
        ((EditText)view.findViewById(R.id.edtxtCodigo)).setText(String.valueOf(bundle.getString("codExpediente")));
        ((EditText)view.findViewById(R.id.edtxtSexo)).setText(String.valueOf(bundle.getString("sexo")));
        Date date = new Date();
        String strDate = df.format(date);
        ((EditText)view.findViewById(R.id.edtxtFecha)).setText(strDate);
        //((EditText)view.findViewById(R.id.edtxtNHojaConsulta)).setText(String.valueOf(bundle.getString("numHojaConsulta")));
        if (!bundle.getString("horasv").equals("null")) {
            ((EditText)view.findViewById(R.id.edtxtHora)).setText(String.valueOf(bundle.getString("horasv")));
        } else {
            Calendar hora = Calendar.getInstance();
            df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            ((EditText)view.findViewById(R.id.edtxtHora)).setText(df.format(hora.getTime()));
        }
        String fechaNac = bundle.getString("fechaNac");
        long milliSeconds= Long.parseLong(fechaNac);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliSeconds);
        String edad = DateUtils.obtenerEdad(calendar);
        ((EditText)view.findViewById(R.id.edtxtEdad)).setText(edad);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        //Número de expediente fisico con la fecha de nacimiento
        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
        String expedienteFisico = numExpFis.format(cal.getTime());
        ((EditText)view.findViewById(R.id.edtxtExpediente)).setText(expedienteFisico);

        ImageButton imgBusquedaEnfermeria = (ImageButton) view.findViewById(R.id.imgBusquedaEnfermeria);
        imgBusquedaEnfermeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busquedaEnfermeria();
            }
        });

        /*if (Integer.parseInt(bundle.getString("usuarioEnfermeria")) > 0) {
            Toast.makeText(getActivity(), "Es una edicion", Toast.LENGTH_LONG).show();
        }*/
        cargarEnfermeria();
        cargarDatosPreclinicos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /***
     * Metodo que realiza el evento de click del botón Enviar.
     * @param view, Objeto
     */
    public void onClick_btnEnviar(View view) {
        Button btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFueraRango = "";
                if (validarCamposRequeridos(view)) {
                    mensajePrevioGuardar(view);
                }
            }
        });
    }

    public void onClick_btnCancel(View view){
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment listaEnfermeria = new ListaEnfermeria();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_content_main, listaEnfermeria);
                transaction.commit();
            }
        });

    }

    public void busquedaEnfermeria() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
        builder.setTitle("Buscar");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.activity_dialog_buscar_generico, null, false);
        final EditText search = (EditText) mView.findViewById(R.id.etxtBuscar);
        builder.setView(mView)
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        Toast.makeText(CONTEXT, "Buscando enfermer@: " + search.getText(), Toast.LENGTH_LONG).show();
                        String busqueda = (search.getText() != null) ? search.getText().toString() : null;
                        filtrarSpinnerEnfermeria(busqueda);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void filtrarSpinnerEnfermeria(final String busqueda) {
        ArrayList<UsuarioDTO> resultadoBusqueda = this.mUsuarios;
        if(!StringUtils.isNullOrEmpty(busqueda)) {
            resultadoBusqueda = new ArrayList<>(Collections2.filter(this.mUsuarios, new Predicate<UsuarioDTO>() {
                @Override
                public boolean apply(UsuarioDTO usuario) {
                    return usuario.getNombre().toLowerCase().contains(busqueda.toLowerCase());
                }
            }));
        }

        adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  resultadoBusqueda);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) VIEW.findViewById(R.id.spnEnfermeria)).setAdapter(adapter);
    }

    private void cargarEnfermeria() {
        mDbAdapter = new HojaConsultaDBAdapter(CONTEXT,false,false);
        mDbAdapter.open();
        List<UsuarioDTO> USUARIOS = mDbAdapter.getUsuariosEnfermeria();
        if (USUARIOS.size() > 0) {
            UsuarioDTO usuario = new UsuarioDTO();
            ArrayList<UsuarioDTO> lstUsuario = new ArrayList<>();
            usuario.setId(0);
            usuario.setNombre("Seleccione al enfermer@");
            lstUsuario.add(usuario);
            lstUsuario.addAll(USUARIOS);
            Spinner spnEnfermeria = (Spinner) VIEW.findViewById(R.id.spnEnfermeria);
            adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  lstUsuario);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEnfermeria.setAdapter(adapter);
            this.mUsuarios = lstUsuario;
        }
    }

    /***
     * Metodo para ejecutar el mensaje de si esta seguro de enviar la hoja de consulta.
     */
    private void mensajePrevioGuardar(View view){
        DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        enviandoDatosPreclinicosServicio(view);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                getResources().getString(
                        R.string.msj_enviar_hoja_consulta),getResources().getString(
                        R.string.title_estudio_sostenible),preguntaEnviarDialogClickListener);
    }

    /***
     * Metodo para llamar el servicio del enviar la hoja de consulta.
     */
    private void enviandoDatosPreclinicosServicio(View view) {
        /*mDbAdapter = new HojaConsultaDBAdapter(binding.getRoot().getContext(),false,false);
        mDbAdapter.open();*/
        ProgressDialog PD;
        PD = new ProgressDialog(CONTEXT);
        if (secHojaConsulta > 0) {

            if (HOJACONSULTA != null) {
                //int IdUsuarioLogeado = ((CssfvApp) view.getContext().getApplicationContext()).getInfoSessionWSDTO().getUserId();
                int idUsuarioEnfermeria =  ((UsuarioDTO) ((Spinner)VIEW.findViewById(R.id.spnEnfermeria)).getSelectedItem()).getId();

                EditText edtxtPeso = (EditText) view.findViewById(R.id.edtxtPeso);
                EditText edtxtTalla = (EditText) view.findViewById(R.id.edtxtTalla);
                EditText edtxtTemp = (EditText) view.findViewById(R.id.edtxtTemp);
                EditText horasV = (EditText) view.findViewById(R.id.edtxtHora);
                EditText expedienteFisico = (EditText) view.findViewById(R.id.edtxtExpediente);

                HOJACONSULTA.setPesoKg(Double.valueOf(edtxtPeso.getText().toString()));
                HOJACONSULTA.setTallaCm(Double.valueOf(edtxtTalla.getText().toString()));
                HOJACONSULTA.setTemperaturac(Double.valueOf(edtxtTemp.getText().toString()));
                HOJACONSULTA.setExpedienteFisico(expedienteFisico.getText().toString());
                HOJACONSULTA.setHorasv(horasV.getText().toString());
                //HOJACONSULTA.setUsuarioEnfermeria(IdUsuarioLogeado);
                HOJACONSULTA.setUsuarioEnfermeria(idUsuarioEnfermeria);
                HOJACONSULTA.setEstado("2");

                boolean resultado = mDbAdapter.editarHojaConsulta(HOJACONSULTA);
                if (resultado) {
                    Toast.makeText(getActivity(), "Operación exitosa", Toast.LENGTH_SHORT).show();
                    PD.setTitle(getResources().getString(R.string.title_procesando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            PD.dismiss();
                            Fragment newFragment = new ListaEnfermeria();
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack
                            transaction.replace(R.id.nav_host_fragment_content_main, newFragment);
                            transaction.addToBackStack(null);
                            // Commit the transaction
                            transaction.commit();
                        }
                    }.start();

                } else {
                    Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_LONG).show();
                    PD.dismiss();
                }
            } else {
                Toast.makeText(getActivity(), "Hoja consulta no encontrada", Toast.LENGTH_LONG).show();
                PD.dismiss();
            }
        } else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }
    }

    public void cargarDatosPreclinicos() {
        if (HOJACONSULTA.getPesoKg() > 0 && HOJACONSULTA.getTallaCm() > 0
                && HOJACONSULTA.getTemperaturac() > 0) {
            ((EditText) VIEW.findViewById(R.id.edtxtPeso)).setText(String.valueOf(HOJACONSULTA.getPesoKg()));
            ((EditText) VIEW.findViewById(R.id.edtxtTalla)).setText(String.valueOf(HOJACONSULTA.getTallaCm()));
            ((EditText) VIEW.findViewById(R.id.edtxtTemp)).setText(String.valueOf(HOJACONSULTA.getTemperaturac()));

            VIEW.findViewById(R.id.btnEnviar).setEnabled(false);
            VIEW.findViewById(R.id.btnCancel).setEnabled(false);
           //VIEW.findViewById(R.id.btnNoAtiendeLlamado).setEnabled(false);

            VIEW.findViewById(R.id.edtxtPeso).setEnabled(false);
            VIEW.findViewById(R.id.edtxtTalla).setEnabled(false);
            VIEW.findViewById(R.id.edtxtTemp).setEnabled(false);

            if(!StringUtils.isNullOrEmpty(String.valueOf(HOJACONSULTA.getUsuarioEnfermeria()))) {
                UsuarioDTO usuarioEncontrado = new UsuarioDTO();
                for(int i = 0; i < adapter.getCount(); i++) {
                    usuarioEncontrado = adapter.getItem(i);
                    if(usuarioEncontrado.getId() ==  Integer.parseInt(String.valueOf(HOJACONSULTA.getUsuarioEnfermeria()))) {
                        break;
                    }
                }
                int posicion = adapter.getPosition(usuarioEncontrado);
                ((Spinner) VIEW.findViewById(R.id.spnEnfermeria)).setSelection(posicion);
            }
        }
    }

    private boolean validarCamposRequeridos(View view) {
        EditText edtxtPeso = (EditText) view.findViewById(R.id.edtxtPeso);
        EditText edtxtTalla = (EditText) view.findViewById(R.id.edtxtTalla);
        EditText edtxtTemp = (EditText) view.findViewById(R.id.edtxtTemp);
        if (StringUtils.isNullOrEmpty(edtxtPeso.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                    getResources().getString(
                            R.string.msj_completar_informacion),getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return false;
        }  else if (StringUtils.isNullOrEmpty(edtxtTalla.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                    getResources().getString(
                            R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);

            return  false;
        } else if (StringUtils.isNullOrEmpty(edtxtTemp.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                    getResources().getString(
                            R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return  false;
        }
        if(((Spinner) VIEW.findViewById(R.id.spnEnfermeria)).getSelectedItem() == null ||
                ((UsuarioDTO)((Spinner) VIEW.findViewById(R.id.spnEnfermeria)).getSelectedItem()).getId() == 0) {
            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                    getResources().getString(
                            R.string.msj_seleccionar_enfermero), getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return  false;
        }
        int cont = 0;
        //if (!estaEnRango(1, 100, edtxtPeso.getText().toString())) { // Rangos anteriores
        if (!estaEnRango(1, 200, edtxtPeso.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_peso));
            cont++;
        }
        //if (!estaEnRango(20, 200, edtxtTalla.getText().toString())) { // Rangos Anteriores
        if (!estaEnRango(20, 220, edtxtTalla.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_talla));
            cont++;
        }
        // if (!estaEnRango(34, 42, edtxtTemp.getText().toString())) { // Rangos Anteriores
        if (!estaEnRango(35.5, 41, edtxtTemp.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp));
            cont++;
        }
        if (cont >0){
            MensajesHelper.mostrarMensajeInfo(view.getContext(), String.format(
                    getResources().getString(
                            R.string.msj_aviso_control_cambios1), vFueraRango), getResources().getString(
                    R.string.title_estudio_sostenible),null);
            return false;
        }
        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }
}
