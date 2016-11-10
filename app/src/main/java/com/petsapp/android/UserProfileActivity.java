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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petsapp.android.Model.PetListModel;
import com.petsapp.android.adapter.PetListAdapter;
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

public class UserProfileActivity extends AppCompatActivity {

    final Context context = this;
    JSONArray arr = null;
    LoginSessionManager loginSessionManager;
    String idString;
    String userName, mobileNo, email, addres, dob, img;
    double lat, lon;
    String latString, lonString;
    TextView uName, uMobile, uMobile2, eMail, addre;
    Bitmap bitmap;
    ImageView profileImg, intentimg;
    Marker mark;
    Marker userLocMark;
    RecyclerView petList;
    ConnectionDetector cd;
    TextView nodatauserprofile;
    private GoogleMap googleMap;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
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
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        uName = (TextView) findViewById(R.id.user_name_id);
        uMobile = (TextView) findViewById(R.id.user_mobi_id);
        uMobile2 = (TextView) findViewById(R.id.user_mobi2_id);
        eMail = (TextView) findViewById(R.id.user_email_id);
        addre = (TextView) findViewById(R.id.user_addr_id);
        intentimg = (ImageView) findViewById(R.id.intentImg);
        nodatauserprofile = (TextView) findViewById(R.id.nodatauserprofile);
        nodatauserprofile.setVisibility(View.GONE);

        intentimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(UserProfileActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(UserProfileActivity.this);
                } else {
                    IntentTask1 intT = new IntentTask1();
                    intT.execute();
                }
            }
        });

        profileImg = (ImageView) findViewById(R.id.user_profile_img_id);
        //  LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        petList = (RecyclerView) findViewById(R.id.user_pet_recy_id);
        //   petList.setLayoutManager(layoutManager);

        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();

        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(UserProfileActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(UserProfileActivity.this);
        } else {
            UserProfileTask task = new UserProfileTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getprofile");

            PetListTask task1 = new PetListTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getmates");
        }

      /* if(Isinternetpresent) {
           UserProfileTask task = new UserProfileTask();
           task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/getprofile"});

           PetListTask task1 = new PetListTask();
           task1.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/getmates"});
       }else{
           showAlertDialog(UserProfileActivity.this, "No Internet Connection", "You don't have internet connection.", false);
       }*/

    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private class UserProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(UserProfileActivity.this, "Loading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&pid=" + URLEncoder.encode(idString, "UTF-8");

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

                    userName = obj.getString("Name");
                    mobileNo = obj.getString("Phone");
                    email = obj.getString("Email");
                    addres = obj.getString("Address");
                    img = obj.getString("Image");

                    latString = obj.getString("lat");
                    lonString = obj.getString("lon");

                    lat = Double.parseDouble(latString);
                    lon = Double.parseDouble(lonString);

                    /****map******/

                    if (googleMap == null) {
                        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_id)).getMap();
                        // Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();

                        if (googleMap == null) {
                            //   Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                        } else {
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(userName);
                            googleMap.addMarker(marker);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    new LatLng(lat, lon)).zoom(12).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                    /****map******/
                    if (email == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

                        builder.setMessage("Please update your profile..")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), FirstProfileActivity.class);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Profile");
                        alert.show();

                    } else {


                        uName.setText(userName);
                        uMobile.setText(mobileNo);
                        uMobile2.setText(mobileNo);
                        eMail.setText(email);
                        addre.setText(addres);
                        Picasso.with(getApplicationContext()).load(img).into(profileImg);

                    }

                    //   Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
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

    /*Pet Recycler View*/

    public class PetListTask extends AsyncTask<String, String, List<PetListModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(UserProfileActivity.this, "Loading",
                    "Please wait...", true);
           dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();

        }

        @Override
        protected List<PetListModel> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                urlParameters = "&pid=" + URLEncoder.encode(idString, "UTF-8");

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
                List<PetListModel> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    PetListModel model = new PetListModel();
                    String nameString, breedString, genderString, ageString;
                    model.setName(obj.getString("pname"));
                    model.setBreed(obj.getString("breed"));
                    model.setAge(obj.getString("age"));
                    model.setGender(obj.getString("gender"));
                    model.setThumbnailUrl(obj.getString("images"));

                    String pName = obj.getString("pname");
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PetListModel> itemObjects) {
            super.onPostExecute(itemObjects);
            if (itemObjects != null && itemObjects.size() > 0) {
                nodatauserprofile.setVisibility(View.GONE);
                petList.setVisibility(View.VISIBLE);
                petList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                CardView card = (CardView) findViewById(R.id.cardpet);
                petList.setHasFixedSize(true);
                PetListAdapter adapter = new PetListAdapter(getApplicationContext(), itemObjects);
                petList.setAdapter(adapter);
            } else {
                nodatauserprofile.setVisibility(View.VISIBLE);
                petList.setVisibility(View.GONE);
            }
        }
    }

    /*Pet Recycler View*/
    private class IntentTask1 extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... args) {
            Intent i = new Intent(getApplicationContext(), UpdateUserProfileActivity.class);

            i.putExtra("name", userName);
            i.putExtra("mobi", mobileNo);
            i.putExtra("mail", email);
            i.putExtra("img", img);
            i.putExtra("addr", addres);

            i.putExtra("latString", latString);
            i.putExtra("lonString", lonString);
            startActivity(i);
            finish();
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
        }
    }

}
