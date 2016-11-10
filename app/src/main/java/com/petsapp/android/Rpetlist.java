package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.Model.Allpetlist;
import com.petsapp.android.adapter.PetRecyclerview;
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

public class Rpetlist extends AppCompatActivity {

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
    public static String PETMATEID = "PETMATEID";
    public static String IMAGES = "IMAGES";
    final Context context = this;
    ConnectionDetector cd;
    Boolean isinternetPresent = false;
    JSONArray arr = null;
    RecyclerView rview;
    ImageView img;
    LoginSessionManager loginSessionManager;
    String idString;
    Button upload;
    TextView nodatarpetlist;
    List<Allpetlist> donorlist;
    ArrayList<HashMap<String, String>> fillMaps = null;
    HashMap<String, String> map = null;
    AllRegpetsListAdapter adapter;
    ListView lview;
    private String urlParameters;
    private ProgressDialog dialog;
    private Bitmap bitmap, bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpetlist);
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
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);
        upload = (Button) findViewById(R.id.add);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                upload.startAnimation(animFadein);

                Intent i = new Intent(getApplicationContext(), PetRegisterActivity.class);
                startActivity(i);
            }
        });
        img = (ImageView) findViewById(R.id.picture);
        rview = (RecyclerView) findViewById(R.id.recyclelist);
        lview = (ListView) findViewById(R.id.listviewallpets);
