package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.Model.BreedModel;
import com.petsapp.android.Model.Data;
import com.petsapp.android.adapter.BreedAdapter;
import com.petsapp.android.adapter.FindAdoptAdapter;
import com.petsapp.android.adapter.RecyclerViewAdapter;
import com.petsapp.android.helper.MyPreferenceManager;
import com.petsapp.android.sessionLogin.LoginSessionManager;

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

public class DemoAutoComplete extends AppCompatActivity {
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
    String genderstr = "";
    String diststr = "0";
    int breedpos, agepos, genderpos, distpos;
    String idString;
    String cate = "";
    ConnectionDetector cd;
    GPSTracker gps;
    double latitude, longitude;
    int typos;
    MyPreferenceManager myPreferenceManager;
    Context context;
    LoginSessionManager loginSessionManager;
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
        myPreferenceManager = new MyPreferenceManager(context);
        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), HomeMateActivity.class);
                startActivity(i);
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }
        context = this;
        myPreferenceManager = new MyPreferenceManager(context);

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        locationSpinner = (Spinner) findViewById(R.id.location_spn_id);

        typos = locationSpinner.getSelectedItemPosition();

        gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            if (gps != null) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
        } else {
            gps.showSettingsAlert();
        }

        breed = (Spinner) findViewById(R.id.breedsp);
        gender = (Spinner) findViewById(R.id.gendersp);
        age = (Spinner) findViewById(R.id.agesp);
        distance = (Spinner) findViewById(R.id.distancesp);

        final String spinVal = String.valueOf(breed.getSelectedItem());

        dogImg = (ImageView) findViewById(R.id.dog);
        catImg = (ImageView) findViewById(R.id.cat);
        others = (ImageView) findViewById(R.id.others);
        dogImg.setImageResource(R.drawable.dog);
        catImg.setImageResource(R.drawable.cat1);
        others.setImageResource(R.drawable.other1);

        RecyclerView recycle = (RecyclerView) findViewById(R.id.recycle);

        recycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycle.setAdapter(adapter);

        //      **************
        dogImg.setImageResource(R.drawable.dog);
        dogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                if (Isinternetpresent) {
                    category = "dog";
                    BreedTask task = new BreedTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcategory");

                    setimage(0);
                } else {
                    showAlertDialog(DemoAutoComplete.this, "No Internet Connection", "You don't have internet connection.", false);
                }

                _LstVar = 1;

            }

        });

        catImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Isinternetpresent) {
                    category = "cat";
                    BreedTask task = new BreedTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcategory");

                    setimage(1);

                    _LstVar = 2;

                } else {
                    showAlertDialog(DemoAutoComplete.this, "No Internet Connection", "You don't have internet connection.", false);
                }
            }
        });
        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Isinternetpresent) {
                    category = "others";
                    BreedTask task = new BreedTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                    DetailsTask task1 = new DetailsTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcategory");

                    setimage(2);


                    _LstVar = 2;

                } else {
                    showAlertDialog(DemoAutoComplete.this, "No Internet Connection", "You don't have internet connection.", false);
                }
            }
        });

        if (Isinternetpresent) {
            BreedTask task = new BreedTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

            DetailsTask task1 = new DetailsTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcategory");
        } else {
            showAlertDialog(DemoAutoComplete.this, "No Internet Connection", "You don't have internet connection.", false);
        }

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
            startActivity(i1);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            breed = (Spinner) findViewById(R.id.breedsp);
            BreedAdapter adapter = new BreedAdapter(getApplicationContext(), R.layout.spinner_row, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breed.setAdapter(adapter);


            locationSpinner.setSelection(Integer.parseInt(myPreferenceManager.getLocationpref()));
            //    typos = locationSpinner.getSelectedItemPosition();
            //

            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // String petcatestr = locationSpinner.getItemAtPosition(position).toString();
                    typos = locationSpinner.getSelectedItemPosition();

                    if (typos == 0) {
                        //
                        //   if (petcatestr.equalsIgnoreCase("Stored Location")) {

                        breed.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        breedpos = breed.getSelectedItemPosition();
                                        breedstr = breed.getItemAtPosition(position).toString();
                                        if (breedpos == 0) {

                                            breedstr = "";
                                            //   Toast.makeText(getApplicationContext(), "Please Select Breed", Toast.LENGTH_LONG).show();
                                        } else {
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");

                                            } else {
                                                //     Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );

                        age = (Spinner) findViewById(R.id.agesp);

                        age.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        agepos = age.getSelectedItemPosition();
                                        agestr = age.getItemAtPosition(position).toString();
                                        if (agepos == 0) {
                                            agestr = "";
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");
                                            } else {
                                                //     Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");
                                            } else {
                                                //       Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
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
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");
                                            } else {
                                                //      Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");
                                            } else {
                                                //    Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );


                        //
                    } else if (typos == 1) {

                        // Toast.makeText(HomeActivity.this, "Please Select", Toast.LENGTH_SHORT).show();
                        breed.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        breedpos = breed.getSelectedItemPosition();
                                        breedstr = breed.getItemAtPosition(position).toString();
                                        if (breedpos == 0) {

                                            breedstr = "";
                                            //   Toast.makeText(getApplicationContext(), "Please Select Breed", Toast.LENGTH_LONG).show();
                                        } else {
                                            if (Isinternetpresent) {

                                               /* latitude = gps.getLatitude();
                                                longitude = gps.getLongitude();*/

                                                latString = String.valueOf(latitude);
                                                lonString = String.valueOf(longitude);

                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcurrentdistance");

                                            } else {
                                                //     Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );

                        age = (Spinner) findViewById(R.id.agesp);

                        age.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        agepos = age.getSelectedItemPosition();
                                        agestr = age.getItemAtPosition(position).toString();
                                        if (agepos == 0) {
                                            agestr = "";
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcurrentdistance");
                                            } else {
                                                //     Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                //    Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
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
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcurrentdistance");
                                            } else {
                                                //   Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (Isinternetpresent) {
                                                MateAddTask task2 = new MateAddTask();
                                                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getdistance");
                                            } else {
                                                //    Toast.makeText(DemoAutoComplete.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        // TODO Auto-generated method stub
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            distance = (Spinner) findViewById(R.id.distancesp);

        }
    }

    public class DetailsTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DemoAutoComplete.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();
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

            if (detailsModels != null) {

                RecyclerView rview = (RecyclerView) findViewById(R.id.recycle);
                CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setHasFixedSize(true);

                FindAdoptAdapter rAdapter = new FindAdoptAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                dialog.dismiss();

            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

                alertDialogBuilder.setTitle("No Any Data");
                alertDialogBuilder
                        .setMessage("")
                        .setCancelable(false);
                alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(getApplicationContext(), HomeMateActivity.class);
                        startActivity(i);

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    public class MateAddTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DemoAutoComplete.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();

        }

        @Override
        protected List<Data> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

//Changed*****

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
            if (detailsModels != null) {

                RecyclerView rview = (RecyclerView) findViewById(R.id.recycle);
                CardView card = (CardView) findViewById(R.id.cardpet);
                rview.setHasFixedSize(true);
                FindAdoptAdapter rAdapter = new FindAdoptAdapter(getApplicationContext(), detailsModels);
                rview.setAdapter(rAdapter);
                dialog.dismiss();
            }
        }
    }
}