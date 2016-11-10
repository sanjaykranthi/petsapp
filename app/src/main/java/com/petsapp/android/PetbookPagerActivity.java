package com.petsapp.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.petsapp.android.Model.PetBookData;
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

public class PetbookPagerActivity extends AppCompatActivity {

    final Context context = this;
    ViewPager viewPager;
    BookPagerAdapter adapter;
    JSONArray arr = null;
    // TextView txtLikes;
    String idString, categ;
    LoginSessionManager loginSessionManager;
    int pos1, pos11;
    ConnectionDetector cd;
    int tab;
    private ProgressDialog dialog;
    private Boolean Isinternetpresent = false;
    private String urlParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petbook_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), InfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#4e4b92"));
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        try {
            Bundle extras = getIntent().getExtras();
            pos1 = extras.getInt("pos");
        } catch (NullPointerException e) {
            //   Toast.makeText(PetbookPagerActivity.this, "Exception" + e, Toast.LENGTH_SHORT).show();
        }
        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(PetbookPagerActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(PetbookPagerActivity.this);
        } else {
            PetBookPagerTask task1 = new PetBookPagerTask();
            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getpetsbook");
        }

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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
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

    public class PetBookPagerTask extends AsyncTask<String, String, List<PetBookData>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
         /*   dialog = ProgressDialog.show(PetbookPagerActivity.this, "Getting Data",
                    "Please wait...", true);
            dialog.show();*/
            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();

        }

        @Override
        protected List<PetBookData> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8");

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

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }
                String finalJson = buffer.toString();
                List<PetBookData> dataModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    PetBookData model = new PetBookData();


                    model.setName(obj.getString("petname"));
                    model.setDetails(obj.getString("details"));
                    model.setId(obj.getString("id"));
                    model.setPhoto(obj.getString("image"));
                    model.setLikes(obj.getString("likes"));
                    model.setUserLiked(obj.getString("liked"));

                    String test = obj.getString("liked");

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
        protected void onPostExecute(List<PetBookData> detailsModels) {
            super.onPostExecute(detailsModels);

            if (detailsModels != null) {

                viewPager = (ViewPager) findViewById(R.id.pager);
                adapter = new BookPagerAdapter(getApplicationContext(), detailsModels);

                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(pos1);

                //    dialog.dismiss();
            } else {
               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
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
                alertDialog.show();*/
            }
        }
    }

    public class PetBookPagerTask1 extends AsyncTask<String, String, List<PetBookData>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
         /*   dialog = ProgressDialog.show(PetbookPagerActivity.this, "Getting Data",
                    "Please wait...", true);
            dialog.show();*/
        }

        @Override
        protected List<PetBookData> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8");

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

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }
                String finalJson = buffer.toString();
                List<PetBookData> dataModelList = new ArrayList<>();
                arr = new JSONArray(finalJson);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    PetBookData model = new PetBookData();

                    model.setName(obj.getString("petname"));
                    model.setDetails(obj.getString("details"));
                    model.setId(obj.getString("id"));
                    model.setPhoto(obj.getString("image"));
                    model.setLikes(obj.getString("likes"));
                    model.setUserLiked(obj.getString("liked"));

                    String test = obj.getString("liked");

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
        protected void onPostExecute(List<PetBookData> detailsModels) {
            super.onPostExecute(detailsModels);

            if (detailsModels != null) {

                viewPager = (ViewPager) findViewById(R.id.pager);
                adapter = new BookPagerAdapter(getApplicationContext(), detailsModels);

                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(pos11);

                //    dialog.dismiss();
            } else {
               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
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
                alertDialog.show();*/
            }
        }
    }

    /*****
     * Adapter
     ***/

    public class BookPagerAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        String urlParameters;
        LoginSessionManager loginSessionManager;
        String idString, pidString;
        String positio;
        JSONArray arr = null;
        Activity activity;
        ConnectionDetector cd;
        String uLikes;
        TextView txtLikes;
        private List<PetBookData> itemList;
        private Boolean Isinternetpresent = false;

        public BookPagerAdapter(Context context, List<PetBookData> itemList) {

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

            TextView txtName;
            TextView txtDetails;

            final TextView txtId;
            final TextView txtUserLikes;
            ImageView imgflag, likeImg;
            final ImageButton leftImg, rightImg;

            loginSessionManager = new LoginSessionManager(container.getContext());
            HashMap<String, String> user = loginSessionManager.getUserDetails();
            idString = user.get(LoginSessionManager.KEY_ID);

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.petbook_pager_view, container,
                    false);


            txtName = (TextView) itemView.findViewById(R.id.pager_petname);
            txtDetails = (TextView) itemView.findViewById(R.id.pager_petdetails);
            txtId = (TextView) itemView.findViewById(R.id.pager_id);
            imgflag = (ImageView) itemView.findViewById(R.id.pager_img);
            likeImg = (ImageView) itemView.findViewById(R.id.pager_petlike);
            txtLikes = (TextView) itemView.findViewById(R.id.likes_no);
            txtUserLikes = (TextView) itemView.findViewById(R.id.pager_ulikes);
            leftImg = (ImageButton) itemView.findViewById(R.id.leftImage);
            rightImg = (ImageButton) itemView.findViewById(R.id.rightImage);

            txtName.setText(itemList.get(position).getName());
            txtDetails.setText(itemList.get(position).getDetails());
            txtId.setText(itemList.get(position).getId());
            txtLikes.setText(itemList.get(position).getLikes());
            txtUserLikes.setText(itemList.get(position).getUserLiked());
            Picasso.with(context).load(itemList.get(position).getphoto()).into(imgflag);

            uLikes = txtUserLikes.getText().toString();

            /*Like button satus 0/1*/
            if (uLikes.equalsIgnoreCase("1")) {

                likeImg.setBackgroundResource(R.drawable.heart_like1);

            } else if (uLikes.equalsIgnoreCase("0")) {

                likeImg.setBackgroundResource(R.drawable.heart_like);
            }
