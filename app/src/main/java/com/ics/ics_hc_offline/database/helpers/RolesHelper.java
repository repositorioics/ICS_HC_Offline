package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.RolesDTO;

import net.sqlcipher.database.SQLiteStatement;

public class RolesHelper extends BindStatementHelper {
    public static RolesDTO crearRol (Cursor cursorRol) {
        RolesDTO mRol = new RolesDTO();
        mRol.setId(cursorRol.getString(cursorRol.getColumnIndex(MainDBConstants.id)));
        mRol.setNombre(cursorRol.getString(cursorRol.getColumnIndex(MainDBConstants.nombre)));
        mRol.setUsuario(cursorRol.getString(cursorRol.getColumnIndex(MainDBConstants.usuario)));
        return mRol;
    }

    public static void fillRolStatement (SQLiteStatement stat, RolesDTO rolesDTO) {
        stat.bindString(1, rolesDTO.getId());
        stat.bindString(2, rolesDTO.getNombre());
        stat.bindString(3, rolesDTO.getUsuario());
    }
}
