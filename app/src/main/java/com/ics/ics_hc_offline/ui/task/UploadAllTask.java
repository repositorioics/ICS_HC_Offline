package com.ics.ics_hc_offline.ui.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.ErrorDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.listeners.UploadListener;
import com.ics.ics_hc_offline.utils.AndroidUtils;
import com.ics.ics_hc_offline.utils.Constants;
import com.ics.ics_hc_offline.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UploadAllTask extends UploadTask {
    private final Context mContext;

    public UploadAllTask(Context context) {
        mContext = context;
    }

    public static final String NAMESPACE = "http://webservice.estudiocohortecssfv.sts_ni.com/";
    public static String URL = "http://192.168.1.99:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
    private static int TIME_OUT = 90000;

    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private static final String METODO_GUARDAR_HOJAS_CONSULTAS = "guardarHojaConsultaOffline";
    private static final String ACCIONSOAP_GUARDAR_HOJAS_CONSULTAS = NAMESPACE + METODO_GUARDAR_HOJAS_CONSULTAS;

    protected static final String TAG = UploadAllTask.class.getSimpleName();

    private HojaConsultaDBAdapter hojaConsultaDBAdapter = null;
    private List<HojaConsultaOffLineDTO> mHojasConsultas = new ArrayList<HojaConsultaOffLineDTO>();

    private String error = null;

    protected UploadListener mStateListener;

    public static final String HOJAS_CONSULTAS = "1";
    private static final String TOTAL_TASK = "1";

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

    @Override
    protected String doInBackground(String... values) {
        try {
            publishProgress("Obteniendo registros de la base de datos", "1", "1");

            hojaConsultaDBAdapter = new HojaConsultaDBAdapter(mContext,false,false);
            hojaConsultaDBAdapter.open();
            String filtro = MainDBConstants.esConsultaTerreno + "='" + Constants.ES_CONSULTA_TERRENO + "'";
            String filtro2 = MainDBConstants.estado + "='" + 7 + "'";
            mHojasConsultas = hojaConsultaDBAdapter.getListHojasConsultas(filtro + " AND " +filtro2, null);

            if (mHojasConsultas.size() <= 0) {
                error = Constants.NO_DATA;
            } else {
                publishProgress("Datos completos!", "2", "2");

                //Enviando datos
                //actualizarBaseDatos(HOJAS_CONSULTAS);
                error = cargarHojaConsulta();
                if (error != null && !error.isEmpty()) {
                    JSONObject jObj = new JSONObject(error);
                    JSONObject mensaje = (JSONObject) jObj.get("mensaje");
                    if(mensaje.getInt("codigo") == 0) {
                        //actualizarBaseDatos(HOJAS_CONSULTAS);
                        publishProgress("Los datos se enviaron correctamente", "0", "0");
                        //Toast.makeText(mContext, "Los datos subieron correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        publishProgress("ERROR al enviar los datos... Intente nuevamente", "0", "0");
                        //Toast.makeText(mContext, "ERROR al enviar los datos... Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }
                /*if (!error.matches(Constants.DATOS_RECIBIDOS)) {
                    actualizarBaseDatos(HOJAS_CONSULTAS);
                    return error;
                }*/
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            return e1.getLocalizedMessage();
        } finally {
            hojaConsultaDBAdapter.close();
        }
        return error;
    }

    /*private void actualizarBaseDatos(String opcion) {
        int c;
        if(opcion.equalsIgnoreCase(HOJAS_CONSULTAS)){
            c = mHojasConsultas.size();
            if(c>0){
                for (HojaConsultaOffLineDTO hojaConsulta : mHojasConsultas) {
                    //visita.setEstado(estado);
                    hojaConsultaDBAdapter.editarHojaConsulta(hojaConsulta);
                    publishProgress("Actualizando visitas en base de datos local", Integer.valueOf(mHojasConsultas.indexOf(hojaConsulta)).toString(), Integer
                            .valueOf(c).toString());
                }
            }
        }
    }*/

    // Hojas Consultas
    protected String cargarHojaConsulta() throws Exception {
        try {
            if(mHojasConsultas.size() > 0) {
                // La URL de la solicitud POST
                publishProgress("Enviando HOJAS_CONSULTAS!", HOJAS_CONSULTAS, TOTAL_TASK);
                SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_HOJAS_CONSULTAS);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = false;

                JSONObject objenvio = new JSONObject();
                String paramJsonArray="";

                // creando el json array para el seguimiento influenza
                JSONArray jsonArray = new JSONArray();
                for (HojaConsultaOffLineDTO hojaConsulta : mHojasConsultas) {
                    JSONObject objenvioHojaConsulta = new JSONObject();
                    //objenvioHojaConsulta.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
                    objenvioHojaConsulta.put("codExpediente", hojaConsulta.getCodExpediente());
                    //objenvioHojaConsulta.put("numHojaConsulta", hojaConsulta.getNumHojaConsulta());
                    //objenvioHojaConsulta.put("numOrdenLlegada", hojaConsulta.getNumOrdenLlegada());
                    objenvioHojaConsulta.put("estado", hojaConsulta.getEstado());
                    objenvioHojaConsulta.put("fechaConsulta", hojaConsulta.getFechaConsulta());
                    objenvioHojaConsulta.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());
                    objenvioHojaConsulta.put("usuarioMedico", hojaConsulta.getUsuarioMedico());
                    objenvioHojaConsulta.put("pesoKg", hojaConsulta.getPesoKg());
                    objenvioHojaConsulta.put("tallaCm", hojaConsulta.getTallaCm());
                    objenvioHojaConsulta.put("temperaturac", hojaConsulta.getTemperaturac());
                    objenvioHojaConsulta.put("presion", null);
                    objenvioHojaConsulta.put("fciaResp", hojaConsulta.getFciaResp());
                    objenvioHojaConsulta.put("fciaCard", hojaConsulta.getFciaCard());
                    objenvioHojaConsulta.put("lugarAtencion", hojaConsulta.getLugarAtencion());
                    objenvioHojaConsulta.put("consulta", hojaConsulta.getConsulta());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getSegChick()))
                        objenvioHojaConsulta.put("segChick", hojaConsulta.getSegChick());
                    objenvioHojaConsulta.put("turno", hojaConsulta.getTurno());
                    objenvioHojaConsulta.put("temMedc", hojaConsulta.getTemMedc());
                    if (!StringUtils.isNullOrEmpty(hojaConsulta.getUltDiaFiebre()))
                        objenvioHojaConsulta.put("ultDiaFiebre", hojaConsulta.getUltDiaFiebre());
                    if (!StringUtils.isNullOrEmpty(hojaConsulta.getUltDosisAntipiretico()))
                    objenvioHojaConsulta.put("ultDosisAntipiretico", hojaConsulta.getUltDosisAntipiretico());
                    objenvioHojaConsulta.put("fiebre", hojaConsulta.getFiebre());
                    objenvioHojaConsulta.put("astenia", hojaConsulta.getAstenia());
                    objenvioHojaConsulta.put("asomnoliento", hojaConsulta.getAsomnoliento());
                    objenvioHojaConsulta.put("malEstado", hojaConsulta.getMalEstado());
                    objenvioHojaConsulta.put("perdidaConsciencia", hojaConsulta.getPerdidaConsciencia());
                    objenvioHojaConsulta.put("inquieto", hojaConsulta.getInquieto());
                    objenvioHojaConsulta.put("convulsiones", hojaConsulta.getConvulsiones());
                    objenvioHojaConsulta.put("hipotermia", hojaConsulta.getHipotermia());
                    objenvioHojaConsulta.put("letargia", hojaConsulta.getLetargia());
                    objenvioHojaConsulta.put("cefalea", hojaConsulta.getCefalea());
                    objenvioHojaConsulta.put("rigidezCuello", hojaConsulta.getRigidezCuello());
                    objenvioHojaConsulta.put("inyeccionConjuntival", hojaConsulta.getInyeccionConjuntival());
                    objenvioHojaConsulta.put("hemorragiaSuconjuntival", hojaConsulta.getHemorragiaSuconjuntival());
                    objenvioHojaConsulta.put("dolorRetroocular", hojaConsulta.getDolorRetroocular());
                    objenvioHojaConsulta.put("fontanelaAbombada", hojaConsulta.getFontanelaAbombada());
                    objenvioHojaConsulta.put("ictericiaConuntival", hojaConsulta.getIctericiaConuntival());
                    objenvioHojaConsulta.put("eritema", hojaConsulta.getEritema());
                    objenvioHojaConsulta.put("dolorGarganta", hojaConsulta.getDolorGarganta());
                    objenvioHojaConsulta.put("adenopatiasCervicales", hojaConsulta.getAdenopatiasCervicales());
                    objenvioHojaConsulta.put("exudado", hojaConsulta.getExudado());
                    objenvioHojaConsulta.put("petequiasMucosa", hojaConsulta.getPetequiasMucosa());
                    objenvioHojaConsulta.put("tos", hojaConsulta.getTos());
                    objenvioHojaConsulta.put("rinorrea", hojaConsulta.getRinorrea());
                    objenvioHojaConsulta.put("congestionNasal", hojaConsulta.getCongestionNasal());
                    objenvioHojaConsulta.put("otalgia", hojaConsulta.getOtalgia());
                    objenvioHojaConsulta.put("aleteoNasal", hojaConsulta.getAleteoNasal());
                    objenvioHojaConsulta.put("apnea", hojaConsulta.getApnea());
                    objenvioHojaConsulta.put("respiracionRapida", hojaConsulta.getRespiracionRapida());
                    objenvioHojaConsulta.put("quejidoEspiratorio", hojaConsulta.getQuejidoEspiratorio());
                    objenvioHojaConsulta.put("estiradorReposo", hojaConsulta.getEstiradorReposo());
                    objenvioHojaConsulta.put("tirajeSubcostal", hojaConsulta.getTirajeSubcostal());
                    objenvioHojaConsulta.put("sibilancias", hojaConsulta.getSibilancias());
                    objenvioHojaConsulta.put("crepitos", hojaConsulta.getCrepitos());
                    objenvioHojaConsulta.put("roncos", hojaConsulta.getRoncos());
                    objenvioHojaConsulta.put("otraFif", hojaConsulta.getOtraFif());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getNuevaFif()))
                        objenvioHojaConsulta.put("nuevaFif", hojaConsulta.getNuevaFif());
                    objenvioHojaConsulta.put("pocoApetito", hojaConsulta.getPocoApetito());
                    objenvioHojaConsulta.put("nausea", hojaConsulta.getNausea());
                    objenvioHojaConsulta.put("dificultadAlimentarse", hojaConsulta.getDificultadAlimentarse());
                    objenvioHojaConsulta.put("vomito12horas", hojaConsulta.getVomito12horas());
                    objenvioHojaConsulta.put("diarrea", hojaConsulta.getDiarrea());
                    objenvioHojaConsulta.put("diarreaSangre", hojaConsulta.getDiarreaSangre());
                    objenvioHojaConsulta.put("estrenimiento", hojaConsulta.getEstrenimiento());
                    objenvioHojaConsulta.put("dolorAbIntermitente", hojaConsulta.getDolorAbIntermitente());
                    objenvioHojaConsulta.put("dolorAbContinuo", hojaConsulta.getDolorAbContinuo());
                    objenvioHojaConsulta.put("epigastralgia", hojaConsulta.getEpigastralgia());
                    objenvioHojaConsulta.put("intoleranciaOral", hojaConsulta.getIntoleranciaOral());
                    objenvioHojaConsulta.put("distensionAbdominal", hojaConsulta.getDistensionAbdominal());
                    objenvioHojaConsulta.put("hepatomegalia", hojaConsulta.getHepatomegalia());
                    objenvioHojaConsulta.put("lenguaMucosasSecas", hojaConsulta.getLenguaMucosasSecas());
                    objenvioHojaConsulta.put("pliegueCutaneo", hojaConsulta.getPliegueCutaneo());
                    objenvioHojaConsulta.put("orinaReducida", hojaConsulta.getOrinaReducida());
                    objenvioHojaConsulta.put("bebeConSed", hojaConsulta.getBebeConSed());
                    objenvioHojaConsulta.put("ojosHundidos", hojaConsulta.getOjosHundidos());
                    objenvioHojaConsulta.put("fontanelaHundida", hojaConsulta.getFontanelaHundida());
                    objenvioHojaConsulta.put("sintomasUrinarios", hojaConsulta.getSintomasUrinarios());
                    objenvioHojaConsulta.put("leucocituria", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("nitritos", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("bilirrubinuria", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("altralgia", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("mialgia", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("lumbalgia", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("dolorCuello", hojaConsulta.getLeucocituria());
                    objenvioHojaConsulta.put("tenosinovitis", hojaConsulta.getTenosinovitis());
                    objenvioHojaConsulta.put("artralgiaProximal", hojaConsulta.getArtralgiaProximal());
                    objenvioHojaConsulta.put("artralgiaDistal", hojaConsulta.getArtralgiaDistal());
                    objenvioHojaConsulta.put("conjuntivitis", hojaConsulta.getConjuntivitis());
                    objenvioHojaConsulta.put("edemaMunecas", hojaConsulta.getEdemaMunecas());
                    objenvioHojaConsulta.put("edemaCodos", hojaConsulta.getEdemaCodos());
                    objenvioHojaConsulta.put("edemaHombros", hojaConsulta.getEdemaHombros());
                    objenvioHojaConsulta.put("edemaRodillas", hojaConsulta.getEdemaRodillas());
                    objenvioHojaConsulta.put("edemaTobillos", hojaConsulta.getEdemaTobillos());
                    objenvioHojaConsulta.put("rahsLocalizado", hojaConsulta.getRahsLocalizado());
                    objenvioHojaConsulta.put("rahsGeneralizado", hojaConsulta.getRahsGeneralizado());
                    objenvioHojaConsulta.put("rashEritematoso", hojaConsulta.getRashEritematoso());
                    objenvioHojaConsulta.put("rahsMacular", hojaConsulta.getRahsMacular());
                    objenvioHojaConsulta.put("rashPapular", hojaConsulta.getRashPapular());
                    objenvioHojaConsulta.put("rahsMoteada", hojaConsulta.getRahsMoteada());
                    objenvioHojaConsulta.put("ruborFacial", hojaConsulta.getRuborFacial());
                    objenvioHojaConsulta.put("equimosis", hojaConsulta.getEquimosis());
                    objenvioHojaConsulta.put("cianosisCentral", hojaConsulta.getCianosisCentral());
                    objenvioHojaConsulta.put("ictericia", hojaConsulta.getIctericia());
                    objenvioHojaConsulta.put("obeso", hojaConsulta.getObeso());
                    objenvioHojaConsulta.put("sobrepeso", hojaConsulta.getSobrepeso());
                    objenvioHojaConsulta.put("sospechaProblema", hojaConsulta.getSospechaProblema());
                    objenvioHojaConsulta.put("normal", hojaConsulta.getNormal());
                    objenvioHojaConsulta.put("bajoPeso", hojaConsulta.getBajoPeso());
                    objenvioHojaConsulta.put("bajoPesoSevero", hojaConsulta.getBajoPesoSevero());
                    objenvioHojaConsulta.put("lactanciaMaterna", hojaConsulta.getLactanciaMaterna());
                    objenvioHojaConsulta.put("vacunasCompletas", hojaConsulta.getVacunasCompletas());
                    objenvioHojaConsulta.put("vacunaInfluenza", hojaConsulta.getVacunaInfluenza());
                    objenvioHojaConsulta.put("fechaVacuna", hojaConsulta.getFechaVacuna());
                    objenvioHojaConsulta.put("interconsultaPediatrica", hojaConsulta.getInterconsultaPediatrica());
                    objenvioHojaConsulta.put("referenciaHospital", hojaConsulta.getReferenciaHospital());
                    objenvioHojaConsulta.put("referenciaDengue", hojaConsulta.getReferenciaDengue());
                    objenvioHojaConsulta.put("referenciaIrag", hojaConsulta.getReferenciaIrag());
                    objenvioHojaConsulta.put("referenciaChik", hojaConsulta.getReferenciaChik());
                    objenvioHojaConsulta.put("eti", hojaConsulta.getEti());
                    objenvioHojaConsulta.put("irag", hojaConsulta.getIrag());
                    objenvioHojaConsulta.put("neumonia", hojaConsulta.getNeumonia());
                    objenvioHojaConsulta.put("saturaciono2", hojaConsulta.getSaturaciono2());
                    objenvioHojaConsulta.put("imc", hojaConsulta.getImc());
                    objenvioHojaConsulta.put("categoria", hojaConsulta.getCategoria());
                    objenvioHojaConsulta.put("cambioCategoria", hojaConsulta.getCambioCategoria());
                    objenvioHojaConsulta.put("manifestacionHemorragica", hojaConsulta.getManifestacionHemorragica());
                    objenvioHojaConsulta.put("pruebaTorniquetePositiva", hojaConsulta.getPruebaTorniquetePositiva());
                    objenvioHojaConsulta.put("petequia10Pt", hojaConsulta.getPetequia10Pt());
                    objenvioHojaConsulta.put("petequia20Pt", hojaConsulta.getPetequia20Pt());
                    objenvioHojaConsulta.put("pielExtremidadesFrias", hojaConsulta.getPielExtremidadesFrias());
                    objenvioHojaConsulta.put("palidezEnExtremidades", hojaConsulta.getPalidezEnExtremidades());
                    objenvioHojaConsulta.put("epistaxis", hojaConsulta.getEpistaxis());
                    objenvioHojaConsulta.put("gingivorragia", hojaConsulta.getGingivorragia());
                    objenvioHojaConsulta.put("petequiasEspontaneas", hojaConsulta.getPetequiasEspontaneas());
                    objenvioHojaConsulta.put("llenadoCapilar2seg", hojaConsulta.getLlenadoCapilar2seg());
                    objenvioHojaConsulta.put("cianosis", hojaConsulta.getCianosis());
                    if(hojaConsulta.getLinfocitosaAtipicos() > 0)
                        objenvioHojaConsulta.put("linfocitosaAtipicos", hojaConsulta.getLinfocitosaAtipicos());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getFechaLinfocitos()))
                        objenvioHojaConsulta.put("fechaLinfocitos", hojaConsulta.getFechaLinfocitos());
                    objenvioHojaConsulta.put("hipermenorrea", hojaConsulta.getHipermenorrea());
                    objenvioHojaConsulta.put("hematemesis", hojaConsulta.getHematemesis());
                    objenvioHojaConsulta.put("melena", hojaConsulta.getMelena());
                    if (hojaConsulta.getHemoconcentracion() > 0)
                        objenvioHojaConsulta.put("hemoconcentracion", hojaConsulta.getHemoconcentracion());
                    objenvioHojaConsulta.put("hospitalizado", hojaConsulta.getHospitalizado());
                    if (!StringUtils.isNullOrEmpty(hojaConsulta.getHospitalizadoEspecificar()))
                        objenvioHojaConsulta.put("hospitalizadoEspecificar", hojaConsulta.getHospitalizadoEspecificar());
                    objenvioHojaConsulta.put("transfusionSangre", hojaConsulta.getTransfusionSangre());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getTransfusionEspecificar()))
                        objenvioHojaConsulta.put("transfusionEspecificar", hojaConsulta.getTransfusionEspecificar());
                    objenvioHojaConsulta.put("tomandoMedicamento", hojaConsulta.getTomandoMedicamento());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getMedicamentoEspecificar()))
                        objenvioHojaConsulta.put("medicamentoEspecificar", hojaConsulta.getMedicamentoEspecificar());
                    objenvioHojaConsulta.put("medicamentoDistinto", hojaConsulta.getMedicamentoDistinto());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getMedicamentoDistEspecificar()))
                        objenvioHojaConsulta.put("medicamentoDistEspecificar", hojaConsulta.getMedicamentoDistEspecificar());
                    objenvioHojaConsulta.put("bhc", hojaConsulta.getBhc());
                    objenvioHojaConsulta.put("serologiaDengue", hojaConsulta.getSerologiaDengue());
                    objenvioHojaConsulta.put("serologiaChik", hojaConsulta.getSerologiaChik());
                    objenvioHojaConsulta.put("gotaGruesa", hojaConsulta.getGotaGruesa());
                    objenvioHojaConsulta.put("extendidoPeriferico", hojaConsulta.getExtendidoPeriferico());
                    objenvioHojaConsulta.put("ego", hojaConsulta.getEgo());
                    objenvioHojaConsulta.put("egh", hojaConsulta.getEgh());
                    objenvioHojaConsulta.put("citologiaFecal", hojaConsulta.getCitologiaFecal());
                    objenvioHojaConsulta.put("factorReumatoideo", hojaConsulta.getFactorReumatoideo());
                    objenvioHojaConsulta.put("albumina", hojaConsulta.getAlbumina());
                    objenvioHojaConsulta.put("astAlt", hojaConsulta.getAstAlt());
                    objenvioHojaConsulta.put("bilirrubinas", hojaConsulta.getBilirrubinas());
                    objenvioHojaConsulta.put("cpk", hojaConsulta.getCpk());
                    objenvioHojaConsulta.put("colesterol", hojaConsulta.getColesterol());
                    objenvioHojaConsulta.put("influenza", hojaConsulta.getInfluenza());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getOtroExamenLab()))
                        objenvioHojaConsulta.put("otroExamenLab", hojaConsulta.getOtroExamenLab());
                    //objenvioHojaConsulta.put("numOrdenLaboratorio", hojaConsulta.getNumOrdenLaboratorio());
                    objenvioHojaConsulta.put("acetaminofen", hojaConsulta.getAcetaminofen());
                    objenvioHojaConsulta.put("asa", hojaConsulta.getAsa());
                    objenvioHojaConsulta.put("ibuprofen", hojaConsulta.getIbuprofen());
                    objenvioHojaConsulta.put("penicilina", hojaConsulta.getPenicilina());
                    objenvioHojaConsulta.put("amoxicilina", hojaConsulta.getAmoxicilina());
                    objenvioHojaConsulta.put("dicloxacilina", hojaConsulta.getDicloxacilina());
                    objenvioHojaConsulta.put("otroAntibiotico", hojaConsulta.getOtroAntibiotico());
                    objenvioHojaConsulta.put("furazolidona", hojaConsulta.getFurazolidona());
                    objenvioHojaConsulta.put("metronidazolTinidazol", hojaConsulta.getMetronidazolTinidazol());
                    objenvioHojaConsulta.put("albendazolMebendazol", hojaConsulta.getAlbendazolMebendazol());
                    objenvioHojaConsulta.put("sulfatoFerroso", hojaConsulta.getSulfatoFerroso());
                    objenvioHojaConsulta.put("sueroOral", hojaConsulta.getSueroOral());
                    objenvioHojaConsulta.put("sulfatoZinc", hojaConsulta.getSulfatoZinc());
                    objenvioHojaConsulta.put("liquidosIv", hojaConsulta.getLiquidosIv());
                    objenvioHojaConsulta.put("prednisona", hojaConsulta.getPrednisona());
                    objenvioHojaConsulta.put("hidrocortisonaIv", hojaConsulta.getHidrocortisonaIv());
                    objenvioHojaConsulta.put("salbutamol", hojaConsulta.getSalbutamol());
                    objenvioHojaConsulta.put("oseltamivir", hojaConsulta.getOseltamivir());
                    objenvioHojaConsulta.put("historiaExamenFisico", hojaConsulta.getHistoriaExamenFisico());
                    objenvioHojaConsulta.put("diagnostico1", hojaConsulta.getDiagnostico1());
                    objenvioHojaConsulta.put("diagnostico2", hojaConsulta.getDiagnostico2());
                    objenvioHojaConsulta.put("diagnostico3", hojaConsulta.getDiagnostico3());
                    objenvioHojaConsulta.put("diagnostico4", hojaConsulta.getDiagnostico4());
                    objenvioHojaConsulta.put("otroDiagnostico", hojaConsulta.getOtroDiagnostico());
                    objenvioHojaConsulta.put("proximaCita", hojaConsulta.getProximaCita());
                    objenvioHojaConsulta.put("horarioClases", hojaConsulta.getHorarioClases());
                    objenvioHojaConsulta.put("fechaCierre", hojaConsulta.getFechaCierre());
                    //objenvioHojaConsulta.put("fechaCambioTurno", hojaConsulta.getFechaCambioTurno());
                    //objenvioHojaConsulta.put("fechaCierreCambioTurno", hojaConsulta.getFechaCierreCambioTurno());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getAmPmUltDiaFiebre()))
                        objenvioHojaConsulta.put("amPmUltDiaFiebre", hojaConsulta.getAmPmUltDiaFiebre());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getHoraUltDosisAntipiretico()))
                        objenvioHojaConsulta.put("horaUltDosisAntipiretico", hojaConsulta.getHoraUltDosisAntipiretico());
                    if(!StringUtils.isNullOrEmpty(hojaConsulta.getAmPmUltDosisAntipiretico()))
                        objenvioHojaConsulta.put("amPmUltDosisAntipiretico", hojaConsulta.getAmPmUltDosisAntipiretico());
                    objenvioHojaConsulta.put("expedienteFisico", hojaConsulta.getExpedienteFisico());
                    objenvioHojaConsulta.put("colegio", hojaConsulta.getColegio());
                    objenvioHojaConsulta.put("fechaOrdenLaboratorio", hojaConsulta.getFechaOrdenLaboratorio());
                    objenvioHojaConsulta.put("estadoCarga", hojaConsulta.getEstadoCarga());
                    objenvioHojaConsulta.put("otro", hojaConsulta.getOtro());
                    if (!StringUtils.isNullOrEmpty(hojaConsulta.getFis()))
                        objenvioHojaConsulta.put("fis", hojaConsulta.getFis());
                    if (!StringUtils.isNullOrEmpty(hojaConsulta.getFif()))
                        objenvioHojaConsulta.put("fif", hojaConsulta.getFif());
                    objenvioHojaConsulta.put("hepatomegaliaCm", hojaConsulta.getHepatomegaliaCm());
                    objenvioHojaConsulta.put("eritrocitos", hojaConsulta.getEritrocitos());
                    objenvioHojaConsulta.put("planes", hojaConsulta.getPlanes());
                    //objenvioHojaConsulta.put("medicoCambioTurno", hojaConsulta.getMedicoCambioTurno());
                    objenvioHojaConsulta.put("hemoconc", hojaConsulta.getHemoconc());
                    objenvioHojaConsulta.put("vomito12h", hojaConsulta.getVomito12h());
                    objenvioHojaConsulta.put("pad", hojaConsulta.getPad());
                    objenvioHojaConsulta.put("pas", hojaConsulta.getPas());
                    objenvioHojaConsulta.put("telef", hojaConsulta.getTelef());
                    objenvioHojaConsulta.put("oel", hojaConsulta.getOel());
                    objenvioHojaConsulta.put("hora", hojaConsulta.getHora());
                    objenvioHojaConsulta.put("horasv", hojaConsulta.getHorasv());
                    //objenvioHojaConsulta.put("noAtiendeLlamadoEnfermeria", hojaConsulta.getNoAtiendeLlamadoEnfermeria());
                    //objenvioHojaConsulta.put("noAtiendeLlamadoMedico", hojaConsulta.getNoAtiendeLlamadoMedico());
                    objenvioHojaConsulta.put("estudiosParticipantes", hojaConsulta.getEstudiosParticipantes());
                    objenvioHojaConsulta.put("cV", hojaConsulta.getcV());
                    //objenvioHojaConsulta.put("consultaRespiratorio", hojaConsulta.getConsultaRespiratorio());

                    jsonArray.put(objenvioHojaConsulta.toString());
                }
                paramJsonArray=jsonArray.toString();
                PropertyInfo paramEviar = new PropertyInfo();
                paramEviar.setValue(paramJsonArray);
                paramEviar.setName("paramHojaConsulta");
                paramEviar.setNamespace("");
                paramEviar.setType(String.class);

                request.addProperty(paramEviar);

                sobre.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
                transporte.call(ACCIONSOAP_GUARDAR_HOJAS_CONSULTAS, sobre, this.HEADER_PROPERTY);
                String resultado = sobre.getResponse().toString();

                return resultado;
            } else{
                return Constants.DATOS_RECIBIDOS;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return e.getMessage();
        }
    }
}
