package com.ics.ics_hc_offline.ui.task;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Properties;

import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.ConsEstudiosDTO;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;
import com.ics.ics_hc_offline.dto.EstadosHojasDTO;
import com.ics.ics_hc_offline.dto.EstudioCatalogoDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaPartsDTO;
import com.ics.ics_hc_offline.dto.PacienteDTO;
import com.ics.ics_hc_offline.dto.RolesDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.ui.activities.server.DownloadBaseActivity;
import com.ics.ics_hc_offline.utils.StringUtils;
import com.ics.ics_hc_offline.wsclass.DataNodoItemArray;


public class DownloadBaseTask extends DownloadTask {
    public static final String NAMESPACE = "http://webservice.estudiocohortecssfv.sts_ni.com/";
    //public static String URL = "http://192.168.1.97:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
    public static String URL = "http://192.168.1.94:8081/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
    private static int TIME_OUT = 200000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private final Context mContext;
    // Storage Permissions

    private static final DownloadBaseActivity Activity = new DownloadBaseActivity();

    static {
        try {
            Properties props = new Properties();
            InputStream inputStream = new FileInputStream("/sdcard/cssfv/config/config.properties");
            props.load(inputStream);
            URL = props.getProperty("CSSFV.URLWS");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public DownloadBaseTask(Context context) {
        mContext = context;
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
    }

    protected static final String TAG = DownloadBaseTask.class.getSimpleName();
    private HojaConsultaDBAdapter hojaConsultaAdapter = null;

    private List<UsuarioDTO> mUsuarios = new ArrayList<>();
    private List<RolesDTO> mRoles = new ArrayList<>();
    private List<ConsEstudiosDTO> mConsEstudios  = new ArrayList<>();
    private List<DiagnosticoDTO> mDiagnosticos = new ArrayList<>();
    private List<EscuelaPacienteDTO> mEscuelas = new ArrayList<>();
    private List<EstudioCatalogoDTO> mEstudios = new ArrayList<>();
    private List<HojaConsultaOffLineDTO> mHojasConsultas  = new ArrayList<>();
    private List<PacienteDTO> mPacientes  = new ArrayList<>();
    private List<EstadosHojasDTO> mEstadosHojas  = new ArrayList<>();
    private List<HojaConsultaPartsDTO> mHojasConsultasParts  = new ArrayList<>();


    public static final String USUARIOS = "1";
    public static final String ROLES = "2";
    public static final String CONS_ESTUDIOS = "3";
    public static final String DIAGNOSTICOS= "4";
    public static final String ESCUELAS = "5";
    public static final String ESTUDIOS = "6";
    public static final String HOJAS_CONSULTAS = "7";
    public static final String PACIENTES = "8";
    public static final String ESTADOS_HOJAS = "9";
    public static final String HOJAS_CONSULTAS_PARTS = "10";
    private static final String TOTAL_TASK_GENERALES = "11";

    private static final String METODO_GET_USUARIOS = "getUsuariosOffline";
    private static final String ACCIONSOAP_GET_USUARIOS = NAMESPACE + METODO_GET_USUARIOS;

    private static final String METODO_GET_ROLES = "getRolesOffline";
    private static final String ACCIONSOAP_GET_ROLES = NAMESPACE + METODO_GET_ROLES;

    public static final String METODO_LISTADIAGNOSTICO = "getListaDiagnostico";
    public static final String ACCIOSOAP_LISTADIAGNOSTICO= NAMESPACE + METODO_LISTADIAGNOSTICO;

    public static final String METODO_LISTA_ESCUELA = "getTodasEscuela";
    public static final String ACCIOSOAP_LISTA_ESCUELA = NAMESPACE + METODO_LISTA_ESCUELA;

    public static final String METODO_ESTUDIO_CATALOGO = "getEstudiosOffline";
    public static final String ACCIOSOAP_ESTUDIO_CATALOGO = NAMESPACE + METODO_ESTUDIO_CATALOGO;

    public static final String METODO_PARTICIPANTES = "getParticipantesOffline";
    public static final String ACCIOSOAP_PARTICIPANTES = NAMESPACE + METODO_PARTICIPANTES;

    public static final String METODO_ESTADOSHOJAS = "getEstadosHojasOffline";
    public static final String ACCIOSOAP_ESTADOSHOJAS = NAMESPACE + METODO_ESTADOSHOJAS;

    public static final String METODO_CONS_ESTUDIOS = "getConsEstudiosOffline";
    public static final String ACCIOSOAP_CONS_ESTUDIOS = NAMESPACE + METODO_CONS_ESTUDIOS;

    public static final String METODO_HOJAS_CONSULTAS = "getHojasConsultasOffline";
    public static final String ACCIOSOAP_HOJAS_CONSULTAS = NAMESPACE + METODO_HOJAS_CONSULTAS;

    public static final String METODO_COUNT_HOJAS_CONSULTAS = "getCantidadHojasConsultas";
    public static final String ACCIOSOAP_COUNT_HOJAS_CONSULTAS = NAMESPACE + METODO_COUNT_HOJAS_CONSULTAS;

    public static final String METODO_HOJAS_CONSULTAS_PARTS = "getPartesHojasConsultasOffline";
    public static final String ACCIOSOAP_HOJAS_CONSULTAS_PARTS = NAMESPACE + METODO_HOJAS_CONSULTAS_PARTS;

    private String error = null;

    @Override
    protected String doInBackground(String... values) {
        try {
            error = descargarUsuarios();
            error = descargarRoles();
            error = descargarDiagnosticos();
            error = descargarEscuelas();
            error = descargarEstudios();
            error = descargarParticipantes();
            error = descargarEstadosHojas();
            error = descargarConsEstudios();
            error = descargarHojasConsultas();
            error = descargarPartesHojasConsultas();
            if (error!=null) return error;
        } catch (Exception e) {
            // Regresa error al descargar
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
        publishProgress("Abriendo base de datos...","1","1");
        hojaConsultaAdapter = new HojaConsultaDBAdapter(mContext, false,false);
        hojaConsultaAdapter.open();
        //Borrar los datos de la base de datos
        hojaConsultaAdapter.borrarUsuarios();
        hojaConsultaAdapter.borrarRoles();
        hojaConsultaAdapter.borrarConsEstudios();
        hojaConsultaAdapter.borrarDiagnosticos();
        hojaConsultaAdapter.borrarEscuelas();
        hojaConsultaAdapter.borrarEstudios();
        hojaConsultaAdapter.borrarHojaConsulta();
        hojaConsultaAdapter.borrarParticipantes();
        hojaConsultaAdapter.borrarEstadosHojas();
        hojaConsultaAdapter.borrarPartesHojaConsulta();
        try {
            if (mUsuarios != null) {
                publishProgress("Insertando usuarios", USUARIOS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertUsuariosBySql(MainDBConstants.USUARIO_TABLE, mUsuarios);
            }
            if (mRoles != null) {
                publishProgress("Insertando roles", ROLES, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertRolesBySql(MainDBConstants.ROLES_TABLE, mRoles);
            }
            if (mConsEstudios != null) {
                publishProgress("Insertando cons estudios", CONS_ESTUDIOS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertConsEstutiosBySql(mConsEstudios);
            }
            if (mDiagnosticos != null) {
                publishProgress("Insertando diagnosticos", DIAGNOSTICOS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertDiagnosticosBySql(mDiagnosticos);
            }
            if (mEscuelas != null) {
                publishProgress("Insertando escuelas", ESCUELAS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertEscuelasBySql(mEscuelas);
            }
            if (mEstudios != null) {
                publishProgress("Insertando estudios", ESTUDIOS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertEstudiosBySql(mEstudios);
            }
            if (mPacientes != null) {
                publishProgress("Insertando participantes", PACIENTES, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertParticipantesBySql(mPacientes);
            }
            if (mEstadosHojas != null) {
                publishProgress("Insertando estados hojas", ESTADOS_HOJAS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertEstadosHojasBySql(mEstadosHojas);
            }
            if (mHojasConsultas != null) {
                publishProgress("Insertando hojas de consultas", HOJAS_CONSULTAS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertHojasConsultasBySql(mHojasConsultas);
            }
            if (mHojasConsultasParts != null) {
                publishProgress("Insertando partes de la hojas de consultas", HOJAS_CONSULTAS_PARTS, TOTAL_TASK_GENERALES);
                hojaConsultaAdapter.bulkInsertPartesHojasConsultasBySql(mHojasConsultasParts);
            }
        } catch (Exception e) {
            // Regresa error al insertar
            e.printStackTrace();
            return e.getLocalizedMessage();
        }finally {
            hojaConsultaAdapter.close();
        }
        return error;
    }

    // Metodo para descargar los usuarios
    protected String descargarUsuarios() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_USUARIOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando usuarios",USUARIOS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIONSOAP_GET_USUARIOS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray usuarioArrayJson = (JSONArray) jObject.get("resultado");

                    for (int i = 0; i < usuarioArrayJson.length(); i++) {
                        JSONObject usuarioJson  = usuarioArrayJson.getJSONObject(i);
                        UsuarioDTO usuario = new UsuarioDTO();
                        usuario.setId(usuarioJson.getInt("id"));
                        usuario.setNombre(usuarioJson.getString("nombre"));
                        usuario.setUsuario(usuarioJson.getString("usuario"));
                        usuario.setCodigoPersonal(usuarioJson.getString("codigoPersonal"));
                        usuario.setPass(usuarioJson.getString("pass"));
                        mUsuarios.add(usuario);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los roles
    protected String descargarRoles() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_ROLES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando roles",ROLES, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIONSOAP_GET_ROLES, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray rolArrayJson = (JSONArray) jObject.get("resultado");

                    for (int i = 0; i < rolArrayJson.length(); i++) {
                        JSONObject rolJson  = rolArrayJson.getJSONObject(i);
                        RolesDTO rol = new RolesDTO();
                        rol.setId(rolJson.getString("id"));
                        rol.setNombre(rolJson.getString("nombre"));
                        rol.setUsuario(rolJson.getString("usuario"));
                        mRoles.add(rol);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los diagnosticos
    protected String descargarDiagnosticos() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_LISTADIAGNOSTICO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando diagnosticos",DIAGNOSTICOS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_LISTADIAGNOSTICO, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray diagArrayJson = (JSONArray) jObject.get("resultado");

                    for (int i = 0; i < diagArrayJson.length(); i++) {
                        JSONObject diagJson  = diagArrayJson.getJSONObject(i);
                        DiagnosticoDTO diagnosticoDTO = new DiagnosticoDTO();
                        diagnosticoDTO.setSecDiagnostico(diagJson.getInt("secDiagnostico"));
                        diagnosticoDTO.setCodigoDignostico(diagJson.getString("codigoDiagnostico"));
                        diagnosticoDTO.setDiagnostico(diagJson.getString("diagnostico"));
                        //diagnosticoDTO.setEstado(diagJson.getString("estado").charAt(0));
                        mDiagnosticos.add(diagnosticoDTO);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar todas las escuelas
    protected String descargarEscuelas() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_LISTA_ESCUELA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando escuelas", ESCUELAS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_LISTA_ESCUELA, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    EscuelaPacienteDTO[] arrEscuelas = gson.fromJson(inicioArrayJson.toString(), EscuelaPacienteDTO[].class);

                    ArrayList<EscuelaPacienteDTO> lstEscuelas = new ArrayList<>(Arrays.asList(arrEscuelas));

                    mEscuelas = lstEscuelas;

                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar el catalogo de estudio
    protected String descargarEstudios() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_ESTUDIO_CATALOGO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando estudios", ESTUDIOS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_ESTUDIO_CATALOGO, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray estCatArrayJson = (JSONArray) jObject.get("resultado");

                    for (int i = 0; i < estCatArrayJson.length(); i++) {
                        JSONObject estCatJson  = estCatArrayJson.getJSONObject(i);
                        EstudioCatalogoDTO estudioCatalogoDTO = new EstudioCatalogoDTO();
                        estudioCatalogoDTO.setCodEstudio(estCatJson.getString("codEstudio"));
                        estudioCatalogoDTO.setDescEstudio(estCatJson.getString("descEstudio"));
                        mEstudios.add(estudioCatalogoDTO);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los participantes
    protected String descargarParticipantes() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_PARTICIPANTES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando pacientes", PACIENTES, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_PARTICIPANTES, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray pacienteArrayJson = (JSONArray) jObject.get("resultado");

                    for (int i = 0; i < pacienteArrayJson.length(); i++) {
                        JSONObject pacienteJson  = pacienteArrayJson.getJSONObject(i);
                        PacienteDTO pacienteDTO = new PacienteDTO();
                        pacienteDTO.setSecPaciente(pacienteJson.getInt("secPaciente"));
                        pacienteDTO.setCodExpediente(pacienteJson.getInt("codExpediente"));
                        pacienteDTO.setNomPaciente(pacienteJson.getString("nombrePaciente"));
                        pacienteDTO.setSexo(pacienteJson.getString("sexo"));
                        pacienteDTO.setDireccion(pacienteJson.getString("direccion"));
                        String fechaNac = pacienteJson.getString("fechaNac");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = format.parse(fechaNac);
                            pacienteDTO.setFechaNac(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        pacienteDTO.setEstado(pacienteJson.getString("retirado").charAt(0));

                        mPacientes.add(pacienteDTO);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los estados de las hojas
    protected String descargarEstadosHojas() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_ESTADOSHOJAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando estados hojas", ESTADOS_HOJAS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_ESTADOSHOJAS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    EstadosHojasDTO[] arrEstados = gson.fromJson(inicioArrayJson.toString(), EstadosHojasDTO[].class);

                    ArrayList<EstadosHojasDTO> lstEstadosHojas = new ArrayList<>(Arrays.asList(arrEstados));

                    mEstadosHojas = lstEstadosHojas;
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los Cons Estudios
    protected String descargarConsEstudios() throws Exception {
        try {
            //this.RES = res;

            SoapObject request = new SoapObject(NAMESPACE, METODO_CONS_ESTUDIOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando cons estudios", CONS_ESTUDIOS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_CONS_ESTUDIOS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    ConsEstudiosDTO[] arrConsEstudios = gson.fromJson(inicioArrayJson.toString(), ConsEstudiosDTO[].class);

                    ArrayList<ConsEstudiosDTO> lstConsEstudios = new ArrayList<>(Arrays.asList(arrConsEstudios));

                    mConsEstudios = lstConsEstudios;
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar los Cons Estudios
    protected Integer descargarCantidadHojasConsulta() throws Exception {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_COUNT_HOJAS_CONSULTAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_COUNT_HOJAS_CONSULTAS, sobre, this.HEADER_PROPERTY);
            Integer resultado = Integer.valueOf(sobre.getResponse().toString());
            return resultado;
        }catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar las hojas de consultas
    protected String descargarHojasConsultas() throws Exception {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_HOJAS_CONSULTAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando hojas de consultas", HOJAS_CONSULTAS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_HOJAS_CONSULTAS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");
                    for (int i = 0; i < inicioArrayJson.length(); i++) {
                        JSONObject hojaConsultaJson = inicioArrayJson.getJSONObject(i);
                        HojaConsultaOffLineDTO hojaConsultaOffLineDTO = new HojaConsultaOffLineDTO();
                        hojaConsultaOffLineDTO.setSecHojaConsulta(hojaConsultaJson.getInt("secHojaConsulta"));
                        hojaConsultaOffLineDTO.setCodExpediente(hojaConsultaJson.getInt("codExpediente"));
                        hojaConsultaOffLineDTO.setNumHojaConsulta(hojaConsultaJson.getInt("numHojaConsulta"));
                        hojaConsultaOffLineDTO.setNumOrdenLlegada(hojaConsultaJson.getInt("numOrdenLlegada"));
                        hojaConsultaOffLineDTO.setEstado(hojaConsultaJson.getString("estado"));
                        hojaConsultaOffLineDTO.setFechaConsulta(hojaConsultaJson.getString("fechaConsulta"));
                        if (!hojaConsultaJson.get("usuarioEnfermeria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUsuarioEnfermeria(hojaConsultaJson.getInt("usuarioEnfermeria"));
                        if (!hojaConsultaJson.get("usuarioMedico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUsuarioMedico(hojaConsultaJson.getInt("usuarioMedico"));
                        if (!hojaConsultaJson.get("pesoKg").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setPesoKg(hojaConsultaJson.getDouble("pesoKg"));
                        } else {
                            hojaConsultaOffLineDTO.setPesoKg((double) 0);
                        }
                        if (!hojaConsultaJson.get("tallaCm").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setTallaCm(hojaConsultaJson.getDouble("tallaCm"));
                        } else {
                            hojaConsultaOffLineDTO.setTallaCm((double) 0);
                        }
                        if (!hojaConsultaJson.get("temperaturac").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setTemperaturac(hojaConsultaJson.getDouble("temperaturac"));
                        } else {
                            hojaConsultaOffLineDTO.setTemperaturac((double) 0);
                        }
                        if (!hojaConsultaJson.get("presion").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPresion(hojaConsultaJson.getString("presion"));
                        if (!hojaConsultaJson.get("fciaResp").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFciaResp(hojaConsultaJson.getInt("fciaResp"));
                        if (!hojaConsultaJson.get("fciaCard").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFciaCard(hojaConsultaJson.getInt("fciaCard"));
                        if (!hojaConsultaJson.get("lugarAtencion").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLugarAtencion(hojaConsultaJson.getString("lugarAtencion"));
                        if (!hojaConsultaJson.get("consulta").toString().equals("null"))
                            hojaConsultaOffLineDTO.setConsulta(hojaConsultaJson.getString("consulta"));
                        if (!hojaConsultaJson.get("segChick").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSegChick(hojaConsultaJson.getString("segChick"));
                        if (!hojaConsultaJson.get("turno").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTurno(hojaConsultaJson.getString("turno"));
                        if (!hojaConsultaJson.get("temMedc").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setTemMedc(hojaConsultaJson.getDouble("temMedc"));
                        } else {
                            hojaConsultaOffLineDTO.setTemMedc((double) 0);
                        }
                        if (!hojaConsultaJson.get("ultDiaFiebre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUltDiaFiebre(hojaConsultaJson.getString("ultDiaFiebre"));
                        if (!hojaConsultaJson.get("ultDosisAntipiretico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUltDosisAntipiretico(hojaConsultaJson.getString("ultDosisAntipiretico"));
                        if (!hojaConsultaJson.get("fiebre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFiebre(hojaConsultaJson.getString("fiebre"));
                        if (!hojaConsultaJson.get("astenia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAstenia(hojaConsultaJson.getString("astenia"));
                        if (!hojaConsultaJson.get("asomnoliento").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAsomnoliento(hojaConsultaJson.getString("asomnoliento"));
                        if (!hojaConsultaJson.get("malEstado").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMalEstado(hojaConsultaJson.getString("malEstado"));
                        if (!hojaConsultaJson.get("perdidaConsciencia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPerdidaConsciencia(hojaConsultaJson.getString("perdidaConsciencia"));
                        if (!hojaConsultaJson.get("inquieto").toString().equals("null"))
                            hojaConsultaOffLineDTO.setInquieto(hojaConsultaJson.getString("inquieto"));
                        if (!hojaConsultaJson.get("convulsiones").toString().equals("null"))
                            hojaConsultaOffLineDTO.setConvulsiones(hojaConsultaJson.getString("convulsiones"));
                        if (!hojaConsultaJson.get("hipotermia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHipotermia(hojaConsultaJson.getString("hipotermia"));
                        if (!hojaConsultaJson.get("letargia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLetargia(hojaConsultaJson.getString("letargia"));
                        if (!hojaConsultaJson.get("cefalea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCefalea(hojaConsultaJson.getString("cefalea"));
                        if (!hojaConsultaJson.get("rigidezCuello").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRigidezCuello(hojaConsultaJson.getString("rigidezCuello"));
                        if (!hojaConsultaJson.get("inyeccionConjuntival").toString().equals("null"))
                            hojaConsultaOffLineDTO.setInyeccionConjuntival(hojaConsultaJson.getString("inyeccionConjuntival"));
                        if (!hojaConsultaJson.get("hemorragiaSuconjuntival").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHemorragiaSuconjuntival(hojaConsultaJson.getString("hemorragiaSuconjuntival"));
                        if (!hojaConsultaJson.get("dolorRetroocular").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDolorRetroocular(hojaConsultaJson.getString("dolorRetroocular"));
                        if (!hojaConsultaJson.get("fontanelaAbombada").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFontanelaAbombada(hojaConsultaJson.getString("fontanelaAbombada"));
                        if (!hojaConsultaJson.get("ictericiaConuntival").toString().equals("null"))
                            hojaConsultaOffLineDTO.setIctericiaConuntival(hojaConsultaJson.getString("ictericiaConuntival"));
                        if (!hojaConsultaJson.get("eritema").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEritema(hojaConsultaJson.getString("eritema"));
                        if (!hojaConsultaJson.get("dolorGarganta").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDolorGarganta(hojaConsultaJson.getString("dolorGarganta"));
                        if (!hojaConsultaJson.get("adenopatiasCervicales").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAdenopatiasCervicales(hojaConsultaJson.getString("adenopatiasCervicales"));
                        if (!hojaConsultaJson.get("exudado").toString().equals("null"))
                            hojaConsultaOffLineDTO.setExudado(hojaConsultaJson.getString("exudado"));
                        if (!hojaConsultaJson.get("petequiasMucosa").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPetequiasMucosa(hojaConsultaJson.getString("petequiasMucosa"));
                        if (!hojaConsultaJson.get("tos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTos(hojaConsultaJson.getString("tos"));
                        if (!hojaConsultaJson.get("rinorrea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRinorrea(hojaConsultaJson.getString("rinorrea"));
                        if (!hojaConsultaJson.get("congestionNasal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCongestionNasal(hojaConsultaJson.getString("congestionNasal"));
                        if (!hojaConsultaJson.get("otalgia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtalgia(hojaConsultaJson.getString("otalgia"));
                        if (!hojaConsultaJson.get("aleteoNasal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAleteoNasal(hojaConsultaJson.getString("aleteoNasal"));
                        if (!hojaConsultaJson.get("apnea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setApnea(hojaConsultaJson.getString("apnea"));
                        if (!hojaConsultaJson.get("respiracionRapida").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRespiracionRapida(hojaConsultaJson.getString("respiracionRapida"));
                        if (!hojaConsultaJson.get("quejidoEspiratorio").toString().equals("null"))
                            hojaConsultaOffLineDTO.setQuejidoEspiratorio(hojaConsultaJson.getString("quejidoEspiratorio"));
                        if (!hojaConsultaJson.get("estiradorReposo").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEstiradorReposo(hojaConsultaJson.getString("estiradorReposo"));
                        if (!hojaConsultaJson.get("tirajeSubcostal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTirajeSubcostal(hojaConsultaJson.getString("tirajeSubcostal"));
                        if (!hojaConsultaJson.get("sibilancias").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSibilancias(hojaConsultaJson.getString("sibilancias"));
                        if (!hojaConsultaJson.get("crepitos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCrepitos(hojaConsultaJson.getString("crepitos"));
                        if (!hojaConsultaJson.get("roncos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRoncos(hojaConsultaJson.getString("roncos"));
                        if (!hojaConsultaJson.get("otraFif").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtraFif(hojaConsultaJson.getString("otraFif"));
                        if (!hojaConsultaJson.get("nuevaFif").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNuevaFif(hojaConsultaJson.getString("nuevaFif"));
                        if (!hojaConsultaJson.get("pocoApetito").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPocoApetito(hojaConsultaJson.getString("pocoApetito"));
                        if (!hojaConsultaJson.get("nausea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNausea(hojaConsultaJson.getString("nausea"));
                        if (!hojaConsultaJson.get("dificultadAlimentarse").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDificultadAlimentarse(hojaConsultaJson.getString("dificultadAlimentarse"));
                        if (!hojaConsultaJson.get("vomito12horas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setVomito12horas(hojaConsultaJson.getString("vomito12horas"));

                        if (!hojaConsultaJson.get("diarrea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDiarrea(hojaConsultaJson.getString("diarrea"));
                        if (!hojaConsultaJson.get("diarreaSangre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDiarreaSangre(hojaConsultaJson.getString("diarreaSangre"));
                        if (!hojaConsultaJson.get("estrenimiento").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEstrenimiento(hojaConsultaJson.getString("estrenimiento"));
                        if (!hojaConsultaJson.get("dolorAbIntermitente").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDolorAbIntermitente(hojaConsultaJson.getString("dolorAbIntermitente"));
                        if (!hojaConsultaJson.get("dolorAbContinuo").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDolorAbContinuo(hojaConsultaJson.getString("dolorAbContinuo"));
                        if (!hojaConsultaJson.get("epigastralgia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEpigastralgia(hojaConsultaJson.getString("epigastralgia"));
                        if (!hojaConsultaJson.get("intoleranciaOral").toString().equals("null"))
                            hojaConsultaOffLineDTO.setIntoleranciaOral(hojaConsultaJson.getString("intoleranciaOral"));
                        if (!hojaConsultaJson.get("distensionAbdominal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDistensionAbdominal(hojaConsultaJson.getString("distensionAbdominal"));
                        if (!hojaConsultaJson.get("hepatomegalia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHepatomegalia(hojaConsultaJson.getString("hepatomegalia"));
                        if (!hojaConsultaJson.get("lenguaMucosasSecas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLenguaMucosasSecas(hojaConsultaJson.getString("lenguaMucosasSecas"));
                        if (!hojaConsultaJson.get("pliegueCutaneo").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPliegueCutaneo(hojaConsultaJson.getString("pliegueCutaneo"));
                        if (!hojaConsultaJson.get("orinaReducida").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOrinaReducida(hojaConsultaJson.getString("orinaReducida"));
                        if (!hojaConsultaJson.get("bebeConSed").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBebeConSed(hojaConsultaJson.getString("bebeConSed"));
                        if (!hojaConsultaJson.get("ojosHundidos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOjosHundidos(hojaConsultaJson.getString("ojosHundidos"));
                        if (!hojaConsultaJson.get("fontanelaHundida").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFontanelaHundida(hojaConsultaJson.getString("fontanelaHundida"));
                        if (!hojaConsultaJson.get("sintomasUrinarios").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSintomasUrinarios(hojaConsultaJson.getString("sintomasUrinarios"));
                        if (!hojaConsultaJson.get("leucocituria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLeucocituria(hojaConsultaJson.getString("leucocituria"));
                        if (!hojaConsultaJson.get("nitritos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNitritos(hojaConsultaJson.getString("nitritos"));
                        if (!hojaConsultaJson.get("bilirrubinuria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBilirrubinuria(hojaConsultaJson.getString("bilirrubinuria"));
                        if (!hojaConsultaJson.get("altralgia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAltralgia(hojaConsultaJson.getString("altralgia"));
                        if (!hojaConsultaJson.get("mialgia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMialgia(hojaConsultaJson.getString("mialgia"));
                        if (!hojaConsultaJson.get("lumbalgia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLumbalgia(hojaConsultaJson.getString("lumbalgia"));
                        if (!hojaConsultaJson.get("dolorCuello").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDolorCuello(hojaConsultaJson.getString("dolorCuello"));
                        if (!hojaConsultaJson.get("tenosinovitis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTenosinovitis(hojaConsultaJson.getString("tenosinovitis"));
                        if (!hojaConsultaJson.get("artralgiaProximal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setArtralgiaProximal(hojaConsultaJson.getString("artralgiaProximal"));
                        if (!hojaConsultaJson.get("artralgiaDistal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setArtralgiaDistal(hojaConsultaJson.getString("artralgiaDistal"));
                        if (!hojaConsultaJson.get("conjuntivitis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setConjuntivitis(hojaConsultaJson.getString("conjuntivitis"));
                        if (!hojaConsultaJson.get("edemaMunecas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEdemaMunecas(hojaConsultaJson.getString("edemaMunecas"));
                        if (!hojaConsultaJson.get("edemaCodos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEdemaCodos(hojaConsultaJson.getString("edemaCodos"));
                        if (!hojaConsultaJson.get("edemaHombros").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEdemaHombros(hojaConsultaJson.getString("edemaHombros"));
                        if (!hojaConsultaJson.get("edemaRodillas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEdemaRodillas(hojaConsultaJson.getString("edemaRodillas"));
                        if (!hojaConsultaJson.get("edemaTobillos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEdemaTobillos(hojaConsultaJson.getString("edemaTobillos"));
                        if (!hojaConsultaJson.get("rahsLocalizado").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRahsLocalizado(hojaConsultaJson.getString("rahsLocalizado"));
                        if (!hojaConsultaJson.get("rahsGeneralizado").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRahsGeneralizado(hojaConsultaJson.getString("rahsGeneralizado"));
                        if (!hojaConsultaJson.get("rashEritematoso").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRashEritematoso(hojaConsultaJson.getString("rashEritematoso"));
                        if (!hojaConsultaJson.get("rahsMacular").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRahsMacular(hojaConsultaJson.getString("rahsMacular"));
                        if (!hojaConsultaJson.get("rashPapular").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRashPapular(hojaConsultaJson.getString("rashPapular"));
                        if (!hojaConsultaJson.get("rahsMoteada").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRahsMoteada(hojaConsultaJson.getString("rahsMoteada"));
                        if (!hojaConsultaJson.get("ruborFacial").toString().equals("null"))
                            hojaConsultaOffLineDTO.setRuborFacial(hojaConsultaJson.getString("ruborFacial"));
                        if (!hojaConsultaJson.get("equimosis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEquimosis(hojaConsultaJson.getString("equimosis"));
                        if (!hojaConsultaJson.get("cianosisCentral").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCianosisCentral(hojaConsultaJson.getString("cianosisCentral"));
                        if (!hojaConsultaJson.get("ictericia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setIctericia(hojaConsultaJson.getString("ictericia"));
                        if (!hojaConsultaJson.get("obeso").toString().equals("null"))
                            hojaConsultaOffLineDTO.setObeso(hojaConsultaJson.getString("obeso"));
                        if (!hojaConsultaJson.get("sobrepeso").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSobrepeso(hojaConsultaJson.getString("sobrepeso"));
                        if (!hojaConsultaJson.get("sospechaProblema").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSospechaProblema(hojaConsultaJson.getString("sospechaProblema"));
                        if (!hojaConsultaJson.get("normal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNormal(hojaConsultaJson.getString("normal"));
                        if (!hojaConsultaJson.get("bajoPeso").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBajoPeso(hojaConsultaJson.getString("bajoPeso"));
                        if (!hojaConsultaJson.get("bajoPesoSevero").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBajoPesoSevero(hojaConsultaJson.getString("bajoPesoSevero"));
                        if (!hojaConsultaJson.get("lactanciaMaterna").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLactanciaMaterna(hojaConsultaJson.getString("lactanciaMaterna"));
                        if (!hojaConsultaJson.get("vacunasCompletas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setVacunasCompletas(hojaConsultaJson.getString("vacunasCompletas"));
                        if (!hojaConsultaJson.get("vacunaInfluenza").toString().equals("null"))
                            hojaConsultaOffLineDTO.setVacunaInfluenza(hojaConsultaJson.getString("vacunaInfluenza"));
                        if (!hojaConsultaJson.get("fechaVacuna").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaVacuna(hojaConsultaJson.getString("fechaVacuna"));
                        if (!hojaConsultaJson.get("interconsultaPediatrica").toString().equals("null"))
                            hojaConsultaOffLineDTO.setInterconsultaPediatrica(hojaConsultaJson.getString("interconsultaPediatrica"));
                        if (!hojaConsultaJson.get("referenciaHospital").toString().equals("null"))
                            hojaConsultaOffLineDTO.setReferenciaHospital(hojaConsultaJson.getString("referenciaHospital"));
                        if (!hojaConsultaJson.get("referenciaDengue").toString().equals("null"))
                            hojaConsultaOffLineDTO.setReferenciaDengue(hojaConsultaJson.getString("referenciaDengue"));
                        if (!hojaConsultaJson.get("referenciaIrag").toString().equals("null"))
                            hojaConsultaOffLineDTO.setReferenciaIrag(hojaConsultaJson.getString("referenciaIrag"));
                        if (!hojaConsultaJson.get("referenciaChik").toString().equals("null"))
                            hojaConsultaOffLineDTO.setReferenciaChik(hojaConsultaJson.getString("referenciaChik"));
                        if (!hojaConsultaJson.get("eti").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEti(hojaConsultaJson.getString("eti"));
                        if (!hojaConsultaJson.get("irag").toString().equals("null"))
                            hojaConsultaOffLineDTO.setIrag(hojaConsultaJson.getString("irag"));
                        if (!hojaConsultaJson.get("neumonia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNeumonia(hojaConsultaJson.getString("neumonia"));
                        if (!hojaConsultaJson.get("saturaciono2").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSaturaciono2(hojaConsultaJson.getInt("saturaciono2"));
                        if (!hojaConsultaJson.get("imc").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setImc(hojaConsultaJson.getDouble("imc"));
                        } else {
                            hojaConsultaOffLineDTO.setImc((double) 0);
                        }
                        if (!hojaConsultaJson.get("categoria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCategoria(hojaConsultaJson.getString("categoria"));
                        if (!hojaConsultaJson.get("cambioCategoria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCambioCategoria(hojaConsultaJson.getString("cambioCategoria"));
                        if (!hojaConsultaJson.get("manifestacionHemorragica").toString().equals("null"))
                            hojaConsultaOffLineDTO.setManifestacionHemorragica(hojaConsultaJson.getString("manifestacionHemorragica"));
                        if (!hojaConsultaJson.get("pruebaTorniquetePositiva").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPruebaTorniquetePositiva(hojaConsultaJson.getString("pruebaTorniquetePositiva"));
                        if (!hojaConsultaJson.get("petequia10Pt").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPetequia10Pt(hojaConsultaJson.getString("petequia10Pt"));
                        if (!hojaConsultaJson.get("petequia20Pt").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPetequia20Pt(hojaConsultaJson.getString("petequia20Pt"));
                        if (!hojaConsultaJson.get("pielExtremidadesFrias").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPielExtremidadesFrias(hojaConsultaJson.getString("pielExtremidadesFrias"));
                        if (!hojaConsultaJson.get("palidezEnExtremidades").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPalidezEnExtremidades(hojaConsultaJson.getString("palidezEnExtremidades"));
                        if (!hojaConsultaJson.get("epistaxis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEpistaxis(hojaConsultaJson.getString("epistaxis"));
                        if (!hojaConsultaJson.get("gingivorragia").toString().equals("null"))
                            hojaConsultaOffLineDTO.setGingivorragia(hojaConsultaJson.getString("gingivorragia"));
                        if (!hojaConsultaJson.get("petequiasEspontaneas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPetequiasEspontaneas(hojaConsultaJson.getString("petequiasEspontaneas"));
                        if (!hojaConsultaJson.get("llenadoCapilar2seg").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLlenadoCapilar2seg(hojaConsultaJson.getString("llenadoCapilar2seg"));
                        if (!hojaConsultaJson.get("cianosis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCianosis(hojaConsultaJson.getString("cianosis"));
                        if (!hojaConsultaJson.get("linfocitosaAtipicos").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setLinfocitosaAtipicos(hojaConsultaJson.getDouble("linfocitosaAtipicos"));
                        } else {
                            hojaConsultaOffLineDTO.setLinfocitosaAtipicos((double) 0);
                        }
                        if (!hojaConsultaJson.get("fechaLinfocitos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaLinfocitos(hojaConsultaJson.getString("fechaLinfocitos"));
                        if (!hojaConsultaJson.get("hipermenorrea").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHipermenorrea(hojaConsultaJson.getString("hipermenorrea"));
                        if (!hojaConsultaJson.get("hematemesis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHematemesis(hojaConsultaJson.getString("hematemesis"));
                        if (!hojaConsultaJson.get("melena").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMelena(hojaConsultaJson.getString("melena"));
                        if (!hojaConsultaJson.get("hemoconcentracion").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setHemoconcentracion(Short.valueOf(hojaConsultaJson.getString("hemoconcentracion")));
                        } else {
                            hojaConsultaOffLineDTO.setHemoconcentracion((short) 0);
                        }
                        if (!hojaConsultaJson.get("hospitalizado").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHospitalizado(hojaConsultaJson.getString("hospitalizado"));
                        if (!hojaConsultaJson.get("hospitalizadoEspecificar").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHospitalizadoEspecificar(hojaConsultaJson.getString("hospitalizadoEspecificar"));
                        if (!hojaConsultaJson.get("transfusionSangre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTransfusionSangre(hojaConsultaJson.getString("transfusionSangre"));
                        if (!hojaConsultaJson.get("transfusionEspecificar").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTransfusionEspecificar(hojaConsultaJson.getString("transfusionEspecificar"));
                        if (!hojaConsultaJson.get("tomandoMedicamento").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTomandoMedicamento(hojaConsultaJson.getString("tomandoMedicamento"));
                        if (!hojaConsultaJson.get("medicamentoEspecificar").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMedicamentoEspecificar(hojaConsultaJson.getString("medicamentoEspecificar"));
                        /*if(!hojaConsultaJson.get("medicamentoDistinto").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMedicamentoDistinto(hojaConsultaJson.getString("medicamentoDistinto"));*/
                        if (!hojaConsultaJson.get("medicamentoDistinto").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMedicamentoDistinto(hojaConsultaJson.getString("medicamentoDistinto"));
                        if (!hojaConsultaJson.get("medicamentoDistEspecificar").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMedicamentoDistEspecificar(hojaConsultaJson.getString("medicamentoDistEspecificar"));
                        if (!hojaConsultaJson.get("bhc").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBhc(hojaConsultaJson.getString("bhc"));
                        if (!hojaConsultaJson.get("serologiaDengue").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSerologiaDengue(hojaConsultaJson.getString("serologiaDengue"));
                        if (!hojaConsultaJson.get("serologiaChik").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSerologiaChik(hojaConsultaJson.getString("serologiaChik"));
                        if (!hojaConsultaJson.get("gotaGruesa").toString().equals("null"))
                            hojaConsultaOffLineDTO.setGotaGruesa(hojaConsultaJson.getString("gotaGruesa"));
                        if (!hojaConsultaJson.get("extendidoPeriferico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setExtendidoPeriferico(hojaConsultaJson.getString("extendidoPeriferico"));
                        if (!hojaConsultaJson.get("ego").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEgo(hojaConsultaJson.getString("ego"));
                        if (!hojaConsultaJson.get("egh").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEgh(hojaConsultaJson.getString("egh"));
                        if (!hojaConsultaJson.get("citologiaFecal").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCitologiaFecal(hojaConsultaJson.getString("citologiaFecal"));
                        if (!hojaConsultaJson.get("factorReumatoideo").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFactorReumatoideo(hojaConsultaJson.getString("factorReumatoideo"));
                        if (!hojaConsultaJson.get("albumina").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAlbumina(hojaConsultaJson.getString("albumina"));
                        if (!hojaConsultaJson.get("astAlt").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAstAlt(hojaConsultaJson.getString("astAlt"));
                        if (!hojaConsultaJson.get("bilirrubinas").toString().equals("null"))
                            hojaConsultaOffLineDTO.setBilirrubinas(hojaConsultaJson.getString("bilirrubinas"));
                        if (!hojaConsultaJson.get("cpk").toString().equals("null"))
                            hojaConsultaOffLineDTO.setCpk(hojaConsultaJson.getString("cpk"));
                        if (!hojaConsultaJson.get("colesterol").toString().equals("null"))
                            hojaConsultaOffLineDTO.setColesterol(hojaConsultaJson.getString("colesterol"));
                        if (!hojaConsultaJson.get("influenza").toString().equals("null"))
                            hojaConsultaOffLineDTO.setInfluenza(hojaConsultaJson.getString("influenza"));
                        if (!hojaConsultaJson.get("otroExamenLab").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtroExamenLab(hojaConsultaJson.getString("otroExamenLab"));
                        if (!hojaConsultaJson.get("numOrdenLaboratorio").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNumOrdenLaboratorio(hojaConsultaJson.getInt("numOrdenLaboratorio"));
                        if (!hojaConsultaJson.get("acetaminofen").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAcetaminofen(hojaConsultaJson.getString("acetaminofen"));
                        if (!hojaConsultaJson.get("asa").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAsa(hojaConsultaJson.getString("asa"));
                        if (!hojaConsultaJson.get("ibuprofen").toString().equals("null"))
                            hojaConsultaOffLineDTO.setIbuprofen(hojaConsultaJson.getString("ibuprofen"));
                        if (!hojaConsultaJson.get("penicilina").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPenicilina(hojaConsultaJson.getString("penicilina"));
                        if (!hojaConsultaJson.get("amoxicilina").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAmoxicilina(hojaConsultaJson.getString("amoxicilina"));
                        if (!hojaConsultaJson.get("dicloxacilina").toString().equals("null"))
                            hojaConsultaOffLineDTO.setDicloxacilina(hojaConsultaJson.getString("dicloxacilina"));
                        if (!hojaConsultaJson.get("otroAntibiotico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtroAntibiotico(hojaConsultaJson.getString("otroAntibiotico"));
                        if (!hojaConsultaJson.get("furazolidona").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFurazolidona(hojaConsultaJson.getString("furazolidona"));
                        if (!hojaConsultaJson.get("metronidazolTinidazol").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMetronidazolTinidazol(hojaConsultaJson.getString("metronidazolTinidazol"));
                        if (!hojaConsultaJson.get("albendazolMebendazol").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAlbendazolMebendazol(hojaConsultaJson.getString("albendazolMebendazol"));
                        if (!hojaConsultaJson.get("sulfatoFerroso").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSulfatoFerroso(hojaConsultaJson.getString("sulfatoFerroso"));
                        if (!hojaConsultaJson.get("sueroOral").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSueroOral(hojaConsultaJson.getString("sueroOral"));
                        if (!hojaConsultaJson.get("sulfatoZinc").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSulfatoZinc(hojaConsultaJson.getString("sulfatoZinc"));
                        if (!hojaConsultaJson.get("liquidosIv").toString().equals("null"))
                            hojaConsultaOffLineDTO.setLiquidosIv(hojaConsultaJson.getString("liquidosIv"));
                        if (!hojaConsultaJson.get("prednisona").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPrednisona(hojaConsultaJson.getString("prednisona"));
                        if (!hojaConsultaJson.get("hidrocortisonaIv").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHidrocortisonaIv(hojaConsultaJson.getString("hidrocortisonaIv"));
                        if (!hojaConsultaJson.get("salbutamol").toString().equals("null"))
                            hojaConsultaOffLineDTO.setSalbutamol(hojaConsultaJson.getString("salbutamol"));
                        if (!hojaConsultaJson.get("oseltamivir").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOseltamivir(hojaConsultaJson.getString("oseltamivir"));
                        if (!hojaConsultaJson.get("historiaExamenFisico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHistoriaExamenFisico(hojaConsultaJson.getString("historiaExamenFisico"));
                        if (!hojaConsultaJson.get("diagnostico1").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setDiagnostico1(Short.parseShort(hojaConsultaJson.getString("diagnostico1")));
                        } else {
                            hojaConsultaOffLineDTO.setDiagnostico1((short) 0);
                        }
                        if (!hojaConsultaJson.get("diagnostico2").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setDiagnostico2(Short.parseShort(hojaConsultaJson.getString("diagnostico2")));
                        } else {
                            hojaConsultaOffLineDTO.setDiagnostico2((short) 0);
                        }
                        if (!hojaConsultaJson.get("diagnostico3").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setDiagnostico3(Short.parseShort(hojaConsultaJson.getString("diagnostico3")));
                        } else {
                            hojaConsultaOffLineDTO.setDiagnostico3((short) 0);
                        }
                        if (!hojaConsultaJson.get("diagnostico4").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setDiagnostico4(Short.parseShort(hojaConsultaJson.getString("diagnostico4")));
                        } else {
                            hojaConsultaOffLineDTO.setDiagnostico4((short) 0);
                        }

                        if (!hojaConsultaJson.get("otroDiagnostico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtroDiagnostico(hojaConsultaJson.getString("otroDiagnostico"));
                        if (!hojaConsultaJson.get("proximaCita").toString().equals("null"))
                            hojaConsultaOffLineDTO.setProximaCita(hojaConsultaJson.getString("proximaCita"));
                        if (!hojaConsultaJson.get("horarioClases").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHorarioClases(hojaConsultaJson.getString("horarioClases"));
                        if (!hojaConsultaJson.get("fechaCierre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaCierre(hojaConsultaJson.getString("fechaCierre"));
                        if (!hojaConsultaJson.get("fechaCambioTurno").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaCambioTurno(hojaConsultaJson.getString("fechaCambioTurno"));
                        if (!hojaConsultaJson.get("fechaCierreCambioTurno").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaCierreCambioTurno(hojaConsultaJson.getString("fechaCierreCambioTurno"));
                        if (!hojaConsultaJson.get("amPmUltDiaFiebre").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAmPmUltDiaFiebre(hojaConsultaJson.getString("amPmUltDiaFiebre"));
                        if (!hojaConsultaJson.get("horaUltDosisAntipiretico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHoraUltDosisAntipiretico(hojaConsultaJson.getString("horaUltDosisAntipiretico"));
                        if (!hojaConsultaJson.get("amPmUltDosisAntipiretico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setAmPmUltDosisAntipiretico(hojaConsultaJson.getString("amPmUltDosisAntipiretico"));
                        if (!hojaConsultaJson.get("expedienteFisico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setExpedienteFisico(hojaConsultaJson.getString("expedienteFisico"));
                        if (!hojaConsultaJson.get("colegio").toString().equals("null"))
                            hojaConsultaOffLineDTO.setColegio(hojaConsultaJson.getString("colegio"));
                        if (!hojaConsultaJson.get("fechaOrdenLaboratorio").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFechaOrdenLaboratorio(hojaConsultaJson.getString("fechaOrdenLaboratorio"));
                        if (!hojaConsultaJson.get("estadoCarga").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEstadoCarga(hojaConsultaJson.getString("estadoCarga"));
                        if (!hojaConsultaJson.get("otro").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOtro(hojaConsultaJson.getString("otro"));

                        if (!hojaConsultaJson.get("fis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFis(hojaConsultaJson.getString("fis"));
                        if (!hojaConsultaJson.get("fif").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFif(hojaConsultaJson.getString("fif"));
                        if (!hojaConsultaJson.get("hepatomegaliaCm").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setHepatomegaliaCm(hojaConsultaJson.getDouble("hepatomegaliaCm"));
                        } else {
                            hojaConsultaOffLineDTO.setHepatomegaliaCm((double) 0);
                        }
                        if (!hojaConsultaJson.get("eritrocitos").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEritrocitos(hojaConsultaJson.getString("eritrocitos"));
                        if (!hojaConsultaJson.get("planes").toString().equals("null"))
                            hojaConsultaOffLineDTO.setPlanes(hojaConsultaJson.getString("planes"));
                        if (!hojaConsultaJson.get("medicoCambioTurno").toString().equals("null"))
                            hojaConsultaOffLineDTO.setMedicoCambioTurno(hojaConsultaJson.getInt("medicoCambioTurno"));
                        if (!hojaConsultaJson.get("hemoconc").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHemoconc(hojaConsultaJson.getString("hemoconc"));
                        if (!hojaConsultaJson.get("vomito12h").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setVomito12h(Short.valueOf(hojaConsultaJson.getString("vomito12h")));
                        } else {
                            hojaConsultaOffLineDTO.setVomito12h((short) 0);
                        }
                        if (!hojaConsultaJson.get("pad").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setPad(Short.valueOf(hojaConsultaJson.getString("pad")));
                        } else {
                            hojaConsultaOffLineDTO.setPad((short) 0);
                        }
                        if (!hojaConsultaJson.get("pas").toString().equals("null")) {
                            hojaConsultaOffLineDTO.setPas(Short.valueOf(hojaConsultaJson.getString("pas")));
                        } else {
                            hojaConsultaOffLineDTO.setPas((short) 0);
                        }
                        if (!hojaConsultaJson.get("telef").toString().equals("null"))
                            hojaConsultaOffLineDTO.setTelef(hojaConsultaJson.getLong("telef"));
                        if (!hojaConsultaJson.get("oel").toString().equals("null"))
                            hojaConsultaOffLineDTO.setOel(hojaConsultaJson.getString("oel"));
                        if (!hojaConsultaJson.get("hora").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHora(hojaConsultaJson.getString("hora"));
                        if (!hojaConsultaJson.get("horasv").toString().equals("null"))
                            hojaConsultaOffLineDTO.setHorasv(hojaConsultaJson.getString("horasv"));
                        if (!hojaConsultaJson.get("noAtiendeLlamadoEnfermeria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNoAtiendeLlamadoEnfermeria(hojaConsultaJson.getString("noAtiendeLlamadoEnfermeria"));
                        if (!hojaConsultaJson.get("noAtiendeLlamadoMedico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setNoAtiendeLlamadoMedico(hojaConsultaJson.getString("noAtiendeLlamadoMedico"));
                        if (!hojaConsultaJson.get("estudiosParticipantes").toString().equals("null"))
                            hojaConsultaOffLineDTO.setEstudiosParticipantes(hojaConsultaJson.getString("estudiosParticipantes"));
                        if (!hojaConsultaJson.get("cV").toString().equals("null"))
                            hojaConsultaOffLineDTO.setcV(hojaConsultaJson.getString("cV"));
                        if (!hojaConsultaJson.get("consultaRespiratorio").toString().equals("null"))
                            hojaConsultaOffLineDTO.setConsultaRespiratorio(hojaConsultaJson.getString("consultaRespiratorio"));
                        hojaConsultaOffLineDTO.setEsConsultaTerreno("N");
                        hojaConsultaOffLineDTO.setStatusSubmitted("");
                        mHojasConsultas.add(hojaConsultaOffLineDTO);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }

    // Metodo para descargar partes de las hojas de consultas
    protected String descargarPartesHojasConsultas() throws Exception {
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_HOJAS_CONSULTAS_PARTS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            sobre.setOutputSoapObject(request);

            publishProgress("Solicitando partes de la hojas de consultas", HOJAS_CONSULTAS_PARTS, TOTAL_TASK_GENERALES);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_HOJAS_CONSULTAS_PARTS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");
                    for (int i = 0; i < inicioArrayJson.length(); i++) {
                        JSONObject hojaConsultaJson = inicioArrayJson.getJSONObject(i);
                        HojaConsultaPartsDTO hojaConsultaOffLineDTO = new HojaConsultaPartsDTO();
                        hojaConsultaOffLineDTO.setSecHojaConsulta(hojaConsultaJson.getInt("secHojaConsulta"));
                        hojaConsultaOffLineDTO.setCodExpediente(hojaConsultaJson.getInt("codExpediente"));
                        hojaConsultaOffLineDTO.setNumHojaConsulta(hojaConsultaJson.getInt("numHojaConsulta"));
                        hojaConsultaOffLineDTO.setEstado(hojaConsultaJson.getString("estado"));
                        hojaConsultaOffLineDTO.setFechaConsulta(hojaConsultaJson.getString("fechaConsulta"));
                        if (!hojaConsultaJson.get("usuarioEnfermeria").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUsuarioEnfermeria(hojaConsultaJson.getInt("usuarioEnfermeria"));
                        if (!hojaConsultaJson.get("usuarioMedico").toString().equals("null"))
                            hojaConsultaOffLineDTO.setUsuarioMedico(hojaConsultaJson.getInt("usuarioMedico"));
                        if (!hojaConsultaJson.get("fis").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFis(hojaConsultaJson.getString("fis"));
                        if (!hojaConsultaJson.get("fif").toString().equals("null"))
                            hojaConsultaOffLineDTO.setFif(hojaConsultaJson.getString("fif"));
                        mHojasConsultasParts.add(hojaConsultaOffLineDTO);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }
    }
}
