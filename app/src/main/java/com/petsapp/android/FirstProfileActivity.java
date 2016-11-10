package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    static final int DATE_PICKER_ID = 0;
    private static final String TAG = FirstProfileActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCZ8-jumSCw5blH5Hh0Dntat1ENDpVnNJk";
    static String encodedImage;//iVBORw0KGgoAAAANSUhEUgAAANkAAAD5CAIAAAAlcAcXAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA2RpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDpGNzdGMTE3NDA3MjA2ODExQkVEQjhGMUE3RjE2NEVENiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpERTI5NjUyOTkzNUYxMUUxOEQ1MTg0RUQyNTY5ODgyQSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpERTI5NjUyODkzNUYxMUUxOEQ1MTg0RUQyNTY5ODgyQSIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M1IFdpbmRvd3MiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpENUZCRDc2QTUzOTBFMTExQTY1RkE2Rjg1QzExODhENyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpGNzdGMTE3NDA3MjA2ODExQkVEQjhGMUE3RjE2NEVENiIvPiA8L3JkZjpEZXNjcmlwdG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
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    final Context context = this;
    TextView uploadImage;
    ImageView profileimg;
    EditText addressText;
    EditText emailText, add;
    Button profileSubmitButton;
    String email = "", address, profile_photo;
    JSONArray arr = null;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String id;
    TextView txtlat, txtlon;
    String txtlatstr, txtlonstr;
    LoginSessionManager loginSessionManager;
    firstprofilesession firstProfSession;
    String idString;
    double latitude, longitude;
    String lat, lang;
    CheckBox locCheck;
    GPSTracker gps;
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
        setContentView(R.layout.activity_first_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }
        uploadImage = (TextView) findViewById(R.id.textImgId);
        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlon = (TextView) findViewById(R.id.txtlon);
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);
        mACTVAddress = (AutoCompleteTextView) findViewById(R.id.address_text_id);
        locCheck = (CheckBox) findViewById(R.id.loccheck);

        gps = new GPSTracker(FirstProfileActivity.this);
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
                                .showInterntOnlySettingsAlert(FirstProfileActivity.this);
                    } else if (!com.petsapp.android.CommonUtilities
                            .isConnectingToInternet(getApplicationContext())) {
                        com.petsapp.android.CommonUtilities
                                .showLocationSettingsAlert(FirstProfileActivity.this);
                    } else {
                        getMyLocationAddress();
                    }
                } else {
                    mACTVAddress.setText("");
                }

                /*if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showInterntOnlySettingsAlert(FirstProfileActivity.this);
                }
                else if (!com.areratech.newpatrimony.CommonUtilities
                        .isConnectingToInternet(getApplicationContext())) {
                    com.areratech.newpatrimony.CommonUtilities
                            .showLocationSettingsAlert(FirstProfileActivity.this);
                }
                else{
                    getMyLocationAddress();
                }*/
            }
        });

        /*Google place Api*/
        mACTVAddress.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_list));
        mACTVAddress.setThreshold(2);
        mACTVAddress.setOnItemClickListener(this);

        emailText = (EditText) findViewById(R.id.email_text_id);

        profileSubmitButton = (Button) findViewById(R.id.profile_submit_button_id);

        if (checkPlayServices()) {

            buildGoogleApiClient();

        }

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FirstProfileActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Upload your photo...");
/*********Camera********/
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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


        profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                profileSubmitButton.startAnimation(animFadein);

                final String email1 = emailText.getText().toString();

                if (mACTVAddress.getText().toString().length() == 0) {
                    mACTVAddress.setError("Please Enter Your Valid Address");
                } else if ((!isValidEmail(email1)) && (emailText.getText().toString().length() > 0)) {
                    emailText.setError("Invalid Email");
                } else if (profileimg.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                   /* Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                    byte[] ba = bao.toByteArray();
                    encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);*/

                } else {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (!isInternetPresent) {
                        showAlertDialog(FirstProfileActivity.this,
                                "No Internet Connection", "You don't have internet connection.", false);
                    } else {

                        loginSessionManager = new LoginSessionManager(getApplicationContext());

                        HashMap<String, String> user = loginSessionManager.getUserDetails();

                        idString = user.get(LoginSessionManager.KEY_ID);

                        email = emailText.getText().toString().trim();
                        //address = addressText.getText().toString();
                        address = mACTVAddress.getText().toString();
                        txtlatstr = txtlat.getText().toString().trim();
                        txtlonstr = txtlon.getText().toString().trim();

                        FirstProfileTask task = new FirstProfileTask();
                        task.execute("http://petsapp.petsappindia.co.in/pat.asmx/register");
                    }
                }
            }
        });
    }

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
              /*  Toast.makeText(getApplicationContext(), strAddress.toString(), Toast.LENGTH_LONG)
                        .show();*/
            } else {
                //  mACTVAddress.setText("Sorry No location found..!");
                showAlertDialog(FirstProfileActivity.this,
                        "Address", "Please enter your address manually.", false);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //  Toast.makeText(getApplicationContext(), "Sorry could not get address..!", Toast.LENGTH_LONG).show();
        }
    }

    /*Start Google place api*/
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

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
/*End Google place api*/

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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

    private class FirstProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
            /*dialog = ProgressDialog.show(FirstProfileActivity.this, "Uploading Data",
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
                        "&mail=" + URLEncoder.encode(email, "UTF-8") +
                        "&addr=" + URLEncoder.encode(address, "UTF-8") +
                        "&img=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&lat=" + URLEncoder.encode("0.0", "UTF-8") +
                        "&lon=" + URLEncoder.encode("0.0", "UTF-8");
                System.out.println("lat==" + txtlatstr);
                System.out.println("lat==" + txtlonstr);
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
                        //  id = obj.getString("id");
                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();

                        Intent fp = new Intent(getApplicationContext(), StartPageActivity.class);
                        startActivity(fp);
                        finish();
                        Log.d("result++++", result);
                    } else {
                        //  Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            //   dialog.dismiss();
        }
    }
}
