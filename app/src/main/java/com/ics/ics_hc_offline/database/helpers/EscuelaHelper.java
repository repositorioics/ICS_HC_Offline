package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;

import net.sqlcipher.database.SQLiteStatement;

public class EscuelaHelper extends BindStatementHelper {
    public static EscuelaPacienteDTO crearEscuela(Cursor cursorEstudio) {
        EscuelaPacienteDTO mEscuela = new EscuelaPacienteDTO();
        mEscuela.setCodEscuela(cursorEstudio.getInt(cursorEstudio.getColumnIndex(MainDBConstants.codEscuela)));
        mEscuela.setDescripcion(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.descripcion)));
        return mEscuela;
    }

    public static void fillEscuelaPacienteStatement(SQLiteStatement stat, EscuelaPacienteDTO escuelaPacienteDTO){
        stat.bindLong(1, escuelaPacienteDTO.getCodEscuela());
        bindString(stat,2, escuelaPacienteDTO.getDescripcion());
    }
}
