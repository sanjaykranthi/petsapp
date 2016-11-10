package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.petsapp.android.sessionLogin.firstprofilesession;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateUserProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    static final int DATE_PICKER_ID = 0;
    private static final String TAG = FirstProfileActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCZ8-jumSCw5blH5Hh0Dntat1ENDpVnNJk";
    static String encodedImage;
    final Context context = this;
    TextView uploadImage;
    ImageView profileimg;
    EditText addressText;
    EditText emailText, add, nameText, phoneText;
    Button profileSubmitButton;
    String email = "", address, profile_photo, name, phone;
    JSONArray arr = null;
    Boolean isInternetPresent = false;
    String id;
    TextView txtlat, txtlon;
    String txtlatstr, txtlonstr;
    LoginSessionManager loginSessionManager;
    firstprofilesession firstProfSession;
    String idString;
    double latitude, longitude;
    String lat, lang;
    ConnectionDetector cd;
    String latValue, lonValue;
    CheckBox locCheck;
    GPSTracker gps;
    String nameValue, mobiValue, mailValue, imgValue, addrValue;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private AutoCompleteTextView mACTVAddress;
    private ProgressDialog dialog;
    private String urlParameters;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private int day, month, year;
    private boolean booladptr = true;
    private Boolean Isinternetpresent = false;

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ind");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
        }
        return resultList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
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

        cd = new ConnectionDetector(getApplicationContext());
        Isinternetpresent = cd.isConnectingToInternet();

        locCheck = (CheckBox) findViewById(R.id.loccheck);

        gps = new GPSTracker(UpdateUserProfileActivity.this);
        if (gps.canGetLocation()) {
            if (gps != null) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
        } else {
            gps.showSettingsAlert();
        }
        locCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //  getMyLocationAddress();

                if (locCheck.isChecked()) {
                    if (!com.petsapp.android.CommonUtilities
                            .isConnectingToInternet(getApplicationContext())) {
                        com.petsapp.android.CommonUtilities
                                .showInterntOnlySettingsAlert(UpdateUserProfileActivity.this);
                    } else if (!com.petsapp.android.CommonUtilities
                            .isConnectingToInternet(getApplicationContext())) {
                        com.petsapp.android.CommonUtilities
                                .showLocationSettingsAlert(UpdateUserProfileActivity.this);
                    } else {
                        getMyLocationAddress();
                    }
                } else {
                    mACTVAddress.setText("");
                }

            }
        });
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        uploadImage = (TextView) findViewById(R.id.textImgId);
        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlon = (TextView) findViewById(R.id.txtlon);
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);
        mACTVAddress = (AutoCompleteTextView) findViewById(R.id.address_text_id);

        nameText = (EditText) findViewById(R.id.name_text_id);
        phoneText = (EditText) findViewById(R.id.phone_text_id);
        emailText = (EditText) findViewById(R.id.email_text_id);


        /****************intent**********/

        try {
            Bundle extras = getIntent().getExtras();
            nameValue = extras.getString("name");
            mobiValue = extras.getString("mobi");
            mailValue = extras.getString("mail");
            imgValue = extras.getString("img");
            addrValue = extras.getString("addr");
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
        nameText.setText(nameValue);
        phoneText.setText(mobiValue);
        emailText.setText(mailValue);
        mACTVAddress.setText(addrValue);

        Picasso.with(getApplicationContext()).load(imgValue).into(profileimg);

        Bitmap bm = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
        //   profileimg.setImageBitmap(photo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
/*Google place Api*/
        mACTVAddress.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_list));
        mACTVAddress.setThreshold(2);
        mACTVAddress.setOnItemClickListener(this);

        /****************intent**********/
        emailText = (EditText) findViewById(R.id.email_text_id);

        profileSubmitButton = (Button) findViewById(R.id.profile_submit_button_id);

        if (checkPlayServices()) {

            buildGoogleApiClient();

        }
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateUserProfileActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Upload your photo...");
/*********Camera********/
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                       /* Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, 2);*/
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intent, 2);

                    }
                });
