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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.Model.PetBookData;
import com.petsapp.android.adapter.PetBookRecyclerAdapter;
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

public class InfoActivity extends AppCompatActivity {

    public static String PNAME = "PNAME";
    public static String DETAILS = "DETAILS";
    public static String ID = "ID";
    public static String IMAGES = "IMAGES";
    final Context context = this;
    ImageView uploadImg;
    ConnectionDetector cd;
    JSONArray arr = null;
    PetBookRecyclerAdapter adapter;
    LoginSessionManager loginSessionManager;
    String idString;
    TextView nodatapetbook;
    RecyclerView rview;
    GridView lview;


    List<PetBookData> donorlist;
    ArrayList<HashMap<String, String>> fillMaps = null;
    HashMap<String, String> map = null;
    petbooklistsListAdapter adapter1;
    private Boolean Isinternetpresent = false;
    private ProgressDialog dialog;
    private GridLayoutManager lLayout;
    private String urlParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
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

//        nodatapetbook = (TextView) findViewById(R.id.nodatapetbook);
//        nodatapetbook.setVisibility(View.GONE);
        rview = (RecyclerView) findViewById(R.id.petbook_recyclr);
        lview = (GridView) findViewById(R.id.listviewpetbook);
        loginSessionManager = new LoginSessionManager(getApplicationContext());

        uploadImg = (ImageView) findViewById(R.id.upload_details_id);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InfoActivity.this, PetBookPetRegActivity.class);
                startActivity(i);
            }
        });

        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();
        if (loginSessionManager.checkLogin()) {
            finish();
        }

        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);
        if (Isinternetpresent) {

/*
            donorlist = CommonUtilities.petbooks;

            fillMaps = new ArrayList<HashMap<String, String>>();
            if(donorlist.size()>0) {
                for (int i = 0; i < donorlist.size(); i++) {
                    //        Toast.makeText(getApplicationContext(), "before:" + donorlist.get(i).getName(), Toast.LENGTH_LONG).show();
                    if (donorlist.get(i).getId() != null) {
                        rview.setVisibility(View.GONE);
                        lview.setVisibility(View.VISIBLE);
                       *//* mSwipeRefreshLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout1.setVisibility(View.VISIBLE);*//*
                        map = new HashMap<String, String>();
                        map.put(PNAME, donorlist.get(i).name);
                        map.put(DETAILS, donorlist.get(i).details);
                        map.put(ID, donorlist.get(i).id);
                        map.put(IMAGES, donorlist.get(i).photo);

                        fillMaps.add(map);

                    }

                }
            }
            else {
                rview.setVisibility(View.VISIBLE);
                lview.setVisibility(View.GONE);
               *//* mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout1.setVisibility(View.GONE);*//*
                PetBookTask task = new PetBookTask();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getpetsbook");
            }
            adapter1 = new petbooklistsListAdapter(this, fillMaps, R.layout.petbooklayout, new String[]{}, new int[]{});


            lview.setAdapter(adapter1);*/

            PetBookTask task = new PetBookTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getpetsbook");


        } else {
            showAlertDialog(InfoActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }

    }

    /*****
     * Ends the listview of offline code
     ****/

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
    public void onBackPressed() {
        Intent i = new Intent(InfoActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /*****
     * Started Listview of Offline code
     ******/

    public class petbooklistsListAdapter extends ArrayAdapter<HashMap<String, String>> {
        final Context context;
        public String PNAME = "PNAME";
        public String ID = "ID";
        public String DETAILS = "DETAILS";
        public String IMAGES = "IMAGES";
        String stateval, userid, petmid;
        HashMap<String, String> currentData;
        private List<HashMap<String, String>> items;
        private int viewId;

        public petbooklistsListAdapter(Context context, ArrayList<HashMap<String, String>> fillMaps, int layout, String[] from, int[] to) {
            super(context, layout, fillMaps);
            this.context = context;
            this.viewId = layout;
            this.items = fillMaps;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View vix = convertView;
            if (convertView == null) {
                if (vix == null) {
                    LayoutInflater vi = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = vi.inflate(R.layout.petbooklayout, null);
                }
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.pet_name);
                holder.details = (TextView) convertView.findViewById(R.id.pet_details);
                holder.id = (TextView) convertView.findViewById(R.id.pet_id);
                holder.imgText = (TextView) convertView.findViewById(R.id.pet_img_text);
                holder.photo = (ImageView) convertView.findViewById(R.id.pet_img);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            currentData = new HashMap<String, String>();
            currentData = items.get(position);
            if (currentData != null) {


                Picasso.with(context).load(currentData.get(IMAGES)).into(holder.photo);
                holder.name.setText(currentData.get(PNAME));
                holder.details.setText(currentData.get(DETAILS));
                holder.id.setText(currentData.get(ID));
                holder.imgText.setText(currentData.get(IMAGES));


            }


            return convertView;
        }

        public class ViewHolder {
            TextView name, details, id, imgText;

            ImageView photo;

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

    public class PetBookTask extends AsyncTask<String, String, List<PetBookData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog = ProgressDialog.show(InfoActivity.this, "Getting Data",
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
                //Send request

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

                List<PetBookData> itemModelList = new ArrayList<>();
                CommonUtilities.petbooks.clear();
                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    PetBookData model = new PetBookData();

                    model.setName(obj.getString("petname"));
                    model.setDetails(obj.getString("details"));
                    model.setId(obj.getString("id"));
                    model.setPhoto(obj.getString("image"));

                    itemModelList.add(model);
                    CommonUtilities.petbooks.add(model);
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
        protected void onPostExecute(List<PetBookData> itemObjects) {
            super.onPostExecute(itemObjects);
            if (itemObjects != null && itemObjects.size() > 0) {
                lLayout = new GridLayoutManager(InfoActivity.this, 2);
//                nodatapetbook.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                //  mSharedPreference = getApplicationContext().getSharedPreferences("AdoptPrefs", Context.MODE_PRIVATE);
                rview.setHasFixedSize(true);
                rview.setLayoutManager(lLayout);
                adapter = new PetBookRecyclerAdapter(getApplicationContext(), itemObjects);
                rview.setAdapter(adapter);
                //   dialog.dismiss();
            } else {
//                nodatapetbook.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
            }
            //  dialog.dismiss();
        }
    }
}
