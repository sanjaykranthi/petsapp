package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    final Context context = this;
    MytripAdapter adapter;
    ArrayList<Data> list = new ArrayList<>();
    LoginSessionManager loginSessionManager;
    String statev;
    String idString, phturl;
    private RecyclerView rview;
    private JSONArray json = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
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
            window.setStatusBarColor(Color.parseColor("#53425b"));
        }
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();


        if (loginSessionManager.checkLogin()) {
            finish();
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showInterntOnlySettingsAlert(ChatListActivity.this);
        } else if (!com.petsapp.android.CommonUtilities
                .isConnectingToInternet(getApplicationContext())) {
            com.petsapp.android.CommonUtilities
                    .showLocationSettingsAlert(ChatListActivity.this);
        } else {


            idString = user.get(LoginSessionManager.KEY_ID);

            rview = (RecyclerView) findViewById(R.id.recycle2);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rview.setLayoutManager(mLayoutManager);
            Mytripstask task = new Mytripstask();
            task.execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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

    public class Mytripstask extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog dialog = new ProgressDialog(ChatListActivity.this);
        private String urlParameters;

        @Override
        protected void onPreExecute() {
           /* dialog.setMessage("Getting Data...");
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            JSONParser jsonParser = new JSONParser();

            try {
                urlParameters = "&id=" + URLEncoder.encode(idString, "UTF-8");
                json = jsonParser.getJSONFromUrl(getResources().getString(R.string.url) + "/invitation_list", urlParameters);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            try {
                if (json.length() > 0) {

                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject;
                        jsonObject = json.getJSONObject(i);
                        Data data = new Data();
                        data.setinvited_on_date(jsonObject.getString("invited_on"));
                        data.setbyname(jsonObject.getString("by_name"));
                        data.setChatIid(jsonObject.getString("id"));
                        data.setstatus(jsonObject.getString("status"));
                        data.setinvited_at_time(jsonObject.getString("invited_at"));
                        data.settype(jsonObject.getString("type"));
                        data.settoid(jsonObject.getString("toid"));
                        data.setPhoto(jsonObject.getString("img"));


                        list.add(data);
                        adapter = new MytripAdapter(getApplicationContext(), list);
                        rview.setAdapter(adapter);

                        //       dialog.dismiss();


                    }
                } else {

                    //   Toast.makeText(getApplicationContext(),"No Chat List", Toast.LENGTH_LONG).show();
                }


                //  dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
           /* if(json==null){
                Toast.makeText(getApplicationContext(), "No Contacts in your Chat Pages", Toast.LENGTH_SHORT).show();
            }else{
                adapter=new MytripAdapter(getApplicationContext(),list);
                rview.setAdapter(adapter);
                //rview.setSelector( R.drawable.listselector);
            }*/
        }
    }

    public class MytripAdapter extends RecyclerView.Adapter<MytripAdapter.ViewHolder> {
        public static final String MyPREFERENCES = "MyPrefs";
        Button mate, remove;
        JSONArray arr = null;
        RecyclerView recycle;
        String stateval, statusval, statusval1, userid, petmid, state1, type, toid1, nameProf, phturl;
        private List<Data> itemList;
        private Context context;
        private SparseBooleanArray selectedItems;
        private SharedPreferences sharedpreferences;
        private String urlParameters;
        //  ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        public MytripAdapter(Context context, List<Data> itemList) {
            this.itemList = itemList;
            this.context = context;
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, null);
            ViewHolder sg = new ViewHolder(layoutview, getItemCount());
            return sg;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.name.setText(itemList.get(position).getbyname());
            Picasso.with(context).load(itemList.get(position).getphoto()).into(holder.icons);
            holder.id.setText(itemList.get(position).getChatIid());
            holder.invited_on.setText(itemList.get(position).getinvited_on_date());
            holder.invited_at.setText(itemList.get(position).getinvited_at_time());
            holder.status.setText(itemList.get(position).getstatus());
            holder.types.setText(itemList.get(position).gettype());
            holder.toid.setText(itemList.get(position).gettoid());
            holder.photourl.setText(itemList.get(position).getphoto());
            //     holder.mate.setText(itemList.get(position).gettextname());
            type = holder.types.getText().toString();
            state1 = holder.status.getText().toString();
            // toid1 = holder.toid.getText().toString();

            if (state1.equalsIgnoreCase("0") && type.equalsIgnoreCase("To")) {


                holder.right.setEnabled(true);
                holder.remove.setEnabled(true);
                holder.cleardecline.setVisibility(View.GONE);
                holder.name.setTextColor(Color.parseColor("#ffffff"));

                holder.linear.setBackgroundColor(getResources().getColor(R.color.mycolor));


            }
            if (state1.equalsIgnoreCase("0") && type.equalsIgnoreCase("From")) {

/*
                holder.right.setVisibility(View.GONE);
                holder.remove.setVisibility(View.VISIBLE);

                holder.remove.setEnabled(true);
                holder.name.setTextColor(Color.parseColor("#ffffff"));

                holder.linear.setBackgroundColor(getResources().getColor(R.color.mycolor));

           */

                holder.right.setVisibility(View.GONE);
                holder.remove.setVisibility(View.GONE);
                holder.cleardecline.setVisibility(View.VISIBLE);
                holder.cleardecline.setEnabled(true);
                holder.name.setTextColor(Color.parseColor("#ffffff"));

                holder.linear.setBackgroundColor(getResources().getColor(R.color.mycolor));


            }
            if (state1.equalsIgnoreCase("1")) {
                holder.right.setVisibility(View.GONE);
                holder.remove.setVisibility(View.GONE);

            }
            if (state1.equalsIgnoreCase("69")) {
                holder.right.setVisibility(View.GONE);
                holder.remove.setVisibility(View.GONE);

            }

        }

        @Override
        public int getItemCount() {
            System.out.println("item" + itemList);
            return this.itemList.size();
        }

        public Data getItem(int pos) {
            return this.itemList.get(pos);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public int position;
            public TextView name, id, invited_on, invited_at, types, status, toid, photourl;
            public int previous = -1;
            public ImageView done;
            public String a;
            LinearLayout linear;
            ImageView icons;
            TextView right, remove;
            ImageView cleardecline;
            // ConnectionDetector cd;

            public ViewHolder(final View itemView, final int getcount) {
                super(itemView);
                linear = (LinearLayout) itemView.findViewById(R.id.linear11);
                name = (TextView) itemView.findViewById(R.id.name);
                //  uid = (TextView) itemView.findViewById(R.id.uid);
                id = (TextView) itemView.findViewById(R.id.id);
                invited_on = (TextView) itemView.findViewById(R.id.invited_on);
                invited_at = (TextView) itemView.findViewById(R.id.invited_at);
                status = (TextView) itemView.findViewById(R.id.status);
                types = (TextView) itemView.findViewById(R.id.type);
                toid = (TextView) itemView.findViewById(R.id.toid);
                recycle = (RecyclerView) itemView.findViewById(R.id.recycle2);
                icons = (ImageView) itemView.findViewById(R.id.icons);
                photourl = (TextView) itemView.findViewById(R.id.photourl);

                //  mate = (TextView) itemView.findViewById(R.id.right);

                right = (TextView) itemView.findViewById(R.id.right);
                remove = (TextView) itemView.findViewById(R.id.remove);
                cleardecline = (ImageView) itemView.findViewById(R.id.cleardecline);
                statusval1 = status.getText().toString();
                if (statusval1.equalsIgnoreCase("0")) {
                    //itemView.setBackgroundColor(Color.BLACK);
                    ((CardView) itemView).setCardBackgroundColor(Color.parseColor("#000000"));
                } else {
                    //itemView.setBackgroundColor(Color.WHITE);
                    ((CardView) itemView).setCardBackgroundColor(Color.parseColor("#ffffff"));
                }

                //   stateval = id.getText().toString();
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phturl = photourl.getText().toString();
                        stateval = id.getText().toString();
                        statusval = status.getText().toString();
                        toid1 = toid.getText().toString();
                        if (statusval.equalsIgnoreCase("1") || statusval.equalsIgnoreCase("69")) {
                            Intent in = new Intent(itemView.getContext(), ChatActivity.class);
                            in.putExtra("msgby", toid1);
                            in.putExtra("phturl", phturl);
                            in.putExtra("nameVal", name.getText().toString());
                            itemView.getContext().startActivity(in);
                        } else if (statusval.equalsIgnoreCase("0")) {
                            Toast.makeText(getApplicationContext(), "Request Pending", Toast.LENGTH_LONG).show();
                        }


                    }
                });


                final CardView card = (CardView) itemView.findViewById(R.id.cardpet);
                View view1 = null;
                right.setOnClickListener(this);
                remove.setOnClickListener(this);
                cleardecline.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int pos = getAdapterPosition();

                switch (view.getId()) {

                    case R.id.right:
                        petmid = id.getText().toString();
                        statev = "1";
                        showAddDialog(statev, "Confirm Chat?");
                        break;
                    case R.id.remove:
                        petmid = id.getText().toString();
                        statev = "2";
                        showRemoveDialog(statev, "Decline Chat?");
                        break;
                    case R.id.cleardecline:
                        petmid = id.getText().toString();
                        statev = "2";
                        showRemoveDialog(statev, "Confirm Removal");
                        break;
                }
            }

            public void showAddDialog(String statev, String msg) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatListActivity.this);
                // builder.setIcon(R.drawable.mt_done_clr);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/invitation_status");
                                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("");
                alert.show();
            }

            public void showRemoveDialog(String statev, String msg) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatListActivity.this);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                new PetRegisterTask().execute("http://petsapp.petsappindia.co.in/pat.asmx/invitation_status");
                                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("");
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
                /*SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                formateDate=df.format(c.getTime());*/

                URL url;
                HttpURLConnection connection = null;
                try {
                    urlParameters = "&invition_id=" + URLEncoder.encode(petmid, "UTF-8") +
                            "&status=" + URLEncoder.encode(statev, "UTF-8");
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

                            new ChatListActivity.GetMembersAsyncTask().execute();
                            //  Toast.makeText(context, state, Toast.LENGTH_LONG).show();
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
            super.onPreExecute();
           /* dialog = ProgressDialog.show(ChatListActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();*/
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

            //  dialog.dismiss();
        }

    }

}
