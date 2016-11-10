package com.petsapp.android;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.petsapp.android.app.Config;
import com.petsapp.android.gcm.GcmIntentService;
import com.petsapp.android.sessionLogin.LoginSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignupActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static EditText input;
    final Context context = this;
    GPSTracker gps;
    double latitude, longitude;
    String x = "0.0", y = "0.0";
    ConnectionDetector cd;
    String srt;
    String name, phone;
    JSONArray arr = null;
    String id;
    LoginSessionManager loginSessionManager;
    String split3;
    AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
    String token;
    Notification_count_session Notificationcountsession;
    private Button signupSubmitButton;
    private EditText nameText, phoneText;
    private Boolean isInternetPresent = false;
    private String urlParameters;
    private ProgressDialog dialog;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private String TAG = LoginActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#5b576e"));
        }

        try {
            DashboardActivity.fa.finish();
        } catch (Exception e) {
        }
        Notificationcountsession = new Notification_count_session(getApplicationContext());
        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(SignupActivity.this);
        } else {
            gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                x = Double.toString(latitude);
                y = Double.toString(longitude);
            } else {
                //  Toast.makeText(context, "GPS is not enabled. Please go to settings menu and enable it..", Toast.LENGTH_SHORT).show();
            }
        }
        signupSubmitButton = (Button) findViewById(R.id.signup_button_id);
        nameText = (EditText) findViewById(R.id.name_text_id);
        phoneText = (EditText) findViewById(R.id.phone_text_id);
        mAwesomeValidation.addValidation(this, R.id.phone_text_id, "^[1-9]([0-9]{1,45}$)", R.string.error_number);

        /*Gcm Start*/
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    token = intent.getStringExtra("token");

                    //        Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                }/* else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

          //          Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }*/
            }
        };
        if (checkPlayServices()) {
            registerGCM();
        }


        /*Gcm End*/

        signupSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                signupSubmitButton.startAnimation(animFadein);

                cd = new ConnectionDetector(getApplicationContext());
                if (mAwesomeValidation.validate()) {
                    if (nameText.getText().toString().length() == 0) {
                        nameText.setError("Please Enter Your Name");
                    } else if (phoneText.getText().toString().length() < 10) {
                        phoneText.setError("Please Enter Your Valid Mobile Number");
                    } else if (token == null) {

                        Toast.makeText(getApplicationContext(), "Internet is very slow, please try again", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(SignupActivity.this, LoginSignupActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showInterntOnlySettingsAlert(SignupActivity.this);
                        } else if (!com.petsapp.android.CommonUtilities
                                .isConnectingToInternet(getApplicationContext())) {
                            com.petsapp.android.CommonUtilities
                                    .showLocationSettingsAlert(SignupActivity.this);
                        } else {
                            name = nameText.getText().toString();
                            phone = phoneText.getText().toString();

                            SignUpTask task = new SignUpTask();
                            try {

                                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/signup?name=" + URLEncoder.encode(name, "UTF-8") + "&phone=" + URLEncoder.encode(phone, "UTF-8") + "&lat=" + URLEncoder.encode(x, "UTF-8") + "&lon=" + URLEncoder.encode(y, "UTF-8") + "&gcmid=" + URLEncoder.encode(token, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                      /*isInternetPresent = cd.isConnectingToInternet();
                     if (!isInternetPresent) {
                         showAlertDialog(SignupActivity.this, "No Internet Connection","You don't have internet connection.", false);
                       Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
                    }
                     else {

                       name = nameText.getText().toString();
                       phone = phoneText.getText().toString();

                       SignUpTask task = new SignUpTask();
                     try {

                         task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/signup?name=" + URLEncoder.encode(name, "UTF-8") + "&phone=" + URLEncoder.encode(phone, "UTF-8") + "&lat=" + URLEncoder.encode(x, "UTF-8") + "&lon=" + URLEncoder.encode(y, "UTF-8") + "&gcmid=" + URLEncoder.encode(token, "UTF-8")});
                     }   catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    }*/
                    }
                }
            }
        });
    }

    /*start gcm*/
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
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

    public void recivedSms(String message) {
        String str = message;
        String[] splited = str.split("\\s+");
        split3 = splited[3];
        input.setText(split3);
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

    public void support() {

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        String idstr = user.get(LoginSessionManager.KEY_ID);
        final Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("NAME_ID", idstr);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 05;
        sendBroadcast(alarmIntent);
        System.out.println("----" + idstr);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    private class SignUpTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
           /* dialog = ProgressDialog.show(SignupActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected String doInBackground(String... urls) {

            String response = "";
            try {
                URL url = new URL(urls[0]);
                System.out.println("URLS" + url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    sb.append(s + "\n");
                    response += s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("----------Response----------" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String state = obj.getString("status");
                    if (state.equals("success")) {
                        id = obj.getString("id");


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("OTP Number Verification");
                        alertDialogBuilder
                                .setMessage("Please Enter OTP Number...")
                                .setCancelable(false);
                        input = new EditText(context);
                        alertDialogBuilder.setView(input)
                                .setNeutralButton("Resend", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int Whichid) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (!com.petsapp.android.CommonUtilities
                                                .isConnectingToInternet(getApplicationContext())) {
                                            com.petsapp.android.CommonUtilities
                                                    .showInterntOnlySettingsAlert(SignupActivity.this);
                                        } else {
                                            srt = input.getEditableText().toString();
                                            SignUpOtpTask task = new SignUpOtpTask();
                                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/otp");
                                        }
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else if (state.equals("Already Registered")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);

                        alertDialogBuilder.setTitle("The Phone You Entered Is Already Registered");
                        alertDialogBuilder
                                .setMessage("Press Ok To Go Login Page")
                                .setCancelable(false);
                        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent ix = new Intent(getApplicationContext(), LoginActivity.class);

                                ix.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ix.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ix.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(ix);
                                finish();
                            }
                        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        //    Toast.makeText(getApplicationContext(),"Please Try After Sometime", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //  dialog.dismiss();
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

    private class SignUpOtpTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            dialog = ProgressDialog.show(SignupActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&id=" + URLEncoder.encode(id, "UTF-8") +
                        "&otp=" + URLEncoder.encode(srt, "UTF-8");
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
                    if (state.equals("success")) {
                        String coun = "0";
                        loginSessionManager = new LoginSessionManager(getApplicationContext());
                        loginSessionManager.createLoginSession(name, id);
                        support();
                        Notificationcountsession.createLoginSession(coun);
                        dialog.dismiss();


                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.dialog_register, null);
                        alertDialog.setView(dialogLayout);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface d) {
                                ImageView image = (ImageView) alertDialog.findViewById(R.id.goProDialogImage);
                                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.drawable.greentick);
                                float imageWidthInPX = (float) image.getWidth();
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                                        Math.round(imageWidthInPX * (float) icon.getHeight() / (float) icon.getWidth()));
                                image.setLayoutParams(layoutParams);
                            }
                        });

                        Intent mainIntent = new Intent(getApplicationContext(), FirstProfileActivity.class);
                        startActivity(mainIntent);
                        finish();

                         /*alert dialog*/

                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("OTP Number Verification");
                        alertDialogBuilder
                                .setMessage("Please Enter Correct OTP Number")
                                .setCancelable(false);
                        final EditText input = new EditText(context);
                        alertDialogBuilder.setView(input)
                                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int Whichid) {
                                        if (!com.petsapp.android.CommonUtilities
                                                .isConnectingToInternet(getApplicationContext())) {
                                            com.petsapp.android.CommonUtilities
                                                    .showInterntOnlySettingsAlert(SignupActivity.this);
                                        } else {
                                            String srt = input.getEditableText().toString();
                                            SignUpOtpTask task = new SignUpOtpTask();

                                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/otp");
                                        }
                                    }
                                })
                                .setNeutralButton("Resend", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        Toast.makeText(getApplicationContext(), "Please Enter Correct OTP Number", Toast.LENGTH_LONG).show();


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }
    }

}
