package com.ics.ics_hc_offline.database.constants;

public class MainDBConstants {
    //Base de datos y tablas
    public static final String DATABASE_NAME = "hojaconsultascryp.sqlite3";
    public static final int DATABASE_VERSION = 2;

    //Tabla Usuario
    public static final String USUARIO_TABLE = "usuario";

    //Campos tabla usuario
    public static final String id = "id";
    public static final String nombre = "nombre" ;
    public static final String usuario = "usuario";
    public static final String codigopersonal = "codigopersonal";
    public static final String pass = "pass";
    public static final String cargo = "cargo";

    //Crear tabla usuario
    public static final String CREATE_USUARIO_TABLE = "create table if not exists "
            + USUARIO_TABLE + " ("
            + id + " integer not null, "
            + nombre + " text, "
            + usuario + " text, "
            + codigopersonal + " text, "
            + pass + " text, "
            + "primary key (" + id + "));";

    public static final String INSERT_USUARIO_TABLE = "insert into "
            + USUARIO_TABLE + "("
            + id + ","
            + nombre + ","
            + usuario + ","
            + codigopersonal + ","
            + pass
            + ") " + "values(?,?,?,?,?)";

    //Tabla Roles
    public static final String ROLES_TABLE = "roles";

    //Crear tabla roles
    public static final String CREATE_ROLES_TABLE = "create table if not exists "
            + ROLES_TABLE + " ("
            + id + " integer not null, "
            + nombre + " text, "
            + usuario + " text, "
            + "primary key (" + id + "));";

    public static final String INSERT_ROLES_TABLE = "insert into "
            + ROLES_TABLE + "("
            + id + ","
            + nombre + ","
            + usuario
            + ") " + "values(?,?,?)";

    //Tabla Participantes
    public static final String PARTICIPANTE_TABLE = "participante";

    // Campos tabla Participantes
    public static final String codExpediente = "codExpediente";
    public static final String secPaciente = "secPaciente";
    public static final String nombrePaciente = "nombrePaciente";
    //public static final String nombre1 = "nombre1";
    //public static final String nombre2 = "nombre2";
    //public static final String apellido1 = "apellido1";
    //public static final String apellido2 = "apellido2";
    public static final String sexo = "sexo";
    public static final String direccion = "direccion";
    public static final String fechaNac = "fechaNac";
    public static final String estado = "estado";

    //Crear tabla participantes
    public static final String CREATE_PARTICIPANTE_TALBE = "create table if not exists "
            + PARTICIPANTE_TABLE + " ("
            + codExpediente + " integer not null, "
            + secPaciente + " integer not null, "
            + nombrePaciente + " text not null, "
            + sexo + " text,"
            + direccion + " text, "
            + fechaNac + " date, "
            + estado + " text not null, "
            + "primary key (" + codExpediente + "));";

    public static final String INSERT_PARTICIPANTE_TABLE = "insert into " + PARTICIPANTE_TABLE + "("
            + codExpediente + ","
            + secPaciente + ","
            + nombrePaciente + ","
            + sexo + ","
            + direccion + ","
            + fechaNac + ","
            + estado
            + ") " + "values(?,?,?,?,?,?,?)";

    //Tabla Diagnostico
    public static final String DIAGNOSTICO_TABLE = "diagnostico";

    // Campos tabla Diagnosticos
    public static final String secDiagnostico = "secDiagnostico";
    public static final String codigoDignostico = "codigoDignostico";
    public static final String diagnostico = "diagnostico";

    //Crear tabla Diagnostico
    public static final String CREATE_DIAGNOSTICO_TABLE= "create table if not exists "
            + DIAGNOSTICO_TABLE + " ("
            + secDiagnostico + " integer not null, "
            + codigoDignostico + " text not null, "
            + diagnostico + " text, "
            + "primary key (" + secDiagnostico + "));";

    public static final String INSERT_DIAGNOSTICO_TABLE = "insert into " + DIAGNOSTICO_TABLE + "("
            + secDiagnostico + ","
            + codigoDignostico + ","
            + diagnostico
            + ") " + "values(?,?,?)";

    //Tabla consEstudios
    public static final String CONSESTUDIOS_TABLE = "consEstudios";

    //Campos tabla consEstudios
    public static final String secConsEstudios = "secConsEstudios";
    public static final String codigoConsentimiento = "codigoConsentimiento";
    public static final String retirado = "retirado";

