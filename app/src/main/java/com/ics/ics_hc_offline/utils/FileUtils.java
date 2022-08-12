package com.ics.ics_hc_offline.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public final class FileUtils {
    private final static String tag = "FileUtils";

    // Storage paths
    public static final String ICS_ROOT = Environment.getExternalStorageDirectory() + "/ics-hc/hojaConsulta/";
    public static final String DATABASE_PATH = ICS_ROOT + "databases/";

    public static final boolean deleteFolder(String path) {
        // not recursive
        if (path != null && storageReady()) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (!file.delete()) {
                        Log.i(tag, "Failed to delete " + file);
                    }
                }
            }
            return dir.delete();
        } else {
            return false;
        }
    }


    public static final boolean createFolder(String path) {
        if (storageReady()) {
            boolean made = true;
            File dir = new File(path);
            if (!dir.exists()) {
                made = dir.mkdirs();
            }
            return made;
        } else {
            return false;
        }
    }

    public static final boolean existFile(String path) {
        boolean exist = false;
        if (storageReady()) {
            File dir = new File(path);
            if (dir.exists()) {
                exist = true;
            }
        }
        return exist;
    }


    public static final boolean deleteFile(String path) {
        if (storageReady()) {
            File f = new File(path);
            return f.delete();
        } else {
            return false;
        }
    }

    public static boolean storageReady() {
        String cardstatus = Environment.getExternalStorageState();
        if (cardstatus.equals(Environment.MEDIA_REMOVED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return false;
        } else {
            return true;
        }
    }
}
