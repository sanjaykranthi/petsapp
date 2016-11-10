package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.app.Config;
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

public class LostFoundNotification extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String token, petid, message;
    String msg1, msgby, msgto;
    JSONArray arr = null;
    String idStr, pidStr, breedStr, ageStr, genderStr, colorStr, dateStr, streetStr, petnameStr, areaStr, aboutStr, imageStr, lostorFoundStr, categoryStr, latStr, lonStr, phoneStr;
    TextView id, pid, breed, age, gender, color, date, street, petname, area, about, lostorFound, category, lat, lon, phone, details;
    ImageView image;
    LoginSessionManager loginSessionManager;
    ImageView interest;
    String idString;
    private String TAG = ChatActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog dialog;
    private String urlParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_notification);
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
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();

        petname = (TextView) findViewById(R.id.petname);
        breed = (TextView) findViewById(R.id.petbreed);
        category = (TextView) findViewById(R.id.petCategory);
        gender = (TextView) findViewById(R.id.petgender);
        lostorFound = (TextView) findViewById(R.id.petStatus);
        about = (TextView) findViewById(R.id.petAbout);
        date = (TextView) findViewById(R.id.petDate);
        details = (TextView) findViewById(R.id.contact);
        image = (ImageView) findViewById(R.id.pet_pic);

        interest = (ImageView) findViewById(R.id.call);

        Intent intent = getIntent();
        petid = intent.getStringExtra("id");
        //  Toast.makeText(getApplicationContext(),"pet id is now ...."+petid,Toast.LENGTH_LONG).show();
        if (loginSessionManager.checkLogin()) {
            finish();
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(LostFoundNotification.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(LostFoundNotification.this);
        } else {
            PetDetailsTask task = new PetDetailsTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostdetails");
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user1 = loginSessionManager.getUserDetails();
        idString = user1.get(LoginSessionManager.KEY_ID);

        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginTask1 task = new LoginTask1();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/invitation");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LostFoundNotification.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /*Interest*/

    /*Notifaction Data*/
    private class PetDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(LostFoundNotification.this, "Loading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&id=" + URLEncoder.encode(petid, "UTF-8");
                System.out.println("petid" + urlParameters);
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

                    petnameStr = obj.getString("petname");
                    breedStr = obj.getString("breed");
                    idStr = obj.getString("id");
                    pidStr = obj.getString("pid");
                    ageStr = obj.getString("age");

                    genderStr = obj.getString("gender");
                    aboutStr = obj.getString("about");
                    dateStr = obj.getString("date");
                    streetStr = obj.getString("street");

                    areaStr = obj.getString("area");
                    imageStr = obj.getString("image");

                    lostorFoundStr = obj.getString("lostorfound");
                    colorStr = obj.getString("color");
                    categoryStr = obj.getString("category");
                    latStr = obj.getString("lat");
                    lonStr = obj.getString("lon");
                    phoneStr = obj.getString("phone");
                    //set
                    petname.setText(petnameStr);
                    breed.setText(breedStr);
                    category.setText(categoryStr);
                    gender.setText(genderStr);

                    lostorFound.setText(lostorFoundStr);
                    about.setText(aboutStr);
                    date.setText(dateStr);
                    details.setText(phoneStr);

                    Picasso.with(getApplicationContext()).load(imageStr).into(image);

                }
                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoginTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(AdoptionPagerAdapter.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&invited_by=" + URLEncoder.encode(idString, "UTF-8") +
                        "&invited_to=" + URLEncoder.encode(pidStr, "UTF-8") +
                        "&type=" + URLEncoder.encode("2", "UTF-8");

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
                    Context context = null;
                    if (state.equals("success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LostFoundNotification.this);
                        builder.setMessage("Your interest has been passed on to the pet owner")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else if (state.equals("Already Communication is Established")) {

                        String invited_by = obj.getString("invited_by");
                        String invited_to = obj.getString("invited_to");
                        String url = obj.getString("url");
                        String name = obj.getString("name");
                        Intent i1 = new Intent(LostFoundNotification.this, ChatActivity.class);
                        i1.putExtra("msgby", invited_to);
                        i1.putExtra("nameVal", name);
                        i1.putExtra("phturl", url);

                        i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i1);
                        finish();

                       /* AlertDialog.Builder builder = new AlertDialog.Builder(LostFoundNotification.this);
                        builder.setMessage("Already Communication is Established")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Play Date");
                        alert.show();*/
                    } else if (state.equals("Please Register Pet")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LostFoundNotification.this);
                        builder.setMessage("Please register a pet first.")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(LostFoundNotification.this, LostFoundPetRegActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        finish();
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //    dialog.dismiss();
        }
    }
}
