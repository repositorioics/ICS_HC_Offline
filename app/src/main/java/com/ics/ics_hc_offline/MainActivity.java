package com.ics.ics_hc_offline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.ics.ics_hc_offline.databinding.ActivityMainBinding;
import com.ics.ics_hc_offline.enfermeria.EmergenciaCargaHojaConsultaActivity;
import com.ics.ics_hc_offline.helper.MensajesHelper;
import com.ics.ics_hc_offline.ui.activities.server.UploadAllActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int UPDATE_SERVER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
         setSupportActionBar(binding.appBarMain.toolbar);

         DrawerLayout drawer = binding.drawerLayout;
         NavigationView navigationView = binding.navView;

         mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_home, R.id.nav_enfermeria, R.id.nav_consulta, R.id.nav_expediente)
                 .setOpenableLayout(drawer)
                 .build();
         NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
         NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
         NavigationUI.setupWithNavController(navigationView, navController);
         View headerView = navigationView.getHeaderView(0);
         TextView navUsername = (TextView) headerView.findViewById(R.id.userName);
         navUsername.setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            String title = menuItem.getTitle().toString();
            if (title.equals("Agregar")) {
                SpannableString spannable = new SpannableString(
                        title
                );
                spannable.setSpan(new ForegroundColorSpan(Color.WHITE),
                        0, spannable.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                menuItem.setTitle(spannable);
            } else {
                SpannableString spannable = new SpannableString(
                        menu.getItem(i).getTitle().toString()
                );
                spannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                        0, spannable.length(), 0);
                menuItem.setTitle(spannable);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_add:
                agregarConsulta();
                return true;
            case R.id.action_upload:
                subirDatos();
                return true;
            case R.id.action_logOut:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        DialogInterface.OnClickListener preguntaCancelarDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Launching the login activity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,
                getResources().getString(
                        R.string.msj_esta_seguro_de_salir), getResources().getString(
                        R.string.title_estudio_sostenible), preguntaCancelarDialogClickListener);
    }

    private void agregarConsulta() {
        //llamar a la pantalla de ingreso de paciente

        Fragment fragment = new EmergenciaCargaHojaConsultaActivity();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .commit();
    }

    private void subirDatos() {
        DialogInterface.OnClickListener preguntaCancelarDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Launching the login activity
                        Intent i = new Intent(getApplicationContext(), UploadAllActivity.class);
                        startActivityForResult(i, UPDATE_SERVER);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,
                getResources().getString(
                        R.string.msj_esta_seguro_de_subir_datos), getResources().getString(
                        R.string.title_estudio_sostenible), preguntaCancelarDialogClickListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}