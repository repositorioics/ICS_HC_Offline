package com.ics.ics_hc_offline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ics.ics_hc_offline.helper.ApkInstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class DescargarApkActivity extends Activity {
    public ProgressDialog PD_CREATE;
    private Context CONTEXT;
    public static String URL_DOWNLOAD_APK;

    static {
        try {
            Properties props = new Properties();
            InputStream inputStream = new FileInputStream("/sdcard/cssfvApk/configApk/configApk.properties");
            props.load(inputStream);
            URL_DOWNLOAD_APK = props.getProperty("DOWNLOAD_OFFLINE_APK");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.CONTEXT = this;
        UpdateApk downloadAndInstall = new UpdateApk();
        downloadAndInstall.setContext(getApplicationContext());
        downloadAndInstall.execute(URL_DOWNLOAD_APK);
        /*Intent i;
        PackageManager manager = getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage("com.ics.ics_hc_offline");
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            UpdateApk downloadAndInstall = new UpdateApk();
            downloadAndInstall.setContext(getApplicationContext());
            downloadAndInstall.execute(URL_DOWNLOAD_APK);
        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class UpdateApk extends AsyncTask<String,Void,Void> {
        //ProgressDialog progressDialog;
        AlertDialog alertDialog;
        int status = 0;

        private Context context;
        public void setContext(Context context){
            this.context = context;
        }

        public void onPreExecute() {
            PD_CREATE = new ProgressDialog(CONTEXT);
            PD_CREATE.setTitle(getResources().getString(R.string.title_obteniendo));
            PD_CREATE.setMessage(getResources().getString(R.string.msj_espere_por_favor));
            PD_CREATE.setCancelable(false);
            PD_CREATE.setIndeterminate(true);
            PD_CREATE.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "ICS_HC_OffLine.apk";
                sdcard += fileName;
                final Uri uri = Uri.parse("file://" + sdcard);

                File myDir = new File(sdcard);
                myDir.mkdirs();
                if(myDir.exists()){
                    myDir.delete();
                }
                FileOutputStream fos = new FileOutputStream(myDir);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.flush();
                fos.close();
                is.close();

                //Llamando a la funcion que instala el APK
                ApkInstaller.installApplication(CONTEXT);

            } catch (FileNotFoundException fnfe) {
                status = 1;
                Log.e("File", "FileNotFoundException! " + fnfe);
            }

            catch(Exception e)
            {
                Log.e("UpdateAPP", "Exception " + e);
            }
            return null;
        }

        public void onPostExecute(Void unused) {
            //progressDialog.dismiss();
            PD_CREATE.dismiss();
            if(status == 1)
                Toast.makeText(CONTEXT,"Apk no disponible",Toast.LENGTH_LONG).show();
        }
    }
}
