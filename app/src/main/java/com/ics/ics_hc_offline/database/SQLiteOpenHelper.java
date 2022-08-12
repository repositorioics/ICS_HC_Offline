package com.ics.ics_hc_offline.database;

import android.content.Context;
import android.os.Environment;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public abstract class SQLiteOpenHelper {
    private final String mPath;
    private final String mName;
    private final int mNewVersion;
    private final Context mContext;
    private final boolean mFromServer;
    private final boolean mCleanDb;

    public static final String ICS_ROOT = Environment.getExternalStorageDirectory() + "/ics-hc/hojaConsulta/";
    public static final String DATABASE_PATH = ICS_ROOT + "databases/";

    private SQLiteDatabase mDatabase = null;
    private boolean mIsInitializing = false;

    public SQLiteOpenHelper(String path, String name, int version, Context context,
                            boolean fromServer, boolean cleanDb) {
        if (version < 1)
            throw new IllegalArgumentException("Version must be >= 1, was " + version);

        mPath = path;
        mName = name;
        mNewVersion = version;
        mContext = context;
        //mPassword = password;
        mFromServer = fromServer;
        mCleanDb = cleanDb;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase; // The database is already open for business
        }
        if (mIsInitializing) {
            throw new IllegalStateException("getWritableDatabase called recursively");
        }
        boolean success = false;
        SQLiteDatabase db = null;
        try {
            SQLiteDatabase.loadLibs(mContext);
            mIsInitializing = true;
            //String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
            //File databaseFile = new File(mContext.getFilesDir().getPath() + "/" + mName);
            File databaseFile = new File( DATABASE_PATH + mName);
            if (databaseFile.exists() && mCleanDb) {
                databaseFile.delete();
            }

            /*if (!databaseFile.exists() && !mFromServer) {
                throw new IllegalStateException(mContext.getString(R.string.errordb));
            }*/
            else {
                db = SQLiteDatabase.openOrCreateDatabase(databaseFile, null, null);
            }


            int version = db.getVersion();
            //int version = 0;
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    //db.setVersion(0);
                    db.setTransactionSuccessful();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);
            success = true;
            return db;
        } catch (Throwable e) {
            throw e;
        } finally{
            mIsInitializing = false;
            if (success) {
                if (mDatabase != null) {
                    try {
                        mDatabase.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // mDatabase.unlock();
                }
                mDatabase = db;
            } else {
                // if (mDatabase != null) mDatabase.unlock();
                if (db != null)
                    db.close();
            }
        }
    }


    /**
     * Close any open database object.
     */
    public synchronized void close() {
        if (mIsInitializing)
            throw new IllegalStateException("Closed during initialization");

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }


    /**
     * Called when the database is created for the first time. This is where the creation of tables
     * and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    public abstract void onCreate(SQLiteDatabase db);


    /**
     * Called when the database needs to be upgraded. The implementation should use this method to
     * drop tables, add tables, or do anything else it needs to upgrade to the new schema version.
     * <p>
     * The SQLite ALTER TABLE documentation can be found <a
     * href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns you can use
     * ALTER TABLE to insert them into a live table. If you rename or remove columns you can use
     * ALTER TABLE to rename the old table, then create the new table and then populate the new
     * table with the contents of the old table.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);


    /**
     * Called when the database has been opened. Override method should check
     * {@link SQLiteDatabase#isReadOnly} before updating the database.
     *
     * @param db The database.
     */
    public void onOpen(SQLiteDatabase db) {
    }
}
