package com.petsapp.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class LostFoundPetProfile extends AppCompatActivity {

    JSONArray arr = null;
    LoginSessionManager loginSessionManager;
    String idString;
    String petnames, petages, petgenders, petcats, petbreeds, petweights, petheights, petsizes, img;
    double lat = 0.0, lon = 0.0;
    TextView petname1, pname, pcat, pgender, pdate, pheight, psize, pbreed, page;
    Bitmap bitmap;
    ImageView profileImg, intrest, intrestTickImg;
    String petn = "", petg = "", peta = "", petb = "", petd = "", pets = "", peth = "", petimg, petlat, petlon, ownerno;
    FloatingActionButton call;
    Button b1, b2;
    TextView dialogText;
    ConnectionDetector cd;
    private GoogleMap googleMap;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_pet_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), FindLostFoundActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#dd8332"));
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());

        HashMap<String, String> user = loginSessionManager.getUserDetails();
        Bundle extras = getIntent().getExtras();
        petn = extras.getString("petname");
        petg = extras.getString("gender");
        peta = extras.getString("age");
        petb = extras.getString("breed");
        peth = extras.getString("lnfStatus");
        petd = extras.getString("date");
        //  pets=extras.getString("size");
        petimg = extras.getString("image");
        petlat = extras.getString("lat");
        petlon = extras.getString("lon");
        ownerno = extras.getString("phone");
        lat = Double.parseDouble(petlat);
        lon = Double.parseDouble(petlon);


        idString = user.get(LoginSessionManager.KEY_ID);
        petname1 = (TextView) findViewById(R.id.user_name_id);
        pname = (TextView) findViewById(R.id.petname);
        page = (TextView) findViewById(R.id.petage);
        pbreed = (TextView) findViewById(R.id.petbreed);
        pgender = (TextView) findViewById(R.id.petgender);
        pdate = (TextView) findViewById(R.id.petDate_idt);
        pheight = (TextView) findViewById(R.id.petStatus);
        //  psize = (TextView) findViewById(R.id.petsize);
        profileImg = (ImageView) findViewById(R.id.user_profile_img_id);
        petname1.setText(petn);
        pname.setText(petn);
        page.setText(peta);
        pgender.setText(petg);
        pbreed.setText(petb);
        pdate.setText(petd);
        pheight.setText(peth);
        //    psize.setText(pets);
        Picasso.with(getApplicationContext()).load(petimg).into(profileImg);
        intrest = (ImageView) findViewById(R.id.call);
        intrestTickImg = (ImageView) findViewById(R.id.calltick);
        intrest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LostFoundPetProfile.this);
                dialog.setContentView(R.layout.dialog);

                dialog.getWindow().setTitleColor(getResources().getColor(R.color.mate_interest));
                dialog.setTitle("Message for pet");
                dialog.setCancelable(false);

                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                dialogText = (TextView) dialog.findViewById(R.id.textView1);
                dialogText.setText("Are you sure want to send a message?");

                b1 = (Button) dialog.findViewById(R.id.button1);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (Isinternetpresent) {

                                if (peth.equalsIgnoreCase("Missing")) {

                                    interstTask task = new interstTask();
                                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/sendlostsms");
                                } else {
                                    interstTask task = new interstTask();
                                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/sendfoundsms");
                                }
                            } else {
                                showAlertDialog(LostFoundPetProfile.this, "No Internet Connection", "You don't have internet connection.", false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //  Toast.makeText(getApplicationContext(), "your interest has been successfully send to pet owner", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });
                b2 = (Button) dialog.findViewById(R.id.button2);
                dialogText = (TextView) dialog.findViewById(R.id.textView1);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


                dialog.show();
            }
        });
        try {
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        //         alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_dashboard) {

            Intent i1 = new Intent(getApplicationContext(), DashboardActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_pat1_id)).getMap();

            if (googleMap == null) {
                //   Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            } else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(petn);
                googleMap.addMarker(marker);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, lon)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  initilizeMap();
    }

    private class interstTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LostFoundPetProfile.this, "Sending request",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&phone=" + URLEncoder.encode(ownerno, "UTF-8") + "&pid=" + URLEncoder.encode(idString, "UTF-8");
                System.out.println("own---" + ownerno);
                System.out.println("pid...." + idString);
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                dialog.dismiss();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            intrestTickImg.setVisibility(View.VISIBLE);
            intrest.setVisibility(View.GONE);
        }
    }
}
