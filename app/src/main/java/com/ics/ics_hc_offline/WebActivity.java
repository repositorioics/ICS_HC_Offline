package com.ics.ics_hc_offline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView webView = findViewById(R.id.webPdf);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("");
    }
}