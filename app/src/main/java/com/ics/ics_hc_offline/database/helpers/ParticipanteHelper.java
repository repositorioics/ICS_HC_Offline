package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.PacienteDTO;

import net.sqlcipher.database.SQLiteStatement;

import java.util.Date;

public class ParticipanteHelper extends BindStatementHelper {
    public static PacienteDTO crearParticipante(Cursor cursor){
        PacienteDTO mParticipante = new PacienteDTO();
        mParticipante.setCodExpediente(cursor.getInt(cursor.getColumnIndex(MainDBConstants.codExpediente)));
        mParticipante.setSecPaciente(cursor.getInt(cursor.getColumnIndex(MainDBConstants.secPaciente)));
        mParticipante.setNomPaciente(cursor.getString(cursor.getColumnIndex(MainDBConstants.nombrePaciente)));
        mParticipante.setSexo(cursor.getString(cursor.getColumnIndex(MainDBConstants.sexo)));
        mParticipante.setFechaNac(new Date(cursor.getLong(cursor.getColumnIndex(MainDBConstants.fechaNac))));
        mParticipante.setDireccion(cursor.getString(cursor.getColumnIndex(MainDBConstants.direccion)));
        mParticipante.setEstado(cursor.getString(cursor.getColumnIndex(MainDBConstants.estado)).charAt(0));
        return mParticipante;
    }

    public static void fillEscuelaPacienteStatement(SQLiteStatement stat, PacienteDTO pacienteDTO){
        stat.bindLong(1, pacienteDTO.getCodExpediente());
        stat.bindLong(2, pacienteDTO.getSecPaciente());
        bindString(stat,3, pacienteDTO.getNomPaciente());
        bindString(stat,4, pacienteDTO.getSexo());
        bindString(stat,5, pacienteDTO.getDireccion());
        bindDate(stat,6, pacienteDTO.getFechaNac());
        stat.bindString(7, String.valueOf(pacienteDTO.getEstado()));
    }
}
