package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.petsapp.android.sessionLogin.LoginSessionManager;
import com.petsapp.android.sessionLogin.MapSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class FirstPetRegistrationActivity extends AppCompatActivity {

    public static JSONArray jsonArr = null;
    static String encodedImage;
    Spinner dogCatSpinner, breedSpinner, heightSpinner, sizeSpinner, genderSpinner;
    EditText petNameText, genderText, ageText, weightText, aboutText, heightText;
    RadioGroup vacciRadioGroup, breedRadioGroup;
    RadioButton vacciRadioYes, vacciRadioNo, breedRadioYes, breedRadioNo, vacciRadioButton, breedRadioButton;
    Button doneButton;
    TextView skip;
    AutoCompleteTextView breedauto;
    String[] petdog = {"Boxer", "Pug", "Pomeranian", "Saint Bernord", "Dachshund"};
    String[] petcat = {"Spotted", "Himalayan", "Leopard", "Siamese", "Maine Coon"};
    String petName, gender, age, weight = null, about = null;
    JSONArray arr = null;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    TextView uploadImage;
    ImageView profileimg;
    int sizePos, genderPos;
    LoginSessionManager loginSessionManager;
    MapSession mapSession;
    String idString;
    String[] categ = {
            "Dog",
            "Cat",
            "Others"
    };
    String cate;
    ArrayAdapter<String> petcategory;
    ArrayList<String> petcateg = new ArrayList<>();
    String dogCatSpinner1, breedSpinner1, heightSpinner1 = null, sizeSpinner1, vacciradioG1 = "", breedradioG1 = "", genderSpinner1;
    int petcatepos;
    String petcatestr, weightVal = "";
    private ProgressDialog dialog;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private String urlParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_pet_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#d23d57"));
        }
        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();

        idString = user.get(LoginSessionManager.KEY_ID);

        dogCatSpinner = (Spinner) findViewById(R.id.dogcat_spinnerId);
        breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId1);
        petcategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categ);
        petcategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogCatSpinner.setAdapter(petcategory);
        dogCatSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        /*if (isInternetPresent) {*/
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

                        } else {
                            //     Toast.makeText(getApplicationContext(), "No Other Pets are Available", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
            }
        });
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);

        //  breedSpinner = (Spinner) findViewById(R.id.breed_spinnerId);
        heightText = (EditText) findViewById(R.id.height_spinnerId);
        sizeSpinner = (Spinner) findViewById(R.id.size_spinnerId);

        petNameText = (EditText) findViewById(R.id.petname_text_id);
        genderSpinner = (Spinner) findViewById(R.id.gender_text_id);
        ageText = (EditText) findViewById(R.id.age_text_id);
        weightText = (EditText) findViewById(R.id.weight_text_id);
        aboutText = (EditText) findViewById(R.id.about_text_id);

        vacciRadioGroup = (RadioGroup) findViewById(R.id.vacci_radiogrp_id);


        vacciRadioYes = (RadioButton) findViewById(R.id.vacci_yes_radio_id);
        vacciRadioNo = (RadioButton) findViewById(R.id.vacci_no_radio_id);

        doneButton = (Button) findViewById(R.id.done_button_id);

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FirstPetRegistrationActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Please upload your pet photo..");
/*********Camera********/
                // alertDialog.setIcon(R.drawable.delete);
                alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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

                String ageVal = ageText.getText().toString();
                weightVal = weightText.getText().toString();

                String genderPosString = genderSpinner.getSelectedItem().toString();
                String sizePosString = sizeSpinner.getSelectedItem().toString();

                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                img.setBounds(0, 0, 30, 30);

                if (petNameText.getText().toString().length() == 0) {
                    //  petNameText.setError("Please Enter Your Pet nmae");
                    petNameText.setCompoundDrawables(null, null, img, null);
                } else if (breedauto.getText().toString().length() == 0) {
                    // breedauto.setError("Please Enter Breed Name");
                    breedauto.setCompoundDrawables(null, null, img, null);
                } else if (genderPosString.equalsIgnoreCase("Gender")) {
                    Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                } else if (ageVal.length() == 0) {
                    //  ageText.setError("Please Enter age between 0 to 20 years");
                    ageText.setCompoundDrawables(null, null, img, null);
                } else if (((Integer.parseInt(ageVal)) > 20)) {
                    // ageText.setError("Please Enter age between 0 to 20 years");
                    ageText.setCompoundDrawables(null, null, img, null);
                } else if (weightVal.length() > 0 && ((Integer.parseInt(weightVal)) > 100)) {
                    // weightText.setError("Please Enter age between 0 to 100 Kg.");
                    weightText.setCompoundDrawables(null, null, img, null);
                } else if (sizePosString.equalsIgnoreCase("Size")) {
                    Toast.makeText(getApplicationContext(), "Please select size", Toast.LENGTH_SHORT).show();
                } else if (vacciRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select vaccination", Toast.LENGTH_LONG).show();
                } else if (profileimg == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                } else {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (!isInternetPresent) {
                        showAlertDialog(FirstPetRegistrationActivity.this,
                                "No Internet Connection", "You don't have internet connection.", false);
                    } else {
                        loginSessionManager = new LoginSessionManager(getApplicationContext());
                        HashMap<String, String> user = loginSessionManager.getUserDetails();

                        idString = user.get(LoginSessionManager.KEY_ID);

                        petName = petNameText.getText().toString();
                        // gender = genderText.getText().toString().trim();
                        age = ageText.getText().toString().trim();
                        weight = weightText.getText().toString().trim();
                        about = aboutText.getText().toString().trim();

                        dogCatSpinner1 = dogCatSpinner.getSelectedItem().toString();
                        breedSpinner1 = breedauto.getText().toString();
                        //   heightSpinner1 = heightSpinner.getSelectedItem().toString();
                        heightSpinner1 = heightText.getText().toString();
                        sizeSpinner1 = sizeSpinner.getSelectedItem().toString();
                        genderSpinner1 = genderSpinner.getSelectedItem().toString();
                        int vacciRadioGroup1 = vacciRadioGroup.getCheckedRadioButtonId();
                        vacciRadioButton = (RadioButton) findViewById(vacciRadioGroup1);
                        vacciradioG1 = vacciRadioButton.getText().toString();

                        PetRegisterTask task = new PetRegisterTask();
                        task.execute("http://petsapp.petsappindia.co.in/pat.asmx/petMate");
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

                int nh = (int) (bitmap.getHeight() * (256.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 256, nh, true);
                profileimg.setImageBitmap(scaled);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.PNG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 2) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            profileimg.setImageBitmap(photo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 90, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
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

    private class PetRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(FirstPetRegistrationActivity.this, "Loading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try {
                urlParameters = "&pcat=" + URLEncoder.encode(dogCatSpinner1, "UTF-8") +
                        "&pname=" + URLEncoder.encode(petName, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedSpinner1, "UTF-8") +
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
                        "&lfstate=" + URLEncoder.encode("0", "UTF-8");
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
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);
                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {
                        //  Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                        System.out.println("staus----" + state);
                        Intent in = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(in);
                        finish();
                        Log.d("result=====post", result);
                        Log.d("result=====state", state);
                        dialog.dismiss();
                    } else {
                        //   Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
            dialog.dismiss();

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

            breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId1);
            ArrayAdapter adp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, result);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedauto.setThreshold(1);
            breedauto.setAdapter(adp);

        }
    }
}