/*********Gallery********/
                alertDialog.setNegativeButton("Browse", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                    }
                });
                alertDialog.show();

            }
        });
// && (emailText.getText().toString().length()>0)
        profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                profileSubmitButton.startAnimation(animFadein);

                final String email1 = emailText.getText().toString();
                if (nameText.getText().toString().length() == 0) {
                    nameText.setError("Please Enter Valid Name");
                } else if (phoneText.getText().toString().length() < 10) {
                    phoneText.setError("Please Enter Valid mobile no.");
                } else if ((!isValidEmail(email1)) && (emailText.getText().toString().length() > 0)) {
                    emailText.setError("Invalid Email");
                } else if (profileimg.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else if (mACTVAddress.getText().toString().length() == 0) {
                    mACTVAddress.setError("Please Enter Your Valid Address");
                } else {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (!isInternetPresent) {
                        showAlertDialog(UpdateUserProfileActivity.this,
                                "No Internet Connection", "You don't have internet connection.", false);
                    } else {


                        name = nameText.getText().toString().trim();
                        phone = phoneText.getText().toString().trim();
                        email = emailText.getText().toString().trim();
                        address = mACTVAddress.getText().toString();
                        txtlatstr = txtlat.getText().toString().trim();
                        txtlonstr = txtlon.getText().toString().trim();

                        if (Isinternetpresent) {
                            UpdateUserProfileTask task = new UpdateUserProfileTask();
                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/updateuserprofile");
                        } else {
                            //   Toast.makeText(UpdateUserProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

/*Start Google place api*/

    public void getMyLocationAddress() {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {

            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }

                mACTVAddress.setText(strAddress.toString());
                /*Toast.makeText(getApplicationContext(), strAddress.toString(), Toast.LENGTH_LONG)
                        .show();*/
            } else {
                //  mACTVAddress.setText("Sorry No location found..!");
                showAlertDialog(UpdateUserProfileActivity.this,
                        "Address", "Please enter your address manually.", false);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //   Toast.makeText(getApplicationContext(), "Sorry could not get address..!", Toast.LENGTH_LONG).show();
        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //  Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
/*End Google place api*/

    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
           /* try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileimg.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }*/
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                cropCapturedImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
           /* try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                int nh = (int) (bitmap.getHeight() * (256.0 / bitmap.getWidth()));
                Bitmap photo = Bitmap.createScaledBitmap(bitmap, 256, nh, true);

                profileimg.setImageBitmap(photo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }*/
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                cropCapturedImage(Uri.fromFile(file));
            } catch (ActivityNotFoundException aNFE) {
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                profileimg.setImageBitmap(thePic);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                System.out.println("base^^^^$$$$$$$" + encodedImage);
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }
    }

    public void cropCapturedImage(Uri picUri) {
        //call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 3);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //   displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
        Intent i = new Intent(UpdateUserProfileActivity.this, UserProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return String.valueOf(resultList.get(index));
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new Filter.FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private class UpdateUserProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
            /*dialog = ProgressDialog.show(UpdateUserProfileActivity.this, "Updating",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&id=" + URLEncoder.encode(idString, "UTF-8") +
                        "&name=" + URLEncoder.encode(name, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&mail=" + URLEncoder.encode(email, "UTF-8") +
                        "&addr=" + URLEncoder.encode(address, "UTF-8") +
                        "&img=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&lat=" + URLEncoder.encode("0.0", "UTF-8") +
                        "&lon=" + URLEncoder.encode("0.0", "UTF-8");
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
                System.out.println("url====" + url);
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
                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                        IntentTask1 intT = new IntentTask1();
                        intT.execute();
                    } else {
                        //    Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

    private class IntentTask1 extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateUserProfileActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... args) {
            Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(i);
            finish();
            return null;
        }

        protected void onPostExecute(Void result) {
            pDialog.dismiss();
        }
    }
}
