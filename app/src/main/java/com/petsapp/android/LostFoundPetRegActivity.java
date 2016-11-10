package com.petsapp.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.petsapp.android.Model.LostFoundRequestModel;
import com.petsapp.android.sessionLogin.LoginSessionManager;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LostFoundPetRegActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final int DATE_PICKER_ID = 0;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCZ8-jumSCw5blH5Hh0Dntat1ENDpVnNJk";
    static String encodedImage;
    final Context context = this;
    Spinner dogCatSpinner, genderSpinner, lostorFoundspinner, dogCatSpinnerFnd;
    AutoCompleteTextView breedauto, areaText, areaTextFnd, areaFoundAt, breedautoFound;
    EditText phoneTextFnd, dateTextFnd, aboutTextFnd;
    EditText ageText, dateText, colorText, streetText, aboutText, phoneText, pnameText, ageMonthText;
    TextView txtlat, txtlon;
    Button doneButton;
    String breedAutoString = "", breedAutoFoundString = "";
    String gender, age = "", color = "", date = "", street = "", area = "", about = "", phone = "", phoneFnd = "", pname = "", areaFnd = "", areaFndAt = "";
    JSONArray arr = null;
    String latString1 = "0.0", lonString1 = "0.0";
    String lnfString;
    String[] petdog = {"Boxer", "Pug", "Pomeranian", "Saint Bernord", "Dachshund"};
    String[] petcat = {"Spotted", "Himalayan", "Leopard", "Siamese", "Maine Coon"};
    TextView uploadImage;
    ImageView profileimg, profileimgFnd;
    LoginSessionManager loginSessionManager;
    String idString;
    ArrayAdapter<String> petcategory;
    ArrayAdapter<String> lostnfoundAdp;
    String dogCatSpinner1 = "", lostorFoundspinner1 = "", genderSpinner1 = "", dogCatSpinnerFnd1 = "";
    int petcatepos, sizePos, breedPos, genderPos, petcateposFnd;
    ConnectionDetector cd;
    String petcatestr, weightVal = "", ageVal = "", petcatestrFnd, ageMonthVal = "";
    String[] categ = {
            "Dog",
            "Cat",
            "Bird",
            "Others"
    };
    // "Please Select",
    String[] lostOrfound = {
            "I lost my pet",
            "I found a lost pet"
    };
    int weightInt = 0;
    String genderPosString, lostnfoundPosString;
    LinearLayout lostLayout, foundLayout;
    String statestatus, statev, pName, age1;
    String lost = "Missing";
    String found = "Spotted";
    float monthToYear, totalAge;
    private ProgressDialog dialog;
    private boolean booladptr = true;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private String urlParameters;
    private Boolean Isinternetpresent = false;
    private int day, month, year;
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            dateText.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));
        }
    };

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
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {

                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_pet_reg);
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

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#dd8332"));
        }

        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();
        idString = user.get(LoginSessionManager.KEY_ID);

        lostLayout = (LinearLayout) findViewById(R.id.layout_lost);
        foundLayout = (LinearLayout) findViewById(R.id.layout_found);
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);
//
        breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId);
        breedautoFound = (AutoCompleteTextView) findViewById(R.id.breed_spinnerFound_Id);

        /*For Found*/
        areaTextFnd = (AutoCompleteTextView) findViewById(R.id.addr_text_id_fnd);
        areaFoundAt = (AutoCompleteTextView) findViewById(R.id.area_text_id_fnd);

        phoneTextFnd = (EditText) findViewById(R.id.phone_text_id_fnd);
        //  dateTextFnd = (EditText) findViewById(R.id.date_text_Id_fnd);
        //  aboutTextFnd = (EditText) findViewById(R.id.about_text_id_fnd);
        dogCatSpinnerFnd = (Spinner) findViewById(R.id.dogcat_spinnerId_fnd);
        //  profileimgFnd = (ImageView) findViewById(R.id.profileImgId_fnd);
         /*For Found*/

        dogCatSpinner = (Spinner) findViewById(R.id.dogcat_spinnerId);
        petcategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categ);
        petcategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogCatSpinner.setAdapter(petcategory);

        lostorFoundspinner = (Spinner) findViewById(R.id.lostorfound_spinner_id);
        lostnfoundAdp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lostOrfound);
        lostnfoundAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lostorFoundspinner.setAdapter(lostnfoundAdp);

        dogCatSpinnerFnd = (Spinner) findViewById(R.id.dogcat_spinnerId_fnd);
        petcategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categ);
        petcategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogCatSpinnerFnd.setAdapter(petcategory);

        dogCatSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        petcatepos = dogCatSpinner.getSelectedItemPosition();
                        petcatestr = dogCatSpinner.getItemAtPosition(position).toString();
                        if (petcatepos == 0) {
                            petcatestr = "dog";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else if (petcatepos == 1) {
                            petcatestr = "cat";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else if (petcatepos == 2) {
                            petcatestr = "bird";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else if (petcatepos == 3) {
                            petcatestr = "others";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else {
                            //     Toast.makeText(getApplicationContext(), "No Other Pets are Available", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
        /****LnF***/

       /*For Found*/

       /* dogCatSpinnerFnd.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        petcateposFnd = dogCatSpinnerFnd.getSelectedItemPosition();
                        petcatestrFnd = dogCatSpinnerFnd.getItemAtPosition(position).toString();
                        if (petcateposFnd == 0) {
                            petcatestr = "dog";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else if (petcateposFnd == 1) {
                            petcatestrFnd = "cat";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");
                        } else {
                            Toast.makeText(getApplicationContext(), "No Other Pets are Available", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );*/

       /*End For Found*/

        lostorFoundspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        int typos = lostorFoundspinner.getSelectedItemPosition();
                      /*  if (typos == 0) {
                            *//*pnameText.setHint("Pet Name");  //(Optional)
                            breedauto.setHint("Breed");   //(Optional)
                            phoneText.setHint("Phone no.*");
                            streetText.setHint("Street *");
                            ageText.setHint("Age");   //(Optional)
                            colorText.setHint("Colour"); //(Optional)
                            dateText.setHint("Date Lost *");
                            areaText.setHint("Area *");
                            aboutText.setHint("About *");*//*

                            lostLayout.setVisibility(View.VISIBLE);
                            foundLayout.setVisibility(View.GONE);

                        }*/

                        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                        img.setBounds(0, 0, 20, 20);

                        if (typos == 0) {
                            pnameText.setHint("Pet Name ");
                            breedauto.setHint("Breed ");
                            //   breedauto.setCompoundDrawables(null, null, img, null);
                            phoneText.setHint("Phone no. ");
                            //   phoneText.setCompoundDrawables(null, null, img, null);
                            //  Upload Photo (Optional)
                            ageText.setHint("Age(Years)");          //(Optional)
                            ageMonthText.setHint("Age(Months)");
                            colorText.setHint("Colour");    // (Optional)
                            dateText.setHint("Date Lost ");
                            //    dateText.setCompoundDrawables(null, null, img, null);
                            areaText.setHint("Last Seen At ");
                            //   areaText.setCompoundDrawables(null, null, img, null);
                            //   aboutText.setHint("About (if your pet is less than one year please specify here)");
                            /*Html.fromHtml("About  <font color='#969595'>(if your pet is less than one year please specify here)</font>")*/
                            aboutText.setHint("Description");

                            lostLayout.setVisibility(View.VISIBLE);
                            foundLayout.setVisibility(View.GONE);
                        }
                        if (typos == 1) {

                            breedautoFound.setHint("Breed ");

                            areaTextFnd.setHint("Address ");
                            //    areaTextFnd.setCompoundDrawables(null, null, img, null);
                            phoneTextFnd.setHint("Phone no. ");
                            //    phoneTextFnd.setCompoundDrawables(null, null, img, null);
                            dateText.setHint("Date Found ");
                            //    dateText.setCompoundDrawables(null, null, img, null);
                            areaFoundAt.setHint("Area Spotted ");
                            //    areaFoundAt.setCompoundDrawables(null, null, img, null);
                            //     aboutText.setHint("About (if your pet is less than one year please specify here)");
                            aboutText.setHint("Description");
                            lostLayout.setVisibility(View.GONE);
                            foundLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );


        ageText = (EditText) findViewById(R.id.age_text_id);
        ageMonthText = (EditText) findViewById(R.id.ageMonth_text_id);
        colorText = (EditText) findViewById(R.id.color_text_id);
        dateText = (EditText) findViewById(R.id.date_text_Id);
        streetText = (EditText) findViewById(R.id.street_text_id);
        areaText = (AutoCompleteTextView) findViewById(R.id.area_text_id);
        aboutText = (EditText) findViewById(R.id.about_text_id);
        phoneText = (EditText) findViewById(R.id.phone_text_id);
        pnameText = (EditText) findViewById(R.id.pname_text_id);
        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlon = (TextView) findViewById(R.id.txtlon);
        dogCatSpinner = (Spinner) findViewById(R.id.dogcat_spinnerId);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner_id);
        doneButton = (Button) findViewById(R.id.done_button_id);

        lnfString = lostorFoundspinner.getSelectedItem().toString();

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        areaText.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_list));
        areaText.setThreshold(2);
        areaText.setOnItemClickListener(this);

        /*Found*/
        areaTextFnd.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_list));
        areaTextFnd.setThreshold(2);
        areaTextFnd.setOnItemClickListener(this);

        areaFoundAt.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_list));
        areaFoundAt.setThreshold(2);
        areaFoundAt.setOnItemClickListener(this);

       /* areaText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (booladptr) {
                    booladptr = false;
                    addAdapterToViews();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                bindViews();
            }
        });*/


        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LostFoundPetRegActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Please upload your pet photo...");