//        nodatarpetlist = (TextView) findViewById(R.id.nodatarpetlist);
//        nodatarpetlist.setVisibility(View.GONE);
        cd = new ConnectionDetector(getApplicationContext());
        isinternetPresent = cd.isConnectingToInternet();

        if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(Rpetlist.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(Rpetlist.this);
        } else {


            AddTask task = new AddTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getmates");

        }


    }

    /*****
     * Ends the listview of offline code
     ****/


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
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

    /*****
     * Started Listview of Offline code
     ******/

    public class AllRegpetsListAdapter extends ArrayAdapter<HashMap<String, String>> implements View.OnClickListener {
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
        public String ADOPTSTATE = "ADOPTSTATE";
        public String PETMATEID = "PETMATEID";
        public String IMAGES = "IMAGES";
        public String TEXTNAME = "TEXTNAME";
        String stateval, userid, petmid;
        String name, age, breed, cat, weight, height, size, vac, crsbreed, lat, lon, about, petmateid, image, gender;
        HashMap<String, String> currentData;
        private List<HashMap<String, String>> items;
        private int viewId;

        public AllRegpetsListAdapter(Context context, ArrayList<HashMap<String, String>> fillMaps, int layout, String[] from, int[] to) {
            super(context, layout, fillMaps);
            this.context = context;
            this.viewId = layout;
            this.items = fillMaps;
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View vix = convertView;
            if (convertView == null) {
                if (vix == null) {
                    LayoutInflater vi = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = vi.inflate(R.layout.allpetslist, null);
                }
                holder = new ViewHolder();

                holder.allpetsid = (RelativeLayout) convertView.findViewById(R.id.allpetsid);
                holder.breed = (TextView) convertView.findViewById(R.id.breed);
                holder.icons = (ImageView) convertView.findViewById(R.id.picture);
                holder.wei = (TextView) convertView.findViewById(R.id.wei);
                holder.heig = (TextView) convertView.findViewById(R.id.heig);
                holder.siz = (TextView) convertView.findViewById(R.id.siz);
                holder.llat = (TextView) convertView.findViewById(R.id.llat);
                holder.llon = (TextView) convertView.findViewById(R.id.llon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.age = (TextView) convertView.findViewById(R.id.age);
                holder.img = (TextView) convertView.findViewById(R.id.img);
                holder.gender = (TextView) convertView.findViewById(R.id.gender);
                holder.petmateId = (TextView) convertView.findViewById(R.id.petmateId);
                holder.about = (TextView) convertView.findViewById(R.id.aboutId);
                holder.category = (TextView) convertView.findViewById(R.id.categoryId);
                holder.vacciText = (TextView) convertView.findViewById(R.id.vacciId);
                holder.yrs = (TextView) convertView.findViewById(R.id.first);
                holder.crossbreedText = (TextView) convertView.findViewById(R.id.crossbreedId);
                holder.allpetsid.setOnClickListener(this);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            currentData = new HashMap<String, String>();
            currentData = items.get(position);
            if (currentData != null) {

                Picasso.with(context).load(currentData.get(IMAGES)).into(holder.icons);

                holder.name.setText(currentData.get(PNAME));
                holder.age.setText(currentData.get(AGE));
                holder.breed.setText(currentData.get(BREED));
                holder.gender.setText(currentData.get(GENDER));
                holder.wei.setText(currentData.get(WEIGHT));
                holder.heig.setText(currentData.get(HEIGHT));
                holder.siz.setText(currentData.get(SIZE));
                holder.img.setText(currentData.get(IMAGES));
                holder.llat.setText(currentData.get(LAT));
                holder.llon.setText(currentData.get(LON));
                holder.petmateId.setText(currentData.get(PETMATEID));
                holder.about.setText(currentData.get(ABOUT));
                holder.category.setText(currentData.get(CAT));
                holder.vacciText.setText(currentData.get(VAC));
                holder.crossbreedText.setText(currentData.get(CRSBRDREQD));


                name = holder.name.getText().toString();
                age = holder.age.getText().toString();
                breed = holder.breed.getText().toString();
                cat = holder.category.getText().toString();
                weight = holder.wei.getText().toString();
                height = holder.heig.getText().toString();
                size = holder.siz.getText().toString();
                vac = holder.vacciText.getText().toString();
                crsbreed = holder.crossbreedText.getText().toString();
                lat = holder.llat.getText().toString();
                lon = holder.llon.getText().toString();
                about = holder.about.getText().toString();
                petmateid = holder.petmateId.getText().toString();
                image = holder.img.getText().toString();
                gender = holder.gender.getText().toString();
                String age1 = holder.age.getText().toString();


                if (age1.equalsIgnoreCase("")) {
                    holder.yrs.setVisibility(View.GONE);
                } else {
                    holder.yrs.setVisibility(View.VISIBLE);
                }
            }
            holder.allpetsid.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // int selection = getSelectedItemPosition();
                    //  int pos = (Integer)v.getTag();
                    Intent in = new Intent(v.getContext(), Petprofilereg.class);
                    //   Intent in = new Intent(v.getContext(), MyPetPagerActivity.class);
                    //   in.putExtra("pos",pos);
                    in.putExtra("petname", name);
                    in.putExtra("age", age);
                    in.putExtra("gender", gender);
                    in.putExtra("breed", breed);
                    in.putExtra("weight", weight);
                    in.putExtra("height", height);
                    in.putExtra("size", size);
                    in.putExtra("lat", lat);
                    in.putExtra("lon", lon);
                    in.putExtra("image", image);
                    in.putExtra("petmateId", petmateid);
                    in.putExtra("about", about);
                    in.putExtra("category", cat);
                    in.putExtra("vacci", vac);
                    in.putExtra("crossbreed", crsbreed);
                    // System.out.println("panme" + ptname.getText().toString());
                    v.getContext().startActivity(in);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView name, age, gender, breed, wei, heig, siz, llat, llon, img, petmateId, about, category, vacciText, crossbreedText, yrs;
            Spinner breedsp, agesp, gendersp, distancesp;
            ImageView photo;
            RelativeLayout allpetsid;
            ImageView icons;

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

    public class AddTask extends AsyncTask<String, String, List<Allpetlist>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog = ProgressDialog.show(Rpetlist.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/
            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();

        }

        @Override
        protected List<Allpetlist> doInBackground(String... urls) {

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
                System.out.println("final json---" + finalJson);
                List<Allpetlist> itemModelList = new ArrayList<>();
                CommonUtilities.allpetslist.clear();
                arr = new JSONArray(finalJson);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Allpetlist model = new Allpetlist();

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

                    String pName = obj.getString("pname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
                    }
                    String pHeight = obj.getString("height");
                    if (pHeight.equalsIgnoreCase("")) {
                        model.setheight("-");
                    } else {
                        model.setheight(pHeight);
                    }

                    String pWeight = obj.getString("weight");
                    if (pWeight.equalsIgnoreCase("")) {
                        model.setWeight("-");
                    } else {
                        model.setWeight(pWeight);
                    }

                    itemModelList.add(model);
                    CommonUtilities.allpetslist.add(model);
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
        protected void onPostExecute(List<Allpetlist> itemObjects) {
            super.onPostExecute(itemObjects);

            if (itemObjects != null && itemObjects.size() > 0) {
//                nodatarpetlist.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                rview.setHasFixedSize(true);
                PetRecyclerview adapter = new PetRecyclerview(getApplicationContext(), itemObjects);
                rview.setAdapter(adapter);
                //  dialog.dismiss();
            } else {
//                nodatarpetlist.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);
                //   dialog.dismiss();
            }
            //  dialog.dismiss();
        }
    }
}
