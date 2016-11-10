package com.petsapp.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;

public class LoginSignupActivity extends AppCompatActivity {

    private static final Integer[] IMAGES = {R.drawable.dogflipper1, R.drawable.dogflipper2, R.drawable.dogflipper3jpg};
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    private ImageButton loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#5b576e"));
        }
        try {
            DashboardActivity.fa.finish();
        } catch (Exception e) {
        }
        loginButton = (ImageButton) findViewById(R.id.login_button_id);
        signupButton = (ImageButton) findViewById(R.id.signup_button_id);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LoginSignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LoginSignupActivity.this, SignupActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
    }
}
