package com.petsapp.android;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Android on 21-03-2016.
 */
public class CustomerRequestservice extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private String URL_str = null;


    public CustomerRequestservice() {

        super("CustomerRequestservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        String responseMessage = "";
        URL url;
        HttpURLConnection connection = null;
        String requestString = intent.getStringExtra(REQUEST_STRING);
        String responseString = requestString;
        try {
            url = new URL(requestString);
            connection = (HttpURLConnection) url.openConnection();
            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            responseMessage = response.toString();

        } catch (Exception e) {

            e.printStackTrace();

        }


        /*Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ChatActivity.MyWebRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_STRING, responseString);
        broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
        sendBroadcast(broadcastIntent);*/

    }

}
