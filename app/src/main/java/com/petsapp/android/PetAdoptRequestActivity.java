package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PetAdoptRequestActivity extends AppCompatActivity {

    Button mate;

    String petn, petg, peta, petb, petw, pets, peth, petimg, petlat, petlon, petvac, petmateid,
            petcrs,
            petstate,
            petabout,
            petphone,
            petcat, petstate1;
    LoginSessionManager loginSessionManager;
    ImageView imagl;
    Bitmap bitmap;
    JSONArray arr = null;
    String idString;
    String encodedImage = "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";
    TextView petname1, pname, pcat, pgender, pweight, pheight, psize, pbreed, page;
    ConnectionDetector cd;
    Calendar c = Calendar.getInstance();
    String formateDate;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_adopt_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2b9a99"));
        }
        loginSessionManager = new LoginSessionManager(getApplicationContext());

        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);
        imagl = (ImageView) findViewById(R.id.imgs);
        Bundle extras = getIntent().getExtras();
        petn = extras.getString("petname");
        petg = extras.getString("gender");
        peta = extras.getString("age");
        petb = extras.getString("breed");
        petw = extras.getString("weight");
        peth = extras.getString("height");
        pets = extras.getString("size");
        petimg = extras.getString("imag");
        petlat = extras.getString("lat");
        petlon = extras.getString("lon");
        petvac = extras.getString("vac");
        petcrs = extras.getString("crsbrdreqd");
        petstate = extras.getString("pstate");
        petabout = extras.getString("about");
        petphone = extras.getString("phone");
        petcat = extras.getString("category");
        petmateid = extras.getString("petmateid");


        petname1 = (TextView) findViewById(R.id.user_name_id);
        pname = (TextView) findViewById(R.id.petname);
        page = (TextView) findViewById(R.id.petage);
        pbreed = (TextView) findViewById(R.id.petbreed);
        pgender = (TextView) findViewById(R.id.petgender);
        pweight = (TextView) findViewById(R.id.petweight);
        pheight = (TextView) findViewById(R.id.petheight);
        psize = (TextView) findViewById(R.id.petsize);

        mate = (Button) findViewById(R.id.matereqest);

        petname1.setText(petn);
        pname.setText(petn);
        page.setText(peta);
        pgender.setText(petg);
        pbreed.setText(petb);
        pweight.setText(petw);
        pheight.setText(peth);
        psize.setText(pets);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formateDate = df.format(c.getTime());

        if (petstate.equalsIgnoreCase("1")) {
            mate.setText("CANCEL REQUEST");
            petstate1 = "0";
        } else {
            mate.setText("REQUEST ADOPTION");
            petstate1 = "1";
        }
        Picasso.with(getApplicationContext()).load(petimg).into(imagl);

        mate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                if (Isinternetpresent) {

                    PetAdoptionTask task = new PetAdoptionTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/adoptionstateupdate");
                } else {
                    showAlertDialog(PetAdoptRequestActivity.this, "No Internet Connection", "You don't have internet connection.", false);
                }
            }
        });

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

    private class PetAdoptionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
            dialog = ProgressDialog.show(PetAdoptRequestActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();
        }


        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&pid=" + URLEncoder.encode(petmateid, "UTF-8") +
                        "&state=" + URLEncoder.encode(petstate1, "UTF-8") +
                        "&date=" + URLEncoder.encode(formateDate, "UTF-8");
                url = new URL(urls[0]);
                System.out.println("sending" + url);
                System.out.println("sending" + urlParameters);
                System.out.println("sending image  =" + encodedImage);
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
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);
                    String state = obj.getString("status");
                    if (state.contains("success")) {

                        //    Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), AddAdoptionActivity.class);
                        startActivity(intent);
                        finish();

                        dialog.dismiss();
                    } else {
                        //      Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
            dialog.dismiss();

        }
    }
}
