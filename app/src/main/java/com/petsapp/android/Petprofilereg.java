package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class Petprofilereg extends AppCompatActivity {
    final Context context = this;
    JSONArray arr = null;
    LoginSessionManager loginSessionManager;
    String idString;
    String petnames, petages, petgenders, petcats, petbreeds, petweights, petheights, petsizes, img;
    double lat = 0.0, lon = 0.0;
    TextView petname1, pname, pcat, pgender, pweight, pheight, psize, pbreed, page;
    Bitmap bitmap;
    ImageView profileImg, intentImg, delImg;
    String petn, petg, peta, petb, petw, pets, peth, petimg, petslat, petslon, petMateId, about, category, vacci, crossBreed;
    //private GoogleMap googleMap;
    private ProgressDialog dialog;
    private String urlParameters;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petprofilereg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Rpetlist.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        try {
            Bundle extras = getIntent().getExtras();
            petn = extras.getString("petname");
            petg = extras.getString("gender");
            peta = extras.getString("age");
            petb = extras.getString("breed");
            petw = extras.getString("weight");
            peth = extras.getString("height");
            pets = extras.getString("size");
            petimg = extras.getString("image");
            petslat = extras.getString("lat");
            petslon = extras.getString("lon");
            about = extras.getString("about");
            petMateId = extras.getString("petmateId");
            category = extras.getString("category");
            vacci = extras.getString("vacci");
            crossBreed = extras.getString("crossbreed");

            lat = Double.parseDouble(petslat);
            lon = Double.parseDouble(petslon);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        petname1 = (TextView) findViewById(R.id.user_name_id);
        pname = (TextView) findViewById(R.id.petname);
        //pcat = (TextView) findViewById(R.id.pettype);
        page = (TextView) findViewById(R.id.petage);
        pbreed = (TextView) findViewById(R.id.petbreed);
        pgender = (TextView) findViewById(R.id.petgender);
        pweight = (TextView) findViewById(R.id.petweight);
        pheight = (TextView) findViewById(R.id.petheight);
        psize = (TextView) findViewById(R.id.petsize);
        profileImg = (ImageView) findViewById(R.id.user_profile_img_id);
        intentImg = (ImageView) findViewById(R.id.intent_img_id);
        delImg = (ImageView) findViewById(R.id.del_img_id);
        petname1.setText(petn);
        pname.setText(petn);
        page.setText(peta);
        pgender.setText(petg);
        pbreed.setText(petb);
        pweight.setText(petw);
        pheight.setText(peth);
        psize.setText(pets);
        Picasso.with(getApplicationContext()).load(petimg).into(profileImg);
      /*  LoadImage task=new LoadImage();
        task.execute(petimg);*/

        intentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentPetTask1 intT = new IntentPetTask1();
                intT.execute();
            }
        });
        delImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DelAlertDialog(Petprofilereg.this, "Delete", "Confirm Removal?", false);
            }
        });

    }

    public void DelAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        //         alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //     if (Isinternetpresent) {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(Petprofilereg.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(Petprofilereg.this);
                } else {
                    DelTask task = new DelTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/deletepet_from_mate");
                }
                //    }else {
                //   Toast.makeText(UpdateUserProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                //    }

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
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_pat_id)).getMap();

            if (googleMap == null) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            } else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(petn);
                googleMap.addMarker(marker);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, lon)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private class IntentPetTask1 extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Petprofilereg.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... args) {
            Intent i = new Intent(getApplicationContext(), UpdatePetProfileActivity.class);


            i.putExtra("petn", petn);
            i.putExtra("petg", petg);
            i.putExtra("peta", peta);
            i.putExtra("petb", petb);
            i.putExtra("petw", petw);
            i.putExtra("peth", peth);
            i.putExtra("pets", pets);
            i.putExtra("petimg", petimg);
            //   i.putExtra("petb", petb);
            i.putExtra("petMateId", petMateId);
            i.putExtra("about", about);
            i.putExtra("category", category);
            i.putExtra("vacci", vacci);
            i.putExtra("crossbreed", crossBreed);


            startActivity(i);
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
        }
    }


    class VeryLongAsyncTask1 extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog progressDialog;

        public VeryLongAsyncTask1(Context ctx) {
            progressDialog = MyCustomProgressDialog.ctor(ctx);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
                /*Intent intent = new Intent(getApplicationContext(), AddAdoptionActivity.class);
                startActivity(intent);*//*
                finish();*/
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

    /****
     * Del Async Task
     ***/

    private class DelTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(Petprofilereg.this, "Deleting",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&pet_id=" + URLEncoder.encode(petMateId, "UTF-8") +
                        "&user_id=" + URLEncoder.encode(idString, "UTF-8");
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();


                System.out.println("url====" + url);

                //Get Response
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
            }

        }

        @Override
        protected void onPostExecute(String result) {

            try {

                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String state = obj.getString("status");
                    if (state.equalsIgnoreCase("Pet Removed from Mating")) {
                        Toast.makeText(getApplicationContext(), "Pet Removed", Toast.LENGTH_LONG).show();

                        Intent i1 = new Intent(Petprofilereg.this, Rpetlist.class);
                        i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(i1);
                        finish();

                    } else {
                        //     Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //    dialog.dismiss();
        }
    }

    /****Del Async Task***/


}