    //Crear tabla consEstudios
    public static final String CREATE_CONSESTUDIOS_TABLE= "create table if not exists "
            + CONSESTUDIOS_TABLE + " ("
            + secConsEstudios + " integer not null, "
            + codExpediente + " integer not null, "
            + codigoConsentimiento + " text, "
            + retirado + " text, "
            + "primary key (" + secConsEstudios + "));";

    public static final String INSERT_CONSESTUDIOS_TABLE = "insert into " + CONSESTUDIOS_TABLE + "("
            + secConsEstudios + ","
            + codExpediente + ","
            + codigoConsentimiento + ","
            + retirado
            + ") " + "values(?,?,?,?)";

    //Tabla estudioCatalogo
    public static final String ESTUDIOCATALOGO_TABLE = "estudioCatalogo";

    //Campos tabla estudioCatalogo
    public static final String codEstudio = "codEstudio";
    public static final String descEstudio = "descEstudio";

    //Crear tabla estudioCatalogo
    public static final String CREATE_ESTUDIOCATALOGO_TABLE= "create table if not exists "
            + ESTUDIOCATALOGO_TABLE + " ("
            + codEstudio + " integer not null, "
            + descEstudio + " text not null, "
            + "primary key (" + codEstudio + "));";

    public static final String INSERT_ESTUDIOCATALOGO_TABLE = "insert into " + ESTUDIOCATALOGO_TABLE + "("
            + codEstudio + ","
            + descEstudio
            + ") " + "values(?,?)";

    //Tabla escuelaCatalogo
    public static final String ESCUELA_TABLE = "escuelaCatalogo";

    //Campos tabla escuelaCatalogo
    public static final String codEscuela = "codEscuela";
    public static final String descripcion = "descripcion";

    //Crear tabla escuelaCatalogo
    public static final String CREATE_ESCUELA_TABLE= "create table if not exists "
            + ESCUELA_TABLE + " ("
            + codEscuela + " integer not null, "
            + descripcion + " text not null, "
            + "primary key (" + codEscuela + "));";

    public static final String INSERT_ESCUELA_TABLE = "insert into " + ESCUELA_TABLE + "("
            + codEscuela + ","
            + descripcion
            + ") " + "values(?,?)";

    //Tabla estados_hojas
    public static final String ESTADOS_HOJAS_TABLE = "estadosHojas";

    //Campos de la tabla estados hoja
    public static final String secEstado = "secEstado";
    public static final String codigo = "codigo";
    //public static final String estado = "codigo";
    public static final String orden = "orden";

    //Crear tabla estados hojas
    public static final String CREATE_ESTADOS_HOJAS= "create table if not exists "
            + ESTADOS_HOJAS_TABLE + " ("
            + secEstado + " integer not null, "
            + codigo + " integer not null, "
            + descripcion + " text, "
            + estado + " text, "
            + orden + " text, "
            + "primary key (" + secEstado + "));";

    public static final String INSERT_ESTADOS_HOJAS = "insert into " + ESTADOS_HOJAS_TABLE + "("
            + secEstado + ","
            + codigo + ","
            + descripcion + ","
            + estado + ","
            + orden
            + ") " + "values(?,?,?,?,?)";

    //Tabla HojaConsulta
    public static final String HOJACONSULTA_TABLE = "hojaConsulta";

