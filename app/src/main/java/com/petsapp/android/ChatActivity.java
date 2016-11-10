package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.petsapp.android.Model.ItemObject1;
import com.petsapp.android.app.Config;
import com.petsapp.android.gcm.GcmIntentService;
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

public class ChatActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String blocksate = "00";
    JSONArray arr = null;
    String chatString, messageType;
    //   private MyWebRequestReceiver receiver;
    Intent wenreq;
    String status, statustext, messagetext, messagetime, messagefromto, idString;
    LoginSessionManager loginSessionManager;
    String toid, nameP;
    TextView nameText;
    String token;
    String msg1, msgby, msgto, photourl;
    ImageView imgpic;
    Notification_count_session Notificationcountsession;
    RecyclerView recycleView;
    ImageView blocke, unblocke;
    String blockstatus;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String urlParameters;
    private ProgressDialog dialog;
    private JSONArray data_array;
    private String TAG = ChatActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#53425b"));
        }
        recycleView = (RecyclerView) findViewById(R.id.recycle);
        imgpic = (ImageView) findViewById(R.id.pic);
        buttonSend = (Button) findViewById(R.id.send);
        nameText = (TextView) findViewById(R.id.nameId);
        blocke = (ImageView) findViewById(R.id.blocke);
        unblocke = (ImageView) findViewById(R.id.unblocke);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        if (loginSessionManager.checkLogin()) {
            finish();
        } else {
            HashMap<String, String> user = loginSessionManager.getUserDetails();
            idString = user.get(LoginSessionManager.KEY_ID);
            //   Bundle extras = getIntent().getExtras();
            Intent intent = getIntent();
            toid = intent.getStringExtra("msgby");
            nameP = intent.getStringExtra("nameVal");
            photourl = intent.getStringExtra("phturl");
            Notificationcountsession = new Notification_count_session(getApplicationContext());
            String coun = "0";
            Notificationcountsession.createLoginSession(coun);
            // Picasso.with(context).load(itemList.get(position).getphoto()).into(holder.icons);
            Picasso.with(getApplicationContext()).load(photourl).into(imgpic);
            nameText.setText(nameP);
            listView.setAdapter(chatArrayAdapter);
            GetMessageTask task = new GetMessageTask();
            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/chat_list");

          /*Start Pet Horizontal List*/
            GetAllPets task2 = new GetAllPets();
            task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/sendpetslist");

        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    token = intent.getStringExtra("token");

                    //   Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    //     Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    msg1 = intent.getStringExtra("message");
                    msgby = intent.getStringExtra("msgby");
                    msgto = intent.getStringExtra("msgto");
                    if (msgby.equalsIgnoreCase(toid) && msgto.equalsIgnoreCase(idString)) {
                        //        Toast.makeText(getApplicationContext(), "Push notification is received!"+msg1, Toast.LENGTH_LONG).show();
                        sendChatMessage1();
                    } else {
                        //        Toast.makeText(getApplicationContext(), "Other Push notification is received!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        };

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //      sendChatMessage();

                chatString = chatText.getText().toString().trim();

                if (chatString.equalsIgnoreCase("")) {
                    Toast.makeText(ChatActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    sendChatMessage();
                    SendTask task = new SendTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/chat_message");
                }

               /* SendTask task = new SendTask();
                task.execute(new String[]{"http://patrimony.udyoog.com/pat.asmx/chat_message"});*/
            }
        });

        blocke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                blockstatus = "1";
                SendTask1 task = new SendTask1();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/blockuser");
            }
        });

        unblocke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                blockstatus = "69";
                SendTask1 task = new SendTask1();
                task.execute("http://petsapp.petsappindia.co.in/pat.asmx/blockuser");
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

    }

    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private boolean sendChatMessage() {
        boolean side = false;
        ChatMessage model = new ChatMessage();
        model.setMsg(chatString);
        model.setMsgtype("From");
        chatArrayAdapter.add(model);
        chatText.setText("");
        //  side = !side;
        return true;
    }

    private boolean sendChatMessage1() {

        boolean side = false;
        ChatMessage model = new ChatMessage();
        model.setMsg(msg1);
        model.setMsgtype("To");
        chatArrayAdapter.add(model);

        return true;
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

    @Override
    public void onBackPressed() {
        Intent i1 = new Intent(getApplicationContext(), ChatListActivity.class);
        i1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i1);
        finish();
    }

    private class SendTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&invited_by=" + URLEncoder.encode(idString, "UTF-8") +
                        "&invited_to=" + URLEncoder.encode(toid, "UTF-8") +
                        "&status=" + URLEncoder.encode(blockstatus, "UTF-8");
