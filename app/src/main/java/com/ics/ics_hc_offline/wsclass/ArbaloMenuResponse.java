package com.ics.ics_hc_offline.wsclass;

import com.ics.ics_hc_offline.dto.NodoItemDTO;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;
import java.util.Vector;

public class ArbaloMenuResponse implements KvmSerializable {

    private Vector<SoapObject> datavector = new Vector<SoapObject>();
    private DataNodoItemArray dataNodoArray = new DataNodoItemArray();

    @Override
    public Object getProperty(int arg0) {
        //return this.datavector.get(arg0);
        return this.dataNodoArray;
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.name = "return";
        info.type = new Vector<NodoItemDTO>().getClass();
    }

    @Override
    public String getInnerText() {
        return null;
    }

    @Override
    public void setInnerText(String s) {

    }

    @Override
    public void setProperty(int index, Object value) {
        this.datavector = (Vector<SoapObject>) value;

        for(int i = 0; i < datavector.size(); i++) {
            dataNodoArray.setProperty(0, datavector.get(i));
        }
    }
}
