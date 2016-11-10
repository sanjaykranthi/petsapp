package com.petsapp.android;

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

public class FollowUsActivity extends AppCompatActivity {

    ImageView navLeft;
    ImageButton findinfoBtn, newsinfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_us);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#029744"));
        }

        findinfoBtn = (ImageButton) findViewById(R.id.findinfoId);
        newsinfoBtn = (ImageButton) findViewById(R.id.newsinfoId);
        navLeft = (ImageView) findViewById(R.id.imageView1);
        navLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(FollowUsActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(FollowUsActivity.this);
                } else {
                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                    navLeft.startAnimation(animFadein);
                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        });

        newsinfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FindInfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }
}
