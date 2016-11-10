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

import com.petsapp.android.Model.LostFoundRequestModel;
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


public class AddLostFoundActivity extends AppCompatActivity {

    public static String ID = "ID";
    public static String PETNAME = "PETNAME";
    public static String BREED = "BREED";
    public static String GENDER = "GENDER";
    public static String AGE = "AGE";
    public static String CAT = "CAT";
    public static String COLOR = "COLOR";
    public static String STREET = "STREET";
    public static String AREA = "AREA";
    public static String ABOUT = "ABOUT";
    public static String LOSTORFOUND = "LOSTORFOUND";
    public static String IMAGE = "IMAGE";
    final Context context = this;
    Button addreg;
    TextView petname;
    String pet;
    Button petRegButton;
    RecyclerView rview;
    SharedPreferences mSharedPreference;
    JSONArray arr = null;
    String profileString;
    LoginSessionManager session;
    LoginSessionManager loginSessionManager;
    String idString;
    String statestatus, statev, pName, age;
    String lost = "Missing";
    String found = "Spotted";
    ConnectionDetector cd;
    Calendar c = Calendar.getInstance();
    String formateDate;
    TextView nodataaddlosyfound;
    List<LostFoundRequestModel> donorlist;
    ArrayList<HashMap<String, String>> fillMaps = null;
    HashMap<String, String> map = null;
    RegLostnFoundpetsListAdapter adapter;
    ListView lview;
    private GridLayoutManager flayout;
    private ProgressDialog dialog;
    private String urlParameters;
    private Bitmap bitmap, bmp;
    private Uri filePath;
    // String lost = "";
    //  String found = "";
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost_found);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LostandFoundActivity.class);
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
        rview = (RecyclerView) findViewById(R.id.recycle2);
        lview = (ListView) findViewById(R.id.listviewlf);


        nodataaddlosyfound = (TextView) findViewById(R.id.nodataaddlosyfound);
        nodataaddlosyfound.setVisibility(View.GONE);
        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();
        if (Isinternetpresent) {
            donorlist = CommonUtilities.lostnfoundreglist;

            fillMaps = new ArrayList<HashMap<String, String>>();
            if (donorlist.size() > 0) {
                for (int i = 0; i < donorlist.size(); i++) {
                    //      Toast.makeText(getApplicationContext(), "before:" + donorlist.get(i).getName(), Toast.LENGTH_LONG).show();
                    if (donorlist.get(i).getbreed() != null) {
                        rview.setVisibility(View.GONE);
                        lview.setVisibility(View.VISIBLE);


                        map = new HashMap<String, String>();
                        map.put(ID, donorlist.get(i).id);
                        map.put(BREED, donorlist.get(i).breed);
                        map.put(GENDER, donorlist.get(i).gender);
                        map.put(AGE, donorlist.get(i).age);
                        map.put(CAT, donorlist.get(i).category);
                        map.put(ABOUT, donorlist.get(i).about);
                        map.put(PETNAME, donorlist.get(i).name);
                        map.put(COLOR, donorlist.get(i).color);
                        map.put(IMAGE, donorlist.get(i).photo);
                        map.put(STREET, donorlist.get(i).street);
                        map.put(AREA, donorlist.get(i).area);
                        map.put(LOSTORFOUND, donorlist.get(i).lostorfound);
                        fillMaps.add(map);

                    }

                }
            } else {
                rview.setVisibility(View.VISIBLE);
                lview.setVisibility(View.GONE);


                LostFoundAddTask task = new LostFoundAddTask();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/getlostnfound");
            }
            adapter = new RegLostnFoundpetsListAdapter(this, fillMaps, R.layout.customlistlostnfound, new String[]{}, new int[]{});
            lview.setAdapter(adapter);

        } else {
            showAlertDialog(AddLostFoundActivity.this, "No Internet Connection", "You don't have internet connection.", false);
        }

        petRegButton = (Button) findViewById(R.id.add);
        petRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                petRegButton.startAnimation(animFadein);

                Intent i = new Intent(getApplicationContext(), LostFoundPetRegActivity.class);
                startActivity(i);
                finish();
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
        Intent i = new Intent(AddLostFoundActivity.this, LostandFoundActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /*****
     * Started Listview of Offline code
     ******/

    public class RegLostnFoundpetsListAdapter extends ArrayAdapter<HashMap<String, String>> {
        final Context context;
        public String ID = "ID";
        public String PETNAME = "PETNAME";
        public String BREED = "BREED";
        public String GENDER = "GENDER";
        public String AGE = "AGE";
        public String CAT = "CAT";
        public String COLOR = "COLOR";
        public String STREET = "STREET";
        public String AREA = "AREA";
        public String ABOUT = "ABOUT";
        public String LOSTORFOUND = "LOSTORFOUND";
        public String IMAGE = "IMAGE";
        String stateval, userid, petmid;
        HashMap<String, String> currentData;
        private List<HashMap<String, String>> items;
        private int viewId;

        public RegLostnFoundpetsListAdapter(Context context, ArrayList<HashMap<String, String>> fillMaps, int layout, String[] from, int[] to) {
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
                    convertView = vi.inflate(R.layout.customlistlostnfound, null);
                }
                holder = new ViewHolder();

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
                holder.txtStatusType = (TextView) convertView.findViewById(R.id.statusType);
                //   mate = (TextView) itemView.findViewById(R.id.right);

                /*Change this two lines*/
                holder.right = (Button) convertView.findViewById(R.id.right);
                holder.remove = (Button) convertView.findViewById(R.id.remove);

                holder.gender = (TextView) convertView.findViewById(R.id.gender);
                holder.breed = (TextView) convertView.findViewById(R.id.breed);
                holder.done = (ImageView) convertView.findViewById(R.id.done);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            currentData = new HashMap<String, String>();
            currentData = items.get(position);
            if (currentData != null) {
                holder.petname.setText(currentData.get(PETNAME));
                Picasso.with(context).load(currentData.get(IMAGE)).into(holder.icons);
                holder.age.setText(currentData.get(AGE));
                holder.gender.setText(currentData.get(GENDER));
                holder.breed.setText(currentData.get(BREED));
                holder.photo.setText(currentData.get(IMAGE));
                holder.category.setText(currentData.get(CAT));
                holder.about.setText(currentData.get(ABOUT));
                holder.pstate.setText(currentData.get(LOSTORFOUND));
                holder.petmateid.setText(currentData.get(ID));


                String state1 = holder.pstate.getText().toString();


                if (state1.equalsIgnoreCase("I lost my pet")) {

                    //     holder.mate.setBackgroundColor(Color.parseColor("#f8a04f"));
           /*Change this two lines*/
                    //      holder.right.setBackgroundResource(R.drawable.done_clr_img);
                    //      holder.remove.setBackgroundResource(R.drawable.remove_img);
                    holder.txtStatusType.setText("Missing");


                } else {
                    //     holder.mate.setBackgroundColor(Color.parseColor("#ed6e27"));
           /*Change this two lines*/
                    //     holder.right.setBackgroundResource(R.drawable.done_img);
                    //     holder.remove.setBackgroundResource(R.drawable.remove_clr_img);
                    holder.txtStatusType.setText("Spotted");
                }


            }

            final ViewHolder finalHolder = holder;
            holder.remove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    petmid = finalHolder.petmateid.getText().toString();
                    stateval = finalHolder.pstate.getText().toString();

                    showAddRemoveDialog();
                }
            });


            return convertView;
        }

        public void showAddRemoveDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(AddLostFoundActivity.this);
            builder.setMessage("Confirm Removal?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/removelostnfound");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            // alert.setTitle("Pet Missing or Spotted");
            alert.show();
        }

        public class ViewHolder {
            public int position;
            public TextView petname, uid, petmateid, mate, age, gender, breed, category, weigh, heigh, siz, vac, crsbrdreqd, about, pstate, laat, loon, phone, photo;
            public int previous = -1;
            public ImageView done;
            public String a;
            ImageView icons;
            Button upload, right, remove;
            TextView txtStatusType;

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
                    urlParameters = "&id=" + URLEncoder.encode(petmid, "UTF-8");

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
                System.out.println("staus----" + result);
                try {
                    arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        System.out.println("--" + arr);
                        JSONObject obj = arr.getJSONObject(i);
                        System.out.println("--" + obj);
                        String state = obj.getString("status");
                        System.out.println("--" + state);
                        if (state.contains("success")) {
                            new GetMembersAsyncTask().execute();
                            // new AddAdoptionActivity().GetMembersAsyncTask().execute();
                            //     Toast.makeText(context, state, Toast.LENGTH_LONG).show();
                        } else {
                            //     Toast.makeText(context, "failed", Toast.LENGTH_LONG).show();
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

    public class LostFoundAddTask extends AsyncTask<String, String, List<LostFoundRequestModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   dialog = ProgressDialog.show(AddLostFoundActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/


            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected List<LostFoundRequestModel> doInBackground(String... urls) {

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

                System.out.println("final json---" + finalJson);

                List<LostFoundRequestModel> itemModelList = new ArrayList<>();
                CommonUtilities.lostnfoundreglist.clear();


                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    LostFoundRequestModel model = new LostFoundRequestModel();

                    model.setId(obj.getString("id"));
                    model.setName(obj.getString("petname"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setcategory(obj.getString("category"));
                    model.setColor(obj.getString("color"));
                    model.setStreet(obj.getString("street"));
                    model.setArea(obj.getString("area"));
                    model.setabout(obj.getString("about"));
                    model.setLostorfound(obj.getString("lostorfound"));
                    model.setThumbnailUrl(obj.getString("image"));
                    model.setPhoto(obj.getString("image"));
                    // model.setd(obj.getString("image"));

                    statestatus = obj.getString("lostorfound");
                    pName = obj.getString("petname");
                    age = obj.getString("age");

                    if (statestatus.equalsIgnoreCase("I lost my pet")) {
                        model.settextname(lost);

                    } else {
                        model.settextname(found);
                    }

                    /*pet Name*/
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");

                    } else {
                        model.setName(pName);
                    }

                    /*age*/
                    if (age.equalsIgnoreCase("")) {
                        model.setage("");

                    } else {
                        model.setage(age + " yrs,");
                    }


                    itemModelList.add(model);
                    CommonUtilities.lostnfoundreglist.add(model);
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
        protected void onPostExecute(List<LostFoundRequestModel> itemObjects) {
            super.onPostExecute(itemObjects);
            if (itemObjects != null && itemObjects.size() > 0) {
                nodataaddlosyfound.setVisibility(View.GONE);
                rview.setVisibility(View.VISIBLE);
                rview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


                // mSharedPreference = getApplicationContext().getSharedPreferences("AdoptPrefs", Context.MODE_PRIVATE);
                rview.setHasFixedSize(true);
                PetLostFoundAdapter adapter = new PetLostFoundAdapter(getApplicationContext(), itemObjects);
                rview.setAdapter(adapter);

                //    dialog.dismiss();
            } else {
                nodataaddlosyfound.setVisibility(View.VISIBLE);
                rview.setVisibility(View.GONE);

                //     dialog.dismiss();
            }
            //  dialog.dismiss();
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

    /***
     * Adapter
     ****/
    public class PetLostFoundAdapter extends RecyclerView.Adapter<PetLostFoundAdapter.ViewHolder> {
        public static final String MyPREFERENCES = "LostfoundPrefs";
        Button mate, remove;
        JSONArray arr = null;
        String stateval, userid, petmid;
        private List<LostFoundRequestModel> itemList;
        private Context context;
        private SparseBooleanArray selectedItems;
        private SharedPreferences sharedpreferences;
        private String urlParameters;


        public PetLostFoundAdapter(Context context, List<LostFoundRequestModel> itemList) {
            this.itemList = itemList;
            this.context = context;
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.lostfound_request_view, null);
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
            holder.siz.setText(itemList.get(position).getsize());
            holder.phone.setText(itemList.get(position).getphone());
            holder.photo.setText(itemList.get(position).getPhoto());
            holder.category.setText(itemList.get(position).getcategory());
            holder.about.setText(itemList.get(position).getabout());
            holder.pstate.setText(itemList.get(position).getLostorfound());
            holder.petmateid.setText(itemList.get(position).getId());
//            holder.mate.setText(itemList.get(position).gettextname());

            String state1 = holder.pstate.getText().toString();

            if (state1.equalsIgnoreCase("I lost my pet")) {

                //     holder.mate.setBackgroundColor(Color.parseColor("#f8a04f"));
           /*Change this two lines*/
                //      holder.right.setBackgroundResource(R.drawable.done_clr_img);
                //      holder.remove.setBackgroundResource(R.drawable.remove_img);
                holder.txtStatusType.setText("Missing");


            } else {
                //     holder.mate.setBackgroundColor(Color.parseColor("#ed6e27"));
           /*Change this two lines*/
                //     holder.right.setBackgroundResource(R.drawable.done_img);
                //     holder.remove.setBackgroundResource(R.drawable.remove_clr_img);
                holder.txtStatusType.setText("Spotted");
            }
        }

        @Override
        public int getItemCount() {
            System.out.println("item" + itemList);
            return this.itemList.size();
        }

        public LostFoundRequestModel getItem(int pos) {
            return this.itemList.get(pos);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public int position;
            public TextView petname, uid, petmateid, mate, age, gender, breed, category, weigh, heigh, siz, vac, crsbrdreqd, about, pstate, laat, loon, phone, photo;
            public int previous = -1;
            public ImageView done;
            public String a;
            ImageView icons;
            Button upload, right, remove;
            TextView txtStatusType;
            ConnectionDetector cd;

            public ViewHolder(final View itemView, final int getcount) {
                super(itemView);

                petname = (TextView) itemView.findViewById(R.id.name);
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
                txtStatusType = (TextView) itemView.findViewById(R.id.statusType);
                //   mate = (TextView) itemView.findViewById(R.id.right);

                /*Change this two lines*/
                right = (Button) itemView.findViewById(R.id.right);
                remove = (Button) itemView.findViewById(R.id.remove);
                //
                gender = (TextView) itemView.findViewById(R.id.gender);
                breed = (TextView) itemView.findViewById(R.id.breed);
                done = (ImageView) itemView.findViewById(R.id.done);
                //   RecyclerView recycle = (RecyclerView) itemView.findViewById(R.id.recycle2);
                final CardView card = (CardView) itemView.findViewById(R.id.cardpet);
                View view1 = null;
                //  mate.setOnClickListener(this);

                /*Change this two lines*/
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
                        if (stateval.equalsIgnoreCase("I lost my pet")) {

                            //   showAddRemoveDialog();
                            break;
                        }
                      /*  else{
                            showAddRemoveDialog();
                            break;
                        }*/

                    case R.id.remove:

                        petmid = petmateid.getText().toString();
                        stateval = pstate.getText().toString();

                        showAddRemoveDialog();
                        break;

                }
            }

            public void showAddRemoveDialog() {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddLostFoundActivity.this);
                builder.setMessage("Confirm Removal?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/removelostnfound");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                // alert.setTitle("Pet Missing or Spotted");
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
                    urlParameters = "&id=" + URLEncoder.encode(petmid, "UTF-8");

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
                System.out.println("staus----" + result);
                try {
                    arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        System.out.println("--" + arr);
                        JSONObject obj = arr.getJSONObject(i);
                        System.out.println("--" + obj);
                        String state = obj.getString("status");
                        System.out.println("--" + state);
                        if (state.contains("success")) {
                            new GetMembersAsyncTask().execute();
                            // new AddAdoptionActivity().GetMembersAsyncTask().execute();
                            //     Toast.makeText(context, state, Toast.LENGTH_LONG).show();
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

    public class GetMembersAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(AddLostFoundActivity.this, "Loading",
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
            System.out.println("----------Response----------" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), FindLostFoundActivity.class);
            startActivity(intent);

            dialog.dismiss();
        }
    }
}
/***
 * Adapter
 ****/
