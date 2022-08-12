package com.ics.ics_hc_offline.sintomasactivities;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.sintomasfragment.EstadoNutriSintomasFragment;

public class EstadoNutriSintomasAcitvity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            EstadoNutriSintomasFragment fragment = new EstadoNutriSintomasFragment();
            String secHojaConsulta = getIntent().getStringExtra("secHojaConsulta");
            String fechaConsulta = getIntent().getStringExtra("fechaConsulta");
            String peso = getIntent().getStringExtra("pesoKg");
            String talla = getIntent().getStringExtra("tallaCm");
            args.putString("secHojaConsulta", secHojaConsulta);
            args.putString("fechaConsulta", fechaConsulta);
            args.putString("pesoKg", peso);
            args.putString("tallaCm", talla);
            fragment.setArguments(args);
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}