package com.petsapp.android.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by WEB DESIGNING on 20-05-2016.
 */
public class BookPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    String urlParameters;
    LoginSessionManager loginSessionManager;
    String idString, pidString;
    JSONArray arr = null;
    Activity activity;
    ConnectionDetector cd;
    private List<Data> itemList;
    private Boolean Isinternetpresent = false;

    public BookPagerAdapter(Context context, List<Data> itemList) {

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
        TextView txtLikes;
        final TextView txtId;
        ImageView imgflag, likeImg;

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

        txtName.setText(itemList.get(position).getName());
        txtDetails.setText(itemList.get(position).getDetails());
        txtId.setText(itemList.get(position).getId());
        txtLikes.setText(itemList.get(position).getLikes());
        Picasso.with(context).load(itemList.get(position).getphoto()).into(imgflag);

        likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cd = new ConnectionDetector(context);
               /* Isinternetpresent = cd.isConnectingToInternet();
                if (Isinternetpresent) {*/

                pidString = txtId.getText().toString();

                LikeTask task = new LikeTask();
                task.execute("http://petsapp.petsappindia.co.in/addlikes");

                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();

              /*  AlertDialog alertDialog = new AlertDialog.Builder(context.getApplicationContext()).create();
                alertDialog.setTitle("Liked");
                alertDialog.setMessage("");
                alertDialog.setIcon(R.drawable.alert_tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();*/


                   /* AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  alert.cancel();
                                }
                            });
                   // AlertDialog alert = builder.create();
                    builder.setTitle("Liked");
                    builder.show();*/
              /*  } else {
                    Toast.makeText(context, "Already Liked", Toast.LENGTH_SHORT).show();
                   *//* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("You don't have internet connection.")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  alert.cancel();
                                }
                            });
                 //   AlertDialog alert = builder.create();
                    builder.setTitle("No Internet Connection");
                    builder.show();*//*
                }*/

            }
        });


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView) object);
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
                    if (state.equals("success")) {


                        AlertDialog alertDialog = new AlertDialog.Builder(context.getApplicationContext()).create();
                        alertDialog.setTitle("Liked");
                        alertDialog.setMessage("");
                        alertDialog.setIcon(R.drawable.alert_tick);
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                        alertDialog.show();

                        //  Toast.makeText(context.getApplicationContext(), "Liked..", Toast.LENGTH_SHORT).show();

                       /* AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Liked");
                        alert.show();*/


                    } else {
                        //  Toast.makeText(context.getApplicationContext(), "Already Liked..", Toast.LENGTH_SHORT).show();
                       /* AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  alert.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Already Liked");
                        alert.show();*/
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
