package com.petsapp.android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.petsapp.android.ConnectionDetector;
import com.petsapp.android.Model.Data;
import com.petsapp.android.R;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WEB DESIGNING on 11-04-2016.
 */
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

        String pidStr = txtPid.getText().toString();

        if (pidStr.equalsIgnoreCase(idString)) {

            interestLayout.setVisibility(View.INVISIBLE);

        }

        intrest = (ImageView) itemView.findViewById(R.id.call);
        intrestTickImg = (ImageView) itemView.findViewById(R.id.calltick);

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                        builder.setMessage("Your interest has been passed on to the pet owner")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Message for Adoption");
                        alert.show();
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
                        "&invited_to=" + URLEncoder.encode(pid, "UTF-8");

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
                     /*   intrestTickImg.setVisibility(View.VISIBLE);
                        intrest.setVisibility(View.GONE);*/

                    } else {
                        //       Toast.makeText(getApplicationContext(),"Please Try Again After Somtime",Toast.LENGTH_LONG).show();
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
}
