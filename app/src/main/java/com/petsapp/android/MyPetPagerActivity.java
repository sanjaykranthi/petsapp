package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.petsapp.android.Model.ItemObject1;
import com.petsapp.android.adapter.MyPetPagerAdapter;
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

public class MyPetPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapter adapter;
    JSONArray arr = null;
    String idString, categ;
    LoginSessionManager loginSessionManager;
    int pos1;
    ConnectionDetector cd;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Rpetlist.class);
                startActivity(i);
                finish();

            }
        });

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        Bundle extras = getIntent().getExtras();
        pos1 = extras.getInt("pos");

        AddTask task = new AddTask();
        task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getmates");


    }

    public class AddTask extends AsyncTask<String, String, List<ItemObject1>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MyPetPagerActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();

        }

        @Override
        protected List<ItemObject1> doInBackground(String... urls) {

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
                System.out.println("url List====" + url);
//
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }
                String finalJson = buffer.toString();
                System.out.println("final json---" + finalJson);
                List<ItemObject1> itemModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    ItemObject1 model = new ItemObject1();
                    model.setName(obj.getString("pname"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setWeight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setsize(obj.getString("size"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setPetmetId(obj.getString("petmateid"));
                    model.setAbout(obj.getString("about"));
                    model.setCategory(obj.getString("cat"));
                    model.setVacci(obj.getString("vac"));
                    model.setCrossbreed(obj.getString("crsbrdreqd"));
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
        protected void onPostExecute(List<ItemObject1> itemObjects) {
            super.onPostExecute(itemObjects);
            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new MyPetPagerAdapter(getApplicationContext(), itemObjects);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(pos1);
            dialog.dismiss();
        }
    }
}
