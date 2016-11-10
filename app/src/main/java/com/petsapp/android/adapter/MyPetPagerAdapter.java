package com.petsapp.android.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.petsapp.android.ConnectionDetector;
import com.petsapp.android.Model.ItemObject1;
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
 * Created by WEB DESIGNING on 18-04-2016.
 */
public class MyPetPagerAdapter extends PagerAdapter {

    TextView dialogText;
    Boolean Isinternetpresent = false;
    ConnectionDetector cd;
    ImageView intrest, intrestTickImg;
    String urlParameters, ownerno;
    LoginSessionManager loginSessionManager;
    String idString, phone;
    Button b1, b2;
    JSONArray arr = null;
    Context context;
    LayoutInflater inflater;
    private List<ItemObject1> itemList;

    public MyPetPagerAdapter(Context context, List<ItemObject1> itemList) {

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
        final TextView txtStatus;
        final TextView txtPhone;
        ImageView imgflag;
        final ImageView editImg, deleteImg;

        loginSessionManager = new LoginSessionManager(container.getContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.myprt_pager_item, container,
                false);

        txtName = (TextView) itemView.findViewById(R.id.petname);
        txtBreed = (TextView) itemView.findViewById(R.id.petbreed);
        txtGender = (TextView) itemView.findViewById(R.id.petgender);
        //  txtStatus = (TextView) itemView.findViewById(R.id.petStatus);
        txtNamePet = (TextView) itemView.findViewById(R.id.petheight);
        txtPhone = (TextView) itemView.findViewById(R.id.petage);

        imgflag = (ImageView) itemView.findViewById(R.id.user_profile_img_id);

        txtName.setText(itemList.get(position).getName());
        txtBreed.setText(itemList.get(position).getbreed());
        txtGender.setText(itemList.get(position).getgender());
        //  txtStatus.setText(itemList.get(position).getLostorfound());
        txtNamePet.setText(itemList.get(position).getName());
        txtPhone.setText(itemList.get(position).getPhone());

        Picasso.with(context).load(itemList.get(position).getPhoto()).into(imgflag);

        editImg = (ImageView) itemView.findViewById(R.id.intent_img_id);
        deleteImg = (ImageView) itemView.findViewById(R.id.del_img_id);

      /*  intrest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cd = new ConnectionDetector(context);
                Isinternetpresent = cd.isConnectingToInternet();

                try {
                    if (Isinternetpresent) {

                        phone = txtPhone.getText().toString();

                        AdoptioninterestTask task = new AdoptioninterestTask();
                        task.execute("http://patrimony.udyoog.com/pat.asmx/adoptsendsms");
                        intrestTickImg.setVisibility(View.VISIBLE);
                        intrest.setVisibility(View.GONE);

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
                    }
                    else{
                        //  showAlertDialog(PagerActivity.this, "No Internet Connection", "You don't have internet connection.", false);
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });*/

        container.
                addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView) object);
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
