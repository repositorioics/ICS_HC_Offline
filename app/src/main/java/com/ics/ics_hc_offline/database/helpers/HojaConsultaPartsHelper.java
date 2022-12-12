package com.ics.ics_hc_offline.database.helpers;

import static com.ics.ics_hc_offline.database.helpers.BindStatementHelper.bindInteger;
import static com.ics.ics_hc_offline.database.helpers.BindStatementHelper.bindString;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaPartsDTO;

import net.sqlcipher.database.SQLiteStatement;


public class HojaConsultaPartsHelper {
    @SuppressLint("Range")
    public static HojaConsultaPartsDTO crearHojaConsulta(Cursor cursorHojaConsulta) {
        HojaConsultaPartsDTO mHojaConsulta = new HojaConsultaPartsDTO();
        mHojaConsulta.setSecHojaConsulta(cursorHojaConsulta.getInt(cursorHojaConsulta.getColumnIndex(MainDBConstants.secHojaConsulta)));
        mHojaConsulta.setCodExpediente(cursorHojaConsulta.getInt(cursorHojaConsulta.getColumnIndex(MainDBConstants.codExpediente)));
        mHojaConsulta.setNumHojaConsulta(cursorHojaConsulta.getInt(cursorHojaConsulta.getColumnIndex(MainDBConstants.numHojaConsulta)));
        mHojaConsulta.setEstado(cursorHojaConsulta.getString(cursorHojaConsulta.getColumnIndex(MainDBConstants.estado)));
        mHojaConsulta.setFechaConsulta(cursorHojaConsulta.getString(cursorHojaConsulta.getColumnIndex(MainDBConstants.fechaConsulta)));
        mHojaConsulta.setUsuarioEnfermeria(cursorHojaConsulta.getInt(cursorHojaConsulta.getColumnIndex(MainDBConstants.usuarioEnfermeria)));
        mHojaConsulta.setUsuarioMedico(cursorHojaConsulta.getShort(cursorHojaConsulta.getColumnIndex(MainDBConstants.usuarioMedico)));
        mHojaConsulta.setFis(cursorHojaConsulta.getString(cursorHojaConsulta.getColumnIndex(MainDBConstants.fis)));
        mHojaConsulta.setFif(cursorHojaConsulta.getString(cursorHojaConsulta.getColumnIndex(MainDBConstants.fif)));
        return mHojaConsulta;
    }

    public static void fillHojaConsultaStatement(SQLiteStatement stat, HojaConsultaPartsDTO hojaConsultaDTO) {
        bindInteger(stat, 1, hojaConsultaDTO.getSecHojaConsulta());
        bindInteger(stat, 2, hojaConsultaDTO.getCodExpediente());
        bindInteger(stat, 3, hojaConsultaDTO.getNumHojaConsulta());
        bindString(stat, 4, String.valueOf(hojaConsultaDTO.getEstado()));
        bindString(stat, 5, hojaConsultaDTO.getFechaConsulta());
        bindInteger(stat, 6, hojaConsultaDTO.getUsuarioEnfermeria());
        bindString(stat, 7, String.valueOf(hojaConsultaDTO.getUsuarioMedico()));
        bindString(stat, 8, hojaConsultaDTO.getFis());
        bindString(stat, 9, hojaConsultaDTO.getFif());
    }
}