    //Campos tabla HojaConsulta
    public static final String secHojaConsulta = "secHojaConsulta";
    public static final String  fechaConsulta = "fechaConsulta";
    public static final String numHojaConsulta = "numHojaConsulta";
    public static final String pesoKg = "pesoKg";
    public static final String tallaCm = "tallaCm";
    public static final String temperaturac = "temperaturac";
    public static final String usuarioEnfermeria = "usuarioEnfermeria";
    public static final String expedienteFisico = "expedienteFisico";
    public static final String historiaExamenFisico = "historiaExamenFisico";
    public static final String planes = "planes";
    public static final String presion = "presion";
    public static final String pas = "pas";
    public static final String pad = "pad";
    public static final String fciaResp = "fciaResp";
    public static final String fciaCard = "fciaCard";
    public static final String  lugarAtencion = "lugarAtencion";
    public static final String consulta = "consulta";
    public static final String segChick = "segChick";
    public static final String turno = "turno";
    public static final String temMedc = "temMedc";
    public static final String fis = "fis";
    public static final String fif = "fif";
    public static final String ultDiaFiebre = "ultDiaFiebre";
    public static final String ultDosisAntipiretico = "ultDosisAntipiretico";
    public static final String amPmUltDiaFiebre = "amPmUltDiaFiebre";
    public static final String horaUltDosisAntipiretico = "horaUltDosisAntipiretico";
    public static final String amPmUltDosisAntipiretico = "amPmUltDosisAntipiretico";
    public static final String fiebre = "fiebre";
    public static final String astenia = "astenia";
    public static final String asomnoliento = "asomnoliento";
    public static final String malEstado = "malEstado";
    public static final String perdidaConsciencia = "perdidaConsciencia";
    public static final String inquieto = "inquieto";
    public static final String convulsiones = "convulsiones";
    public static final String hipotermia = "hipotermia";
    public static final String letargia = "letargia";
    public static final String pocoApetito = "pocoApetito";
    public static final String nausea = "nausea";
    public static final String dificultadAlimentarse = "dificultadAlimentarse";
    public static final String vomito12horas = "vomito12horas";
    public static final String vomito12h = "vomito12h";
    public static final String diarrea = "diarrea";
    public static final String diarreaSangre = "diarreaSangre";
    public static final String estrenimiento = "estrenimiento";
    public static final String dolorAbIntermitente = "dolorAbIntermitente";
    public static final String dolorAbContinuo = "dolorAbContinuo";
    public static final String epigastralgia = "epigastralgia";
    public static final String intoleranciaOral = "intoleranciaOral";
    public static final String distensionAbdominal = "distensionAbdominal";
    public static final String hepatomegalia = "hepatomegalia";
    public static final String hepatomegaliaCm = "hepatomegaliaCm";
    public static final String altralgia = "altralgia";
    public static final String mialgia = "mialgia";
    public static final String lumbalgia = "lumbalgia";
    public static final String dolorCuello = "dolorCuello";
    public static final String tenosinovitis = "tenosinovitis";
    public static final String artralgiaProximal = "artralgiaProximal";
    public static final String artralgiaDistal = "artralgiaDistal";
    public static final String conjuntivitis = "conjuntivitis";
    public static final String edemaMunecas = "edemaMunecas";
    public static final String edemaCodos = "edemaCodos";
    public static final String edemaHombros = "edemaHombros";
    public static final String edemaRodillas = "edemaRodillas";
    public static final String edemaTobillos = "edemaTobillos";
    public static final String cefalea = "cefalea";
    public static final String rigidezCuello = "rigidezCuello";
    public static final String inyeccionConjuntival = "inyeccionConjuntival";
    public static final String hemorragiaSuconjuntival = "hemorragiaSuconjuntival";
    public static final String dolorRetroocular = "dolorRetroocular";
    public static final String fontanelaAbombada = "fontanelaAbombada";
    public static final String ictericiaConuntival = "ictericiaConuntival";
    public static final String lenguaMucosasSecas = "lenguaMucosasSecas";
    public static final String pliegueCutaneo = "pliegueCutaneo";
    public static final String orinaReducida = "orinaReducida";
    public static final String bebeConSed = "bebeConSed";
    public static final String ojosHundidos = "ojosHundidos";
    public static final String fontanelaHundida = "fontanelaHundida";
    public static final String rahsLocalizado = "rahsLocalizado";
    public static final String rahsGeneralizado = "rahsGeneralizado";
    public static final String rashEritematoso = "rashEritematoso";
    public static final String rahsMacular = "rahsMacular";
    public static final String rashPapular = "rashPapular";
    public static final String rahsMoteada = "rahsMoteada";
    public static final String ruborFacial = "ruborFacial";
    public static final String equimosis = "equimosis";
    public static final String cianosisCentral = "cianosisCentral";
    public static final String ictericia = "ictericia";
    public static final String eritema = "eritema";
    public static final String dolorGarganta = "dolorGarganta";
    public static final String adenopatiasCervicales = "adenopatiasCervicales";
    public static final String exudado = "exudado";
    public static final String petequiasMucosa = "petequiasMucosa";
    public static final String sintomasUrinarios = "sintomasUrinarios";
    public static final String leucocituria = "leucocituria";
    public static final String nitritos = "nitritos";
    public static final String eritrocitos = "eritrocitos";
    public static final String bilirrubinuria = "bilirrubinuria";
    public static final String obeso = "obeso";
    public static final String sobrepeso = "sobrepeso";
    public static final String sospechaProblema = "sospechaProblema";
    public static final String normal = "normal";
    public static final String bajoPeso = "bajoPeso";
    public static final String bajoPesoSevero = "bajoPesoSevero";
    public static final String tos = "tos";
    public static final String rinorrea = "rinorrea";
    public static final String congestionNasal = "congestionNasal";
    public static final String otalgia = "otalgia";
    public static final String aleteoNasal = "aleteoNasal";
    public static final String apnea = "apnea";
    public static final String respiracionRapida = "respiracionRapida";
    public static final String quejidoEspiratorio = "quejidoEspiratorio";
    public static final String estiradorReposo = "estiradorReposo";
    public static final String tirajeSubcostal = "tirajeSubcostal";
    public static final String sibilancias = "sibilancias";
    public static final String crepitos = "crepitos";
    public static final String roncos = "roncos";
    public static final String otraFif = "otraFif";
    public static final String nuevaFif =  "nuevaFif";
    public static final String interconsultaPediatrica = "interconsultaPediatrica";
    public static final String referenciaHospital = "referenciaHospital";
    public static final String referenciaDengue = "referenciaDengue";
    public static final String referenciaIrag = "referenciaIrag";
    public static final String referenciaChik = "referenciaChik";
    public static final String eti = "eti";
    public static final String irag = "irag";
    public static final String neumonia = "neumonia";
    public static final String lactanciaMaterna = "lactanciaMaterna";
    public static final String vacunasCompletas = "vacunasCompletas";
    public static final String vacunaInfluenza = "vacunaInfluenza";
    public static final String fechaVacuna = "fechaVacuna";
    public static final String saturaciono2 = "saturaciono2";
    public static final String imc = "imc";
    public static final String categoria = "categoria";
    public static final String cambioCategoria = "cambioCategoria";
    public static final String manifestacionHemorragica = "manifestacionHemorragica";
    public static final String pruebaTorniquetePositiva = "pruebaTorniquetePositiva";
    public static final String petequia10Pt = "petequia10Pt";
    public static final String petequia20Pt = "petequia20Pt";
    public static final String pielExtremidadesFrias = "pielExtremidadesFrias";
    public static final String palidezEnExtremidades = "palidezEnExtremidades";
    public static final String epistaxis = "epistaxis";
    public static final String gingivorragia = "gingivorragia";
    public static final String petequiasEspontaneas = "petequiasEspontaneas";
    public static final String llenadoCapilar2seg = "llenadoCapilar2seg";
    public static final String cianosis = "cianosis";
    public static final String linfocitosaAtipicos = "linfocitosaAtipicos";
    public static final String fechaLinfocitos = "fechaLinfocitos";
    public static final String hipermenorrea = "hipermenorrea";
    public static final String hematemesis = "hematemesis";
    public static final String melena = "melena";
    public static final String hemoconc = "hemoconc";
    public static final String hemoconcentracion = "hemoconcentracion";
    public static final String hospitalizado = "hospitalizado";
    public static final String hospitalizadoEspecificar = "hospitalizadoEspecificar";
    public static final String transfusionSangre = "transfusionSangre";
    public static final String transfusionEspecificar = "transfusionEspecificar";
    public static final String tomandoMedicamento = "tomandoMedicamento";
    public static final String medicamentoEspecificar = "medicamentoEspecificar";
    public static final String medicamentoDistinto = "medicamentoDistinto";
    public static final String medicamentoDistEspecificar = "medicamentoDistEspecificar";
    public static final String horasv = "horasv";
    public static final String acetaminofen = "acetaminofen";
    public static final String asa = "asa";
    public static final String ibuprofen = "ibuprofen";
    public static final String penicilina = "penicilina";
    public static final String amoxicilina = "amoxicilina";
    public static final String dicloxacilina = "dicloxacilina";
    public static final String otroAntibiotico = "otroAntibiotico";
    public static final String furazolidona = "furazolidona";
    public static final String metronidazolTinidazol = "metronidazolTinidazol";
    public static final String albendazolMebendazol = "albendazolMebendazol";
    public static final String sulfatoFerroso = "sulfatoFerroso";
    public static final String sueroOral = "sueroOral";
    public static final String sulfatoZinc = "sulfatoZinc";
    public static final String liquidosIv = "liquidosIv";
    public static final String prednisona = "prednisona";
    public static final String hidrocortisonaIv = "hidrocortisonaIv";
    public static final String salbutamol = "salbutamol";
    public static final String oseltamivir = "oseltamivir";
    public static final String telef = "telef";
    public static final String proximaCita = "proximaCita";
    public static final String horarioClases = "horarioClases";
    public static final String colegio = "colegio";
    public static final String bhc = "bhc";
    public static final String serologiaDengue = "serologiaDengue";
    public static final String serologiaChik = "serologiaChik";
    public static final String gotaGruesa = "gotaGruesa";
    public static final String extendidoPeriferico = "extendidoPeriferico";
    public static final String ego = "ego";
    public static final String egh = "egh";
    public static final String citologiaFecal = "citologiaFecal";
    public static final String factorReumatoideo = "factorReumatoideo";
    public static final String albumina = "albumina";
    public static final String astAlt = "astAlt";
    public static final String bilirrubinas = "bilirrubinas";
    public static final String cpk = "cpk";
    public static final String colesterol = "colesterol";
    public static final String influenza = "influenza";
    public static final String otroExamenLab = "otroExamenLab";
    public static final String oel = "oel";
    public static final String numOrdenLaboratorio = "numOrdenLaboratorio";
    public static final String fechaOrdenLaboratorio = "fechaOrdenLaboratorio";
    public static final String diagnostico1 = "diagnostico1";
    public static final String diagnostico2 = "diagnostico2";
    public static final String diagnostico3 = "diagnostico3";
    public static final String diagnostico4 = "diagnostico4";
    public static final String otroDiagnostico = "otroDiagnostico";
    public static final String fechaCierre = "fechaCierre";
    public static final String fechaCambioTurno = "fechaCambioTurno";
    public static final String fechaCierreCambioTurno = "fechaCierreCambioTurno";
    public static final String usuarioMedico = "usuarioMedico";
    public static final String medicoCambioTurno = "medicoCambioTurno";
    public static final String otro = "otro";
    public static final String estudiosParticipantes = "estudiosParticipantes";
    public static final String cV = "cV";
    public static final String consultaRespiratorio = "consultaRespiratorio";
    public static final String esConsultaTerreno = "esConsultaTerreno";
    public static final String ordenLlegada = "ordenLlegada";
    public static final String estadoCarga = "estadoCarga";
    public static final String hora = "hora";
    public static final String noAtiendeLlamadoEnfermeria = "noAtiendeLlamadoEnfermeria";
    public static final String noAtiendeLlamadoMedico = "noAtiendeLlamadoMedico";
    public static final String statusSubmitted = "statusSubmitted";

