package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.Model.BreedModel;
import com.petsapp.android.Model.Data;
import com.petsapp.android.adapter.BreedAdapter;
import com.petsapp.android.adapter.RecyclerViewAdapter;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FindLostFoundActivity extends AppCompatActivity {

    final Context context = this;
    RecyclerViewAdapter adapter;
    List<Data> list = new ArrayList<>();
    Spinner breed, age, gender, distance, lostfoundsp, locationSpinner;
    ImageView dogImg, catImg, others, birdImg;
    RecyclerView recycle;
    TextView recyText;
    String latString = "0.0", lonString = "0.0";
    Double lat = 0.0, lon = 0.0;
    int _LstVar = 0;
    JSONArray arr = null;
    String category = "dog";
    String cat, dog;
    String breedstr = "";
    String agestr = "";
    String genderstr = "";
    String diststr = "0";
    String lnfstr = "";
    int breedpos, agepos, genderpos, distpos, lnfPos;
    String idString, storedLat, storedLon;
    String cate = "";
    ConnectionDetector cd;
    String categ, lnfStatus, pName, agestr1, breedstr1, dists;
    int distsInt;
    String lost = "Missing";
    String found = "Spotted";
    String col = "#5b576e";
    GPSTracker gps;
    double latitude, longitude;
    int typos;
    //TextView nodatalostfound;
    RecyclerView rview;
    LoginSessionManager loginSessionManager;
    String[] gndrstrArray = {"Gender", "Male", "Female"};
    String[] misSpotstrArray = {"Missing/spotted", "Missing", "Spotted"};
    AutoCompleteTextView breedAuto;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_lost_found);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);

        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), LostandFoundActivity.class);
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

        /*Bundle extras = getIntent().getExtras();
        storedLat = extras.getString("storedLat");
        storedLon = extras.getString("storedLon");*/

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        LatLonTask task1 = new LatLonTask();
        task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlatlon");

        locationSpinner = (Spinner) findViewById(R.id.location_spn_id);

        typos = locationSpinner.getSelectedItemPosition();
        breedAuto = (AutoCompleteTextView) findViewById(R.id.breed_auto_Id);
        breed = (Spinner) findViewById(R.id.breedsp);
        gender = (Spinner) findViewById(R.id.gendersp);
        age = (Spinner) findViewById(R.id.agesp);
        distance = (Spinner) findViewById(R.id.distancesp);
        lostfoundsp = (Spinner) findViewById(R.id.lostfoundsp);
        final String spinVal = String.valueOf(breed.getSelectedItem());
        dogImg = (ImageView) findViewById(R.id.dog);
        catImg = (ImageView) findViewById(R.id.cat);
        others = (ImageView) findViewById(R.id.others);
        birdImg = (ImageView) findViewById(R.id.bird);
        dogImg.setImageResource(R.drawable.lostdog1);
        catImg.setImageResource(R.drawable.lostcat);
        birdImg.setImageResource(R.drawable.bird);
        others.setImageResource(R.drawable.lostother);
