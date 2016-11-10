package com.petsapp.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class Privacy_Policy extends AppCompatActivity {

    WebView privacynpolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy__policy);
        privacynpolicy = (WebView) findViewById(R.id.privacynpolicy);
        privacynpolicy.getSettings().setJavaScriptEnabled(true);
        privacynpolicy.loadUrl("http://petsapp.petsappindia.co.in/policy.html");
    }
}
