package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.petsapp.android.Model.ItemObject;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class AddPetMateActivity extends AppCompatActivity {

    public static String PNAME = "PNAME";
    public static String BREED = "BREED";
    public static String GENDER = "GENDER";
    public static String AGE = "AGE";
    public static String CAT = "CAT";
    public static String WEIGHT = "WEIGHT";
    public static String HEIGHT = "HEIGHT";
    public static String SIZE = "SIZE";
    public static String VAC = "VAC";
    public static String CRSBRDREQD = "CRSBRDREQD";
    public static String PHONE = "PHONE";
    public static String LAT = "LAT";
    public static String LON = "LON";
    public static String ABOUT = "ABOUT";
    public static String PSTATE = "PSTATE";
    public static String PETMATEID = "PETMATEID";
    public static String IMAGES = "IMAGES";
    final Context context = this;
    Button addreg;
    TextView petname;
    String pet;
    Button upload;
    RecyclerView rview;
    SharedPreferences mSharedPreference;
    JSONArray arr = null;
    String profileString;
    LoginSessionManager session;
    LoginSessionManager loginSessionManager;
    String idString;
    String statestatus, statev;
    String mate = "MATE";
    String remove = "REMOVE";
    ConnectionDetector cd;
    Calendar c = Calendar.getInstance();
    String formateDate, pmId;
    TextView nodataaddpetmate;
    List<ItemObject> donorlist;
    ArrayList<HashMap<String, String>> fillMaps = null;
    HashMap<String, String> map = null;
    RegplaydatepetsListAdapter adapter;
    ListView lview;
    private GridLayoutManager flayout;
    private ProgressDialog dialog;
    private String urlParameters;
    private Bitmap bitmap, bmp;
    private Uri filePath;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet_mate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), HomeMateActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());

        HashMap<String, String> user = loginSessionManager.getUserDetails();

        idString = user.get(LoginSessionManager.KEY_ID);
        rview = (RecyclerView) findViewById(R.id.recycle2);
        lview = (ListView) findViewById(R.id.listviewpd);