//                Toast.makeText(getApplicationContext(),urlParameters,Toast.LENGTH_LONG).show();

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
                //     return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String state = obj.getString("status");
                    if (state.equals("success")) {
                        Intent i1 = new Intent(ChatActivity.this, ChatListActivity.class);
                        startActivity(i1);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please Try Again After Sometime", Toast.LENGTH_LONG).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class SendTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(MainActivity.this, "Authenticating",
                    "Please wait...", true);
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&msgby=" + URLEncoder.encode(idString, "UTF-8") +
                        "&msgto=" + URLEncoder.encode(toid, "UTF-8") +
                        "&msg=" + URLEncoder.encode(chatString, "UTF-8");
//                Toast.makeText(getApplicationContext(),urlParameters,Toast.LENGTH_LONG).show();

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
                //     return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String state = obj.getString("status");
                    if (state.equals("success")) {
                        // Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        chatText.setText("");
                        //   dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Try Again After Sometime", Toast.LENGTH_LONG).show();
                        //    dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //  dialog.dismiss();
        }
    }

    public class GetMessageTask extends AsyncTask<String, String, List<ChatMessage>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(MainActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();*/

        }

        @Override
        protected List<ChatMessage> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&byid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&toid=" + URLEncoder.encode(toid, "UTF-8");
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

                List<ChatMessage> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    /// String state = obj.getString("status");
                    blocksate = obj.getString("blockstatus");
                    ChatMessage model = new ChatMessage();
                    if (obj.isNull("messages")) {
                        model.setMsg("Welcome to PetsApp");
                        model.setMsgtype("From");
                        model.setMsg_on("");
                        model.setStatus("success");
                        model.setBlockstatus(obj.getString("blockstatus"));
                    } else {
                        model.setMsg(obj.getString("messages"));
                        model.setMsgtype(obj.getString("msgtype"));
                        model.setMsg_on(obj.getString("msg_on"));
                        model.setStatus(obj.getString("status"));
                        model.setBlockstatus(obj.getString("blockstatus"));
                    }
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
        protected void onPostExecute(List<ChatMessage> itemObjects) {
            super.onPostExecute(itemObjects);
            //   ChatArrayAdapter adapter = new ChatArrayAdapter(getApplicationContext(),itemObjects);
            if (blocksate.equalsIgnoreCase("1")) {
                blocke.setVisibility(View.GONE);
                unblocke.setVisibility(View.VISIBLE);

            } else if (blocksate.equalsIgnoreCase("69")) {
                unblocke.setVisibility(View.GONE);
                blocke.setVisibility(View.VISIBLE);

            } else {
                unblocke.setVisibility(View.VISIBLE);

            }

            listView = (ListView) findViewById(R.id.msgview);
            //   ChatArrayAdapter adapter = new ChatArrayAdapter(MainActivity.this,itemObjects);
            chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right, itemObjects);
            listView.setAdapter(chatArrayAdapter);
            // adapter.notifyDataSetChanged();

            // dialog.dismiss();
        }
    }

    public class GetAllPets extends AsyncTask<String, String, List<ItemObject1>> {

        @Override
        protected List<ItemObject1> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                urlParameters = "&uid=" + URLEncoder.encode(toid, "UTF-8");
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setUseCaches(true);
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
                List<ItemObject1> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    ItemObject1 model = new ItemObject1();

                    model.setName(obj.getString("petname"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setPhoto(obj.getString("images"));
                    model.setType(obj.getString("type"));
                    //  model.setPetmetId(obj.getString("petmateid"));

                    String pName = obj.getString("petname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
                    }

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

            if (itemObjects != null && itemObjects.size() > 0) {
                recycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                recycleView.setHasFixedSize(true);
                ChatPetRecyclerAdapter adapter = new ChatPetRecyclerAdapter(getApplicationContext(), itemObjects);
                recycleView.setAdapter(adapter);
                // dialog.dismiss();
            }
        }
    }


    /*Chat List Adapter*/
    public class ChatPetRecyclerAdapter extends RecyclerView.Adapter<ChatPetRecyclerAdapter.ViewHolder> {
        private List<ItemObject1> itemList;
        private Context context;

        public ChatPetRecyclerAdapter(Context context, List<ItemObject1> itemList) {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Picasso.with(context).load(itemList.get(position).getPhoto()).into(viewHolder.icons);
            viewHolder.name.setText(itemList.get(position).getName());
            viewHolder.age.setText(itemList.get(position).getage());
            viewHolder.breed.setText(itemList.get(position).getbreed());
            viewHolder.gender.setText(itemList.get(position).getgender());
            viewHolder.pettype.setText(itemList.get(position).getType());
            viewHolder.img.setText(itemList.get(position).getPhoto());
            String pettypes = viewHolder.pettype.getText().toString();
            if (pettypes.contains("petstate")) {

                viewHolder.name.setTextColor(getResources().getColor(R.color.pink));
                viewHolder.yrs.setTextColor(getResources().getColor(R.color.pink));
                viewHolder.age.setTextColor(getResources().getColor(R.color.pink));
                viewHolder.second.setTextColor(getResources().getColor(R.color.pink));
                viewHolder.gender.setTextColor(getResources().getColor(R.color.pink));
                viewHolder.breed.setTextColor(getResources().getColor(R.color.pink));

            } else if (pettypes.contains("lostnfound")) {
                viewHolder.name.setTextColor(getResources().getColor(R.color.yellow));
                viewHolder.yrs.setTextColor(getResources().getColor(R.color.yellow));
                viewHolder.age.setTextColor(getResources().getColor(R.color.yellow));
                viewHolder.second.setTextColor(getResources().getColor(R.color.yellow));
                viewHolder.gender.setTextColor(getResources().getColor(R.color.yellow));
                viewHolder.breed.setTextColor(getResources().getColor(R.color.yellow));

            } else if (pettypes.contains("petadoptstate")) {

                viewHolder.name.setTextColor(getResources().getColor(R.color.green));
                viewHolder.yrs.setTextColor(getResources().getColor(R.color.green));
                viewHolder.age.setTextColor(getResources().getColor(R.color.green));
                viewHolder.second.setTextColor(getResources().getColor(R.color.green));
                viewHolder.gender.setTextColor(getResources().getColor(R.color.green));
                viewHolder.breed.setTextColor(getResources().getColor(R.color.green));
            }
            String age1 = viewHolder.age.getText().toString();
            String gender1 = viewHolder.gender.getText().toString();
            if (age1.equalsIgnoreCase("")) {
                viewHolder.yrs.setVisibility(View.GONE);
            } else {
                viewHolder.yrs.setVisibility(View.VISIBLE);
            }

            if (gender1.equalsIgnoreCase("")) {
                viewHolder.second.setVisibility(View.GONE);
            } else {
                viewHolder.second.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_petlist, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, age, gender, breed, img, yrs, pettype, second;
            ImageView icons;

            public ViewHolder(final View itemView) {
                super(itemView);
                breed = (TextView) itemView.findViewById(R.id.breed);
                second = (TextView) itemView.findViewById(R.id.second);
                pettype = (TextView) itemView.findViewById(R.id.pettype);
                icons = (ImageView) itemView.findViewById(R.id.picture);
                name = (TextView) itemView.findViewById(R.id.name);
                age = (TextView) itemView.findViewById(R.id.age);
                img = (TextView) itemView.findViewById(R.id.img);
                gender = (TextView) itemView.findViewById(R.id.gender);
                yrs = (TextView) itemView.findViewById(R.id.first);

                itemView.setOnClickListener(new View.OnClickListener() {
                    int pos = getAdapterPosition();

                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(v.getContext(), ChatsPetPagerActivity.class);
                        int pos = getAdapterPosition();
                        in.putExtra("pos", pos);
                        in.putExtra("toId", toid);
                        v.getContext().startActivity(in);
                    }
                });
            }
        }
    }
}
