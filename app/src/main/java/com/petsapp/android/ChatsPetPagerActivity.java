package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.petsapp.android.Model.ItemObject1;
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

public class ChatsPetPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ChatPagerAdapter adapter;
    JSONArray arr = null;
    String idString, categ;
    LoginSessionManager loginSessionManager;
    int pos1;
    ConnectionDetector cd;
    int tab;
    String brd, ageS, dist, gnd, latval, lonval, pager;
    String toId;
    private ProgressDialog dialog;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_pet_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), ChatListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        idString = user.get(LoginSessionManager.KEY_ID);

        Bundle extras = getIntent().getExtras();
        pos1 = extras.getInt("pos");
        toId = extras.getString("toId");

        try {
            if (!CommonUtilities
                    .isConnectingToInternet(getApplicationContext())) {
                CommonUtilities
                        .showInterntOnlySettingsAlert(ChatsPetPagerActivity.this);
            } else if (!CommonUtilities
                    .isConnectingToInternet(getApplicationContext())) {
                CommonUtilities
                        .showLocationSettingsAlert(ChatsPetPagerActivity.this);
            } else {
                GetPagerDataTask task2 = new GetPagerDataTask();
                task2.execute("http://petsapp.petsappindia.co.in/pat.asmx/sendpetslist");

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
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

    public class GetPagerDataTask extends AsyncTask<String, String, List<ItemObject1>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ChatsPetPagerActivity.this, "Fetching Data",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected List<ItemObject1> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                urlParameters = "&uid=" + URLEncoder.encode(toId, "UTF-8");

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

                List<ItemObject1> itemModelList = new ArrayList<>();

                arr = new JSONArray(finalJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    ItemObject1 model = new ItemObject1();
                    model.setName(obj.getString("petname"));
                    model.setCategory(obj.getString("category"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setPhoto(obj.getString("images"));
                    model.setType(obj.getString("type"));
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
        protected void onPostExecute(List<ItemObject1> detailsModels) {
            super.onPostExecute(detailsModels);
            if (detailsModels != null) {

                viewPager = (ViewPager) findViewById(R.id.chat_pager);
                adapter = new ChatPagerAdapter(getApplicationContext(), detailsModels);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(pos1);
                dialog.dismiss();
            }
        }
    }

    /********
     * Adapter Start
     ******/

    public class ChatPagerAdapter extends PagerAdapter {

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
        private List<ItemObject1> itemList;

        public ChatPagerAdapter(Context context, List<ItemObject1> itemList) {

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

            TextView txtName, txtBreed, txtAge, txtGender, txtceteg, txtType;
            TextView txtName1, txtBreed1, txtAge1, txtGender1, txtceteg1;
            final TextView txtPid;
            ImageView imgflag;
            final ImageButton leftImg, rightImg;

            loginSessionManager = new LoginSessionManager(container.getContext());
            HashMap<String, String> user = loginSessionManager.getUserDetails();
            idString = user.get(LoginSessionManager.KEY_ID);


            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.chat_vpager_item, container, false);

            txtName = (TextView) itemView.findViewById(R.id.petname);
            txtBreed = (TextView) itemView.findViewById(R.id.petbreed);
            txtGender = (TextView) itemView.findViewById(R.id.petgender);
            txtceteg = (TextView) itemView.findViewById(R.id.petCategory);
            txtAge = (TextView) itemView.findViewById(R.id.petAge);
            txtType = (TextView) itemView.findViewById(R.id.petType);

            imgflag = (ImageView) itemView.findViewById(R.id.flag);

            leftImg = (ImageButton) itemView.findViewById(R.id.leftImage);
            rightImg = (ImageButton) itemView.findViewById(R.id.rightImage);

            txtName.setText(itemList.get(position).getName());
            txtBreed.setText(itemList.get(position).getbreed());
            txtGender.setText(itemList.get(position).getgender());
            txtceteg.setText(itemList.get(position).getCategory());
            txtAge.setText(itemList.get(position).getage());
            txtType.setText(itemList.get(position).getType());

            txtName1 = (TextView) itemView.findViewById(R.id.pTitle);
            txtBreed1 = (TextView) itemView.findViewById(R.id.breedTitle);
            txtGender1 = (TextView) itemView.findViewById(R.id.gTitle);
            txtceteg1 = (TextView) itemView.findViewById(R.id.cateTitle);
            txtAge1 = (TextView) itemView.findViewById(R.id.ageTitle);

            Picasso.with(context).load(itemList.get(position).getPhoto()).into(imgflag);


            String nameStr = txtName.getText().toString().trim();
            if (nameStr.equalsIgnoreCase("")) {
                txtName.setText("-");
            }

            String ageStr = txtAge.getText().toString().trim();
            if (ageStr.equalsIgnoreCase("")) {
                txtAge.setText("-");
            }

            /*Color of fonts*/
            String typeStr = txtType.getText().toString().trim();

            if (typeStr.equalsIgnoreCase("petstate")) {//Play Date

                txtName1.setTextColor(Color.parseColor("#d24c63"));
                txtBreed1.setTextColor(Color.parseColor("#d24c63"));
                txtGender1.setTextColor(Color.parseColor("#d24c63"));
                txtceteg1.setTextColor(Color.parseColor("#d24c63"));
                txtAge1.setTextColor(Color.parseColor("#d24c63"));

            } else if (typeStr.equalsIgnoreCase("petadoptstate")) { //Adoption

                txtName1.setTextColor(Color.parseColor("#2b9a99"));
                txtBreed1.setTextColor(Color.parseColor("#2b9a99"));
                txtGender1.setTextColor(Color.parseColor("#2b9a99"));
                txtceteg1.setTextColor(Color.parseColor("#2b9a99"));
                txtAge1.setTextColor(Color.parseColor("#2b9a99"));

            } else if (typeStr.equalsIgnoreCase("lostnfound")) { // Lost & Found

                txtName1.setTextColor(Color.parseColor("#dd8332"));
                txtBreed1.setTextColor(Color.parseColor("#dd8332"));
                txtGender1.setTextColor(Color.parseColor("#dd8332"));
                txtceteg1.setTextColor(Color.parseColor("#dd8332"));
                txtAge1.setTextColor(Color.parseColor("#dd8332"));

            }

     /*Color of fonts*/


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

                    if (tab == getCount() - 1) {
                        rightImg.setVisibility(View.GONE);
                    }

                    tab++;
                    viewPager.setCurrentItem(tab);
                    //   rightImg.setVisibility(View.GONE);
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
    }

}
