package com.ics.ics_hc_offline.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.ics.ics_hc_offline.CssfvApp;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.database.helpers.ConsEstudiosHelper;
import com.ics.ics_hc_offline.database.helpers.DiagnosticoHelper;
import com.ics.ics_hc_offline.database.helpers.EscuelaHelper;
import com.ics.ics_hc_offline.database.helpers.EstadosHojasHelper;
import com.ics.ics_hc_offline.database.helpers.EstudiosHelper;
import com.ics.ics_hc_offline.database.helpers.HojaConsultaHelper;
import com.ics.ics_hc_offline.database.helpers.ParticipanteHelper;
import com.ics.ics_hc_offline.database.helpers.RolesHelper;
import com.ics.ics_hc_offline.database.helpers.UsuarioHelper;
import com.ics.ics_hc_offline.dto.ConsEstudiosDTO;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;
import com.ics.ics_hc_offline.dto.EstadosHojasDTO;
import com.ics.ics_hc_offline.dto.EstudioCatalogoDTO;
import com.ics.ics_hc_offline.dto.ExpedienteDTO;
import com.ics.ics_hc_offline.dto.GrillaCierreDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.InicioDTO;
import com.ics.ics_hc_offline.dto.PacienteDTO;
import com.ics.ics_hc_offline.dto.RolesDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.utils.FileUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;
import net.sqlcipher.database.SQLiteStatement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HojaConsultaDBAdapter {
    private DataBaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;
    //private final String mPassword;
    private final boolean mFromServer;
    private final boolean mCleanDb;

    public HojaConsultaDBAdapter(Context context, boolean fromServer, boolean cleanDb) {
        mContext = context;
        //mPassword = password;
        mFromServer = fromServer;
        mCleanDb = cleanDb;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context mContext, boolean mFromServer, boolean mCleanDb) {
            super(FileUtils.DATABASE_PATH, MainDBConstants.DATABASE_NAME, MainDBConstants.DATABASE_VERSION, mContext,
                    mFromServer, mCleanDb);
            createStorage();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MainDBConstants.CREATE_USUARIO_TABLE);
            db.execSQL(MainDBConstants.CREATE_ROLES_TABLE);
            db.execSQL(MainDBConstants.CREATE_CONSESTUDIOS_TABLE);
            db.execSQL(MainDBConstants.CREATE_DIAGNOSTICO_TABLE);
            db.execSQL(MainDBConstants.CREATE_ESCUELA_TABLE);
            db.execSQL(MainDBConstants.CREATE_ESTUDIOCATALOGO_TABLE);
            db.execSQL(MainDBConstants.CREATE_HOJACONSULTA_TABLE);
            db.execSQL(MainDBConstants.CREATE_PARTICIPANTE_TALBE);
            db.execSQL(MainDBConstants.CREATE_ESTADOS_HOJAS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
            if(oldVersion==0) return;
            if(oldVersion==1) {
                db.execSQL("Drop table " + MainDBConstants.HOJACONSULTA_TABLE);
                db.execSQL(MainDBConstants.CREATE_HOJACONSULTA_TABLE);
            }
        }
    }

    public static boolean createStorage() {
        return FileUtils.createFolder(FileUtils.DATABASE_PATH);
    }

    public HojaConsultaDBAdapter open() throws SQLException {
        mDbHelper = new DataBaseHelper(mContext,mFromServer,mCleanDb);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null)
            mDbHelper.close();
    }

    /**
     * Crea un cursor desde la base de datos
     *
     * @return cursor
     */
    public Cursor crearCursor(String tabla, String whereString, String projection[], String ordenString) throws SQLException {
        Cursor c = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tabla);
        c = qb.query(mDb,projection,whereString,null,null,null,ordenString);
        return c;
    }

    //Limpiar la tabla de usuarios de la base de datos
    public boolean borrarUsuarios() {
        return mDb.delete(MainDBConstants.USUARIO_TABLE, null, null) > 0;
    }

    //Limpiar la tabla roles de la base de datos
    public boolean borrarRoles() {
        return mDb.delete(MainDBConstants.ROLES_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de cons estudios de la base de datos
    public boolean borrarConsEstudios() {
        return mDb.delete(MainDBConstants.CONSESTUDIOS_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de Estudios de la base de datos
    public boolean borraEstudios() {
        return mDb.delete(MainDBConstants.ESTUDIOCATALOGO_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de Diagnosticos de la base de datos
    public boolean borrarDiagnosticos() {
        return mDb.delete(MainDBConstants.DIAGNOSTICO_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de escuela de la base de datos
    public boolean borrarEscuelas() {
        return mDb.delete(MainDBConstants.ESCUELA_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de estudios de la base de datos
    public boolean borrarEstudios() {
        return mDb.delete(MainDBConstants.ESTUDIOCATALOGO_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de participantes de la base de datos
    public boolean borrarParticipantes() {
        return mDb.delete(MainDBConstants.PARTICIPANTE_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de estados hojas de la base de datos
    public boolean borrarEstadosHojas() {
        return mDb.delete(MainDBConstants.ESTADOS_HOJAS_TABLE, null, null) > 0;
    }

    //Limpiar la tabla de hoja_consulta de la base de datos
    public boolean borrarHojaConsulta() {
        return mDb.delete(MainDBConstants.HOJACONSULTA_TABLE, null, null) > 0;
    }

    //Obtener un usuario de la base de datos local
    public UsuarioDTO getUsuario(String filtro, String orden) throws SQLException {
        UsuarioDTO mUser = null;
        Cursor cursorUser = crearCursor(MainDBConstants.USUARIO_TABLE, filtro, null, orden);
        if (cursorUser != null && cursorUser.getCount() > 0) {
            cursorUser.moveToFirst();
            mUser= UsuarioHelper.crearUsuario(cursorUser);
        }
        if (!cursorUser.isClosed()) cursorUser.close();
        return mUser;
    }

    public List<UsuarioDTO> getUsuariosEnfermeria() throws SQLException {
        List<UsuarioDTO> mUser = new ArrayList<UsuarioDTO>();
        String query = "SELECT a.id, a.nombre, a.usuario, a.codigopersonal, a.pass " +
                "FROM usuario a " +
                "INNER JOIN roles b ON a.usuario = b.usuario " +
                "WHERE b.nombre = 'Enfermeria' " +
                "GROUP BY a.nombre ORDER BY a.nombre ASC";
        Cursor cursorUser = mDb.rawQuery(query, new String[]{});
        if (cursorUser != null && cursorUser.getCount() > 0) {
            cursorUser.moveToFirst();
            mUser.clear();
            do {
                UsuarioDTO usuario = null;
                usuario = UsuarioHelper.crearUsuario(cursorUser);
                if (!usuario.getNombre().trim().equals("Administrador")
                        && !usuario.getNombre().trim().equals("BRENDA LOPEZ")
                        && !usuario.getNombre().trim().equals("JAIRO CAREY")
                        && !usuario.getNombre().trim().equals("LILLIAM CASTILLO")
                        && !usuario.getNombre().trim().equals("SANTIAGO CARBALLO")
                        && !usuario.getNombre().trim().equals("ANZONY VANEGAS")
                        && !usuario.getNombre().trim().equals("LIZANDRO SERRANO")
                        && !usuario.getNombre().trim().equals("Miguel Salinas")
                        && !usuario.getNombre().trim().contains("DR. PLAZAOLA")
                        && !usuario.getNombre().trim().contains("DR. OJEDA")
                        && !usuario.getNombre().trim().contains("DR. ROMERO")
                        && !usuario.getNombre().trim().contains("DRA. KUAN")) {
                    mUser.add(usuario);
                }
            } while (cursorUser.moveToNext());
        }
        if (!cursorUser.isClosed()) cursorUser.close();
        return mUser;
    }

    //Obtener la lista de diagnosticos de la base de datos
    public List<DiagnosticoDTO> getDiagnosticos(String filtro, String orden) throws SQLException {
        List<DiagnosticoDTO> mDiagnosticos = new ArrayList<DiagnosticoDTO>();
        Cursor cursorDiagnosticos = crearCursor(MainDBConstants.DIAGNOSTICO_TABLE, filtro, null, orden);
        if (cursorDiagnosticos != null && cursorDiagnosticos.getCount() > 0) {
            cursorDiagnosticos.moveToFirst();
            mDiagnosticos.clear();
            do {
                DiagnosticoDTO mDiagnostico = null;
                mDiagnostico = DiagnosticoHelper.crearDiagnostico(cursorDiagnosticos);
                mDiagnosticos.add(mDiagnostico);
            } while (cursorDiagnosticos.moveToNext());
        }
        if (!cursorDiagnosticos.isClosed()) cursorDiagnosticos.close();
        return mDiagnosticos;
    }

    public DiagnosticoDTO getDiagnostico(String filtro, String orden) throws SQLException {
        DiagnosticoDTO mDiagnostico = null;
        Cursor cursormDiagnostico = crearCursor(MainDBConstants.DIAGNOSTICO_TABLE , filtro, null, orden);
        if (cursormDiagnostico != null && cursormDiagnostico.getCount() > 0) {
            cursormDiagnostico.moveToFirst();
            mDiagnostico= DiagnosticoHelper.crearDiagnostico(cursormDiagnostico);
        }
        if (!cursormDiagnostico.isClosed()) cursormDiagnostico.close();
        return mDiagnostico;
    }

    //Obtener la lista de escuelas de la base de datos
    public List<EscuelaPacienteDTO> getEscuelas(String filtro, String orden) throws SQLException {
        List<EscuelaPacienteDTO> mEscuelas = new ArrayList<EscuelaPacienteDTO>();
        Cursor cursorEscuelas = crearCursor(MainDBConstants.ESCUELA_TABLE, filtro, null, orden);
        if (cursorEscuelas != null && cursorEscuelas.getCount() > 0) {
            cursorEscuelas.moveToFirst();
            mEscuelas.clear();
            do {
                EscuelaPacienteDTO mEscuela = null;
                mEscuela = EscuelaHelper.crearEscuela(cursorEscuelas);
                mEscuelas.add(mEscuela);
            } while (cursorEscuelas.moveToNext());
        }
        if (!cursorEscuelas.isClosed()) cursorEscuelas.close();
        return mEscuelas;
    }

    public EscuelaPacienteDTO getEscuela(String filtro, String orden) throws SQLException {
        EscuelaPacienteDTO mEscuela = null;
        Cursor cursorEscuela = crearCursor(MainDBConstants.ESCUELA_TABLE , filtro, null, orden);
        if (cursorEscuela != null && cursorEscuela.getCount() > 0) {
            cursorEscuela.moveToFirst();
            mEscuela= EscuelaHelper.crearEscuela(cursorEscuela);
        }
        if (!cursorEscuela.isClosed()) cursorEscuela.close();
        return mEscuela;
    }

    //Obtener una Hoja de Consulta de la base de datos
    public HojaConsultaOffLineDTO getHojaConsulta(String filtro, String orden) throws SQLException {
        HojaConsultaOffLineDTO mHojaConsulta = null;
        Cursor cursorHojaConsulta = crearCursor(MainDBConstants.HOJACONSULTA_TABLE , filtro, null, orden);
        if (cursorHojaConsulta != null && cursorHojaConsulta.getCount() > 0) {
            cursorHojaConsulta.moveToFirst();
            mHojaConsulta= HojaConsultaHelper.crearHojaConsulta(cursorHojaConsulta);
        }
        if (!cursorHojaConsulta.isClosed()) cursorHojaConsulta.close();
        return mHojaConsulta;
    }

    //Obtener la lista de hojas de consulta activas en el dia de la base de datos
    public List<HojaConsultaOffLineDTO> getListHojasConsultas(String filtro, String orden) throws SQLException {
        List<HojaConsultaOffLineDTO> mHojasConsultas = new ArrayList<HojaConsultaOffLineDTO>();
        Cursor cursorHojasConsultas = crearCursor(MainDBConstants.HOJACONSULTA_TABLE, filtro, null, orden);
        if (cursorHojasConsultas != null && cursorHojasConsultas.getCount() > 0) {
            cursorHojasConsultas.moveToFirst();
            mHojasConsultas.clear();
            do {
                HojaConsultaOffLineDTO mHojasConsulta = null;
                mHojasConsulta = HojaConsultaHelper.crearHojaConsulta(cursorHojasConsultas);
                mHojasConsultas.add(mHojasConsulta);
            } while (cursorHojasConsultas.moveToNext());
        }
        if (!cursorHojasConsultas.isClosed()) cursorHojasConsultas.close();
        return mHojasConsultas;
    }

    //Crear una nueva hoja de consulta
    public boolean createNewHojaConsulta(int filtro, Context context) {
        boolean result = false;
        String estudios = obtenerEstudiosParticipante(filtro);

        String numHojaConsulta = obtenerNumeroHojaConsulta();

        HojaConsultaOffLineDTO hojaConsulta = new HojaConsultaOffLineDTO();
        hojaConsulta.setNumOrdenLlegada(0);
        hojaConsulta.setCodExpediente(filtro);
        hojaConsulta.setNumHojaConsulta(numHojaConsulta != null ? Integer.parseInt(numHojaConsulta) + 1 : 1);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = formatter.format(date);

        hojaConsulta.setFechaConsulta(strDate);
        hojaConsulta.setEstudiosParticipantes(estudios);
        hojaConsulta.setEstado("1");
        hojaConsulta.setEsConsultaTerreno("S");
        hojaConsulta.setDiagnostico1((short) 0);
        hojaConsulta.setDiagnostico2((short) 0);
        hojaConsulta.setDiagnostico3((short) 0);
        hojaConsulta.setDiagnostico4((short) 0);
        hojaConsulta.setStatusSubmitted("N");
        long resultado = crearNuevaHojaConsulta(hojaConsulta);
        if (resultado != -1) {
            //success
            result = true;
        }
        return result;
    }

    public GrillaCierreDTO getUsuarioGrillaCierre(String usuario, String valor) throws SQLException {
        GrillaCierreDTO mUser = null;
        String params = "and uv.id = " + usuario + " and rv.nombre LIKE " + valor;
        String query = "select rv.nombre as cargo, uv.codigoPersonal, uv.nombre as nombre " +
                "from usuario uv, roles rv " +
                "where uv.usuario = rv.usuario " + params;
        Cursor cursorUser = mDb.rawQuery(query, new String[]{});
        if (cursorUser != null && cursorUser.getCount() > 0) {
            cursorUser.moveToFirst();
            mUser= UsuarioHelper.crearUsuarioGrilla(cursorUser);
        }
        if (!cursorUser.isClosed()) cursorUser.close();
        return mUser;
    }

    //Obtener la lista de enfermeria
    @SuppressLint("Range")
    public ArrayList<InicioDTO> getListaEnfermeria() throws SQLException {
        ArrayList<InicioDTO> mHojasConsultas = new ArrayList<InicioDTO>();
        String query = "SELECT DISTINCT h.secHojaConsulta, h.codExpediente, " +
                "h.numHojaConsulta, " +
                "p.nombrePaciente, " +
                "e.descripcion, e.estado, " +
                "p.sexo, p.fechaNac, " +
                "h.fechaConsulta, " +
                "h.ordenLlegada, e.codigo, " +
                "(SELECT um.nombre FROM usuario um WHERE coalesce(h.usuarioMedico, -1) = um.id) as nombre, " +
                "h.usuarioEnfermeria, " +
                "h.usuarioMedico, " +
                "h.horasv, " +
                "h.pesoKg, h.tallaCm, h.temperaturac " +
                "FROM hojaConsulta h, participante p, estadosHojas e " +
                "WHERE p.codExpediente = h.codExpediente " +
                "AND e.codigo = h.estado " +
                "AND e.codigo = '1' ";

        //Cursor cursor = mDb.rawQuery("select count(*) from hojaConsulta;", new String[]{});
        Cursor cursorHojasConsultas = mDb.rawQuery(query, new String[]{});

        if (cursorHojasConsultas != null && cursorHojasConsultas.getCount() > 0) {
            cursorHojasConsultas.moveToFirst();
            mHojasConsultas.clear();
            do {
                InicioDTO mHojasConsulta = null;
                mHojasConsulta = HojaConsultaHelper.crearListaConsulta(cursorHojasConsultas);
                mHojasConsultas.add(mHojasConsulta);
            } while (cursorHojasConsultas.moveToNext());
        }
        if (!cursorHojasConsultas.isClosed()) cursorHojasConsultas.close();
        return mHojasConsultas;
    }

    //Obtener la lista de consulta
    @SuppressLint("Range")
    public ArrayList<InicioDTO> getListaConsulta() throws SQLException {
        ArrayList<InicioDTO> mHojasConsultas = new ArrayList<InicioDTO>();
        String query = "SELECT DISTINCT h.secHojaConsulta, h.codExpediente, " +
                "h.numHojaConsulta, " +
                "p.nombrePaciente, " +
                "e.descripcion, e.estado, " +
                "p.sexo, p.fechaNac, " +
                "h.fechaConsulta, " +
                "h.ordenLlegada, e.codigo, " +
                "(SELECT um.nombre FROM usuario um WHERE coalesce(h.usuarioMedico, -1) = um.id) as nombre, " +
                "h.usuarioEnfermeria, " +
                "h.usuarioMedico, " +
                "h.horasv, " +
                "h.pesoKg, h.tallaCm, h.temperaturac " +
                "FROM hojaConsulta h, participante p, estadosHojas e " +
                "WHERE p.codExpediente = h.codExpediente " +
                "AND e.codigo = h.estado " +
                "AND e.codigo > '1' AND e.codigo < '7' ";

        Cursor cursorHojasConsultas = mDb.rawQuery(query, new String[]{});

        if (cursorHojasConsultas != null && cursorHojasConsultas.getCount() > 0) {
            cursorHojasConsultas.moveToFirst();
            mHojasConsultas.clear();
            do {
                InicioDTO mHojasConsulta = null;
                mHojasConsulta = HojaConsultaHelper.crearListaConsulta(cursorHojasConsultas);
                mHojasConsultas.add(mHojasConsulta);
            } while (cursorHojasConsultas.moveToNext());
        }
        if (!cursorHojasConsultas.isClosed()) cursorHojasConsultas.close();
        return mHojasConsultas;
    }

    //Obtener la lista de consulta para la pontalla de expediente
    public ArrayList<ExpedienteDTO> getListaConsultaExpediente(String codigo) throws SQLException {
        ArrayList<ExpedienteDTO> mConsultasExpediente = new ArrayList<>();
        String query = "SELECT a.numHojaConsulta, a.fechaCierre, a.secHojaConsulta, " +
                "b.descripcion, " +
                "(SELECT u.nombre FROM usuario u WHERE a.usuarioMedico = u.id) as nombre " +
                "FROM hojaConsulta a, estadosHojas b " +
                "WHERE a.estado = b.codigo " +
                "AND a.codExpediente = ? " +
                "ORDER by a.numHojaConsulta DESC";

        Cursor cursorConsultasExpediente = mDb.rawQuery(query, new String[]{ codigo });
        if (cursorConsultasExpediente != null && cursorConsultasExpediente.getCount() > 0) {
            cursorConsultasExpediente.moveToFirst();
            mConsultasExpediente.clear();
            do {
                ExpedienteDTO mConsultaExpediente = null;
                mConsultaExpediente = HojaConsultaHelper.crearListaExpediente(cursorConsultasExpediente);
                try {
                    DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    DateFormat targetFecha = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    DateFormat targetHora = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date date = null;
                    date = originalFormat.parse(mConsultaExpediente.getFechaCierre());

                    String fechaCierre = targetFecha.format(date);
                    String horaCierre = targetHora.format(date);

                    mConsultaExpediente.setFechaCierre(fechaCierre);
                    mConsultaExpediente.setHoraCierre(horaCierre);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                mConsultasExpediente.add(mConsultaExpediente);
            } while (cursorConsultasExpediente.moveToNext());
        }
        if(!cursorConsultasExpediente.isClosed()) cursorConsultasExpediente.close();
        return mConsultasExpediente;
    }

    //Obtener un Participante de la base de datos
    public PacienteDTO getParticipante(String filtro, String orden) throws SQLException {
        PacienteDTO mParticipante = null;
        Cursor cursorParticipante = crearCursor(MainDBConstants.PARTICIPANTE_TABLE , filtro, null, orden);
        if (cursorParticipante != null && cursorParticipante.getCount() > 0) {
            cursorParticipante.moveToFirst();
            mParticipante= ParticipanteHelper.crearParticipante(cursorParticipante);
        }
        if (!cursorParticipante.isClosed()) cursorParticipante.close();
        return mParticipante;
    }

    public boolean bulkInsertUsuariosBySql(String tabla, List<?> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            mDb.beginTransaction();
            SQLiteStatement stat = null;
            switch (tabla) {
                case MainDBConstants.USUARIO_TABLE:
                    stat = mDb.compileStatement(MainDBConstants.INSERT_USUARIO_TABLE);
                    for (Object remoteAppInfo : list) {
                        UsuarioHelper.fillUsuarioStatement(stat, (UsuarioDTO) remoteAppInfo);
                        long result = stat.executeInsert();
                        if (result < 0) return false;
                    }
                    break;
                default:
                    break;
            }

            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertRolesBySql(String tabla, List<RolesDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_ROLES_TABLE);
            mDb.beginTransaction();
            for (RolesDTO remoteAppInfo : list) {
                RolesHelper.fillRolStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        }  catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertConsEstutiosBySql(List<ConsEstudiosDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_CONSESTUDIOS_TABLE);
            mDb.beginTransaction();
            for (ConsEstudiosDTO remoteAppInfo : list) {
                ConsEstudiosHelper.fillConsEstudioStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertDiagnosticosBySql(List<DiagnosticoDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_DIAGNOSTICO_TABLE);
            mDb.beginTransaction();
            for (DiagnosticoDTO remoteAppInfo : list) {
                DiagnosticoHelper.fillDiagnosticoStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertEscuelasBySql(List<EscuelaPacienteDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_ESCUELA_TABLE);
            mDb.beginTransaction();
            for (EscuelaPacienteDTO remoteAppInfo : list) {
                EscuelaHelper.fillEscuelaPacienteStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertEstudiosBySql(List<EstudioCatalogoDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_ESTUDIOCATALOGO_TABLE);
            mDb.beginTransaction();
            for (EstudioCatalogoDTO remoteAppInfo : list) {
                EstudiosHelper.fillEstudiosStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertParticipantesBySql(List<PacienteDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_PARTICIPANTE_TABLE);
            mDb.beginTransaction();
            for (PacienteDTO remoteAppInfo : list) {
                ParticipanteHelper.fillEscuelaPacienteStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertHojasConsultasBySql(List<HojaConsultaOffLineDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_HOJACONSULTA_TABLE);
            mDb.beginTransaction();
            for (HojaConsultaOffLineDTO remoteAppInfo : list) {
                HojaConsultaHelper.fillHojaConsultaStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean bulkInsertEstadosHojasBySql(List<EstadosHojasDTO> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return false;
        }
        try {
            SQLiteStatement stat = mDb.compileStatement(MainDBConstants.INSERT_ESTADOS_HOJAS);
            mDb.beginTransaction();
            for (EstadosHojasDTO remoteAppInfo : list) {
                EstadosHojasHelper.fillEstadosHojaStatement(stat, remoteAppInfo);
                long result = stat.executeInsert();
                if (result < 0) return false;
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != mDb) {
                    mDb.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /*
    * Metodo para crear una nueva hoja de consulta
    * */
    public long crearNuevaHojaConsulta(HojaConsultaOffLineDTO hojaConsulta) {
        ContentValues cv = HojaConsultaHelper.crearHojaConsultaValues(hojaConsulta);
        return mDb.insertOrThrow(MainDBConstants.HOJACONSULTA_TABLE, null, cv);
    }

    /*
    * Metodo para actualizar la hoja de consulta por el secuancial.
    * */
    public boolean editarHojaConsulta(HojaConsultaOffLineDTO hojaConsulta) {
        ContentValues cv = HojaConsultaHelper.crearHojaConsultaValues(hojaConsulta);
        return mDb.update(MainDBConstants.HOJACONSULTA_TABLE, cv, MainDBConstants.secHojaConsulta + "="
                    + hojaConsulta.getSecHojaConsulta(), null) > 0;
    }

    /*
    * Metodo para obtenre el ultimo numero de hoja de consulta
    * */
    @SuppressLint("Range")
    public String obtenerNumeroHojaConsulta() {
        String numeroHoja = "";
        String numHojaConsultaQuery = "SELECT max(h.numHojaConsulta) AS numHojaConsulta FROM hojaConsulta h";
        Cursor cursor = mDb.rawQuery(numHojaConsultaQuery, null);
        if (cursor.moveToFirst()) {
            numeroHoja = cursor.getString(cursor.getColumnIndex("numHojaConsulta"));
        }
        cursor.close();
        return numeroHoja;
    }

    /*
    * Metodo para obterner los estudios del participante
    * */
    public String obtenerEstudiosParticipante(int filtro) {
        String resultado = "";
        ArrayList<EstudioCatalogoDTO>  mEstudiosCatalogo = new ArrayList<EstudioCatalogoDTO>();
        String query = "SELECT DISTINCT ec.codEstudio, ec.descEstudio " +
                "FROM consEstudios c, estudioCatalogo ec " +
                "WHERE c.codigoConsentimiento = ec.codEstudio " +
                "AND c.codExpediente = ? and c.retirado != '1' " +
                "ORDER by ec.descEstudio ASC";

        Cursor cursorEstudiosParticipante = mDb.rawQuery(query, new String[]{ String.valueOf(filtro)});

        if (cursorEstudiosParticipante != null && cursorEstudiosParticipante.getCount() > 0) {
            cursorEstudiosParticipante.moveToFirst();
            mEstudiosCatalogo.clear();
            do {
                EstudioCatalogoDTO mEstudioCatalogo = null;
                mEstudioCatalogo = EstudiosHelper.crearEstudio(cursorEstudiosParticipante);
                mEstudiosCatalogo.add(mEstudioCatalogo);
            } while (cursorEstudiosParticipante.moveToNext());
        }
        if (!cursorEstudiosParticipante.isClosed()) cursorEstudiosParticipante.close();
        StringBuilder str = new StringBuilder("");
        if (mEstudiosCatalogo.size() > 1) {
            for (EstudioCatalogoDTO value : mEstudiosCatalogo) {
                // Each element in ArrayList is appended
                // followed by comma
                str.append(value.getDescEstudio()).append(",");
            }
            // StringBuffer to String conversion
            resultado = str.toString();
        } else {
            resultado = mEstudiosCatalogo.get(0).getDescEstudio();
        }
        return resultado;
    }
    /*
    * Metodo que obtiene todas las hojas de consulta por codigo expediente
    * Se realiza una comparacion entre la fecha de consulta y la fecha actual
    * Si existe una hoja de consulta con la fecha actual retorna un True
    * Si el valor es True no se podra crear la hoja de consulta para el codigo ingresado.
    * */
    public boolean validarSiExisteUnaHojaActiva(int filtro, String orden) throws SQLException {
        boolean resultado = false;
        String query = "SELECT * " +
                "FROM hojaConsulta " +
                "WHERE codExpediente = ? " +
                "AND DATE(fechaConsulta) = CURRENT_DATE " +
                "AND estado NOT IN('7', '8')";
        Cursor cursorHojasConsultas = mDb.rawQuery(query, new String[]{ String.valueOf(filtro)});
        HojaConsultaOffLineDTO mHojaConsulta = null;
        if (cursorHojasConsultas != null && cursorHojasConsultas.getCount() > 0) {
            cursorHojasConsultas.moveToFirst();
            mHojaConsulta= HojaConsultaHelper.crearHojaConsulta(cursorHojasConsultas);
        }
        if (!cursorHojasConsultas.isClosed()) cursorHojasConsultas.close();
        if (mHojaConsulta != null) {
            resultado = true;
        }
        return resultado;
    }
}
