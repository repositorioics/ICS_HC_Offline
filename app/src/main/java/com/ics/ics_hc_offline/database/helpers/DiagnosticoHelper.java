package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteStatement;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;

public class DiagnosticoHelper extends BindStatementHelper {
    public static DiagnosticoDTO crearDiagnostico(Cursor cursorEstudio) {
        DiagnosticoDTO mDiagnostico = new DiagnosticoDTO();
        mDiagnostico.setSecDiagnostico(cursorEstudio.getInt(cursorEstudio.getColumnIndex(MainDBConstants.secDiagnostico)));
        mDiagnostico.setCodigoDignostico(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.codigoDignostico)));
        mDiagnostico.setDiagnostico(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.diagnostico)));
        //mDiagnostico.setEstado(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.estado)).charAt(0));
        return mDiagnostico;
    }

    public static void fillDiagnosticoStatement(SQLiteStatement stat, DiagnosticoDTO diagnosticoDTO){
        stat.bindLong(1, diagnosticoDTO.getSecDiagnostico());
        bindString(stat,2, diagnosticoDTO.getCodigoDignostico());
        bindString(stat,3, diagnosticoDTO.getDiagnostico());
        //stat.bindString(4, String.valueOf(diagnosticoDTO.getEstado()));
    }
}