/*********Camera********/
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
                alertDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                    }
                });
                alertDialog.show();
            }
        });


        genderPos = genderSpinner.getSelectedItemPosition();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                doneButton.startAnimation(animFadein);

                int typos = lostorFoundspinner.getSelectedItemPosition();

                if (typos == 0) {
                    forLostPet();
                }
                if (typos == 1) {
                    forFoundPet();
                }
            }
        });
    }

    private void bindViews() {
        areaText = (AutoCompleteTextView) findViewById(R.id.area_text_id);
        String address = areaText.getText().toString();
        GeocodingLocation.getAddressFromLocation(address,
                getApplicationContext(), new GeocoderHandler());
    }

    private void addAdapterToViews() {
        areaText.setAdapter(new AutoCompleteAdapter(this));
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // set date picker as current date
                DatePickerDialog _date = new DatePickerDialog(this, pickerListener, year, month,
                        day) {
                    @Override
                    public void onDateChanged(DatePicker view, int myear, int mmonthOfYear, int mdayOfMonth) {
                        if (myear > year)
                            view.updateDate(year, month, day);

                        if (mmonthOfYear > month && myear == year)
                            view.updateDate(year, month, day);

                        if (mdayOfMonth > day && myear == year && mmonthOfYear == month)
                            view.updateDate(year, month, day);
                    }
                };
                return _date;

        }
        return null;
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

    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void showAlertDialog1(Context context, Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        //  alertDialog.setIcon(R.drawable.alert_tick);
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent in = new Intent(getApplicationContext(), AddLostFoundActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(in);
                finish();
            }
        });*/
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_register, null);
        alertDialog.setView(dialogLayout);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        //
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView image = (ImageView) alertDialog.findViewById(R.id.goProDialogImage);
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.greentick);
                float imageWidthInPX = (float) image.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float) icon.getHeight() / (float) icon.getWidth()));
                image.setLayoutParams(layoutParams);


            }
        });


        //
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

   /* public void showAlertDialog1(Context context, String title, String message,
                                 Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.alert_tick);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent in = new Intent(getApplicationContext(), AddLostFoundActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                startActivity(in);

                finish();
            }
        });
        alertDialog.show();
    }*/

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //   Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /*End Place Api*/
    public void forLostPet() {

        ageVal = ageText.getText().toString();

        /*new age*/
        ageMonthVal = ageMonthText.getText().toString();

        if (ageVal.length() == 0) {
            ageVal = "0";
        }

        if (ageMonthVal.length() == 0) {
            age = ageVal;
        } else {
            try {
                //  monthToYear = Float.parseFloat(ageMonthVal) / 12;
                monthToYear = Float.parseFloat(new DecimalFormat("##.#").format(Float.parseFloat(ageMonthVal) / 12));
                totalAge = Integer.parseInt(ageVal) + monthToYear;
                age = String.valueOf(totalAge);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ArithmeticException ae) {
                ae.printStackTrace();
            }
        }
        /*End new age*/

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
        img.setBounds(0, 0, 30, 30);

        genderPosString = genderSpinner.getSelectedItem().toString();
        lostnfoundPosString = lostorFoundspinner.getSelectedItem().toString();
        /*if (lostnfoundPosString.equalsIgnoreCase("Please Select")) {
            Toast.makeText(getApplicationContext(), "Please select missing or spotted", Toast.LENGTH_SHORT).show();
        }*//* else if (pnameText.getText().toString().length() == 0) {
          //  pnameText.setError("Please enter name");
            pnameText.setCompoundDrawables(null, null, img, null);
        }*/
        if (breedauto.getText().toString().length() == 0) {
            //    breedauto.setError("Please select breed");
            //  breedauto.setCompoundDrawables(null, null, img, null);
            Toast.makeText(getApplicationContext(), "Please select breed", Toast.LENGTH_SHORT).show();
        } else if (genderPosString.equalsIgnoreCase("Gender")) {
            Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
        } else if (phoneText.getText().toString().length() > 0 && phoneText.getText().toString().length() < 10) {
            Toast.makeText(getApplicationContext(), "Please Enter phone number", Toast.LENGTH_SHORT).show();
        } else if (ageVal.length() > 0 && ((Integer.parseInt(ageVal)) > 40)) {
            Toast.makeText(getApplicationContext(), "Please Enter age between 0 to 40 yrs.", Toast.LENGTH_SHORT).show();
        } else if (ageMonthVal.length() > 0 && ((Integer.parseInt(ageMonthVal)) > 11)) {
            Toast.makeText(getApplicationContext(), "Please Enter month between 0 to 11.", Toast.LENGTH_SHORT).show();
        } else if (areaText.getText().toString().length() == 0) {
            //   areaText.setError("Please Enter full address");
            Toast.makeText(getApplicationContext(), "Please Enter full address", Toast.LENGTH_SHORT).show();
            // areaText.setCompoundDrawables(null, null, img, null);
        } else if ((dateText.getText().toString().length() == 0)) {
            //    dateText.setError("Please enter date");
            Toast.makeText(getApplicationContext(), "Please enter date", Toast.LENGTH_SHORT).show();
            // dateText.setCompoundDrawables(null, null, img, null);
        }/* else if (aboutText.getText().toString().length() == 0) {
          //  aboutText.setError("Please Enter about pet");
            aboutText.setCompoundDrawables(null, null, img, null);
        }*/ else if (encodedImage == null) { //data:image/png;base64,

            Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.petprofile);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();
            encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
        } else {
            cd = new ConnectionDetector(getApplicationContext());
            Isinternetpresent = cd.isConnectingToInternet();
            if (!Isinternetpresent) {
                showAlertDialog(LostFoundPetRegActivity.this,
                        "No Internet Connection", "You don't have internet connection.", false);
            } else {

                //     age = ageText.getText().toString().trim();
                color = colorText.getText().toString().trim();
                date = dateText.getText().toString().trim();
                street = streetText.getText().toString().trim();
                area = areaText.getText().toString().trim();
                about = aboutText.getText().toString().trim();
                breedAutoString = breedauto.getText().toString();
                phone = phoneText.getText().toString();
                pname = pnameText.getText().toString().trim();

                dogCatSpinner1 = dogCatSpinner.getSelectedItem().toString();
                genderSpinner1 = genderSpinner.getSelectedItem().toString();
                lostorFoundspinner1 = lostorFoundspinner.getSelectedItem().toString();
                latString1 = txtlat.getText().toString().trim();
                lonString1 = txtlon.getText().toString().trim();

                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                if (Isinternetpresent) {

                    LostPetRegisterTask task = new LostPetRegisterTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/addlostnfound");
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(LostFoundPetRegActivity.this);

                   // builder.setTitle("Missing");
                    builder.setMessage("Register lost pet?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            LostPetRegisterTask task = new LostPetRegisterTask();
                            task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/addlostnfound"});
                            //  dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
*/

/*
                    LostPetRegisterTask task = new LostPetRegisterTask();
                    task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/addlostnfound"});
*/

                } else {
                    //      Toast.makeText(LostFoundPetRegActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**/
    public void forFoundPet() {

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
        img.setBounds(0, 0, 20, 20);

        genderPosString = genderSpinner.getSelectedItem().toString();
        lostnfoundPosString = lostorFoundspinner.getSelectedItem().toString();
      /*  if (lostnfoundPosString.equalsIgnoreCase("Please Select")) {
            Toast.makeText(getApplicationContext(), "Please select missing or spotted", Toast.LENGTH_SHORT).show();
        }*/
        if (areaTextFnd.getText().toString().length() == 0) {
            //  streetText.setError("Please Enter street name");
            Toast.makeText(getApplicationContext(), "Please enter address", Toast.LENGTH_SHORT).show();
            // areaTextFnd.setCompoundDrawables(null, null, img, null);
        } /*else if ((phoneTextFnd.getText().toString().length() < 10)) {
            //  phoneText.setError("Please Enter phone no.");
           // phoneTextFnd.setCompoundDrawables(null, null, img, null);
            Toast.makeText(getApplicationContext(), "Please enter phone no.", Toast.LENGTH_SHORT).show();
        }*/ else if (phoneTextFnd.getText().toString().length() > 0 && phoneTextFnd.getText().toString().length() < 10) {
            Toast.makeText(getApplicationContext(), "Please Enter phone number", Toast.LENGTH_SHORT).show();
        } else if (areaFoundAt.getText().toString().length() == 0) {
            // areaText.setError("Please Enter full address");
            //  areaFoundAt.setCompoundDrawables(null, null, img, null);
            Toast.makeText(getApplicationContext(), "Please enter area name", Toast.LENGTH_SHORT).show();
        } else if ((dateText.getText().toString().length() == 0)) {
            //  dateText.setError("Please Enter date");
            //dateText.setCompoundDrawables(null, null, img, null);
            Toast.makeText(getApplicationContext(), "Please enter date", Toast.LENGTH_SHORT).show();
        }/* else if (profileimg.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
        } else if (encodedImage == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
        }*/ else if (encodedImage == null) { //data:image/png;base64,

            Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.petprofile);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();
            encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
        } else {
            cd = new ConnectionDetector(getApplicationContext());
            Isinternetpresent = cd.isConnectingToInternet();
            if (!Isinternetpresent) {
                showAlertDialog(LostFoundPetRegActivity.this,
                        "No Internet Connection", "You don't have internet connection.", false);
            } else {

                date = dateText.getText().toString().trim();
                areaFnd = areaTextFnd.getText().toString().trim();
                areaFndAt = areaFoundAt.getText().toString().trim();
                about = aboutText.getText().toString().trim();

                breedAutoFoundString = breedautoFound.getText().toString();

                phoneFnd = phoneTextFnd.getText().toString();
                dogCatSpinnerFnd1 = dogCatSpinnerFnd.getSelectedItem().toString();
                lostorFoundspinner1 = lostorFoundspinner.getSelectedItem().toString();
                latString1 = txtlat.getText().toString().trim();
                lonString1 = txtlon.getText().toString().trim();

                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                if (Isinternetpresent) {

                    FoundPetRegisterTask task = new FoundPetRegisterTask();
                    task.execute("http://petsapp.petsappindia.co.in/pat.asmx/foundlostpet");

                   /* AlertDialog.Builder builder = new AlertDialog.Builder(LostFoundPetRegActivity.this);

                //    builder.setTitle("Spotted");
                    builder.setMessage("Register spotted  pet?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            FoundPetRegisterTask task = new FoundPetRegisterTask();
                            task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/foundlostpet"});
                            //  dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();*/

                    /*FoundPetRegisterTask task = new FoundPetRegisterTask();
                    task.execute(new String[]{"http://petsapp.petsappindia.co.in/pat.asmx/foundlostpet"});*/

                } else {
                    //   Toast.makeText(LostFoundPetRegActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class AutoCompleteAdapter extends ArrayAdapter<Address> implements Filterable {
        private LayoutInflater mInflater;
        private Geocoder mGeocoder;
        private StringBuilder mSb = new StringBuilder();

        public AutoCompleteAdapter(final Context context) {
            super(context, -1);
            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            mGeocoder = new Geocoder(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }
            tv.setText(createFormattedAddressFromAddress(getItem(position)));
            return tv;
        }

        private String createFormattedAddressFromAddress(final Address address) {
            mSb.setLength(0);
            final int addressLineSize = address.getMaxAddressLineIndex();
            for (int i = 0; i < addressLineSize; i++) {
                mSb.append(address.getAddressLine(i));
                if (i != addressLineSize - 1) {
                    mSb.append(", ");
                }
            }
            return mSb.toString();
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    List<Address> addressList = null;
                    if (constraint != null) {
                        try {
                            addressList = mGeocoder.getFromLocationName((String) constraint, 20);
                        } catch (IOException e) {
                        }
                    }
                    if (addressList == null) {
                        addressList = new ArrayList<Address>();
                    }
                    final FilterResults filterResults = new FilterResults();
                    filterResults.values = addressList;
                    filterResults.count = addressList.size();
                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final FilterResults results) {
                    clear();
                    for (Address address : (List<Address>) results.values) {
                        add(address);
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : createFormattedAddressFromAddress((Address) resultValue);
                }
            };
            return myFilter;
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String locationAddress1 = null;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    locationAddress1 = bundle.getString("address1");
                    break;
                default:
                    locationAddress = null;
            }
            txtlat.setText(locationAddress);
            txtlon.setText(locationAddress1);
        }
    }

/*Start Place Api*/

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

    private class LostPetRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* dialog = ProgressDialog.show(LostFoundPetRegActivity.this, "Loading",
                    "Please wait...", true);
            dialog.sho*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();


        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&category=" + URLEncoder.encode(dogCatSpinner1, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedAutoString, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&age=" + URLEncoder.encode(age, "UTF-8") +
                        "&gender=" + URLEncoder.encode(genderSpinner1, "UTF-8") +
                        "&color=" + URLEncoder.encode(color, "UTF-8") +
                        "&date=" + URLEncoder.encode(date, "UTF-8") +
                        "&street=" + URLEncoder.encode(street, "UTF-8") +
                        "&area=" + URLEncoder.encode(area, "UTF-8") +
                        "&about=" + URLEncoder.encode(about, "UTF-8") +
                        "&image=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&petname=" + URLEncoder.encode(pname, "UTF-8") +
                        "&lat=" + URLEncoder.encode("0.0", "UTF-8") +
                        "&lon=" + URLEncoder.encode("0.0", "UTF-8") +
                        "&lostorfound=" + URLEncoder.encode(lostorFoundspinner1, "UTF-8");


                url = new URL(urls[0]);
                System.out.println("sending" + url);
                System.out.println("sending" + urlParameters);
                System.out.println("sending image  =" + encodedImage);
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

                List<LostFoundRequestModel> itemModelList = new ArrayList<>();
                itemModelList.clear();
                CommonUtilities.lostnfoundreglist.clear();
                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);
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
                    age1 = obj.getString("age");

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
                    if (age1.equalsIgnoreCase("")) {
                        model.setage("");

                    } else {
                        model.setage(age1 + " yrs,");
                    }


                    itemModelList.add(model);
                    CommonUtilities.lostnfoundreglist.add(model);

                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {

                        ageText.setText("");
                        ageMonthText.setText("");
                        colorText.setText("");
                        //    streetText.setText("");
                        areaText.setText("");
                        breedauto.setText("");
                        dateText.setText("");
                        aboutText.setText("");

                        encodedImage = null;
                        profileimg.setImageDrawable(null);

                        /*showAlertDialog1(LostFoundPetRegActivity.this,
                                "Successfull !", "Successfully Registered..", false);*/
                        showAlertDialog1(LostFoundPetRegActivity.this, false);
                        Intent in = new Intent(getApplicationContext(), AddLostFoundActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(in);
                        finish();
                        System.out.println("staus----" + state);
                        //   dialog.dismiss();
                    } else {
                        //        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        //  dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
            //   dialog.dismiss();

        }
    }

    private class FoundPetRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* dialog = ProgressDialog.show(LostFoundPetRegActivity.this, "Loading",
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
                urlParameters = "&uid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedAutoFoundString, "UTF-8") +
                        "&lostorfound=" + URLEncoder.encode(lostorFoundspinner1, "UTF-8") +
                        "&category=" + URLEncoder.encode(dogCatSpinnerFnd1, "UTF-8") +
                        "&addr=" + URLEncoder.encode(areaFnd, "UTF-8") +
                        "&phoneno=" + URLEncoder.encode(phoneFnd, "UTF-8") +
                        "&datef=" + URLEncoder.encode(date, "UTF-8") +
                        "&areaf=" + URLEncoder.encode(areaFndAt, "UTF-8") +
                        "&about=" + URLEncoder.encode(about, "UTF-8") +
                        "&image=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&lat=" + URLEncoder.encode("0.0", "UTF-8") +
                        "&lon=" + URLEncoder.encode("0.0", "UTF-8");

                /**
                 *
                 */


                url = new URL(urls[0]);
                System.out.println("sending" + url);
                System.out.println("sending" + urlParameters);
                System.out.println("sending image  =" + encodedImage);
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

                List<LostFoundRequestModel> itemModelList = new ArrayList<>();
                itemModelList.clear();
                CommonUtilities.lostnfoundreglist.clear();
                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);
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
                    age1 = obj.getString("age");

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
                    if (age1.equalsIgnoreCase("")) {
                        model.setage("");

                    } else {
                        model.setage(age1 + " yrs,");
                    }


                    itemModelList.add(model);
                    CommonUtilities.lostnfoundreglist.add(model);

                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {

                        ageText.setText("");
                        ageMonthText.setText("");
                        colorText.setText("");
                        streetText.setText("");
                        areaText.setText("");
                        breedauto.setText("");
                        dateText.setText("");
                        aboutText.setText("");

                       /* showAlertDialog1(LostFoundPetRegActivity.this,
                                "Successfull !", "Successfully Registered..", false);*/
                        showAlertDialog1(LostFoundPetRegActivity.this, false);
                        Intent in = new Intent(getApplicationContext(), AddLostFoundActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(in);
                        finish();
                        System.out.println("staus----" + state);
                        dialog.dismiss();
                    } else {
                        //     Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        //       dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
//            dialog.dismiss();

        }
    }

    public class BreedTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                urlParameters = "&catg=" + URLEncoder.encode(petcatestr, "UTF-8")
                        + "&user_id=" + URLEncoder.encode(idString, "UTF-8");

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

                List<String> breedModelList = new ArrayList<String>();

                arr = new JSONArray(finalJson);

                JSONObject jObj = arr.getJSONObject(0);

                System.out.println("final json---" + jObj);

                JSONArray childArray = jObj.getJSONArray("breed");

                final String[] str1 = new String[childArray.length()];

                for (int i = 0; i < childArray.length(); i++) {
                    JSONObject obj = childArray.getJSONObject(i);
                    str1[i] = obj.getString("name");
                }
                breedModelList = new ArrayList<String>();

                for (int j = 0; j < str1.length; j++) {
                    breedModelList.add(str1[j]);
                }
                return breedModelList;

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
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId);
            ArrayAdapter adp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, result);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedauto.setThreshold(1);
            breedauto.setAdapter(adp);

            /*For Found*/
            breedautoFound = (AutoCompleteTextView) findViewById(R.id.breed_spinnerFound_Id);
            ArrayAdapter adp1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, result);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedautoFound.setThreshold(1);
            breedautoFound.setAdapter(adp1);
        }
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
}
