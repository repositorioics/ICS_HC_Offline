package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.EstudioCatalogoDTO;

import net.sqlcipher.database.SQLiteStatement;

public class EstudiosHelper extends BindStatementHelper {
    public static EstudioCatalogoDTO crearEstudio(Cursor cursorEstudio) {
        EstudioCatalogoDTO mEstudio = new EstudioCatalogoDTO();
        mEstudio.setCodEstudio(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.codEstudio)));
        mEstudio.setDescEstudio(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.descEstudio)));
        return mEstudio;
    }

    public static void fillEstudiosStatement(SQLiteStatement stat, EstudioCatalogoDTO estudioCatalogoDTO){
        stat.bindString(1, estudioCatalogoDTO.getCodEstudio());
        bindString(stat,2, estudioCatalogoDTO.getDescEstudio());
    }
}
