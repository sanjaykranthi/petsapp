package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.petsapp.android.Model.Data;
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

public class PagerActivity extends AppCompatActivity {

    final Context context = this;
    ViewPager viewPager;
    PagerAdapter adapter;
    JSONArray arr = null;
    String idString, categ;
    LoginSessionManager loginSessionManager;
    int pos1;
    String brd = "", ageS = "", dist = "", gnd = "", ownerno, latval, lonval, lnfst = "", lnfst1 = "", pager;
    Button b1, b2;
    TextView dialogText;
    ConnectionDetector cd;
    ImageView intrest, intrestTickImg;
    String dists;
    float distsInt;
    int tab;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), FindLostFoundActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#dd8332"));
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        Bundle extras = getIntent().getExtras();
        pos1 = extras.getInt("pos");
        categ = extras.getString("categ");

        brd = extras.getString("brd");
        ageS = extras.getString("ageS");
        gnd = extras.getString("gnd");
        dist = extras.getString("dist");
        latval = extras.getString("lat");
        lonval = extras.getString("lon");
        //  lnfst = extras.getString("lnf");
        pager = extras.getString("pager");

       /* if (lnfst.equalsIgnoreCase("")) {

            lnfst1 = "";
        } else if (lnfst.equalsIgnoreCase("Missing")) {
            lnfst1 = "I lost my pet";
        } else {
            lnfst1 = "I found a lost pet";
        }*/


       /* if (lnfst.equalsIgnoreCase("Missing")) {

            lnfst1 = "I lost my pet";
        } else {
            lnfst1 = "I found a lost pet";
        }*/

      /*  try {
         //   distsInt = Integer.parseInt(dist);
            distsInt = Float.parseFloat(dist);

            if (distsInt < 11) {
                dists = "0";

            } else {

                dists = "0";
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }*/

        // if (dist.equalsIgnoreCase("")) {
        if (pager.equalsIgnoreCase("lfdist")) {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(PagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(PagerActivity.this);
                } else {
                    MateAddTask task2 = new MateAddTask();
                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddistance");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (pager.equalsIgnoreCase("lfcurrdist")) {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(PagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(PagerActivity.this);
                } else {

                    MateAddTask task2 = new MateAddTask();
                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfoundcurrentdistance");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(PagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(PagerActivity.this);
                } else {

                    ViewPagerTaskDetails task1 = new ViewPagerTaskDetails();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfounddetails");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


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

    public class ViewPagerTaskDetails extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(PagerActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();

        }

        @Override
        protected List<Data> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {


                urlParameters = "&categ=" + URLEncoder.encode(categ, "UTF-8");

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

                List<Data> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    /// String state = obj.getString("status");

                    Data model = new Data();
                    model.setpId(obj.getString("pid"));
                    model.setId(obj.getString("id"));
                    model.setName(obj.getString("petname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setcateg(obj.getString("category"));
                    model.setColor(obj.getString("color"));
                    model.setStreet(obj.getString("street"));
                    model.setArea(obj.getString("area"));
                    model.setAbout(obj.getString("about"));
                    model.setLostorfound(obj.getString("lostorfound"));
                    model.setThumbnailUrl(obj.getString("image"));
                    model.setPhoto(obj.getString("image"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setphone(obj.getString("phone"));
                    model.setDate(obj.getString("date"));
                    itemModelList.add(model);

                }
                return itemModelList;
//
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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Data> itemObjects) {
            super.onPostExecute(itemObjects);

            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new ViewPagerAdapter(getApplicationContext(), itemObjects);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(pos1);
            //
            dialog.dismiss();
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

    public class MateAddTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   dialog = ProgressDialog.show(PagerActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected List<Data> doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

//Changed*****
                urlParameters = "&id=" + URLEncoder.encode(idString, "UTF-8") +
                        "&category=" + URLEncoder.encode(categ, "UTF-8") +
                        "&breed=" + URLEncoder.encode(brd, "UTF-8") +
                        "&gender=" + URLEncoder.encode(gnd, "UTF-8") +
                        "&lat=" + URLEncoder.encode(latval, "UTF-8") +
                        "&lon=" + URLEncoder.encode(lonval, "UTF-8") +
                        "&lostorfound=" + URLEncoder.encode(lnfst1, "UTF-8");

                //"&dist=" + URLEncoder.encode(dists, "UTF-8") +
                // "&age=" + URLEncoder.encode(ageS, "UTF-8") +

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
                System.out.println("url List====" + url);
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                String finalJson = buffer.toString();
                List<Data> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Data model = new Data();
                    model.setpId(obj.getString("pid"));
                    model.setName(obj.getString("petname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setDate(obj.getString("date"));
                    model.setLostorfound(obj.getString("lostorfound"));
                    model.setcateg(obj.getString("category"));
                    model.setphone(obj.getString("phone"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setDist(obj.getString("distance"));
                    //    model.setDate(obj.getString("lfdate"));

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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Data> detailsModels) {
            super.onPostExecute(detailsModels);
            //  if (detailsModels != null) {
            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new ViewPagerAdapter(getApplicationContext(), detailsModels);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(pos1);
            //
            //  dialog.dismiss();
            //    }
        }
    }
 /*Adapter for lost and found*/

    public class ViewPagerAdapter extends PagerAdapter {


        ProgressDialog dialog;
        TextView dialogText;
        Boolean Isinternetpresent = false;
        ConnectionDetector cd;
        ImageView intrest, intrestTickImg;
        String urlParameters, ownerno;
        LoginSessionManager loginSessionManager;
        String idString, phone, pid;
        Button b1, b2;
        JSONArray arr = null;


        Context context;
        LayoutInflater inflater;
        private List<Data> itemList;

        public ViewPagerAdapter(Context context, List<Data> itemList) {

            this.itemList = itemList;
            this.context = context;
        }


        @Override
        public int getCount() throws NullPointerException {

            int sz = itemList.size();

            return sz;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            // Declare Variables
            TextView txtName;
            TextView txtNamePet;
            TextView txtBreed;
            TextView txtGender;
            TextView txtAbout;
            TextView txtDate;
            TextView txtArea;
            TextView txtCategory;
            final TextView txtStatus;
            final TextView txtPhone;
            final TextView txtPid;
            ImageView imgflag;
            final ImageView intrest, intrestTickImg;
            LinearLayout contactLayout;
            final ImageButton leftImg, rightImg;

            loginSessionManager = new LoginSessionManager(container.getContext());
            HashMap<String, String> user = loginSessionManager.getUserDetails();
            idString = user.get(LoginSessionManager.KEY_ID);


            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.viewpager_item, container,
                    false);


            txtName = (TextView) itemView.findViewById(R.id.petname);
            txtBreed = (TextView) itemView.findViewById(R.id.petbreed);
            txtGender = (TextView) itemView.findViewById(R.id.petgender);
            txtStatus = (TextView) itemView.findViewById(R.id.petStatus);
            txtNamePet = (TextView) itemView.findViewById(R.id.user_name_id);
            txtAbout = (TextView) itemView.findViewById(R.id.petAbout);
            txtPhone = (TextView) itemView.findViewById(R.id.phone);
            txtDate = (TextView) itemView.findViewById(R.id.petDate);
            txtArea = (TextView) itemView.findViewById(R.id.petArea);
            txtCategory = (TextView) itemView.findViewById(R.id.petCategory);
            txtPid = (TextView) itemView.findViewById(R.id.pid);
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            contactLayout = (LinearLayout) itemView.findViewById(R.id.contactLayout);
            leftImg = (ImageButton) itemView.findViewById(R.id.leftImage);
            rightImg = (ImageButton) itemView.findViewById(R.id.rightImage);

            txtName.setText(itemList.get(position).getName());
            txtBreed.setText(itemList.get(position).getBreed());
            txtGender.setText(itemList.get(position).getGender());
            txtStatus.setText(itemList.get(position).getLostorfound());
            txtNamePet.setText(itemList.get(position).getName());
            txtPhone.setText(itemList.get(position).getphone());
            txtAbout.setText(itemList.get(position).getAbout());
            txtDate.setText(itemList.get(position).getDate());
            txtArea.setText(itemList.get(position).getArea());
            txtCategory.setText(itemList.get(position).getcateg());
            txtPid.setText(itemList.get(position).getpId());

            Picasso.with(context).load(itemList.get(position).getphoto()).into(imgflag);


            String nameStr = txtName.getText().toString().trim();
            if (nameStr.equalsIgnoreCase("")) {
                txtName.setText("-");
            }

            String abtStr = txtAbout.getText().toString().trim();
            if (abtStr.equalsIgnoreCase("")) {
                txtAbout.setText("-");
            }

            String breedStr = txtBreed.getText().toString().trim();
            if (breedStr.equalsIgnoreCase("")) {
                txtBreed.setText("-");
            }

            String pidStr = txtPid.getText().toString();

            if (pidStr.equalsIgnoreCase(idString)) {

                contactLayout.setVisibility(View.INVISIBLE);

            }

            intrest = (ImageView) itemView.findViewById(R.id.call);
            intrestTickImg = (ImageView) itemView.findViewById(R.id.calltick);

            tab = viewPager.getCurrentItem();
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    tab = viewPager.getCurrentItem();
                    if (position == getCount() - 1) {
                        rightImg.setVisibility(View.GONE);
                    } else {
                        rightImg.setVisibility(View.VISIBLE);
                    }
                    if (position == 0) {
                        leftImg.setVisibility(View.GONE);
                    } else {
                        leftImg.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            if (tab == getCount() - 1) {
                rightImg.setVisibility(View.GONE);
            } else {
                rightImg.setVisibility(View.VISIBLE);
            }
            if (tab == 0) {
                leftImg.setVisibility(View.GONE);
            } else {
                leftImg.setVisibility(View.VISIBLE);
            }

            leftImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (tab > 0) {
                        tab--;
                        viewPager.setCurrentItem(tab);
                    } else if (tab == 0) {
                        viewPager.setCurrentItem(tab);
                        leftImg.setVisibility(View.GONE);
                    }
                }
            });

            rightImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // int tab = viewPager.getCurrentItem();
                    // tab = getCount();
                    if (tab == getCount() - 1) {
                        rightImg.setVisibility(View.GONE);
                    }

                    tab++;
                    viewPager.setCurrentItem(tab);
                    //   rightImg.setVisibility(View.GONE);
                }
            });


            intrest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    cd = new ConnectionDetector(context);
                    Isinternetpresent = cd.isConnectingToInternet();

                    try {

                        if (Isinternetpresent) {

                            //  String status = txtStatus.getText().toString();
                            phone = txtPhone.getText().toString();
                            pid = txtPid.getText().toString();
                            LoginTask1 task = new LoginTask1();
                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/invitation");


                        } else {
                            //  showAlertDialog(PagerActivity.this, "No Internet Connection", "You don't have internet connection.", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ScrollView) object);
        }
    /*AsyncTask*/

        private class interstTask extends AsyncTask<String, Void, String> {

      /*  @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(container.getContext(), "Sending request",
                    "Please wait...", true);
            dialog.show();
        }*/

            @Override
            protected String doInBackground(String... urls) {
                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&phone=" + URLEncoder.encode(phone, "UTF-8") + "&pid=" + URLEncoder.encode(idString, "UTF-8");
                    System.out.println("own---" + phone);
                    System.out.println("pid...." + idString);
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
//                dialog.dismiss();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    arr = new JSONArray(result);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String state = obj.getString("status");
                        if (state.equals("success")) {
                            intrestTickImg.setVisibility(View.VISIBLE);
                            intrest.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    /*AsyncTask*/

    /*Interest Button*/

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
                            "&invited_to=" + URLEncoder.encode(pid, "UTF-8") +
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
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
                            Intent i1 = new Intent(PagerActivity.this, ChatActivity.class);
                            i1.putExtra("msgby", invited_to);
                            i1.putExtra("nameVal", name);
                            i1.putExtra("phturl", url);

                            i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i1);
                            finish();
                          /*  AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
                            builder.setMessage("You have already sent a chat request.")
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
                            builder.setMessage("Please register a pet first.")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent(PagerActivity.this, LostFoundPetRegActivity.class);
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
}