    //Crear tabla HojaConsulta
    public static final String CREATE_HOJACONSULTA_TABLE= "create table if not exists "
            + HOJACONSULTA_TABLE + " ("
            + secHojaConsulta + " integer not null, "
            + codExpediente + " integer not null, "
            + numHojaConsulta + " integer not null, "
            + ordenLlegada + " text, "
            + estado + " integer, "
            + fechaConsulta + " text not null, "
            + pesoKg + " real, "
            + tallaCm + " real, "
            + temperaturac + " real, "
            + usuarioEnfermeria + " integer, "
            + expedienteFisico + " text, "
            + historiaExamenFisico + " text, "
            + planes + " text, "

            + presion + " integer, "
            + pas + " integer, "
            + pad + " integer, "
            + fciaResp + " integer, "
            + fciaCard + " integer, "
            + lugarAtencion + " text, "
            + consulta + " text, "
            + segChick + " text, "
            + turno + " text, "
            + temMedc + " real, "
            + fis + " date, "
            + fif + " date, "
            + ultDiaFiebre + " date, "
            + ultDosisAntipiretico + " date, "
            + amPmUltDiaFiebre + " text, "
            + horaUltDosisAntipiretico + " text, "
            + amPmUltDosisAntipiretico + " text, "
            + fiebre + " text, "
            + astenia + " text, "
            + asomnoliento + " text, "
            + malEstado + " text, "
            + perdidaConsciencia + " text, "
            + inquieto + " text, "
            + convulsiones + " text, "
            + hipotermia + " text, "
            + letargia + " text, "
            + pocoApetito + " text, "
            + nausea + " text, "
            + dificultadAlimentarse + " text, "
            + vomito12horas + " text, "
            + vomito12h + " text, "
            + diarrea + " text, "
            + diarreaSangre + " text, "
            + estrenimiento + " text, "
            + dolorAbIntermitente + " text, "
            + dolorAbContinuo + " text, "
            + epigastralgia + " text, "
            + intoleranciaOral + " text, "
            + distensionAbdominal + " text, "
            + hepatomegalia + " text, "
            + hepatomegaliaCm + " text, "
            + altralgia + " text, "
            + mialgia + " text, "
            + lumbalgia + " text, "
            + dolorCuello + " text, "
            + tenosinovitis + " text, "
            + artralgiaProximal + " text, "
            + artralgiaDistal + " text, "
            + conjuntivitis + " text, "
            + edemaMunecas + " text, "
            + edemaCodos + " text, "
            + edemaHombros + " text, "
            + edemaRodillas + " text, "
            + edemaTobillos + " text, "
            + cefalea + " text, "
            + rigidezCuello + " text, "
            + inyeccionConjuntival + " text, "
            + hemorragiaSuconjuntival + " text, "
            + dolorRetroocular + " text, "
            + fontanelaAbombada + " text, "
            + ictericiaConuntival + " text, "
            + lenguaMucosasSecas + " text, "
            + pliegueCutaneo + " text, "
            + orinaReducida + " text, "
            + bebeConSed + " text, "
            + ojosHundidos + " text, "
            + fontanelaHundida + " text, "
            + rahsLocalizado + " text, "
            + rahsGeneralizado + " text, "
            + rashEritematoso + " text, "
            + rahsMacular + " text, "
            + rashPapular + " text, "
            + rahsMoteada + " text, "
            + ruborFacial + " text, "
            + equimosis + " text, "
            + cianosisCentral + " text, "
            + ictericia + " text, "
            + eritema + " text, "
            + dolorGarganta + " text, "
            + adenopatiasCervicales + " text, "
            + exudado + " text, "
            + petequiasMucosa + " text, "
            + sintomasUrinarios + " text, "
            + leucocituria + " text, "
            + nitritos + " text, "
            + eritrocitos + " text, "
            + bilirrubinuria + " text, "
            + obeso + " text, "
            + sobrepeso + " text, "
            + sospechaProblema + " text, "
            + normal + " text, "
            + bajoPeso + " text, "
            + bajoPesoSevero + " text, "
            + tos + " text, "
            + rinorrea + " text, "
            + congestionNasal + " text, "
            + otalgia + " text, "
            + aleteoNasal + " text, "
            + apnea + " text, "
            + respiracionRapida + " text, "
            + quejidoEspiratorio + " text, "
            + estiradorReposo + " text, "
            + tirajeSubcostal + " text, "
            + sibilancias + " text, "
            + crepitos + " text, "
            + roncos + " text, "
            + otraFif + " text, "
            + nuevaFif + " text, "
            + interconsultaPediatrica + " text, "
            + referenciaHospital + " text, "
            + referenciaDengue + " text, "
            + referenciaIrag + " text, "
            + referenciaChik + " text, "
            + eti + " text, "
            + irag + " text, "
            + neumonia + " text, "
            + lactanciaMaterna + " text, "
            + vacunasCompletas + " text, "
            + vacunaInfluenza + " text, "
            + fechaVacuna + " date, "
            + saturaciono2 + " integer, "
            + imc + " real, "
            + categoria + " text, "
            + cambioCategoria + " text, "
            + manifestacionHemorragica + " text, "
            + pruebaTorniquetePositiva + " text, "
            + petequia10Pt + " text, "
            + petequia20Pt + " text, "
            + pielExtremidadesFrias + " text, "
            + palidezEnExtremidades + " text, "
            + epistaxis + " text, "
            + gingivorragia + " text, "
            + petequiasEspontaneas + " text, "
            + llenadoCapilar2seg + " text, "
            + cianosis + " text, "
            + linfocitosaAtipicos + " text, "
            + fechaLinfocitos + " date, "
            + hipermenorrea + " text, "
            + hematemesis + " text, "
            + melena + " text, "
            + hemoconc + " text, "
            + hemoconcentracion + " text, "
            + hospitalizado + " text, "
            + hospitalizadoEspecificar + " text, "
            + transfusionSangre + " text, "
            + transfusionEspecificar + " text, "
            + tomandoMedicamento + " text, "
            + medicamentoEspecificar + " text, "
            + medicamentoDistinto + " text, "
            + medicamentoDistEspecificar + " text, "
            + horasv + " text, "
            + acetaminofen + " text, "
            + asa + " text, "
            + ibuprofen + " text, "
            + penicilina + " text, "
            + amoxicilina + " text, "
            + dicloxacilina + " text, "
            + otroAntibiotico + " text, "
            + furazolidona + " text, "
            + metronidazolTinidazol + " text, "
            + albendazolMebendazol + " text, "
            + sulfatoFerroso + " text, "
            + sueroOral + " text, "
            + sulfatoZinc + " text, "
            + liquidosIv + " text, "
            + prednisona + " text, "
            + hidrocortisonaIv + " text, "
            + salbutamol + " text, "
            + oseltamivir + " text, "
            + telef + " integer, "
            + proximaCita + " date, "
            + horarioClases + " text, "
            + colegio + " text, "
            + bhc + " text, "
            + serologiaDengue + " text, "
            + serologiaChik + " text, "
            + gotaGruesa + " text, "
            + extendidoPeriferico + " text, "
            + ego + " text, "
            + egh + " text, "
            + citologiaFecal + " text, "
            + factorReumatoideo + " text, "
            + albumina + " text, "
            + astAlt + " text, "
            + bilirrubinas + " text, "
            + cpk + " text, "
            + colesterol + " text, "
            + influenza + " text, "
            + otroExamenLab + " text, "
            + oel + " text, "
            + numOrdenLaboratorio + " integer, "
            + fechaOrdenLaboratorio + " date, "
            + diagnostico1 + " integer, "
            + diagnostico2 + " integer, "
            + diagnostico3 + " integer, "
            + diagnostico4 + " integer, "
            + otroDiagnostico + " text, "
            + fechaCierre + " long, "
            + fechaCambioTurno + " text, "
            + fechaCierreCambioTurno + " text, "
            + usuarioMedico + " integer, "
            + medicoCambioTurno + " integer, "
            + otro + " text, "
            + estudiosParticipantes + " text, "
            + cV + " text, "
            + consultaRespiratorio + " text, "
            + esConsultaTerreno + " text, "
            + estadoCarga + " text, "
            + hora + " text, "
            + noAtiendeLlamadoEnfermeria + " text, "
            + noAtiendeLlamadoMedico + " text, "
            + statusSubmitted + " text, "
            + "primary key (" + secHojaConsulta + "));";