/*View pager*/
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

/*Ends View pager*/
  /*Like button satus 0/1*/
            likeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewPager.getCurrentItem();
                    positio = String.valueOf(position);
                    cd = new ConnectionDetector(context);

                    pidString = txtId.getText().toString();
                    //     uLikes = txtUserLikes.getText().toString();
                    if (!com.petsapp.android.CommonUtilities
                            .isConnectingToInternet(getApplicationContext())) {
                        com.petsapp.android.CommonUtilities
                                .showInterntOnlySettingsAlert(PetbookPagerActivity.this);
                    } else if (!com.petsapp.android.CommonUtilities
                            .isConnectingToInternet(getApplicationContext())) {
                        com.petsapp.android.CommonUtilities
                                .showLocationSettingsAlert(PetbookPagerActivity.this);
                    } else {
                        LikeTask task = new LikeTask();
                        task.execute("http://petsapp.petsappindia.co.in/pat.asmx/addlikes");
                    }
                }
            });

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ScrollView) object);

            /*String value = (txtLikes.getText().toString())+1;
            txtLikes.setText(value);*/


        }

        private class LikeTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&petid=" + URLEncoder.encode(pidString, "UTF-8") +
                            "&uid=" + URLEncoder.encode(idString, "UTF-8");

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
                super.onPostExecute(result);
                try {
                    arr = new JSONArray(result);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String state = obj.getString("status");
                        if (state.equalsIgnoreCase("Liked")) {

                            Toast.makeText(PetbookPagerActivity.this, "Liked", Toast.LENGTH_SHORT).show();
                           /* Intent i1 = new Intent(PetbookPagerActivity.this, PetbookPagerActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i1.putExtra("pos", pos1);
                            startActivity(i1);
                            finish();*/
                            pos11 = Integer.parseInt(positio);
                            PetBookPagerTask1 task1 = new PetBookPagerTask1();
                            task1.execute("http://petsapp.petsappindia.co.in/pat.asmx/getpetsbook");

                        } else {
                            Toast.makeText(PetbookPagerActivity.this, "Already Liked", Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
