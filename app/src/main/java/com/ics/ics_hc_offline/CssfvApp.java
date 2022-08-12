package com.ics.ics_hc_offline;

import android.app.Application;

import com.ics.ics_hc_offline.dto.InfoSessionWSDTO;
import com.ics.ics_hc_offline.wsclass.DataNodoItemArray;

public class CssfvApp extends Application {
    private InfoSessionWSDTO infoSessionWSDTO;
    private DataNodoItemArray menuArray;

    public InfoSessionWSDTO getInfoSessionWSDTO() {
        return infoSessionWSDTO;
    }

    public void setInfoSessionWSDTO(InfoSessionWSDTO infoSessionWSDTO) {
        this.infoSessionWSDTO = infoSessionWSDTO;
    }

    public DataNodoItemArray getMenuArray() {
        return menuArray;
    }

    public void setMenuArray(DataNodoItemArray menuArray) {
        this.menuArray = menuArray;
    }
}
