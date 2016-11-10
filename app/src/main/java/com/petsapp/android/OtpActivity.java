package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.petsapp.android.sessionLogin.LoginSessionManager;

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

public class OtpActivity extends AppCompatActivity {

    private static EditText otpText;
    JSONArray arr = null;
    Boolean isInternetPresent = false;
    String id, otpString;
    String name, email, idString;
    String split3;
    ConnectionDetector cd;
    LoginSessionManager loginSessionManager;
    private Button btpSubmitButton;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        otpText = (EditText) findViewById(R.id.otp_text_id);
        btpSubmitButton = (Button) findViewById(R.id.otp_button_id);

        btpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                otpString = otpText.getText().toString();

                loginSessionManager = new LoginSessionManager(getApplicationContext());

                HashMap<String, String> user = loginSessionManager.getUserDetails();

                idString = user.get(LoginSessionManager.KEY_ID);


                SignUpOtpTask task = new SignUpOtpTask();
                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/otp");
                } else {
                    //      Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void receivedSms(String message) {
        String str = message;
        String[] splited = str.split("\\s+");
        split3 = splited[3];
        otpText.setText(split3);
    }

    private class SignUpOtpTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            dialog = ProgressDialog.show(OtpActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&id=" + URLEncoder.encode(idString, "UTF-8") +
                        "&otp=" + URLEncoder.encode(otpString, "UTF-8");
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
                        // id = obj.getString("id");
                        //   Toast.makeText(getApplicationContext(), "Success==" + state, Toast.LENGTH_LONG).show();

                        Intent mainIntent = new Intent(getApplicationContext(), FirstProfileActivity.class);
                        startActivity(mainIntent);
                        dialog.dismiss();
                    } else {

                        //     Toast.makeText(getApplicationContext(), "Failed==" + state, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
