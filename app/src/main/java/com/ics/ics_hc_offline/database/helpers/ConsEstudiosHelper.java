package com.ics.ics_hc_offline.database.helpers;

import android.database.Cursor;

import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.ConsEstudiosDTO;

import net.sqlcipher.database.SQLiteStatement;

public class ConsEstudiosHelper extends BindStatementHelper {
    public static ConsEstudiosDTO crearConsEstudios(Cursor cursorConsEstudio) {
        ConsEstudiosDTO mConsEstudio = new ConsEstudiosDTO();
        mConsEstudio.setSecConsEstudios(cursorConsEstudio.getInt(cursorConsEstudio.getColumnIndex(MainDBConstants.secConsEstudios)));
        mConsEstudio.setCodigoExpediente(cursorConsEstudio.getInt(cursorConsEstudio.getColumnIndex(MainDBConstants.codExpediente)));
        mConsEstudio.setCodigoConsentimiento(cursorConsEstudio.getString(cursorConsEstudio.getColumnIndex(MainDBConstants.codigoConsentimiento)));
        mConsEstudio.setRetirado(cursorConsEstudio.getString(cursorConsEstudio.getColumnIndex(MainDBConstants.retirado)));

        return  mConsEstudio;
    }

    public static void fillConsEstudioStatement (SQLiteStatement stat, ConsEstudiosDTO consEstudiosDTO) {
        stat.bindLong(1, consEstudiosDTO.getSecConsEstudios());
        stat.bindLong(2, consEstudiosDTO.getCodigoExpediente());
        stat.bindString(3, consEstudiosDTO.getCodigoConsentimiento());
        stat.bindString(4, consEstudiosDTO.getRetirado());
    }
}
