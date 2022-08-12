package com.ics.ics_hc_offline.expediente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.ics_hc_offline.ui.activities.server.UploadAllActivity;

public class Upload extends Fragment {
    private static final int UPDATE_SERVER = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Intent i = new Intent(getContext().getApplicationContext(), UploadAllActivity.class);
        startActivityForResult(i, UPDATE_SERVER);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
