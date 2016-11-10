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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdoptionPagerActivity extends AppCompatActivity {

    final Context context = this;
    ViewPager viewPager;
    PagerAdapter adapter;
    JSONArray arr = null;
    String idString, categ;
    LoginSessionManager loginSessionManager;
    int pos1;
    ConnectionDetector cd;
    String dists;
    float distsInt;
    String brd, ageS, ageSM, dist, gnd, ownerno, latval, lonval, lnfst, lnfst1, pager;
    int tab;
    RadioGroup firstPetRgroup, vacciPetRgroup, allergicPetRgroup, financePetRgroup, livePetRgroup;
    RadioButton firstPetYes, firstPetNo, vacciPetYes, vacciPetNo, allergicPetYes, allergicPetNo, financePetYes, financePetNo, livePetYes, livePetNo;
    String firstPetStr, vacciPetStr, allergicPetStr, financePetStr, livePetStr;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), FindAdoptActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        Bundle extras = getIntent().getExtras();
        pos1 = extras.getInt("pos");
        categ = extras.getString("categ");
        brd = extras.getString("brd");
        ageS = extras.getString("ageS");
        ageSM = extras.getString("ageSM");
        gnd = extras.getString("gnd");
        dist = extras.getString("dist");
        latval = extras.getString("lat");
        lonval = extras.getString("lon");
        pager = extras.getString("pager");
       /* try {
            distsInt =Float.parseFloat(dist);

            if (distsInt < 11) {
                dists = "0";

            } else {

                dists = "11";
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }*/

        //   if (dist.equalsIgnoreCase("")) {
        if (pager.equalsIgnoreCase("adoptdist")) {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(AdoptionPagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AdoptionPagerActivity.this);
                } else {
                    MateAddTask task2 = new MateAddTask();
                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptdistance");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (pager.equalsIgnoreCase("adoptcurrdist")) {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(AdoptionPagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AdoptionPagerActivity.this);
                } else {
                    MateAddTask task2 = new MateAddTask();
                    task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcurrentdistance");
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(AdoptionPagerActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AdoptionPagerActivity.this);
                } else {

                    AdoptPagerTask task1 = new AdoptPagerTask();
                    task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getadoptcategory");
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

    public class AdoptPagerTask extends AsyncTask<String, String, List<Data>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            /*dialog = ProgressDialog.show(AdoptionPagerActivity.this, "Fetching Data",
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

                urlParameters = "&catg=" + URLEncoder.encode(categ, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(idString, "UTF-8");

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
//
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();
                List<Data> dataModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);
                JSONObject jObj = arr.getJSONObject(0);
                JSONArray childArray = jObj.getJSONArray("details");
                for (int i = 0; i < childArray.length(); i++) {
                    JSONObject obj = childArray.getJSONObject(i);
                    Data model = new Data();
                    model.setpId(obj.getString("pid"));
                    model.setName(obj.getString("pname"));
                    model.setBreed(obj.getString("breed"));
                    model.setGender(obj.getString("gender"));
                    model.setAge(obj.getString("age"));
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setsize(obj.getString("size"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setphone(obj.getString("phone"));
                    model.setDate(obj.getString("date"));
                    model.setcateg(obj.getString("cat"));
                    model.setAbout(obj.getString("about"));
                    categ = obj.getString("cat");

                    dataModelList.add(model);
                }

                return dataModelList;
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
        protected void onPostExecute(List<Data> detailsModels) {
            super.onPostExecute(detailsModels);

            if (detailsModels != null) {

                viewPager = (ViewPager) findViewById(R.id.pager);
                adapter = new AdoptionPagerAdapter(getApplicationContext(), detailsModels);

                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(pos1);
                //
                //   dialog.dismiss();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder.setTitle("No Any Data");
                alertDialogBuilder
                        .setMessage("")
                        .setCancelable(false);
                alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(getApplicationContext(), AdoptionActivity.class);
                        startActivity(i);

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
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
           /* dialog = ProgressDialog.show(AdoptionPagerActivity.this, "Fetching Data",
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

                urlParameters = "&pid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&categ=" + URLEncoder.encode(categ, "UTF-8") +
                        "&breed=" + URLEncoder.encode(brd, "UTF-8") +
                        "&age=" + URLEncoder.encode(ageS, "UTF-8") +
                        "&gnd=" + URLEncoder.encode(gnd, "UTF-8") +
                        "&lat=" + URLEncoder.encode(latval, "UTF-8") +
                        "&lon=" + URLEncoder.encode(lonval, "UTF-8");

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
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setAbout(obj.getString("about"));
                    model.setphone(obj.getString("phone"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setDate(obj.getString("adate"));
                    model.setcateg(obj.getString("cat"));

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
        protected void onPostExecute(List<Data> detailsModels) {
            super.onPostExecute(detailsModels);
            if (detailsModels != null) {
                viewPager = (ViewPager) findViewById(R.id.pager);
                adapter = new AdoptionPagerAdapter(getApplicationContext(), detailsModels);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(pos1);

                // dialog.dismiss();
            }
            //   dialog.dismiss();
        }
    }

    /***
     * Adapter Start
     ******/
    public class AdoptionPagerAdapter extends PagerAdapter {

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

        public AdoptionPagerAdapter(Context context, List<Data> itemList) {

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
            TextView txtAge;
            TextView txtCategory;
            TextView txtHeight;
            TextView txtWeight;
            TextView txtAbout;
            final TextView txtStatus;
            final TextView txtPhone;
            final TextView txtPid;
            ImageView imgflag;
            LinearLayout interestLayout;
            final ImageView intrest, intrestTickImg;
            final ImageButton leftImg, rightImg;


            loginSessionManager = new LoginSessionManager(container.getContext());
            HashMap<String, String> user = loginSessionManager.getUserDetails();
            idString = user.get(LoginSessionManager.KEY_ID);


            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.adoption_vpager_item, container,
                    false);


            txtName = (TextView) itemView.findViewById(R.id.petname);
            txtBreed = (TextView) itemView.findViewById(R.id.petbreed);
            txtGender = (TextView) itemView.findViewById(R.id.petgender);
            //  txtStatus = (TextView) itemView.findViewById(R.id.petStatus);
            txtNamePet = (TextView) itemView.findViewById(R.id.user_name_id);
            txtPhone = (TextView) itemView.findViewById(R.id.phone);
            txtAge = (TextView) itemView.findViewById(R.id.petAge);
            txtCategory = (TextView) itemView.findViewById(R.id.petCategory);
            txtHeight = (TextView) itemView.findViewById(R.id.petHeight);
            txtWeight = (TextView) itemView.findViewById(R.id.petWeight);
            txtAbout = (TextView) itemView.findViewById(R.id.petAbout);
            txtPid = (TextView) itemView.findViewById(R.id.pid);
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
            interestLayout = (LinearLayout) itemView.findViewById(R.id.interestLayout);
            leftImg = (ImageButton) itemView.findViewById(R.id.leftImage);
            rightImg = (ImageButton) itemView.findViewById(R.id.rightImage);

            txtName.setText(itemList.get(position).getName());
            txtBreed.setText(itemList.get(position).getBreed());
            txtGender.setText(itemList.get(position).getGender());
            txtAge.setText(itemList.get(position).getAge());
            //  txtStatus.setText(itemList.get(position).getLostorfound());
            txtNamePet.setText(itemList.get(position).getName());
            txtPhone.setText(itemList.get(position).getphone());
            txtCategory.setText(itemList.get(position).getcateg());
            txtHeight.setText(itemList.get(position).getheight());
            txtWeight.setText(itemList.get(position).getweight());
            txtAbout.setText(itemList.get(position).getAbout());
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

            String ageStr = txtAge.getText().toString().trim();
            if (ageStr.equalsIgnoreCase("")) {
                txtAge.setText("-");
            }


            String pidStr = txtPid.getText().toString();

            if (pidStr.equalsIgnoreCase(idString)) {

                interestLayout.setVisibility(View.INVISIBLE);

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
             /*   final Dialog dialog = new Dialog(container.getContext());
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("Message for pet");
                dialog.setCancelable(false);*/

                    cd = new ConnectionDetector(context);
                    Isinternetpresent = cd.isConnectingToInternet();
              /*  dialogText = (TextView) dialog.findViewById(R.id.textView1);
                dialogText.setText("Are you sure want to send a message?");

                b1 = (Button) dialog.findViewById(R.id.button1);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {*/
                    try {
                        if (Isinternetpresent) {
                            //    String status = txtStatus.getText().toString();
                            phone = txtPhone.getText().toString();
                            pid = txtPid.getText().toString();
                                /*AdoptioninterestTask task = new AdoptioninterestTask();
                                task.execute("http://patrimony.udyoog.com/pat.asmx/adoptsendsms");*/

                            LoginTask1 task = new LoginTask1();
                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/invitation");

                               /* intrestTickImg.setVisibility(View.VISIBLE);
                                intrest.setVisibility(View.GONE);*/
                        } else {
                            //  showAlertDialog(PagerActivity.this, "No Internet Connection", "You don't have internet connection.", false);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                 /*   dialog.cancel();
                }
            });
            b2=(Button)dialog.findViewById(R.id.button2);
            dialogText=(TextView)dialog.findViewById(R.id.textView1);
            b2.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                dialog.cancel();
            }
            }
            );
            dialog.show();*/
                }
            });

            container.
                    addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ScrollView) object);
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
                            "&invited_to=" + URLEncoder.encode(pid, "UTF-8") +
                            "&type=" + URLEncoder.encode("1", "UTF-8");

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

                            AlertDialog.Builder builder = new AlertDialog.Builder(AdoptionPagerActivity.this);
                            builder.setMessage("Your interest has been passed on to the pet owner")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Message for Adoption");
                            alert.show();


                        } else if (state.equals("Already Communication is Established")) {
                            String invited_by = obj.getString("invited_by");
                            String invited_to = obj.getString("invited_to");
                            String url = obj.getString("url");
                            String name = obj.getString("name");
                            Intent i1 = new Intent(AdoptionPagerActivity.this, ChatActivity.class);
                            i1.putExtra("msgby", invited_to);
                            i1.putExtra("nameVal", name);
                            i1.putExtra("phturl", url);

                            i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i1);
                            finish();

                           /* intrestTickImg.setVisibility(View.VISIBLE);
                            intrest.setVisibility(View.GONE);

*//*
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdoptionPagerActivity.this);
                            builder.setMessage("You have already sent a chat request.")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  alert.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Message for Adoption");
                            alert.show();*/
                        } else if (state.equals("Please Register Pet")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(AdoptionPagerActivity.this);
                            builder.setMessage("Please register a pet first.")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Intent i = new Intent(AdoptionPagerActivity.this, AdoptionPetRegActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            finish();

                                            //  alert.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Message for Adoption");
                            alert.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //    dialog.dismiss();
            }
        }

        private class AdoptioninterestTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&phone=" + URLEncoder.encode(phone, "UTF-8") + "&pid=" + URLEncoder.encode(idString, "UTF-8");
                    System.out.println("own---" + ownerno);
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
                    System.out.println("url====" + url);
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

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String state = obj.getString("status");
                        if (state.equals("success")) {
                           /* intrestTickImg.setVisibility(View.VISIBLE);
                            intrest.setVisibility(View.GONE);*/

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        private class InterestDialogTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... urls) {

                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8") +
                            "&oid=" + URLEncoder.encode(pid, "UTF-8") +
                            "&q1=" + URLEncoder.encode(firstPetStr, "UTF-8") +
                            "&q2=" + URLEncoder.encode(vacciPetStr, "UTF-8") +
                            "&q3=" + URLEncoder.encode(allergicPetStr, "UTF-8") +
                            "&q4=" + URLEncoder.encode(financePetStr, "UTF-8") +
                            "&q5=" + URLEncoder.encode(livePetStr, "UTF-8");

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
                            Toast.makeText(AdoptionPagerActivity.this, "Succeess", Toast.LENGTH_SHORT).show();
                        } else if (state.equalsIgnoreCase("Already liked")) {
                            Toast.makeText(AdoptionPagerActivity.this, "Already send a request", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
