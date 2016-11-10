package com.petsapp.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AlarmReceiver extends BroadcastReceiver {
    GPSTracker gps;
    double latitude, longitude;
    String passwd, x, y;

    @Override
    public void onReceive(Context context, Intent intent) {
        passwd = intent.getStringExtra("NAME_ID");
        System.out.println("--got id--" + passwd);
        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            x = Double.toString(latitude);
            y = Double.toString(longitude);
        } else {
            Toast.makeText(context, "GPS is not enabled. Please go to settings menu and enable it..", Toast.LENGTH_SHORT).show();
        }
        DownloadWebPageTask task = new DownloadWebPageTask();

        try {
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/updatelatlon?uid=" + passwd + "&clat=" + URLEncoder.encode(Double.toString(latitude), "UTF-8") + "&clon=" + URLEncoder.encode(Double.toString(longitude), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
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
            if (result.contains("success")) {
                gps.stopUsingGPS();
                System.out.println("----------GPS stop-----" + result);
            } else {
                System.out.println("----------GPS error-----" + result);
            }
        }
    }
}