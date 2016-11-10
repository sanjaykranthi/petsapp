package com.petsapp.android;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.petsapp.android.Model.AdoptRequestModel;
import com.petsapp.android.Model.ItemObject;
import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.petsapp.android.sessionLogin.MapSession;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PetRegisterActivity extends AppCompatActivity {

    static String encodedImage;
    final Context context = this;
    Spinner dogCatSpinner, heightSpinner, sizeSpinner, genderSpinner;
    AutoCompleteTextView breedauto;
    EditText petNameText, genderText, ageText, weightText, aboutText, heightText, ageMonthText;
    RadioGroup vacciRadioGroup, breedRadioGroup;
    RadioButton vacciRadioYes, vacciRadioNo, breedRadioYes, breedRadioNo, vacciRadioButton, breedRadioButton;
    Button doneButton;
    String breedAutoString;
    String petName, gender, age = "", weight = "", about = "";
    JSONArray arr = null;
    String[] petdog = {"Boxer", "Pug", "Pomeranian", "Saint Bernord", "Dachshund"};
    String[] petcat = {"Spotted", "Himalayan", "Leopard", "Siamese", "Maine Coon"};
    TextView uploadImage;
    ImageView profileimg;
    LoginSessionManager loginSessionManager;
    MapSession mapSession;
    String idString;
    ArrayAdapter<String> petcategory;
    String dogCatSpinner1, breedSpinner1, heightSpinner1 = null, sizeSpinner1, vacciradioG1 = "", breedradioG1 = "", genderSpinner1;
    int petcatepos, sizePos, breedPos, genderPos;
    ConnectionDetector cd;
    String petcatestr, weightVal = "", ageVal = "", ageMonthVal = "";
    String[] categ = {
            "Dog",
            "Cat",
            "Others"
    };
    int weightInt = 0;
    Calendar c = Calendar.getInstance();
    String formateDate;
    String statestatus, statev;
    float monthToYear, totalAge;
    private ProgressDialog dialog;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private String urlParameters;
    private Boolean Isinternetpresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Rpetlist.class);
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
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);
//
        breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId);
        sizeSpinner = (Spinner) findViewById(R.id.size_spinnerId);
        dogCatSpinner = (Spinner) findViewById(R.id.dogcat_spinnerId);
        petcategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categ);
        petcategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogCatSpinner.setAdapter(petcategory);

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

                            sizeSpinner.setVisibility(View.VISIBLE);

                        } else if (petcatepos == 1) {
                            petcatestr = "cat";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                            sizeSpinner.setVisibility(View.GONE);

                        } else if (petcatepos == 2) {
                            petcatestr = "others";

                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                            sizeSpinner.setVisibility(View.GONE);

                        } else {
                            //      Toast.makeText(getApplicationContext(), "No Other Pets are Available", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        // heightSpinner = (Spinner) findViewById(R.id.height_spinnerId);
        heightText = (EditText) findViewById(R.id.height_spinnerId);


        petNameText = (EditText) findViewById(R.id.petname_text_id);
        genderSpinner = (Spinner) findViewById(R.id.gender_text_id);
        ageText = (EditText) findViewById(R.id.age_text_id);
        ageMonthText = (EditText) findViewById(R.id.ageMonth_text_id);
        weightText = (EditText) findViewById(R.id.weight_text_id);
        aboutText = (EditText) findViewById(R.id.about_text_id);

        vacciRadioGroup = (RadioGroup) findViewById(R.id.vacci_radiogrp_id);
        //  breedRadioGroup = (RadioGroup) findViewById(R.id.breed_radiogrp_id);

        vacciRadioYes = (RadioButton) findViewById(R.id.vacci_yes_radio_id);
        vacciRadioNo = (RadioButton) findViewById(R.id.vacci_no_radio_id);

        doneButton = (Button) findViewById(R.id.done_button_id);

        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
        img.setBounds(0, 0, 20, 20);

        //  breedauto.setCompoundDrawables(null, null, img, null);
        breedauto.setHint("Breed");
        aboutText.setHint("About");
        //  petNameText.setCompoundDrawables(null, null, img, null);

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PetRegisterActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Please upload your pet photo...");
/*********Camera********/
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      /*  Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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

        sizePos = sizeSpinner.getSelectedItemPosition();
        //  breedPos = breedSpinner.getSelectedItemPosition();
        genderPos = genderSpinner.getSelectedItemPosition();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                doneButton.startAnimation(animFadein);

                ageVal = ageText.getText().toString();
                ageMonthVal = ageMonthText.getText().toString();

                if (ageVal.length() == 0) {
                    ageVal = "0";
                }

                if (ageMonthVal.length() == 0) {
                    age = ageVal;
                } else {
                    try {
                        //   monthToYear = Float.parseFloat(ageMonthVal) / 12;
                        monthToYear = Float.parseFloat(new DecimalFormat("##.#").format(Float.parseFloat(ageMonthVal) / 12));
                        totalAge = Integer.parseInt(ageVal) + monthToYear;
                        age = String.valueOf(totalAge);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (ArithmeticException ae) {
                        ae.printStackTrace();
                    }
                }
                weightVal = weightText.getText().toString();

                String genderPosString = genderSpinner.getSelectedItem().toString();
                String sizePosString = sizeSpinner.getSelectedItem().toString();

                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                img.setBounds(0, 0, 30, 30);

                if (petNameText.getText().toString().length() == 0) {
                    //   petNameText.setError("Please Enter Your Pet nmae");
                    //    petNameText.setCompoundDrawables(null, null, img, null);
                    Toast.makeText(getApplicationContext(), "Please enter pet name", Toast.LENGTH_SHORT).show();
                } else if (breedauto.getText().toString().length() == 0) {
                    //  breedauto.setError("Please Enter Breed Name");
                    //  breedauto.setCompoundDrawables(null, null, img, null);
                    Toast.makeText(getApplicationContext(), "Please enter breed name", Toast.LENGTH_SHORT).show();
                } else if (genderPosString.equalsIgnoreCase("Gender")) {
                    Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                }/* else if (ageText.getText().toString().length() == 0) {
                    //   ageText.setError("Please Enter age between 0 to 20 years");
                    ageText.setCompoundDrawables(null, null, img, null);
                } */ else if (ageVal.length() > 0 && ((Integer.parseInt(ageVal)) > 40)) {
                    Toast.makeText(getApplicationContext(), "Please Enter age between 0 to 40 yrs.", Toast.LENGTH_SHORT).show();
                } else if (ageMonthVal.length() > 0 && ((Integer.parseInt(ageMonthVal)) > 11)) {
                    Toast.makeText(getApplicationContext(), "Please Enter month between 0 to 11.", Toast.LENGTH_SHORT).show();
                } else if (weightVal.length() > 0 && ((Integer.parseInt(weightVal)) > 100)) {
                    //   weightText.setError("Please Enter age between 0 to 100 Kg.");
                    Toast.makeText(getApplicationContext(), "Please Enter weight between 0 to 100 Kg.", Toast.LENGTH_SHORT).show();
                    //  weightText.setCompoundDrawables(null, null, img, null);
                } /*else if (sizePosString.equalsIgnoreCase("Size")) {
                    Toast.makeText(getApplicationContext(), "Please select size", Toast.LENGTH_SHORT).show();
                }*/ else if (vacciRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select vaccination", Toast.LENGTH_SHORT).show();
                }/* else if (breedRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select cross breed", Toast.LENGTH_SHORT).show();
                }*/ else if (profileimg.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    cd = new ConnectionDetector(getApplicationContext());
                    Isinternetpresent = cd.isConnectingToInternet();
                    if (!Isinternetpresent) {
                        showAlertDialog(PetRegisterActivity.this,
                                "No Internet Connection", "You don't have internet connection.", false);
                    } else {
                        petName = petNameText.getText().toString();
                        // gender = genderText.getText().toString().trim();
                        //    age = ageText.getText().toString().trim();
                        weight = weightText.getText().toString().trim();
                        about = aboutText.getText().toString().trim();

                        dogCatSpinner1 = dogCatSpinner.getSelectedItem().toString();
                        // breedSpinner1 = breedSpinner.getSelectedItem().toString();
                        //   heightSpinner1 = heightSpinner.getSelectedItem().toString();
                        heightSpinner1 = heightText.getText().toString();
                        sizeSpinner1 = sizeSpinner.getSelectedItem().toString();

                        genderSpinner1 = genderSpinner.getSelectedItem().toString();

                        int vacciRadioGroup1 = vacciRadioGroup.getCheckedRadioButtonId();
                        //   int breedRadioGroup1 = breedRadioGroup.getCheckedRadioButtonId();

                        vacciRadioButton = (RadioButton) findViewById(vacciRadioGroup1);
                        //    breedRadioButton = (RadioButton) findViewById(breedRadioGroup1);

                        vacciradioG1 = vacciRadioButton.getText().toString();
                        //                  breedradioG1 = breedRadioButton.getText().toString();
                        breedAutoString = breedauto.getText().toString();
                        cd = new ConnectionDetector(getApplicationContext());
                        Isinternetpresent = cd.isConnectingToInternet();

                        if (sizeSpinner1.equalsIgnoreCase("Size")) {
                            sizeSpinner1 = "";
                        } else {
                        }


                        if (Isinternetpresent) {
                            PetRegisterTask task = new PetRegisterTask();
                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/petMate");
                        } else {
                            //     Toast.makeText(PetRegisterActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
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

      /* alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {

               Intent in = new Intent(getApplicationContext(), InfoActivity.class);
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
   /* public void showAlertDialog1(Context context, String title, String message,
                                 Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.alert_tick);
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent in = new Intent(getApplicationContext(), Rpetlist.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(in);
                finish();
            }
        });

        alertDialog.show();
    }*/

       /* final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent in = new Intent(getApplicationContext(), Rpetlist.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(in);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  finish();
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/

    // }

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

    private class PetRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
            /*dialog = ProgressDialog.show(PetRegisterActivity.this, "Loading",
                    "Please wait...", true);
            dialog.show();*/

            VeryLongAsyncTask1 pars88 = new VeryLongAsyncTask1(context);
            pars88.execute();
        }

        @Override
        protected String doInBackground(String... urls) {

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            formateDate = df.format(c.getTime());

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&pcat=" + URLEncoder.encode(dogCatSpinner1, "UTF-8") +
                        "&pname=" + URLEncoder.encode(petName, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedAutoString, "UTF-8") +
                        "&gndr=" + URLEncoder.encode(genderSpinner1, "UTF-8") +
                        "&age=" + URLEncoder.encode(age, "UTF-8") +
                        "&wght=" + URLEncoder.encode(weight, "UTF-8") +
                        "&hght=" + URLEncoder.encode(heightSpinner1, "UTF-8") +
                        "&size=" + URLEncoder.encode(sizeSpinner1, "UTF-8") +
                        "&vac=" + URLEncoder.encode(vacciradioG1, "UTF-8") +
                        "&abt=" + URLEncoder.encode(about, "UTF-8") +
                        "&img=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&pid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&state=" + URLEncoder.encode("0", "UTF-8") +
                        "&adoptstate=" + URLEncoder.encode("0", "UTF-8") +
                        "&lfstate=" + URLEncoder.encode("0", "UTF-8") +
                        "&date=" + URLEncoder.encode(formateDate, "UTF-8") +
                        "&adate=" + URLEncoder.encode(formateDate, "UTF-8");
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
            String mate = "ADOPTION";
            String remove = "REMOVE";
            String mate1 = "MATE";


            try {
                List<AdoptRequestModel> itemModelList = new ArrayList<>();
                itemModelList.clear();
                CommonUtilities.adoptionreglist.clear();


                List<ItemObject> itemModelList1 = new ArrayList<>();
                itemModelList1.clear();
                CommonUtilities.playdatereglist.clear();

                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);

                    AdoptRequestModel model = new AdoptRequestModel();

                    model.setName(obj.getString("pname"));
                    model.setbreed(obj.getString("breed"));
                    model.setgender(obj.getString("gender"));
                    model.setage(obj.getString("age"));
                    model.setcategory(obj.getString("cat"));
                    model.setweight(obj.getString("weight"));
                    model.setheight(obj.getString("height"));
                    model.setsize(obj.getString("size"));
                    model.setvac(obj.getString("vac"));
                    model.setcrsbrdreqd(obj.getString("crsbrdreqd"));
                    model.setphone(obj.getString("phone"));
                    model.setlat(obj.getString("lat"));
                    model.setlon(obj.getString("lon"));
                    model.setabout(obj.getString("about"));
                    model.setpstate(obj.getString("adoptstate"));
                    model.setpetmateid(obj.getString("petmateid"));
                    model.setThumbnailUrl(obj.getString("images"));
                    model.setPhoto(obj.getString("images"));
                    statestatus = obj.getString("adoptstate");

                    if (statestatus.equalsIgnoreCase("0")) {
                        model.settextname(mate);
                    } else {
                        model.settextname(remove);
                    }

                    String pName = obj.getString("pname");
                    if (pName.equalsIgnoreCase("")) {
                        model.setName("(No Name)");
                    } else {
                        model.setName(pName);
                    }

                    itemModelList.add(model);
                    CommonUtilities.adoptionreglist.add(model);

                    ItemObject model1 = new ItemObject();

                    model1.setName(obj.getString("pname"));
                    model1.setbreed(obj.getString("breed"));
                    model1.setgender(obj.getString("gender"));
                    model1.setage(obj.getString("age"));
                    model1.setcategory(obj.getString("cat"));
                    model1.setweight(obj.getString("weight"));
                    model1.setheight(obj.getString("height"));
                    model1.setsize(obj.getString("size"));
                    model1.setvac(obj.getString("vac"));
                    model1.setcrsbrdreqd(obj.getString("crsbrdreqd"));
                    model1.setphone(obj.getString("phone"));
                    model1.setlat(obj.getString("lat"));
                    model1.setlon(obj.getString("lon"));
                    model1.setabout(obj.getString("about"));
                    model1.setpstate(obj.getString("pstate"));
                    model1.setpetmateid(obj.getString("petmateid"));
                    model1.setThumbnailUrl(obj.getString("images"));
                    model1.setPhoto(obj.getString("images"));
                    statestatus = obj.getString("pstate");
                    if (statestatus.equalsIgnoreCase("0")) {
                        model1.settextname(mate1);
                    } else {
                        model1.settextname(remove);
                    }

                    String pName1 = obj.getString("pname");
                    if (pName1.equalsIgnoreCase("")) {
                        model1.setName("(No Name)");
                    } else {
                        model.setName(pName);
                    }

                    itemModelList1.add(model1);
                    CommonUtilities.playdatereglist.add(model1);


                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {
                        petNameText.setText("");
                        ageText.setText("");
                        weightText.setText("");
                        heightText.setText("");
                        aboutText.setText("");

                        encodedImage = null;
                        profileimg.setImageDrawable(null);

                        showAlertDialog1(PetRegisterActivity.this, false);
                       /* showAlertDialog1(PetRegisterActivity.this,
                                "Successfull !", "Successfully Registered..", false);*/
                        //     Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                        Intent in = new Intent(getApplicationContext(), Rpetlist.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(in);
                        finish();
                        //    System.out.println("staus----" + state);
                     /*   Intent in = new Intent(getApplicationContext(), AddPetMateActivity.class);
                        startActivity(in);*/

                        Log.d("result=====post", result);
                        Log.d("result=====state", state);
                        //        dialog.dismiss();
                    } else {
                        //   Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        //         dialog.dismiss();
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

        }
    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(PetRegisterActivity.this, HomeMateActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }*/

}
