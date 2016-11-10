package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

public class AdoptionActivity extends AppCompatActivity {

    final Context context = this;
    ImageView nav, homeImage;
    String address;
    String storedLat, storedLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2b9a99"));
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
                            .showInterntOnlySettingsAlert(AdoptionActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AdoptionActivity.this);
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

        final ImageButton b1 = (ImageButton) findViewById(R.id.placeAdoptionId);
        final ImageButton b2 = (ImageButton) findViewById(R.id.adoptionId);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                b1.startAnimation(animFadein);

/*
                VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
                pars88.execute();*/
                Intent in = new Intent(getApplicationContext(), AddAdoptionActivity.class);
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
                            .showInterntOnlySettingsAlert(AdoptionActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AdoptionActivity.this);
                } else {
                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                    b2.startAnimation(animFadein);
                    Intent in = new Intent(getApplicationContext(), FindAdoptActivity.class);
                    //    in.putExtra("storedLat", storedLat);
                    //    in.putExtra("storedLon", storedLon);
                    startActivity(in);
                    finish();
                }
            }
        });
    }


    class VeryLongAsyncTask1 extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog progressDialog;

        public VeryLongAsyncTask1(Context ctx) {
            progressDialog = MyCustomProgressDialog.ctor(ctx);
            // progressDialog.setMessage("loding........");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {// sleep for 5 seconds
                Thread.sleep(3000);
                Intent intent = new Intent(getApplicationContext(), AddAdoptionActivity.class);
                startActivity(intent);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }
}
