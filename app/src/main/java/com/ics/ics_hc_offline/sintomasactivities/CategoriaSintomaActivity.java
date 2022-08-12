package com.ics.ics_hc_offline.sintomasactivities;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.ics.ics_hc_offline.R;
import com.ics.ics_hc_offline.sintomasfragment.CategoriaSintomaFragment;

public class CategoriaSintomaActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            CategoriaSintomaFragment fragment = new CategoriaSintomaFragment();
            String secHojaConsulta = getIntent().getStringExtra("secHojaConsulta");
            String fechaConsulta = getIntent().getStringExtra("fechaConsulta");
            String fechaNac = getIntent().getStringExtra("fechaNac");
            args.putString("secHojaConsulta", secHojaConsulta);
            args.putString("fechaConsulta", fechaConsulta);
            args.putString("fechaNac", fechaNac);
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
