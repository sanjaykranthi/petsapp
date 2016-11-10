package com.petsapp.android;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray data_array = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONArray getJSONFromUrl(String targeturl) {

        try {
            URL url = new URL(targeturl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setUseCaches(false);
            con.setDoInput(true);

            InputStream is = con.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            data_array = new JSONArray(json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // return JSON String
        return data_array;
    }

    public JSONObject getJSONFromUrlobj(String targeturl) {

        try {
            URL url = new URL(targeturl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setUseCaches(false);
            con.setDoInput(true);

            InputStream is = con.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            jObj = new JSONObject(json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // return JSON String
        return jObj;
    }

	/*For chat changes*/

    public JSONArray getJSONFromUrl(String targeturl, String urlParameters) {

        try {
            URL url = new URL(targeturl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setDoInput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream is = con.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            data_array = new JSONArray(json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // return JSON String
        return data_array;
    }

}