//        nodatalostfound = (TextView) findViewById(R.id.nodatalostfound);
//        nodatalostfound.setVisibility(View.INVISIBLE);
        rview = (RecyclerView) findViewById(R.id.recycle);
        //      **************
        dogImg.setImageResource(R.drawable.lostdog1);

        gender = (Spinner) findViewById(R.id.gendersp);
        lostfoundsp = (Spinner) findViewById(R.id.lostfoundsp);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FindLostFoundActivity.this, R.layout.spinner_row, R.id.spinner_text, misSpotstrArray);
        adapter1.setDropDownViewResource(R.layout.spinner_row1);
        lostfoundsp.setAdapter(adapter1);


        ArrayAdapter<String> adapterG = new ArrayAdapter<String>(FindLostFoundActivity.this, R.layout.spinner_row, R.id.spinner_text, gndrstrArray);
        adapterG.setDropDownViewResource(R.layout.spinner_row1);
        gender.setAdapter(adapterG);

       /* dogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();

                if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                }
                else if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                }
                else{
                    category = "dog";
                    BreedTask task = new BreedTask();
                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");

                    setimage(0);
                    _LstVar = 1;
                }


            }

        });

        catImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                }
                else if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                }
                else{
                    category = "cat";
                    BreedTask task = new BreedTask();
                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");
                    setimage(1);
                    _LstVar = 2;
                }


            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                }
                else if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                }
                else{
                    category = "others";
                    BreedTask task = new BreedTask();
                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");

                    setimage(2);
                    _LstVar = 2;
                }


            }
        });*/

        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(FindLostFoundActivity.this);
        } else {
            BreedTask task = new BreedTask();
            task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

          /*  DetailsTask task1 = new DetailsTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
            categ = "dog";
            latString = storedLat;
            lonString = storedLon;
            MateAddTask task2 = new MateAddTask();
            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");


          /*  gps = new GPSTracker(getApplicationContext());
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();*/
        }
    }

    public void setimage(int i) {
        if (i == 0) {
            dogImg.setImageResource(0);
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(R.drawable.lostdog1);
            catImg.setImageResource(R.drawable.lostcat);
            birdImg.setImageResource(R.drawable.bird);
            others.setImageResource(R.drawable.lostother);
        } else if (i == 1) {
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(0);
            dogImg.setImageResource(R.drawable.lostdog);
            catImg.setImageResource(R.drawable.lostcat1);
            birdImg.setImageResource(R.drawable.bird);
            others.setImageResource(R.drawable.lostother);
        } else if (i == 2) {
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(0);
            dogImg.setImageResource(R.drawable.lostdog);
            catImg.setImageResource(R.drawable.lostcat);
            birdImg.setImageResource(R.drawable.bird1);
            others.setImageResource(R.drawable.lostother);
        } else {
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(0);
            others.setImageResource(R.drawable.lostother1);
            dogImg.setImageResource(R.drawable.lostdog);
            birdImg.setImageResource(R.drawable.bird);
            catImg.setImageResource(R.drawable.lostcat);
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

    /*Adaptr close*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(FindLostFoundActivity.this, LostandFoundActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public class BreedTask1 extends AsyncTask<String, String, List<BreedModel>> {

        @Override
        protected List<BreedModel> doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&category=" + URLEncoder.encode(category, "UTF-8");

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
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();
                System.out.println("final json---" + finalJson);
                List<BreedModel> breedModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);
                JSONObject jObj = arr.getJSONObject(0);
                System.out.println("final json---" + jObj);
                JSONArray childArray = jObj.getJSONArray("breed");
                for (int i = 0; i < childArray.length(); i++) {
                    JSONObject obj = childArray.getJSONObject(i);
                    BreedModel model = new BreedModel();
                    String nameString, breedString, genderString, ageString;
                    model.setName(obj.getString("name"));
                    breedModelList.add(model);
                }

                return breedModelList;
//
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BreedModel> result) {
            super.onPostExecute(result);

            breed = (Spinner) findViewById(R.id.breedsp);
            BreedAdapter adapter = new BreedAdapter(getApplicationContext(), R.layout.spinner_row, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breed.setAdapter(adapter);
        }
    }

    public class BreedTask extends AsyncTask<String, String, List<BreedModel>> {

        @Override
        protected List<BreedModel> doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&category=" + URLEncoder.encode(category, "UTF-8");

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
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();
                System.out.println("final json---" + finalJson);
                List<BreedModel> breedModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);
                JSONObject jObj = arr.getJSONObject(0);
                System.out.println("final json---" + jObj);
                JSONArray childArray = jObj.getJSONArray("breed");
                for (int i = 0; i < childArray.length(); i++) {
                    JSONObject obj = childArray.getJSONObject(i);
                    BreedModel model = new BreedModel();
                    String nameString, breedString, genderString, ageString;
                    model.setName(obj.getString("name"));
                    breedModelList.add(model);
                }

                return breedModelList;
//
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BreedModel> result) {
            super.onPostExecute(result);

           /* breed = (Spinner) findViewById(R.id.breedsp);
            BreedAdapter adapter = new BreedAdapter(getApplicationContext(), R.layout.spinner_row, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breed.setAdapter(adapter);*/

            ArrayAdapter adp = new ArrayAdapter(getApplicationContext(), R.layout.breed_auto_text, result);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedAuto.setThreshold(1);
            breedAuto.setAdapter(adp);

            locationSpinner = (Spinner) findViewById(R.id.location_spn_id);

            typos = locationSpinner.getSelectedItemPosition();

            locationSpinner.setSelection(1);
            //    typos = locationSpinner.getSelectedItemPosition();
            //

            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    typos = locationSpinner.getSelectedItemPosition();

                    if (typos == 0) {
                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(FindLostFoundActivity.this);
                        } else {

                            MateAddTask task2 = new MateAddTask();
                            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");    // getlostnfounddetails
                        }


                        breedAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    breedstr = breedAuto.getText().toString();
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                }

                            }
                        });


                        /*breed.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        breedpos = breed.getSelectedItemPosition();
                                        breedstr = breed.getItemAtPosition(position).toString();
                                        if (breedpos == 0) {
                                            breedstr = "";

                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                            // agestr = "";
                                            //  genderstr = "";
                                            //  lnfstr = "I lost my pet";
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }

                                           *//* if (Isinternetpresent) {
                                                lnfstr = "I lost my pet";
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            } else {
                                                Toast.makeText(FindLostFoundActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*//*
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );*/

                        gender = (Spinner) findViewById(R.id.gendersp);
                        gender.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        genderpos = gender.getSelectedItemPosition();
                                        genderstr = gender.getItemAtPosition(position).toString();
                                        if (genderpos == 0) {
                                            //   breedstr = "";

                                            //  agestr = "";
                                            genderstr = "";
                                            //    lnfstr = "I lost my pet";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                           /* if(Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }else{
                                                Toast.makeText(FindLostFoundActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        lostfoundsp = (Spinner) findViewById(R.id.lostfoundsp);
                        lostfoundsp.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        lnfPos = lostfoundsp.getSelectedItemPosition();
                                        //  lnfstr = lostfoundsp.getItemAtPosition(position).toString();
                                        if (lnfPos == 0) {
                                            //    breedstr = "";
                                            lnfstr = ""; //chnge
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }

                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                if (lnfPos == 1) {
                                                    lnfstr = "I lost my pet";
                                                } else if (lnfPos == 2) {
                                                    lnfstr = "I found a lost pet";
                                                }
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                           /* if(Isinternetpresent) {

                                                if (lnfPos == 1) {
                                                    lnfstr = "I lost my pet";
                                                }else if (lnfPos == 2){
                                                    lnfstr = "I found a lost pet";
                                                }
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }else{
                                                Toast.makeText(FindLostFoundActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                    } else if (typos == 1) {
                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(FindLostFoundActivity.this);
                        } else {
                            latString = storedLat;
                            lonString = storedLon;
                            MateAddTask task2 = new MateAddTask();
                            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                        }

                        breedAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    breedstr = breedAuto.getText().toString();
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                }

                            }
                        });

                       /* breed.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        breedpos = breed.getSelectedItemPosition();
                                        breedstr = breed.getItemAtPosition(position).toString();
                                        if (breedpos == 0) {
                                            breedstr = "";

                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                            // agestr = "";
                                            //  genderstr = "";
                                            //  lnfstr = "I lost my pet";
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }

                                           *//* if (Isinternetpresent) {
                                                lnfstr = "I lost my pet";
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            } else {
                                                Toast.makeText(FindLostFoundActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*//*
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );*/

                        //     gender = (Spinner) findViewById(R.id.gendersp);
                        gender.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        genderpos = gender.getSelectedItemPosition();
                                        genderstr = gender.getItemAtPosition(position).toString();
                                        if (genderpos == 0) {
                                            //   breedstr = "";

                                            //  agestr = "";
                                            genderstr = "";
                                            //    lnfstr = "I lost my pet";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                           /* if(Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }else{
                                                Toast.makeText(FindLostFoundActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        //  lostfoundsp = (Spinner) findViewById(R.id.lostfoundsp);
                        lostfoundsp.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        lnfPos = lostfoundsp.getSelectedItemPosition();
                                        //  lnfstr = lostfoundsp.getItemAtPosition(position).toString();
                                        if (lnfPos == 0) {
                                            //    breedstr = "";
                                            lnfstr = ""; //chnge
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }

                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                if (lnfPos == 1) {
                                                    lnfstr = "I lost my pet";
                                                } else if (lnfPos == 2) {
                                                    lnfstr = "I found a lost pet";
                                                }
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }
                                           /* if(Isinternetpresent) {

                                                if (lnfPos == 1) {
                                                    lnfstr = "I lost my pet";
                                                }else if (lnfPos == 2){
                                                    lnfstr = "I found a lost pet";
                                                }
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                                            }else{
                                                Toast.makeText(FindLostFoundActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        //

                        dogImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "dog";
                                    categ = "dog";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                                   /* DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");

                                    setimage(0);
                                    _LstVar = 1;
                                }


                            }

                        });

                        catImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "cat";
                                    categ = "cat";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                    /*DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");

                                    setimage(1);
                                    _LstVar = 2;
                                }


                            }
                        });

                        others.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "others";
                                    categ = "others";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                   /* DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");

                                    setimage(3);
                                    _LstVar = 2;
                                }
                            }
                        });

                        birdImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "bird";
                                    categ = "bird";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");

                                    setimage(2);
                                    _LstVar = 1;
                                }


                            }

                        });

                    } else if (typos == 2) {
                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(FindLostFoundActivity.this);
                        } else {

                            gps = new GPSTracker(FindLostFoundActivity.this);
                            if (gps.canGetLocation()) {
                                if (gps != null) {
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                }
                            } else {
                                gps.showSettingsAlert();
                            }

                            latString = String.valueOf(latitude).trim();
                            lonString = String.valueOf(longitude).trim();
                            MateAddTask task2 = new MateAddTask();
                            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                        }

                        breedAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    breedstr = breedAuto.getText().toString();
                                    latString = String.valueOf(latitude);
                                    lonString = String.valueOf(longitude);
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                }
                            }
                        });

                        /*breed.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        breedpos = breed.getSelectedItemPosition();
                                        breedstr = breed.getItemAtPosition(position).toString();
                                        if (breedpos == 0) {
                                            breedstr = "";

                                            // agestr = "";
                                            //   genderstr = "";
                                            //   lnfstr = "I lost my pet";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                //   lnfstr = "I lost my pet";
                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            }
                                            else{
                                                //   lnfstr = "I lost my pet";
                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }
                                           *//* if (Isinternetpresent) {
                                                lnfstr = "I lost my pet";
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            } else {
                                                Toast.makeText(FindLostFoundActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*//*
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );*/

                        gender = (Spinner) findViewById(R.id.gendersp);
                        gender.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        genderpos = gender.getSelectedItemPosition();
                                        genderstr = gender.getItemAtPosition(position).toString();
                                        if (genderpos == 0) {
                                            //   breedstr = "";

                                            //  agestr = "";
                                            genderstr = "";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }
                                            //   lnfstr = "I lost my pet";
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        lostfoundsp = (Spinner) findViewById(R.id.lostfoundsp);
                        lostfoundsp.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        lnfPos = lostfoundsp.getSelectedItemPosition();
                                        //  lnfstr = lostfoundsp.getItemAtPosition(position).toString();
                                        if (lnfPos == 0) {
                                            //   breedstr = "";
                                            lnfstr = ""; //chnge

                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {

                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }

                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                                            } else {
                                                if (lnfPos == 1) {
                                                    lnfstr = "I lost my pet";
                                                } else if (lnfPos == 2) {
                                                    lnfstr = "I found a lost pet";
                                                }

                                                latString = String.valueOf(latitude).trim();
                                                lonString = String.valueOf(longitude).trim();

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        dogImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "dog";
                                    categ = "dog";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                                   /* DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = String.valueOf(latitude).trim();
                                    lonString = String.valueOf(longitude).trim();

                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");

                                    setimage(0);
                                    _LstVar = 1;
                                }


                            }

                        });

                        catImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "cat";
                                    categ = "cat";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                    /*DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = String.valueOf(latitude).trim();
                                    lonString = String.valueOf(longitude).trim();

                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");

                                    setimage(1);
                                    _LstVar = 2;
                                }


                            }
                        });

                        others.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "others";
                                    categ = "others";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                   /* DetailsTask task1 = new DetailsTask();
                                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");*/
                                    latString = String.valueOf(latitude).trim();
                                    lonString = String.valueOf(longitude).trim();

                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");

                                    setimage(3);
                                    _LstVar = 2;
                                }


                            }
                        });

                        birdImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(FindLostFoundActivity.this);
                                } else {
                                    category = "bird";
                                    categ = "bird";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                                    latString = String.valueOf(latitude).trim();
                                    lonString = String.valueOf(longitude).trim();

                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");

                                    setimage(2);
                                    _LstVar = 1;
                                }
                            }

                        });
                    }

                   /* dogImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                            }
                            else{
                                category = "dog";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");
                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");

                                setimage(0);
                                _LstVar = 1;
                            }


                        }

                    });

                    catImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                            }
                            else{
                                category = "cat";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");
                                setimage(1);
                                _LstVar = 2;
                            }


                        }
                    });

                    others.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showInterntOnlySettingsAlert(FindLostFoundActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(FindLostFoundActivity.this);
                            }
                            else{
                                category = "others";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getlostfoundBreed");

                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");

                                setimage(2);
                                _LstVar = 2;
                            }


                        }
                    });*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    private class LatLonTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(FindLostFoundActivity.this, "Loading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8");

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
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                arr = new JSONArray(result);
                System.out.println(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    storedLat = obj.getString("lat");
                    storedLon = obj.getString("lon");

                    //   lat = Double.parseDouble(latString);
                    //   lon = Double.parseDouble(lonString);

                    //     Toast.makeText(getApplicationContext(), storedLat+" "+storedLon, Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DetailsTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            /*dialog = ProgressDialog.show(FindLostFoundActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/
        }

        @Override
        protected List<Data> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&categ=" + URLEncoder.encode(category, "UTF-8");

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
                System.out.println("url List====" + url);
//
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();
                List<Data> dataModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Data model = new Data();
                    model.setId(obj.getString("id"));
                    model.setName(obj.getString("petname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setcateg(obj.getString("category"));
                    model.setColor(obj.getString("color"));
                    model.setStreet(obj.getString("street"));
                    model.setArea(obj.getString("area"));
                    model.setAbout(obj.getString("about"));
                    model.setLostorfound(obj.getString("lostorfound"));
                    model.setThumbnailUrl(obj.getString("image"));
                    model.setPhoto(obj.getString("image"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setphone(obj.getString("phone"));
                    model.setDate(obj.getString("date"));

                    categ = obj.getString("category");
                    latString = obj.getString("lat");
                    lonString = obj.getString("lon");

                    lnfStatus = obj.getString("lostorfound");
                    pName = obj.getString("petname");
                    agestr1 = obj.getString("age");
                    breedstr1 = obj.getString("breed");

                    if (lnfStatus.equalsIgnoreCase("I lost my pet")) {
                        model.setLostorfound(lost);
                    } else {
                        model.setLostorfound(found);
                    }

                    /*pet Name*/

                    if (pName.equalsIgnoreCase("")) {
                        model.setName("-");

                    } else {
                        model.setName(pName);
                    }
                    /*age*/
                    if (agestr1.equalsIgnoreCase("")) {
                        model.setAge("");

                    } else {
                        model.setAge(agestr1 + " yrs,");
                    }

                    if (breedstr1.equalsIgnoreCase("")) {
                        model.setBreed("-");
                    } else {
                        model.setBreed(breedstr1);
                    }

                    dataModelList.add(model);
                }

                return dataModelList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Data> detailsModels) {
            super.onPostExecute(detailsModels);

            if (detailsModels != null && detailsModels.size() > 0) {

//                nodatalostfound.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


                //  CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setHasFixedSize(true);
                FindPetAdapter rAdapter = new FindPetAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                // dialog.dismiss();
            } else {
//                nodatalostfound.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
            }
            //   dialog.dismiss();
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

    public class MateAddTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* dialog = ProgressDialog.show(FindLostFoundActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected List<Data> doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

//Changed*****
                urlParameters = "&id=" + URLEncoder.encode(idString, "UTF-8") +
                        "&category=" + URLEncoder.encode(categ, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedstr, "UTF-8") +
                        "&gender=" + URLEncoder.encode(genderstr, "UTF-8") +
                        "&lat=" + URLEncoder.encode(latString, "UTF-8") +
                        "&lon=" + URLEncoder.encode(lonString, "UTF-8") +
                        "&lostorfound=" + URLEncoder.encode(lnfstr, "UTF-8");

                // "&age=" + URLEncoder.encode(agestr, "UTF-8") +
                //"&dist=" + URLEncoder.encode(diststr, "UTF-8") +

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
                System.out.println("url List====" + url);
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                String finalJson = buffer.toString();
                List<Data> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Data model = new Data();
                    model.setName(obj.getString("petname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setDate(obj.getString("date"));
                    model.setLostorfound(obj.getString("lostorfound"));
                    model.setcateg(obj.getString("category"));
                    model.setphone(obj.getString("phone"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setDist(obj.getString("distance"));
                    model.setPager(obj.getString("pager"));
                    //    model.setDate(obj.getString("lfdate"));

                    lnfStatus = obj.getString("lostorfound");
                    pName = obj.getString("petname");
                    agestr1 = obj.getString("age");
                    breedstr1 = obj.getString("breed");

                    if (lnfStatus.equalsIgnoreCase("I lost my pet")) {
                        model.setLostorfound(lost);
                        model.setArea(obj.getString("missing_area"));

                    } else {
                        model.setLostorfound(found);
                        model.setArea(obj.getString("spotted_area"));
                    }

                    /*pet Name*/
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("-");
                        //     window.setStatusBarColor(Color.parseColor("#5b576e"));

                    } else {
                        model.setName(pName);
                    }
                     /*age*/
                    if (agestr1.equalsIgnoreCase("")) {
                        model.setAge("");

                    } else {
                        model.setAge(agestr1 + " yrs,");
                    }

                    if (breedstr1.equalsIgnoreCase("")) {
                        model.setBreed("-");
                        //     window.setStatusBarColor(Color.parseColor("#5b576e"));

                    } else {
                        model.setBreed(breedstr1);
                    }

                    itemModelList.add(model);
                }
                return itemModelList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Data> detailsModels) {
            super.onPostExecute(detailsModels);
            if (detailsModels != null && detailsModels.size() > 0) {
//                nodatalostfound.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setHasFixedSize(true);
                FindPetAdapter rAdapter = new FindPetAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                //   dialog.dismiss();
            } else {
//                nodatalostfound.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
            }
            //   dialog.dismiss();
        }
    }

    /*adapter start*/
    public class FindPetAdapter extends RecyclerView.Adapter<FindPetAdapter.ViewHolder> {

        private List<Data> itemList;
        private Context context;


        public FindPetAdapter(Context context, List<Data> itemList) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Picasso.with(context).load(itemList.get(position).getphoto()).into(viewHolder.photo);
            viewHolder.name.setText(itemList.get(position).getName());
            viewHolder.age.setText(itemList.get(position).getAge());
            viewHolder.breed.setText(itemList.get(position).getBreed());
            viewHolder.gender.setText(itemList.get(position).getGender());
            viewHolder.categ.setText(itemList.get(position).getcateg());
            // viewHolder.heigh.setText(itemList.get(position).getheight());
            // viewHolder.size.setText(itemList.get(position).getsize());
            viewHolder.imag.setText(itemList.get(position).getphoto());
            viewHolder.latt.setText(itemList.get(position).getlat());
            viewHolder.lonn.setText(itemList.get(position).getlon());
            viewHolder.phone.setText(itemList.get(position).getphone());
            viewHolder.date.setText(itemList.get(position).getDate());
            viewHolder.lnfStatus.setText(itemList.get(position).getLostorfound());
            viewHolder.dist.setText(itemList.get(position).getDist());
            viewHolder.pager.setText(itemList.get(position).getPager());
            viewHolder.areaTxt.setText(itemList.get(position).getArea());

            String state1 = viewHolder.lnfStatus.getText().toString();

            if (state1.equalsIgnoreCase("Missing")) {

                viewHolder.lnfStatus.setBackgroundColor(Color.parseColor("#f8a04f"));

            } else {
                viewHolder.lnfStatus.setBackgroundColor(Color.parseColor("#ed6e27"));
            }


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lnf_cntct_card_view, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, age, gender, breed, categ, heigh, size, imag, latt, lonn, phone, date, lnfStatus, dist, pager, areaTxt;
            ImageView photo;

            public ViewHolder(final View itemView) {
                super(itemView);
                breed = (TextView) itemView.findViewById(R.id.breed);
                name = (TextView) itemView.findViewById(R.id.name);
                age = (TextView) itemView.findViewById(R.id.age);
                categ = (TextView) itemView.findViewById(R.id.weig);
                heigh = (TextView) itemView.findViewById(R.id.heigh);
                size = (TextView) itemView.findViewById(R.id.size);
                imag = (TextView) itemView.findViewById(R.id.imag);
                latt = (TextView) itemView.findViewById(R.id.latt);
                lonn = (TextView) itemView.findViewById(R.id.lonn);
                gender = (TextView) itemView.findViewById(R.id.gender);
                photo = (ImageView) itemView.findViewById(R.id.picture);
                phone = (TextView) itemView.findViewById(R.id.phone);
                date = (TextView) itemView.findViewById(R.id.dateId);
                lnfStatus = (TextView) itemView.findViewById(R.id.status_text_id);
                dist = (TextView) itemView.findViewById(R.id.dist);
                areaTxt = (TextView) itemView.findViewById(R.id.areaId);
                pager = (TextView) itemView.findViewById(R.id.pager);

                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Intent i = new Intent(v.getContext(), PagerActivity.class);
                        i.putExtra("pos", pos);
                        i.putExtra("categ", categ.getText().toString());
//for distance
                       /* i.putExtra("brd", breed.getText().toString());
                        i.putExtra("gnd", gender.getText().toString());
                        i.putExtra("ageS", age.getText().toString());
                        i.putExtra("lat", latt.getText().toString());
                        i.putExtra("lon", lonn.getText().toString());
                        i.putExtra("dist", dist.getText().toString());
                        i.putExtra("lnf", lnfStatus.getText().toString());
                        i.putExtra("pager", pager.getText().toString());*/

                        i.putExtra("brd", breedstr);
                        i.putExtra("gnd", genderstr);
                        i.putExtra("ageS", agestr);
                        i.putExtra("lat", latString);
                        i.putExtra("lon", lonString);
                        i.putExtra("dist", dist.getText().toString());
                        i.putExtra("lnf", lnfStatus.getText().toString());
                        i.putExtra("pager", pager.getText().toString());

                        v.getContext().startActivity(i);
                    }
                });

            }

        }

    }
}