    public static final String INSERT_HOJACONSULTA_TABLE = "insert into " + HOJACONSULTA_TABLE + "("
            + secHojaConsulta + ","
            + codExpediente + ","
            + numHojaConsulta + ","
            + ordenLlegada + ","
            + estado + ","
            + fechaConsulta + ","
            + pesoKg + ","
            + tallaCm + ","
            + temperaturac + ","
            + usuarioEnfermeria + ","
            + expedienteFisico + ","
            + historiaExamenFisico + ","
            + planes + ","
            + presion + ","
            + pas + ","
            + pad + ","
            + fciaResp + ","
            + fciaCard + ","
            + lugarAtencion + ","
            + consulta + ","
            + segChick + ","
            + turno + ","
            + temMedc + ","
            + fis + ","
            + fif + ","
            + ultDiaFiebre + ","
            + ultDosisAntipiretico + ","
            + amPmUltDiaFiebre + ","
            + horaUltDosisAntipiretico + ","
            + amPmUltDosisAntipiretico + ","
            + fiebre + ","
            + astenia + ","
            + asomnoliento + ","
            + malEstado + ","
            + perdidaConsciencia + ","
            + inquieto + ","
            + convulsiones + ","
            + hipotermia + ","
            + letargia + ","
            + pocoApetito + ","
            + nausea + ","
            + dificultadAlimentarse + ","
            + vomito12horas + ","
            + vomito12h + ","
            + diarrea + ","
            + diarreaSangre + ","
            + estrenimiento + ","
            + dolorAbIntermitente + ","
            + dolorAbContinuo + ","
            + epigastralgia + ","
            + intoleranciaOral + ","
            + distensionAbdominal + ","
            + hepatomegalia + ","
            + hepatomegaliaCm + ","
            + altralgia + ","
            + mialgia + ","
            + lumbalgia + ","
            + dolorCuello + ","
            + tenosinovitis + ","
            + artralgiaProximal + ","
            + artralgiaDistal + ","
            + conjuntivitis + ","
            + edemaMunecas + ","
            + edemaCodos + ","
            + edemaHombros + ","
            + edemaRodillas + ","
            + edemaTobillos + ","
            + cefalea + ","
            + rigidezCuello + ","
            + inyeccionConjuntival + ","
            + hemorragiaSuconjuntival + ","
            + dolorRetroocular + ","
            + fontanelaAbombada + ","
            + ictericiaConuntival + ","
            + lenguaMucosasSecas + ","
            + pliegueCutaneo + ","
            + orinaReducida + ","
            + bebeConSed + ","
            + ojosHundidos + ","
            + fontanelaHundida + ","
            + rahsLocalizado + ","
            + rahsGeneralizado + ","
            + rashEritematoso + ","
            + rahsMacular + ","
            + rashPapular + ","
            + rahsMoteada + ","
            + ruborFacial + ","
            + equimosis + ","
            + cianosisCentral + ","
            + ictericia + ","
            + eritema + ","
            + dolorGarganta + ","
            + adenopatiasCervicales + ","
            + exudado + ","
            + petequiasMucosa + ","
            + sintomasUrinarios + ","
            + leucocituria + ","
            + nitritos + ","
            + eritrocitos + ","
            + bilirrubinuria + ","
            + obeso + ","
            + sobrepeso + ","
            + sospechaProblema + ","
            + normal + ","
            + bajoPeso + ","
            + bajoPesoSevero + ","
            + tos + ","
            + rinorrea + ","
            + congestionNasal + ","
            + otalgia + ","
            + aleteoNasal + ","
            + apnea + ","
            + respiracionRapida + ","
            + quejidoEspiratorio + ","
            + estiradorReposo + ","
            + tirajeSubcostal + ","
            + sibilancias + ","
            + crepitos + ","
            + roncos + ","
            + otraFif + ","
            + nuevaFif + ","
            + interconsultaPediatrica + ","
            + referenciaHospital + ","
            + referenciaDengue + ","
            + referenciaIrag + ","
            + referenciaChik + ","
            + eti + ","
            + irag + ","
            + neumonia + ","
            + lactanciaMaterna + ","
            + vacunasCompletas + ","
            + vacunaInfluenza + ","
            + fechaVacuna + ","
            + saturaciono2 + ","
            + imc + ","
            + categoria + ","
            + cambioCategoria + ","
            + manifestacionHemorragica + ","
            + pruebaTorniquetePositiva + ","
            + petequia10Pt + ","
            + petequia20Pt + ","
            + pielExtremidadesFrias + ","
            + palidezEnExtremidades + ","
            + epistaxis + ","
            + gingivorragia + ","
            + petequiasEspontaneas + ","
            + llenadoCapilar2seg + ","
            + cianosis + ","
            + linfocitosaAtipicos + ","
            + fechaLinfocitos + ","
            + hipermenorrea + ","
            + hematemesis + ","
            + melena + ","
            + hemoconc + ","
            + hemoconcentracion + ","
            + hospitalizado + ","
            + hospitalizadoEspecificar + ","
            + transfusionSangre + ","
            + transfusionEspecificar + ","
            + tomandoMedicamento + ","
            + medicamentoEspecificar + ","
            + medicamentoDistinto + ","
            + medicamentoDistEspecificar + ","
            + horasv + ","
            + acetaminofen + ","
            + asa + ","
            + ibuprofen + ","
            + penicilina + ","
            + amoxicilina + ","
            + dicloxacilina + ","
            + otroAntibiotico + ","
            + furazolidona + ","
            + metronidazolTinidazol + ","
            + albendazolMebendazol + ","
            + sulfatoFerroso + ","
            + sueroOral + ","
            + sulfatoZinc + ","
            + liquidosIv + ","
            + prednisona + ","
            + hidrocortisonaIv + ","
            + salbutamol + ","
            + oseltamivir + ","
            + telef + ","
            + proximaCita + ","
            + horarioClases + ","
            + colegio + ","
            + bhc + ","
            + serologiaDengue + ","
            + serologiaChik + ","
            + gotaGruesa + ","
            + extendidoPeriferico + ","
            + ego + ","
            + egh + ","
            + citologiaFecal + ","
            + factorReumatoideo + ","
            + albumina + ","
            + astAlt + ","
            + bilirrubinas + ","
            + cpk + ","
            + colesterol + ","
            + influenza + ","
            + otroExamenLab + ","
            + oel + ","
            + numOrdenLaboratorio + ","
            + fechaOrdenLaboratorio + ","
            + diagnostico1 + ","
            + diagnostico2 + ","
            + diagnostico3 + ","
            + diagnostico4 + ","
            + otroDiagnostico + ","
            + fechaCierre + ","
            + fechaCambioTurno + ","
            + fechaCierreCambioTurno + ","
            + usuarioMedico + ","
            + medicoCambioTurno + ","
            + otro + ","
            + estudiosParticipantes + ","
            + cV + ","
            + consultaRespiratorio + ","
            + esConsultaTerreno + ","
            + estadoCarga + ","
            + hora + ","
            + noAtiendeLlamadoEnfermeria + ","
            + noAtiendeLlamadoMedico + ","
            + statusSubmitted
            + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
}
