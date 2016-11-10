package com.petsapp.android;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.petsapp.android.app.Config;
import com.petsapp.android.helper.MyPreferenceManager;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.petsapp.android.sessionLogin.MapSession;
import com.petsapp.android.sessionLogin.firstprofilesession;
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

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static Activity fa;
    static Button notifCount;
    static int mNotifCount = 0;
    final Context context = this;
    LinearLayout navProfile, navHome, navConReq, navShare, navEdit, navMsg, navFavProfile, navSettings, navPrivacy, navAbout, navinvite, navterms, navSignout;
    ImageView mateImageButton, lost, info, adoption, follow;
    String userName, img, address, storedLat, storedLon;
    JSONArray arr = null;
    String idString;
    ImageView profImage;
    TextView profNameText;
    Bitmap bitmap;
    ConnectionDetector cd;
    String token;
    Notification_count_session Notificationcountsession;
    MyPreferenceManager myPreferenceManager;
    private LoginSessionManager loginSessionManager;
    private firstprofilesession firstprofilesession;
    private MapSession mapSession;
    private GoogleMap googleMap;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        //Permission checking
        if (ContextCompat.checkSelfPermission(this, permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this,
                permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_CONTACTS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this,
                permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.CALL_PHONE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this,
                permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission.READ_EXTERNAL_STORAGE}, 1);
        }


        myPreferenceManager = new MyPreferenceManager(context);


        fa = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#5d4b68"));
        }
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        firstprofilesession = new firstprofilesession(getApplicationContext());
        Notificationcountsession = new Notification_count_session(getApplicationContext());
        mapSession = new MapSession(getApplicationContext());
        if (loginSessionManager.checkLogin()) {
            finish();
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(DashboardActivity.this);
        } else {
            if (myPreferenceManager.getLocationpref() == null) {
                locationprefselector();
            }
            HashMap<String, String> user = Notificationcountsession.getUserDetails();

            idString = user.get(Notification_count_session.KEY_NAME);
            mNotifCount = Integer.parseInt(idString);
            UserProfileTask task = new UserProfileTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getprofile");

        }
        HashMap<String, String> user = loginSessionManager.getUserDetails();

        idString = user.get(LoginSessionManager.KEY_ID);
         /*Gcm Start*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    token = intent.getStringExtra("token");

                    //   Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    //     Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    //    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());
                }
            }
        };

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.navbar_icon, null);
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mateImageButton = (ImageView) findViewById(R.id.mate_img_id);
        lost = (ImageView) findViewById(R.id.lostfound_img_id);
        follow = (ImageView) findViewById(R.id.follow_img_id);
        info = (ImageView) findViewById(R.id.articles_img_id);
        adoption = (ImageView) findViewById(R.id.adoption_img_id);
        profImage = (ImageView) findViewById(R.id.nav_prof_img_id);
        profNameText = (TextView) findViewById(R.id.prof_name_id);

        mateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                mateImageButton.startAnimation(animFadein);

                Intent in = new Intent(getApplicationContext(), HomeMateActivity.class);
                //  in.putExtra("ADDRESS", address);
                // in.putExtra("storedLat", storedLat);
                //  in.putExtra("storedLon", storedLon);
                startActivity(in);
            }
        });
        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                lost.startAnimation(animFadein);
                Intent in = new Intent(getApplicationContext(), LostandFoundActivity.class);
                //   in.putExtra("storedLat", storedLat);
                //   in.putExtra("storedLon", storedLon);
                startActivity(in);
            }
        });
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                follow.startAnimation(animFadein);
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(DashboardActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(DashboardActivity.this);
                } else {
                    Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                    startActivity(in);
                }
                Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(in);
            }
        });
        adoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                adoption.startAnimation(animFadein);
                Intent in = new Intent(getApplicationContext(), AdoptionActivity.class);
                //  in.putExtra("ADDRESS", address);
                //  in.putExtra("storedLat", storedLat);
                //  in.putExtra("storedLon", storedLon);
                startActivity(in);
               /* AdoptionAsyncTask pars1 = new AdoptionAsyncTask();
                pars1.execute();*/
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                info.startAnimation(animFadein);
                Intent in = new Intent(getApplicationContext(), FollowUsActivity.class);
                startActivity(in);
            }
        });


        navProfile = (LinearLayout) findViewById(R.id.nav_profile_id);
        navEdit = (LinearLayout) findViewById(R.id.nav_edit_pet_id);
        navPrivacy = (LinearLayout) findViewById(R.id.nav_privacy_id);
        //   navAbout = (LinearLayout) findViewById(R.id.nav_about_id);
        navSignout = (LinearLayout) findViewById(R.id.nav_signout_id);
        navinvite = (LinearLayout) findViewById(R.id.nav_invite_your_friends);
        navterms = (LinearLayout) findViewById(R.id.nav_terms_services);

        navProfile.setOnClickListener(this);
        navEdit.setOnClickListener(this);
        navPrivacy.setOnClickListener(this);
        //  navAbout.setOnClickListener(this);
        navSignout.setOnClickListener(this);
        navinvite.setOnClickListener(this);
        navterms.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        myPreferenceManager.changeLocationpref(null);
        Log.d("Tagg::", "" + myPreferenceManager.getLocationpref());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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


  /*end gcm*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;*/

        if (mNotifCount != 0) {

            getMenuInflater().inflate(R.menu.chat_menu, menu);
            MenuItem item = menu.findItem(R.id.action_chat);
            MenuItemCompat.setActionView(item, R.layout.feed_update_count);
            notifCount = (Button) MenuItemCompat.getActionView(item);
            View count = menu.findItem(R.id.action_chat).getActionView();
            notifCount.setText(String.valueOf(mNotifCount));

            notifCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i1 = new Intent(DashboardActivity.this, ChatListActivity.class);
                    startActivity(i1);
                }
            });
        } else {
            getMenuInflater().inflate(R.menu.chat_menu, menu);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_chat) {
            //     Toast.makeText(DashboardActivity.this, "No any new messages", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(DashboardActivity.this, ChatListActivity.class);
            startActivity(i1);
            return true;
        }
        if (id == R.id.action_twitter) {
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("twitter://user?screen_name=PetsApp_India"));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/#!/PetsApp_India")));
            }
            return true;
        }
        if (id == R.id.action_fb) {
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1585554178408901"));
                startActivity(intent);
                finish();
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/PetsAppIndia")));
            }

            return true;
        }
        if (id == R.id.action_insta) {
            Uri uri = Uri.parse("https://www.insgy.com/tag/petsapp");
            Intent instapage = new Intent(Intent.ACTION_VIEW, uri);

            instapage.setPackage("com.instagram.android");

            try {
                startActivity(instapage);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.insgy.com/tag/petsapp")));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.nav_profile_id:
                Intent i1 = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(i1);
                break;

            case R.id.nav_edit_pet_id:
                Intent i2 = new Intent(DashboardActivity.this, Rpetlist.class);
                startActivity(i2);
                break;

            case R.id.nav_privacy_id:

                Intent i3 = new Intent(DashboardActivity.this, Privacy_policy_Page.class);
                startActivity(i3);
                break;
            case R.id.nav_invite_your_friends:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out Petsapp for your Smartphone.Download it today from https://play.google.com/store/apps/details?id=com.petsapp.android");
                startActivity(Intent.createChooser(sharingIntent, "Share"));

//                Intent i4 = new Intent(DashboardActivity.this, Privacy_policy_Page.class);
//                startActivity(i4);
                break;
            case R.id.nav_terms_services:

                Intent i5 = new Intent(DashboardActivity.this, terms_of_services.class);
                startActivity(i5);
                break;

            case R.id.nav_signout_id:
                loginSessionManager.logoutUser();
                // finish();
                break;

        }

    }

    /*Lat Lon*/
    private void locationprefselector() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Location Preference");
        alertDialogBuilder
                .setMessage("Select your location preference")
                .setCancelable(false)
                .setPositiveButton("Current", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myPreferenceManager.changeLocationpref(Integer.toString(1));
                        Log.d("TAG::", myPreferenceManager.getLocationpref());
                    }
                })
                .setNegativeButton("Stored", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myPreferenceManager.changeLocationpref(Integer.toString(0));
                        Log.d("TAG::", myPreferenceManager.getLocationpref());
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //Log.d("Locationpref::",myPreferenceManager.getLocationpref());
    }

    /**********
     * Profile Image
     *************/

    private class UserProfileTask extends AsyncTask<String, Void, String> {


      /*  @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DashboardActivity.this, "Loading",
                    "Please wait...", true);
            dialog.show();
        }*/

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

            if (result != null) {

                try {
                    arr = new JSONArray(result);

                    System.out.println(result);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        userName = obj.getString("Name");
                        img = obj.getString("Image");
                        address = obj.getString("Address");
                        profNameText.setText(userName);

                        Picasso.with(getApplicationContext()).load(img).into(profImage);
                        if (address.equalsIgnoreCase("")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    context);
                            alertDialogBuilder.setTitle("Profile Registration Is Required");
                            alertDialogBuilder
                                    .setMessage("Press Ok to Register Your Profile")
                                    .setCancelable(false);
                            alertDialogBuilder
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int Whichid) {
                                            YourAsyncTask pars = new YourAsyncTask();
                                            pars.execute();
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            System.out.println("---------------blood group--"
                                    + address
                            );
                        }
                        //    dialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //  dialog.dismiss();
                }
                //  dialog.dismiss();
            } else {
                //  Toast.makeText(getApplicationContext(), "Please update your profile ", Toast.LENGTH_LONG).show();
            }

        }
    }

    private class YourAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... args) {
            Intent intent = new Intent(getApplicationContext(), FirstProfileActivity.class);
            startActivity(intent);
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
        }
    }

    private class AdoptionAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... args) {
            Intent intent = new Intent(getApplicationContext(), AdoptionActivity.class);
            startActivity(intent);
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
        }
    }
}
