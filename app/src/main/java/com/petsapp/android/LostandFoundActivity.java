package com.petsapp.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LostandFoundActivity extends AppCompatActivity {

    final Context context = this;
    ImageView nav;
    String storedLat, storedLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostand_found);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#dd8332"));
        }
       /* Bundle extras = getIntent().getExtras();
        storedLat = extras.getString("storedLat");
        storedLon = extras.getString("storedLon");*/
        nav = (ImageView) findViewById(R.id.imageView1);
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(LostandFoundActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(LostandFoundActivity.this);
                } else {
                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                    nav.startAnimation(animFadein);
                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        });
        final ImageButton b1 = (ImageButton) findViewById(R.id.lostId);
        final ImageButton b2 = (ImageButton) findViewById(R.id.foundId);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                b1.startAnimation(animFadein);

                Intent in = new Intent(getApplicationContext(), AddLostFoundActivity.class);
                startActivity(in);
                finish();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(LostandFoundActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(LostandFoundActivity.this);
                } else {

                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                    b2.startAnimation(animFadein);

                    Intent in = new Intent(getApplicationContext(), FindLostFoundActivity.class);
                    /*in.putExtra("storedLat", storedLat);
                    in.putExtra("storedLon", storedLon);*/
                    startActivity(in);
                    finish();
                }
            }
        });
    }
}


