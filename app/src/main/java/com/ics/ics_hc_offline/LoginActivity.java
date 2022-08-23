package com.ics.ics_hc_offline;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.InfoSessionWSDTO;
import com.ics.ics_hc_offline.dto.NodoItemDTO;
import com.ics.ics_hc_offline.dto.PacienteDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.ics.ics_hc_offline.ui.activities.server.DownloadBaseActivity;
import com.ics.ics_hc_offline.utils.DesEncrypter;
import com.ics.ics_hc_offline.utils.TemplatePDF;
import com.ics.ics_hc_offline.wsclass.DataNodoItemArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {
    private Context CONTEXT;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private static final int UPDATE_EQUIPO = 11;
    private SharedPreferences settings;
    TemplatePDF templatePDF;
    private HojaConsultaDBAdapter mDbAdapter;
    private static List<HojaConsultaOffLineDTO> HOJACONSULTA = null;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            System.exit(0);
        }

        setContentView(R.layout.activity_login);
        this.CONTEXT = this;

        settings =
                PreferenceManager.getDefaultSharedPreferences(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1 || id == EditorInfo.IME_NULL) { //cambio R.id.login por el valor 1
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        ImageButton imgBtnDataBaseDownload = (ImageButton) findViewById(R.id.imgBtnDataBaseDownload);
        imgBtnDataBaseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                HOJACONSULTA = mDbAdapter.getListHojasConsultas(MainDBConstants.statusSubmitted  + "='" + "N" + "'", null);
                if (HOJACONSULTA.size() > 0) {
                    createDialog();
                } else {
                    Intent i;
                    i = new Intent(getApplicationContext(), DownloadBaseActivity.class);
                    startActivityForResult(i, UPDATE_EQUIPO);
                }

            }
        });

        ImageButton imgBtnDownloadAPK = (ImageButton) findViewById(R.id.imgBtnDownloadAPK);
        imgBtnDownloadAPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaDownloadAPK();
            }
        });

        /*Boton para descargar datos
        Button mDownload = (Button) findViewById(R.id.dowload_button);
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(getApplicationContext(), DownloadBaseActivity.class);
                startActivityForResult(i, UPDATE_EQUIPO);
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                HOJACONSULTA = mDbAdapter.getHojaConsulta(MainDBConstants.secHojaConsulta  + "='" + 68272 + "'", null);
                templatePDF = new TemplatePDF(CONTEXT.getApplicationContext());
                templatePDF.openDocument();
                templatePDF.generateHojaConsultaPdf(HOJACONSULTA);
                templatePDF.closeDocument();
                printPdf();
            }
        });*/

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        takePermissions();

    }

    public void printPdf() {
        try {
            File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
            if (arch.exists()) {
                Uri uri = FileProvider.getUriForFile(CONTEXT, CONTEXT.getApplicationContext().getPackageName() + ".provider", arch);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                //intent.setAction(StorageManager.ACTION_CLEAR_APP_CACHE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //error si se quita esto
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CONTEXT, "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ActivityNotFoundException e) {
            Log.e("appViewPdf", e.toString());
        }
        //templatePDF.appViewPdf();
    }

    public void takePermissions() {
        if (isPermissionGranted()) {
            Toast.makeText(this, "Permisos otorgados", Toast.LENGTH_SHORT).show();
        } else {
            takePermission();
        }
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            //Para android 11
            return Environment.isExternalStorageManager();
        } else {
            //Menor que android 11
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 100);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 100);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        Toast.makeText(this, "Permiso otorgados", Toast.LENGTH_SHORT).show();
                    } else {
                        takePermission();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 101) {
                boolean readExternalEstorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (readExternalEstorage) {
                    Toast.makeText(this, "Permisod otorgados", Toast.LENGTH_SHORT).show();
                } else {
                    takePermission();
                }
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            /*mAuthTask = new UserLoginTask(user, password, this);
            mAuthTask.execute((Void) null);*/
            ingresarLocal();
        }
    }

    /*Metodo para ingresar a la aplicacion de forma offline*/
    private void ingresarLocal() {
        //Presenta la actividad principal si valida localmente

        String user = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        DesEncrypter desEncrypter = new DesEncrypter(password.trim());
        String pwdEncyp = desEncrypter.encrypt(password);

        HojaConsultaDBAdapter mDbAdapter = new HojaConsultaDBAdapter(getApplicationContext(),false,false);
        try{
            mDbAdapter.open();
            InfoSessionWSDTO mInfoSession = new InfoSessionWSDTO();

            UsuarioDTO authUsuario = mDbAdapter.getUsuario(MainDBConstants.usuario  + "='" + user + "'", null);
            UsuarioDTO authPwd = mDbAdapter.getUsuario(MainDBConstants.pass  + "='" + pwdEncyp.trim() + "'", null);

            if (authUsuario == null) {
                showProgress(false);
                mEmailView
                        .setError(getString(R.string.error_invalid_user));
                mEmailView.requestFocus();
            } else if (authPwd == null) {
                showProgress(false);
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else if (authUsuario != null && authPwd !=  null) {
                //crearMenu();
                InfoSessionWSDTO infoSession = new InfoSessionWSDTO();
                infoSession.setUserId(authUsuario.getId());
                infoSession.setUser(authUsuario.getUsuario());
                infoSession.setNameUser(authUsuario.getNombre());
                ((CssfvApp)getApplication()).setInfoSessionWSDTO(infoSession);
                Intent intentPantallaInicio = new Intent(CONTEXT,
                        MainActivity.class);
                startActivity(intentPantallaInicio);
                finish();
            } else {

            }
        }
        catch(SQLException e){
            showProgress(false);
            mPasswordView
                    .setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
        catch(Exception e){
            showProgress(false);
            mPasswordView
                    .setError(e.getMessage());
            mPasswordView.requestFocus();
        }
        finally{
            mDbAdapter.close();
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.nav_header_title));
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(this.getString(R.string.wipe_db_confirm));
        builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Finish app
                Intent i;
                i = new Intent(getApplicationContext(), DownloadBaseActivity.class);
                startActivityForResult(i, UPDATE_EQUIPO);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertaDownloadAPK() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.title_estudio_sostenible));
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setMessage("Inciar a descargar la app...");
        dialog.setPositiveButton(this.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        descargarYActualizar();
                    }
                });
        dialog.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void descargarYActualizar() {
        Intent intent = new Intent(CONTEXT, DescargarApkActivity.class);
        startActivity(intent);
        finish();
    }
}
