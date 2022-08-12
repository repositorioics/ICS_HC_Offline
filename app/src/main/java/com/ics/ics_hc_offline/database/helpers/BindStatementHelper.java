package com.ics.ics_hc_offline.database.helpers;

import net.sqlcipher.database.SQLiteStatement;

import java.util.Date;

public class BindStatementHelper {
    public static void bindString(SQLiteStatement stat, int index, String value){
        if (value == null) {
            stat.bindNull(index);
        } else {
            stat.bindString(index, value);
        }
    }

    public static void bindDate(SQLiteStatement stat, int index, Date value){
        if (value == null) {
            stat.bindNull(index);
        } else {
            stat.bindLong(index, value.getTime());
        }
    }

    public static void bindInteger(SQLiteStatement stat, int index, Integer value){
        if (value == null) {
            stat.bindNull(index);
        } else {
            stat.bindLong(index, value);
        }
    }

    public static void bindLong(SQLiteStatement stat, int index, Long value){
        if (value == null) {
            stat.bindNull(index);
        } else {
            stat.bindLong(index, value);
        }
    }

    public static void bindDouble(SQLiteStatement stat, int index, Double value){
        if (value == null) {
            stat.bindNull(index);
        } else {
            stat.bindDouble(index, value);
        }
    }
}
