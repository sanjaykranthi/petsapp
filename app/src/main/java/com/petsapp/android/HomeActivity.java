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
import android.util.Log;
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
import com.petsapp.android.helper.MyPreferenceManager;
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

public class HomeActivity extends AppCompatActivity {

    final Context context = this;
    RecyclerViewAdapter adapter;
    List<Data> list = new ArrayList<>();
    Spinner breed, age, gender, distance, locationSpinner;
    ImageView dogImg, catImg, others;
    RecyclerView recycle;
    TextView recyText;
    String latString = "0.0", lonString = "0.0";
    Double lat = 0.0, lon = 0.0;
    int _LstVar = 0;
    JSONArray arr = null;
    String category = "dog", categ;
    String cat, dog;
    String breedstr = "";
    String agestr = "";
    String agestr2 = "";
    String genderstr = "";
    String diststr = "0";
    int breedpos, agepos, genderpos, distpos;
    String idString, storedLat, storedLon;
    String cate = "";
    ConnectionDetector cd;
    GPSTracker gps;
    double latitude, longitude;
    int typos;
    LoginSessionManager loginSessionManager;
    TextView nodataplay;
    RecyclerView rview;
    String[] agestrArray = {"Age (years)", "<1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"};
    String[] gndrstrArray = {"Gender", "Male", "Female"};
    AutoCompleteTextView breedAuto;
    MyPreferenceManager myPreferenceManager;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), HomeMateActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }

       /* Bundle extras = getIntent().getExtras();
        storedLat = extras.getString("storedLat");
        storedLon = extras.getString("storedLon");*/
        myPreferenceManager = new MyPreferenceManager(context);
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        LatLonTask task1 = new LatLonTask();
        task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlatlon");

        locationSpinner = (Spinner) findViewById(R.id.location_spn_id);
        locationSpinner.setSelection(Integer.parseInt(myPreferenceManager.getLocationpref()));
        typos = locationSpinner.getSelectedItemPosition();


        breedAuto = (AutoCompleteTextView) findViewById(R.id.breed_auto_Id);
        breed = (Spinner) findViewById(R.id.breedsp);
        gender = (Spinner) findViewById(R.id.gendersp);
        age = (Spinner) findViewById(R.id.agesp);
        distance = (Spinner) findViewById(R.id.distancesp);
        nodataplay = (TextView) findViewById(R.id.nodatainplaydate);
        nodataplay.setVisibility(View.GONE);
        final String spinVal = String.valueOf(breed.getSelectedItem());
        rview = (RecyclerView) findViewById(R.id.recycle);
        dogImg = (ImageView) findViewById(R.id.dog);
        catImg = (ImageView) findViewById(R.id.cat);
        others = (ImageView) findViewById(R.id.others);
        dogImg.setImageResource(R.drawable.dog);
        catImg.setImageResource(R.drawable.cat1);
        others.setImageResource(R.drawable.other1);


        //      **************
        dogImg.setImageResource(R.drawable.dog);

        age = (Spinner) findViewById(R.id.agesp);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(HomeActivity.this, R.layout.spinner_row, R.id.spinner_text, agestrArray);
        // adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        age.setAdapter(adapter1);

        gender = (Spinner) findViewById(R.id.gendersp);
        ArrayAdapter<String> adapterG = new ArrayAdapter<String>(HomeActivity.this, R.layout.spinner_row, R.id.spinner_text, gndrstrArray);
        adapterG.setDropDownViewResource(R.layout.spinner_row1);
        gender.setAdapter(adapterG);


        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(HomeActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(HomeActivity.this);
        } else {


            BreedTask task = new BreedTask();
            task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

           /* DetailsTask task1 = new DetailsTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");*/

            categ = "dog";
            latString = storedLat;
            lonString = storedLon;
            MateAddTask task2 = new MateAddTask();
            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");

          /*  gps = new GPSTracker(getApplicationContext());
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();*/
        }
       /* if (Isinternetpresent) {
            BreedTask task = new BreedTask();
            task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

            DetailsTask task1 = new DetailsTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
        } else {
            showAlertDialog(HomeActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }*/

    }

    public void setimage(int i) {
        if (i == 0) {
            dogImg.setImageResource(0);
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(R.drawable.dog);
            catImg.setImageResource(R.drawable.cat1);
            others.setImageResource(R.drawable.other1);
        } else if (i == 1) {
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(0);
            dogImg.setImageResource(R.drawable.bruno);
            catImg.setImageResource(R.drawable.cat2);
            others.setImageResource(R.drawable.other1);
        } else {
            catImg.setImageResource(0);
            others.setImageResource(0);
            dogImg.setImageResource(0);
            others.setImageResource(R.drawable.other2);
            dogImg.setImageResource(R.drawable.bruno);
            catImg.setImageResource(R.drawable.cat1);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(HomeActivity.this, HomeMateActivity.class);
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

                urlParameters = "&catg=" + URLEncoder.encode(category, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(idString, "UTF-8");

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
                    /// String state = obj.getString("status");

                    BreedModel model = new BreedModel();
                    model.setName(obj.getString("name"));

                    breedModelList.add(model);
                }
                return breedModelList;
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

                urlParameters = "&catg=" + URLEncoder.encode(category, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(idString, "UTF-8");

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
                    /// String state = obj.getString("status");

                    BreedModel model = new BreedModel();
                    model.setName(obj.getString("name"));

                    breedModelList.add(model);
                }
                return breedModelList;
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

          /*  breed = (Spinner) findViewById(R.id.breedsp);
            BreedAdapter adapter = new BreedAdapter(getApplicationContext(), R.layout.spinner_row, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breed.setAdapter(adapter);*/

            ArrayAdapter adp = new ArrayAdapter(getApplicationContext(), R.layout.breed_auto_text, result);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedAuto.setThreshold(1);
            breedAuto.setAdapter(adp);
            //    typos = locationSpinner.getSelectedItemPosition();
            //
            locationSpinner.setSelection(Integer.parseInt(myPreferenceManager.getLocationpref()));
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // String petcatestr = locationSpinner.getItemAtPosition(position).toString();
                    typos = locationSpinner.getSelectedItemPosition();
                    Log.d("Deepak typos Check:", "" + typos);
                    if (typos == 0) {
                        Log.d("Deepak stored Check:", "" + latString + ",," + lonString);
                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(HomeActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(HomeActivity.this);
                        } else {
                            latString = storedLat;
                            lonString = storedLon;
                            MateAddTask task2 = new MateAddTask();
                            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                        }
                        //   if (petcatestr.equalsIgnoreCase("Stored Location")) {

                        breedAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    breedstr = breedAuto.getText().toString();
                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
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
                                            //   Toast.makeText(getApplicationContext(), "Please Select Breed", Toast.LENGTH_LONG).show();

                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }


                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );*/

                        age = (Spinner) findViewById(R.id.agesp);

                        age.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        agepos = age.getSelectedItemPosition();
                                        agestr = age.getItemAtPosition(position).toString();

                                        /*try {
                                            String string = agestrAll;
                                            String[] parts = string.split("-");
                                            agestr = parts[0];
                                            agestr2 = parts[1];
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }*/

                                        if (agepos == 1) {
                                            agestr = "0";
                                        }

                                        if (agepos == 0) {
                                            agestr = "";
                                            agestr2 = "";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        gender = (Spinner) findViewById(R.id.gendersp);

                        gender.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        genderpos = gender.getSelectedItemPosition();
                                        genderstr = gender.getItemAtPosition(position).toString();

                                        if (genderpos == 0) {
                                            genderstr = "";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = storedLat;
                                                lonString = storedLon;
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "dog";
                                    categ = "dog";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");

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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "cat";
                                    categ = "cat";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "others";
                                    categ = "others";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = storedLat;
                                    lonString = storedLon;
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");

                                    setimage(2);
                                    _LstVar = 2;
                                }
                            }
                        });


                        //
                    } else if (typos == 1) {

                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(HomeActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(HomeActivity.this);
                        } else {

                            gps = new GPSTracker(HomeActivity.this);
                            if (gps.canGetLocation()) {
                                if (gps != null) {
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                }
                            } else {
                                gps.showSettingsAlert();
                            }

                            latString = String.valueOf(latitude);
                            lonString = String.valueOf(longitude);
                            MateAddTask task2 = new MateAddTask();
                            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                        }

                        breedAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    breedstr = breedAuto.getText().toString();
                                    latString = String.valueOf(latitude);
                                    lonString = String.valueOf(longitude);
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
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
                                            //   Toast.makeText(getApplicationContext(), "Please Select Breed", Toast.LENGTH_LONG).show();

                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }

                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }
                                           *//* if (Isinternetpresent) {

                                               *//**//* latitude = gps.getLatitude();
                                                longitude = gps.getLongitude();*//**//*

                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");

                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*//*

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );*/

                        age = (Spinner) findViewById(R.id.agesp);

                        age.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        agepos = age.getSelectedItemPosition();
                                        agestr = age.getItemAtPosition(position).toString();

                                        /*try {
                                            String string = agestrAll;
                                            String[] parts = string.split("-");
                                            agestr = parts[0];
                                            agestr2 = parts[1];
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }*/

                                        if (agepos == 1) {
                                            agestr = "0";
                                        }

                                        if (agepos == 0) {
                                            agestr = "";
                                            agestr2 = "";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                }
                        );

                        gender = (Spinner) findViewById(R.id.gendersp);

                        gender.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        genderpos = gender.getSelectedItemPosition();
                                        genderstr = gender.getItemAtPosition(position).toString();

                                        if (genderpos == 0) {
                                            genderstr = "";
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        } else {
                                            if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                                            } else if (!com.petsapp.android.CommonUtilities
                                                    .isConnectingToInternet(getApplicationContext())) {
                                                com.petsapp.android.CommonUtilities
                                                        .showLocationSettingsAlert(HomeActivity.this);
                                            } else {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            }
                                           /* if (Isinternetpresent) {
                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
                                            } else {
                                                Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "dog";
                                    categ = "dog";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = String.valueOf(latitude);
                                    lonString = String.valueOf(longitude);
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");

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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "cat";
                                    categ = "cat";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = String.valueOf(latitude);
                                    lonString = String.valueOf(longitude);
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");
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
                                            .showInterntOnlySettingsAlert(HomeActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(HomeActivity.this);
                                } else {
                                    category = "others";
                                    categ = "others";
                                    BreedTask1 task = new BreedTask1();
                                    task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                                    latString = String.valueOf(latitude);
                                    lonString = String.valueOf(longitude);
                                    MateAddTask task2 = new MateAddTask();
                                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcurrentdistance");

                                    setimage(2);
                                    _LstVar = 2;
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
                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(HomeActivity.this);
                            }
                            else{
                                category = "dog";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

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
                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(HomeActivity.this);
                            }
                            else{
                                category = "cat";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
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
                                        .showInterntOnlySettingsAlert(HomeActivity.this);
                            }
                            else if (!com.areratech.newpatrimony.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.areratech.newpatrimony.CommonUtilities
                                        .showLocationSettingsAlert(HomeActivity.this);
                            }
                            else{
                                category = "others";
                                BreedTask1 task = new BreedTask1();
                                task.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                                DetailsTask task1 = new DetailsTask();
                                task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

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
            if (Integer.parseInt(myPreferenceManager.getLocationpref()) == 0) {
                locationSpinner.setSelection(1, true);
                locationSpinner.setSelection(0, true);
            } else {
                locationSpinner.setSelection(0, true);
                locationSpinner.setSelection(1, true);
            }
        }
    }

    public class DetailsTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(HomeActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/
        }

        @Override
        protected List<Data> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&catg=" + URLEncoder.encode(category, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(idString, "UTF-8");

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
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();

                System.out.println("final json---" + finalJson);
                List<Data> dataModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);

                JSONObject jObj = arr.getJSONObject(0);

                System.out.println("final json---" + jObj);

                JSONArray childArray = jObj.getJSONArray("details");

                for (int i = 0; i < childArray.length(); i++) {
                    JSONObject obj = childArray.getJSONObject(i);
                    Data model = new Data();

                    model.setName(obj.getString("pname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setsize(obj.getString("size"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setphone(obj.getString("phone"));
                    model.setDate(obj.getString("date"));

                    model.setcateg(obj.getString("cat"));
                    categ = obj.getString("cat");

                    latString = obj.getString("lat");
                    lonString = obj.getString("lon");

                    lat = Double.parseDouble(latString);
                    lon = Double.parseDouble(lonString);

                    String pName = obj.getString("pname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
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
                nodataplay.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);

                CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                rview.setHasFixedSize(true);

                RecyclerViewAdapter rAdapter = new RecyclerViewAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                //   dialog.dismiss();

            } else {
                nodataplay.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
            }
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
           /* dialog = ProgressDialog.show(HomeActivity.this, "Fetching Data",
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
                Log.d("Deepak check::", "" + latString + ",," + lonString);
                urlParameters = "&pid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&categ=" + URLEncoder.encode(categ, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedstr, "UTF-8") +
                        "&age=" + URLEncoder.encode(agestr, "UTF-8") +
                        "&gnd=" + URLEncoder.encode(genderstr, "UTF-8") +
                        "&lat=" + URLEncoder.encode(latString, "UTF-8") +
                        "&lon=" + URLEncoder.encode(lonString, "UTF-8");

                //"&dist=" + URLEncoder.encode(diststr, "UTF-8") +

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
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                String finalJson = buffer.toString();

                System.out.println("final json---" + finalJson);

                List<Data> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Data model = new Data();
                    model.setName(obj.getString("petname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    //model.setsize(obj.getString("size"));
                    model.setphone(obj.getString("phone"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setDate(obj.getString("date"));
                    model.setcateg(obj.getString("cat"));
                    model.setDist(obj.getString("distance"));
                    model.setPager(obj.getString("pager"));

                    String pName = obj.getString("petname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
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

                nodataplay.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                rview.setHasFixedSize(true);
                RecyclerViewAdapter rAdapter = new RecyclerViewAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                // dialog.dismiss();
            } else {
                nodataplay.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
            }
        }
    }

    /*Adapter*/

    private class LatLonTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(HomeActivity.this, "Loading",
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

                    //        Toast.makeText(getApplicationContext(), storedLat+" "+storedLon, Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private List<Data> itemList;
        private Context context;


        public RecyclerViewAdapter(Context context, List<Data> itemList) {
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
            viewHolder.weigh.setText(itemList.get(position).getweight());
            viewHolder.heigh.setText(itemList.get(position).getheight());
            viewHolder.size.setText(itemList.get(position).getsize());
            viewHolder.imag.setText(itemList.get(position).getphoto());
            viewHolder.latt.setText(itemList.get(position).getlat());
            viewHolder.lonn.setText(itemList.get(position).getlon());
            viewHolder.phone.setText(itemList.get(position).getphone());
            viewHolder.date.setText(itemList.get(position).getDate());
            viewHolder.categ.setText(itemList.get(position).getcateg());
            viewHolder.dist.setText(itemList.get(position).getDist());
            viewHolder.pager.setText(itemList.get(position).getPager());

            String age1 = viewHolder.age.getText().toString();
            if (age1.equalsIgnoreCase("")) {
                viewHolder.yrs.setVisibility(View.GONE);
            } else {
                viewHolder.yrs.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, age, gender, breed, weigh, heigh, size, imag, latt, lonn, phone, date, categ, dist, yrs, pager;
            Spinner breedsp, agesp, gendersp, distancesp;
            ImageView photo;

            public ViewHolder(final View itemView) {
                super(itemView);
                breed = (TextView) itemView.findViewById(R.id.breed);
                name = (TextView) itemView.findViewById(R.id.name);
                age = (TextView) itemView.findViewById(R.id.age);
                weigh = (TextView) itemView.findViewById(R.id.weig);
                heigh = (TextView) itemView.findViewById(R.id.heigh);
                size = (TextView) itemView.findViewById(R.id.size);
                imag = (TextView) itemView.findViewById(R.id.imag);
                latt = (TextView) itemView.findViewById(R.id.latt);
                lonn = (TextView) itemView.findViewById(R.id.lonn);
                gender = (TextView) itemView.findViewById(R.id.gender);
                photo = (ImageView) itemView.findViewById(R.id.picture);
                phone = (TextView) itemView.findViewById(R.id.phone);
                date = (TextView) itemView.findViewById(R.id.dateId);
                categ = (TextView) itemView.findViewById(R.id.categ);
                dist = (TextView) itemView.findViewById(R.id.dist);
                yrs = (TextView) itemView.findViewById(R.id.first);
                pager = (TextView) itemView.findViewById(R.id.pager);
                CardView card = (CardView) itemView.findViewById(R.id.card);
                itemView.setOnClickListener(new View.OnClickListener() {
                    int pos = getAdapterPosition();

                    @Override
                    public void onClick(View v) {

                        Intent in = new Intent(v.getContext(), MatePagerActivity.class);
                        int pos = getAdapterPosition();
                        in.putExtra("pos", pos);
                        in.putExtra("categ", categ.getText().toString());


                        in.putExtra("brd", breedstr);
                        in.putExtra("gnd", genderstr);
                        in.putExtra("ageS", agestr);

                        in.putExtra("lat", latString);
                        in.putExtra("lon", lonString);
                        in.putExtra("dist", diststr);
                        in.putExtra("pager", pager.getText().toString());

                        v.getContext().startActivity(in);

                    }
                });

            }

        }
    }
}
