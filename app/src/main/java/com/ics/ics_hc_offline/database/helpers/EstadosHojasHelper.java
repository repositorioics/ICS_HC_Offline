package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.EstadosHojasDTO;

import net.sqlcipher.database.SQLiteStatement;

public class EstadosHojasHelper extends BindStatementHelper {
    public static EstadosHojasDTO crearEscuela(Cursor cursorEstudio) {
        EstadosHojasDTO mEstadoHojas = new EstadosHojasDTO();
        mEstadoHojas.setSecEstado(cursorEstudio.getInt(cursorEstudio.getColumnIndex(MainDBConstants.secEstado)));
        mEstadoHojas.setCodigo(cursorEstudio.getInt(cursorEstudio.getColumnIndex(MainDBConstants.codigo)));
        mEstadoHojas.setDescripcion(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.descripcion)));
        mEstadoHojas.setEstado(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.secEstado)));
        mEstadoHojas.setOrden(cursorEstudio.getString(cursorEstudio.getColumnIndex(MainDBConstants.secEstado)));
        return mEstadoHojas;
    }

    public static void fillEstadosHojaStatement(SQLiteStatement stat, EstadosHojasDTO estadosHojasDTO){
        stat.bindLong(1, estadosHojasDTO.getSecEstado());
        stat.bindLong(2, estadosHojasDTO.getCodigo());
        bindString(stat,3, estadosHojasDTO.getDescripcion());
        bindString(stat,4, estadosHojasDTO.getEstado());
        bindString(stat,5, estadosHojasDTO.getOrden());
    }
}
