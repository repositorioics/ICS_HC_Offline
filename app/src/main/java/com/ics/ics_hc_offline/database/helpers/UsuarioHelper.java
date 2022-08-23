package com.ics.ics_hc_offline.database.helpers;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.GrillaCierreDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;

import net.sqlcipher.database.SQLiteStatement;

public class UsuarioHelper {
    @SuppressLint("Range")
    public static UsuarioDTO crearUsuario (Cursor cursorUsuario) {
        UsuarioDTO mUsuario = new UsuarioDTO();
        mUsuario.setId(cursorUsuario.getInt(cursorUsuario.getColumnIndex(MainDBConstants.id)));
        mUsuario.setNombre(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.nombre)));
        mUsuario.setUsuario(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.usuario)));
        mUsuario.setCodigoPersonal(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.codigopersonal)));
        mUsuario.setPass(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.pass)));
        return mUsuario;
    }

    @SuppressLint("Range")
    public static GrillaCierreDTO crearUsuarioGrilla(Cursor cursorUsuario) {
        GrillaCierreDTO mUsuario = new GrillaCierreDTO();
        mUsuario.setCargo(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.cargo)));
        mUsuario.setCodigoPersonal(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.codigopersonal)));
        mUsuario.setNombre(cursorUsuario.getString(cursorUsuario.getColumnIndex(MainDBConstants.nombre)));
        return mUsuario;
    }

    public static void fillUsuarioStatement (SQLiteStatement stat, UsuarioDTO usuarioDTO) {
        stat.bindLong(1, usuarioDTO.getId());
        stat.bindString(2, usuarioDTO.getNombre());
        stat.bindString(3, usuarioDTO.getUsuario());
        stat.bindString(4, usuarioDTO.getCodigoPersonal());
        stat.bindString(5, usuarioDTO.getPass());
    }
}