//        nodataaddpetmate = (TextView) findViewById(R.id.nodataaddpetmate);
//        nodataaddpetmate.setVisibility(View.GONE);
        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(AddPetMateActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(AddPetMateActivity.this);
        } else {
            donorlist = CommonUtilities.playdatereglist;

            fillMaps = new ArrayList<HashMap<String, String>>();
            if (donorlist.size() > 0) {
                for (int i = 0; i < donorlist.size(); i++) {
                    //      Toast.makeText(getApplicationContext(), "before:" + donorlist.get(i).getName(), Toast.LENGTH_LONG).show();
                    if (donorlist.get(i).getbreed() != null) {
                        rview.setVisibility(View.GONE);
                        lview.setVisibility(View.VISIBLE);

                        map = new HashMap<String, String>();
                        map.put(PNAME, donorlist.get(i).name);
                        map.put(BREED, donorlist.get(i).breed);
                        map.put(GENDER, donorlist.get(i).gender);
                        map.put(AGE, donorlist.get(i).age);
                        map.put(CAT, donorlist.get(i).category);
                        map.put(WEIGHT, donorlist.get(i).weight);
                        map.put(HEIGHT, donorlist.get(i).height);
                        map.put(SIZE, donorlist.get(i).size);
                        map.put(VAC, donorlist.get(i).vac);
                        map.put(CRSBRDREQD, donorlist.get(i).crsbrdreqd);
                        map.put(PHONE, donorlist.get(i).phone);
                        map.put(LAT, donorlist.get(i).lat);
                        map.put(LON, donorlist.get(i).lon);
                        map.put(ABOUT, donorlist.get(i).about);
                        map.put(PSTATE, donorlist.get(i).pstate);
                        map.put(PETMATEID, donorlist.get(i).petmateid);
                        map.put(IMAGES, donorlist.get(i).photo);

                        fillMaps.add(map);

                    }

                }
            } else {
                rview.setVisibility(View.VISIBLE);
                lview.setVisibility(View.GONE);

                MateAddTask task = new MateAddTask();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getmates");
            }
            adapter = new RegplaydatepetsListAdapter(this, fillMaps, R.layout.customplaydate, new String[]{}, new int[]{});
            lview.setAdapter(adapter);

        }

        upload = (Button) findViewById(R.id.add);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showInterntOnlySettingsAlert(AddPetMateActivity.this);
                } else if (!com.petsapp.android.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.petsapp.android.CommonUtilities
                            .showLocationSettingsAlert(AddPetMateActivity.this);
                } else {
                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                    upload.startAnimation(animFadein);
                    Intent i = new Intent(getApplicationContext(), PetMateRegisterActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });


    }

    /*****
     * Ends the listview of offline code
     ****/


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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddPetMateActivity.this, HomeMateActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /*****
     * Started Listview of Offline code
     ******/

    public class RegplaydatepetsListAdapter extends ArrayAdapter<HashMap<String, String>> {
        final Context context;
        public String PNAME = "PNAME";
        public String BREED = "BREED";
        public String GENDER = "GENDER";
        public String AGE = "AGE";
        public String CAT = "CAT";
        public String WEIGHT = "WEIGHT";
        public String HEIGHT = "HEIGHT";
        public String SIZE = "SIZE";
        public String VAC = "VAC";
        public String CRSBRDREQD = "CRSBRDREQD";
        public String PHONE = "PHONE";
        public String LAT = "LAT";
        public String LON = "LON";
        public String ABOUT = "ABOUT";
        public String PSTATE = "PSTATE";
        public String PETMATEID = "PETMATEID";
        public String IMAGES = "IMAGES";
        String stateval, userid, petmid;
        HashMap<String, String> currentData;
        private List<HashMap<String, String>> items;
        private int viewId;

        public RegplaydatepetsListAdapter(Context context, ArrayList<HashMap<String, String>> fillMaps, int layout, String[] from, int[] to) {
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
                    convertView = vi.inflate(R.layout.customplaydate, null);
                }
                holder = new ViewHolder();
                holder.petname = (TextView) convertView.findViewById(R.id.name);

                holder.petname = (TextView) convertView.findViewById(R.id.name);
                holder.icons = (ImageView) convertView.findViewById(R.id.icons);
                holder.age = (TextView) convertView.findViewById(R.id.age);
                holder.petmateid = (TextView) convertView.findViewById(R.id.petmateid);
                holder.category = (TextView) convertView.findViewById(R.id.cate);
                holder.weigh = (TextView) convertView.findViewById(R.id.weigh);
                holder.heigh = (TextView) convertView.findViewById(R.id.height);
                holder.siz = (TextView) convertView.findViewById(R.id.siz);
                holder.vac = (TextView) convertView.findViewById(R.id.vac);
                holder.crsbrdreqd = (TextView) convertView.findViewById(R.id.crsbrdreqd);
                holder.about = (TextView) convertView.findViewById(R.id.about);
                holder.pstate = (TextView) convertView.findViewById(R.id.pstate);
                holder.laat = (TextView) convertView.findViewById(R.id.laat);
                holder.loon = (TextView) convertView.findViewById(R.id.loon);
                holder.phone = (TextView) convertView.findViewById(R.id.phone);
                holder.photo = (TextView) convertView.findViewById(R.id.photo);
                holder.gender = (TextView) convertView.findViewById(R.id.gender);
                holder.breed = (TextView) convertView.findViewById(R.id.breed);
                holder.yrs = (TextView) convertView.findViewById(R.id.second);
                holder.done = (ImageView) convertView.findViewById(R.id.done);

                holder.right = (Button) convertView.findViewById(R.id.right2);
                holder.remove = (Button) convertView.findViewById(R.id.remove2);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            currentData = new HashMap<String, String>();
            currentData = items.get(position);
            if (currentData != null) {
                holder.petname.setText(currentData.get(PNAME));
                Picasso.with(context).load(currentData.get(IMAGES)).into(holder.icons);


                holder.age.setText(currentData.get(AGE));
                holder.gender.setText(currentData.get(GENDER));
                holder.breed.setText(currentData.get(BREED));
                holder.weigh.setText(currentData.get(WEIGHT));
                holder.heigh.setText(currentData.get(HEIGHT));
                holder.siz.setText(currentData.get(SIZE));
                holder.phone.setText(currentData.get(PHONE));
                holder.photo.setText(currentData.get(IMAGES));
                holder.vac.setText(currentData.get(VAC));
                holder.crsbrdreqd.setText(currentData.get(CRSBRDREQD));
                holder.category.setText(currentData.get(CAT));
                holder.about.setText(currentData.get(ABOUT));
                holder.pstate.setText(currentData.get(PSTATE));
                holder.laat.setText(currentData.get(LAT));
                holder.loon.setText(currentData.get(LON));
                holder.petmateid.setText(currentData.get(PETMATEID));
                //     holder.mate.setText(itemList.get(position).gettextname());

                String state1 = holder.pstate.getText().toString();
                String age1 = holder.age.getText().toString();


                if (state1.equalsIgnoreCase("0")) {

                    holder.right.setBackgroundResource(R.drawable.done_img);
                    holder.remove.setBackgroundResource(R.drawable.mt_rmv_clr);
                    holder.right.setEnabled(true);
                    holder.remove.setEnabled(false);


                }
                if (state1.equalsIgnoreCase("1")) {


                    holder.right.setBackgroundResource(R.drawable.mt_done_clr);
                    holder.remove.setBackgroundResource(R.drawable.remove_img);
                    holder.right.setEnabled(false);
                    holder.remove.setEnabled(true);

                }

                if (age1.equalsIgnoreCase("")) {
                    holder.yrs.setVisibility(View.GONE);
                } else {
                    holder.yrs.setVisibility(View.VISIBLE);
                }

            }
            final ViewHolder finalHolder = holder;
            holder.right.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    petmid = finalHolder.petmateid.getText().toString();
                    stateval = finalHolder.pstate.getText().toString();

                    if (stateval.equalsIgnoreCase("0")) {

                        statev = "1";
                        showAddDialog(statev, "Confirm Registration?");

                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    petmid = finalHolder.petmateid.getText().toString();
                    stateval = finalHolder.pstate.getText().toString();

                    if (stateval.equalsIgnoreCase("1")) {
                        statev = "0";
                        showRemoveDialog(statev, "Confirm Removal?");

                    }
                }
            });


            return convertView;
        }

        public void showAddDialog(String statev, String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AddPetMateActivity.this);
            // builder.setIcon(R.drawable.mt_done_clr);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!com.petsapp.android.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.petsapp.android.CommonUtilities
                                        .showInterntOnlySettingsAlert(AddPetMateActivity.this);
                            } else if (!com.petsapp.android.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.petsapp.android.CommonUtilities
                                        .showLocationSettingsAlert(AddPetMateActivity.this);
                            } else {
                                new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/stateupdate");
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            //   alert.setTitle("Pet Mate");
            alert.show();
        }

        public void showRemoveDialog(String statev, String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AddPetMateActivity.this);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!com.petsapp.android.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.petsapp.android.CommonUtilities
                                        .showInterntOnlySettingsAlert(AddPetMateActivity.this);
                            } else if (!com.petsapp.android.CommonUtilities
                                    .isConnectingToInternet(getApplicationContext())) {
                                com.petsapp.android.CommonUtilities
                                        .showLocationSettingsAlert(AddPetMateActivity.this);
                            } else {
                                new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/stateupdate");
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            //   alert.setTitle("Pet Mate");
            alert.show();
        }

        public class ViewHolder {
            public int position;
            public TextView petname, uid, petmateid, mate, age, gender, breed, category, weigh, heigh, siz, vac, crsbrdreqd, about, pstate, laat, loon, phone, photo, yrs;
            public int previous = -1;
            public ImageView done;
            public String a;
            ImageView icons;
            Button upload, right, remove;

        }

        private class PetRegisterTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(String... urls) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                formateDate = df.format(c.getTime());

                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&petmateid=" + URLEncoder.encode(petmid, "UTF-8") +
                            "&st=" + URLEncoder.encode(statev, "UTF-8") +
                            "&date=" + URLEncoder.encode(formateDate, "UTF-8") +
                            "&uid=" + URLEncoder.encode(idString, "UTF-8");
                    url = new URL(urls[0]);
                    System.out.println("sending" + url);
                    System.out.println("sending" + urlParameters);
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
                }
            }

            @Override
            protected void onPostExecute(String result) {
                String mate = "MATE";
                String remove = "REMOVE";
                System.out.println("staus----" + result);
                try {
                    List<ItemObject> itemModelList = new ArrayList<>();
                    itemModelList.clear();
                    CommonUtilities.playdatereglist.clear();
                    arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        System.out.println("--" + arr);
                        JSONObject obj = arr.getJSONObject(i);
                        ItemObject model = new ItemObject();

                        model.setName(obj.getString("pname"));
                        model.setbreed(obj.getString("breed"));
                        model.setgender(obj.getString("gender"));
                        model.setage(obj.getString("age"));
                        model.setcategory(obj.getString("cat"));
                        model.setweight(obj.getString("weight"));
                        model.setheight(obj.getString("height"));
                        model.setsize(obj.getString("size"));
                        model.setvac(obj.getString("vac"));
                        model.setcrsbrdreqd(obj.getString("crsbrdreqd"));
                        model.setphone(obj.getString("phone"));
                        model.setlat(obj.getString("lat"));
                        model.setlon(obj.getString("lon"));
                        model.setabout(obj.getString("about"));
                        model.setpstate(obj.getString("pstate"));
                        model.setpetmateid(obj.getString("petmateid"));
                        model.setThumbnailUrl(obj.getString("images"));
                        model.setPhoto(obj.getString("images"));
                        statestatus = obj.getString("pstate");
                        if (statestatus.equalsIgnoreCase("0")) {
                            model.settextname(mate);
                        } else {
                            model.settextname(remove);
                        }

                        String pName = obj.getString("pname");
                        if (pName.equalsIgnoreCase("")) {
                            model.setName("(No Name)");
                        } else {
                            model.setName(pName);
                        }

                        itemModelList.add(model);
                        CommonUtilities.playdatereglist.add(model);

                        System.out.println("--" + obj);
                        String state = obj.getString("status");

                        System.out.println("--" + state);
                        if (state.contains("success")) {

                            //    String nameid = obj.getString("petmateid");
                            //  new AddPetMateActivity.GetMembersAsyncTask().execute();
                            // Toast.makeText(context, state, Toast.LENGTH_LONG).show();
                        } else {
                            //    Toast.makeText(context, "failed", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException ee) {
                    ee.printStackTrace();
                }
            }
        }

    }

    public class MateAddTask extends AsyncTask<String, String, List<ItemObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* dialog = ProgressDialog.show(AddPetMateActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();

        }

        @Override
        protected List<ItemObject> doInBackground(String... urls) {

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
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }
                String finalJson = buffer.toString();
                System.out.println("final json---" + finalJson);
                List<ItemObject> itemModelList = new ArrayList<>();

                CommonUtilities.playdatereglist.clear();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    ItemObject model = new ItemObject();

                    model.setName(obj.getString("pname"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setcategory(obj.getString("cat"));
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setsize(obj.getString("size"));
                    model.setvac(obj.getString("vac"));
                    model.setcrsbrdreqd(obj.getString("crsbrdreqd"));
                    model.setphone(obj.getString("phone"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setabout(obj.getString("about"));
                    model.setpstate(obj.getString("pstate"));
                    model.setpetmateid(obj.getString("petmateid"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    statestatus = obj.getString("pstate");
                    if (statestatus.equalsIgnoreCase("0")) {
                        model.settextname(mate);
                    } else {
                        model.settextname(remove);
                    }

                    String pName = obj.getString("pname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
                    }

                    itemModelList.add(model);
                    CommonUtilities.playdatereglist.add(model);
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
        protected void onPostExecute(List<ItemObject> itemObjects) {
            super.onPostExecute(itemObjects);
            if (itemObjects != null && itemObjects.size() > 0) {
//                nodataaddpetmate.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                rview.setHasFixedSize(true);
                RecyclerViewAdapter1 adapter = new RecyclerViewAdapter1(getApplicationContext(), itemObjects);
                rview.setAdapter(adapter);

                //   dialog.dismiss();
            } else {
//                nodataaddpetmate.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);

                //    dialog.dismiss();
            }
            //   dialog.dismiss();
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

    public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder> {
        public static final String MyPREFERENCES = "MyPrefs";
        Button mate, remove;
        JSONArray arr = null;
        JSONArray arr1 = null;
        String stateval, userid, petmid;
        private List<ItemObject> itemList;
        private Context context;
        private SparseBooleanArray selectedItems;
        private SharedPreferences sharedpreferences;
        private String urlParameters;


        public RecyclerViewAdapter1(Context context, List<ItemObject> itemList) {
            this.itemList = itemList;
            this.context = context;
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.findmateview, null);
            ViewHolder sg = new ViewHolder(layoutview, getItemCount());
            return sg;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.petname.setText(itemList.get(position).getName());
            Picasso.with(context).load(itemList.get(position).getPhoto()).into(holder.icons);
            holder.age.setText(itemList.get(position).getage());
            holder.gender.setText(itemList.get(position).getgender());
            holder.breed.setText(itemList.get(position).getbreed());
            holder.weigh.setText(itemList.get(position).getweight());
            holder.heigh.setText(itemList.get(position).getheight());
            holder.siz.setText(itemList.get(position).getsize());
            holder.phone.setText(itemList.get(position).getphone());
            holder.photo.setText(itemList.get(position).getPhoto());
            holder.vac.setText(itemList.get(position).getvac());
            holder.crsbrdreqd.setText(itemList.get(position).getcrsbrdreqd());
            holder.category.setText(itemList.get(position).getcategory());
            holder.about.setText(itemList.get(position).getabout());
            holder.pstate.setText(itemList.get(position).getpstate());
            holder.laat.setText(itemList.get(position).getlat());
            holder.loon.setText(itemList.get(position).getlon());
            holder.petmateid.setText(itemList.get(position).getpetmateid());
            //     holder.mate.setText(itemList.get(position).gettextname());

            String state1 = holder.pstate.getText().toString();
            String age1 = holder.age.getText().toString();
            if (state1.equalsIgnoreCase("0")) {
                   /* holder.right.setBackgroundResource(R.drawable.mt_done_clr);
                    holder.remove.setBackgroundResource(R.drawable.remove_img);
                    holder.right.setEnabled(true);
                    holder.remove.setEnabled(false);*/
                holder.right.setBackgroundResource(R.drawable.done_img);
                holder.remove.setBackgroundResource(R.drawable.mt_rmv_clr);
                holder.right.setEnabled(true);
                holder.remove.setEnabled(false);


            }
            if (state1.equalsIgnoreCase("1")) {
               /* holder.right.setBackgroundResource(R.drawable.done_img);
                holder.remove.setBackgroundResource(R.drawable.mt_rmv_clr);
                holder.right.setEnabled(false);
                holder.remove.setEnabled(true);*/

                holder.right.setBackgroundResource(R.drawable.mt_done_clr);
                holder.remove.setBackgroundResource(R.drawable.remove_img);
                holder.right.setEnabled(false);
                holder.remove.setEnabled(true);

            }

            if (age1.equalsIgnoreCase("")) {
                holder.yrs.setVisibility(View.GONE);
            } else {
                holder.yrs.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            System.out.println("item" + itemList);
            return this.itemList.size();
        }

        public ItemObject getItem(int pos) {
            return this.itemList.get(pos);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public int position;
            public TextView petname, uid, petmateid, mate, age, gender, breed, category, weigh, heigh, siz, vac, crsbrdreqd, about, pstate, laat, loon, phone, photo, yrs;
            public int previous = -1;
            public ImageView done;
            public String a;
            ImageView icons;
            Button upload, right, remove;
            ConnectionDetector cd;

            public ViewHolder(final View itemView, final int getcount) {
                super(itemView);

                petname = (TextView) itemView.findViewById(R.id.name);
                //  uid = (TextView) itemView.findViewById(R.id.uid);
                icons = (ImageView) itemView.findViewById(R.id.icons);
                age = (TextView) itemView.findViewById(R.id.age);
                petmateid = (TextView) itemView.findViewById(R.id.petmateid);
                category = (TextView) itemView.findViewById(R.id.cate);
                weigh = (TextView) itemView.findViewById(R.id.weigh);
                heigh = (TextView) itemView.findViewById(R.id.height);
                siz = (TextView) itemView.findViewById(R.id.siz);
                vac = (TextView) itemView.findViewById(R.id.vac);
                crsbrdreqd = (TextView) itemView.findViewById(R.id.crsbrdreqd);
                about = (TextView) itemView.findViewById(R.id.about);
                pstate = (TextView) itemView.findViewById(R.id.pstate);
                laat = (TextView) itemView.findViewById(R.id.laat);
                loon = (TextView) itemView.findViewById(R.id.loon);
                phone = (TextView) itemView.findViewById(R.id.phone);
                photo = (TextView) itemView.findViewById(R.id.photo);
                gender = (TextView) itemView.findViewById(R.id.gender);
                breed = (TextView) itemView.findViewById(R.id.breed);
                yrs = (TextView) itemView.findViewById(R.id.second);
                done = (ImageView) itemView.findViewById(R.id.done);

                //  mate = (TextView) itemView.findViewById(R.id.right);

                right = (Button) itemView.findViewById(R.id.right);
                remove = (Button) itemView.findViewById(R.id.remove);

                stateval = pstate.getText().toString();

                RecyclerView recycle = (RecyclerView) itemView.findViewById(R.id.recycle2);
                final CardView card = (CardView) itemView.findViewById(R.id.cardpet);
                View view1 = null;
                // mate.setOnClickListener(this);
                right.setOnClickListener(this);
                remove.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int pos = getAdapterPosition();

                switch (view.getId()) {

                    case R.id.right:
                        petmid = petmateid.getText().toString();
                        stateval = pstate.getText().toString();
                        if (stateval.equalsIgnoreCase("0")) {
                            statev = "1";
                            showAddDialog(statev, "Confirm Registration?");
                            break;
                        }
                       /* else{
                            statev = "0";
                            showRemoveDialog(statev, "Are you sure to remove this pet from mate?");
                            break;
                        }*/
                    case R.id.remove:
                        petmid = petmateid.getText().toString();
                        stateval = pstate.getText().toString();

                        if (stateval.equalsIgnoreCase("1")) {
                            statev = "0";
                            showRemoveDialog(statev, "Confirm Removal?");
                            break;
                        }
                }
            }

            public void showAddDialog(String statev, String msg) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddPetMateActivity.this);
                // builder.setIcon(R.drawable.mt_done_clr);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(AddPetMateActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(AddPetMateActivity.this);
                                } else {
                                    new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/stateupdate");
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                //   alert.setTitle("Pet Mate");
                alert.show();
            }

            public void showRemoveDialog(String statev, String msg) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddPetMateActivity.this);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showInterntOnlySettingsAlert(AddPetMateActivity.this);
                                } else if (!com.petsapp.android.CommonUtilities
                                        .isConnectingToInternet(getApplicationContext())) {
                                    com.petsapp.android.CommonUtilities
                                            .showLocationSettingsAlert(AddPetMateActivity.this);
                                } else {
                                    new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/stateupdate");
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                //   alert.setTitle("Pet Mate");
                alert.show();
            }
        }

        private class PetRegisterTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(String... urls) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                formateDate = df.format(c.getTime());

                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&petmateid=" + URLEncoder.encode(petmid, "UTF-8") +
                            "&st=" + URLEncoder.encode(statev, "UTF-8") +
                            "&date=" + URLEncoder.encode(formateDate, "UTF-8") +
                            "&uid=" + URLEncoder.encode(idString, "UTF-8");
                    url = new URL(urls[0]);
                    System.out.println("sending" + url);
                    System.out.println("sending" + urlParameters);
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
                }
            }

            @Override
            protected void onPostExecute(String result) {
                String mate = "MATE";
                String remove = "REMOVE";
                System.out.println("staus----" + result);
                try {
                    List<ItemObject> itemModelList = new ArrayList<>();
                    itemModelList.clear();
                    CommonUtilities.playdatereglist.clear();
                    arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        System.out.println("--" + arr);
                        JSONObject obj = arr.getJSONObject(i);
                        System.out.println("--" + obj);
                        ItemObject model = new ItemObject();

                        model.setName(obj.getString("pname"));
                        model.setbreed(obj.getString("breed"));
                        model.setgender(obj.getString("gender"));
                        model.setage(obj.getString("age"));
                        model.setcategory(obj.getString("cat"));
                        model.setweight(obj.getString("weight"));
                        model.setheight(obj.getString("height"));
                        model.setsize(obj.getString("size"));
                        model.setvac(obj.getString("vac"));
                        model.setcrsbrdreqd(obj.getString("crsbrdreqd"));
                        model.setphone(obj.getString("phone"));
                        model.setlat(obj.getString("lat"));
                        model.setlon(obj.getString("lon"));
                        model.setabout(obj.getString("about"));
                        model.setpstate(obj.getString("pstate"));
                        model.setpetmateid(obj.getString("petmateid"));
                        model.setThumbnailUrl(obj.getString("images"));
                        model.setPhoto(obj.getString("images"));
                        statestatus = obj.getString("pstate");
                        if (statestatus.equalsIgnoreCase("0")) {
                            model.settextname(mate);
                        } else {
                            model.settextname(remove);
                        }

                        String pName = obj.getString("pname");
                        if (pName.equalsIgnoreCase("")) {
                            model.setName("(No Name)");
                        } else {
                            model.setName(pName);
                        }

                        itemModelList.add(model);
                        CommonUtilities.playdatereglist.add(model);

                        String state = obj.getString("status");
                        System.out.println("--" + state);
                        if (state.contains("success")) {


                        } else {
                            //  Toast.makeText(context, "failed", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    public class GetMembersAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
            dialog = ProgressDialog.show(AddPetMateActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();
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
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }
}
