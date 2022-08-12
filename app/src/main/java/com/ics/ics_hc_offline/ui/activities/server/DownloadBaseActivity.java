package com.ics.ics_hc_offline.ui.activities.server;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.listeners.DownloadListener;
import com.ics.ics_hc_offline.ui.task.DownloadBaseTask;
import com.ics.ics_hc_offline.utils.FileUtils;

public class DownloadBaseActivity extends Activity implements DownloadListener {
    protected static final String TAG = DownloadBaseActivity.class.getSimpleName();

    private String username;
    private String password;
    private String url;
    private SharedPreferences settings;
    private DownloadBaseTask downloadBaseTask;
    private final static int PROGRESS_DIALOG = 1;
    private ProgressDialog mProgressDialog;
    private static final int REQUESTCODE_STORAGE_PERMISSION = 23;

    // ***************************************
    // Metodos de la actividad
    // ***************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.app_name) + " > "
                + getString(R.string.download));
        /*Boolean readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!readPermission && !writePermission) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUESTCODE_STORAGE_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUESTCODE_STORAGE_PERMISSION);
        }*/
        if (!FileUtils.storageReady()) {
            //Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error),Toast.LENGTH_LONG);
            @SuppressLint("StringFormatMatches") Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error),Toast.LENGTH_LONG);
            toast.show();
            setResult(RESULT_CANCELED);
            finish();
        }

        settings =
                PreferenceManager.getDefaultSharedPreferences(this);

        /*url =
                settings.getString(PreferencesActivity.KEY_SERVER_URL, this.getString(R.string.default_server_url));
        username =
                settings.getString(PreferencesActivity.KEY_USERNAME,
                        null);

        password =
                ((MyIcsApplication) this.getApplication()).getPassApp();*/

        downloadBaseTask = (DownloadBaseTask) getLastNonConfigurationInstance();
        if (downloadBaseTask == null) {
            downloadAllData();
        }
    }

    private void downloadAllData(){
        if (downloadBaseTask != null)
            return;
        showDialog(PROGRESS_DIALOG);
        downloadBaseTask = new DownloadBaseTask(this.getApplicationContext());
        downloadBaseTask.setDownloadListener(DownloadBaseActivity.this);
        downloadBaseTask.execute();
    }

    @Override
    public void downloadComplete(String result) {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (result != null) {
            Intent intent = new Intent();
            intent.putExtra("resultado", result);
            setResult(RESULT_CANCELED, intent);
        } else {
            setResult(RESULT_OK);
        }

        downloadBaseTask = null;
        finish();
    }

    @Override
    public void progressUpdate(String message, int progress, int max) {

        mProgressDialog.setMax(max);
        mProgressDialog.setProgress(progress);
        mProgressDialog.setTitle(message);

    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return downloadBaseTask;
    }

    @Override
    protected void onDestroy() {
        if (downloadBaseTask != null) {
            downloadBaseTask.setDownloadListener(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (downloadBaseTask != null) {
            downloadBaseTask.setDownloadListener(this);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == PROGRESS_DIALOG) {
            mProgressDialog = createDownloadDialog();
            return mProgressDialog;
        }
        return null;
    }

    private ProgressDialog createDownloadDialog() {

        ProgressDialog dialog = new ProgressDialog(this);
        DialogInterface.OnClickListener loadingButtonListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downloadBaseTask.setDownloadListener(null);
                Intent intent = new Intent();
                intent.putExtra("resultado", getString(R.string.err_cancel));
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
        dialog.setTitle(getString(R.string.loading));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setButton(getString(R.string.cancel),
                loadingButtonListener);
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        if (id == PROGRESS_DIALOG) {
            ProgressDialog progress = (ProgressDialog) dialog;
            progress.setTitle(getString(R.string.loading));
            progress.setProgress(0);
        }
    }
}
