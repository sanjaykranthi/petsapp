package com.petsapp.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.HashMap;
import java.util.List;

public class StartPageActivity extends AppCompatActivity {

    static String encodedImage;
    final Context context = this;
    Spinner dogCatSpinner, heightSpinner, sizeSpinner, genderSpinner;
    AutoCompleteTextView breedauto;
    EditText petNameText, genderText, ageText, ageMonthText, weightText, aboutText, heightText;
    RadioGroup vacciRadioGroup;
    RadioButton vacciRadioYes, vacciRadioNo, vacciRadioButton, breedRadioButton;
    Button doneButton, skipButton;
    String breedAutoString;
    String petName, gender, age = "";
    JSONArray arr = null;
    String[] petdog = {"Boxer", "Pug", "Pomeranian", "Saint Bernord", "Dachshund"};
    String[] petcat = {"Spotted", "Himalayan", "Leopard", "Siamese", "Maine Coon"};
    TextView uploadImage;
    ImageView profileimg;
    LoginSessionManager loginSessionManager;
    MapSession mapSession;
    String idString;
    ArrayAdapter<String> petcategory;
    String dogCatSpinner1, breedSpinner1, heightSpinner1 = null, genderSpinner1, ageVal = "", ageMonthVal = "";
    int petcatepos, breedPos, genderPos;
    ConnectionDetector cd;
    String petcatestr, weightVal = "";
    String[] categ = {
            "Dog",
            "Cat",
            "Others"
    };
    int weightInt = 0;
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
        setContentView(R.layout.activity_start_page);

        loginSessionManager = new LoginSessionManager(getApplicationContext());
        HashMap<String, String> user = loginSessionManager.getUserDetails();

        idString = user.get(LoginSessionManager.KEY_ID);
        uploadImage = (TextView) findViewById(R.id.textImgId);
        profileimg = (ImageView) findViewById(R.id.profileImgId);
//
        breedauto = (AutoCompleteTextView) findViewById(R.id.breed_spinnerId);
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

                        } else if (petcatepos == 1) {
                            petcatestr = "cat";
                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                        } else if (petcatepos == 2) {
                            petcatestr = "others";
                            BreedTask taskBreed = new BreedTask();
                            taskBreed.execute(" http://petsapp.petsappindia.co.in/pat.asmx/getcategory");

                        } else {
                            //    Toast.makeText(getApplicationContext(), "No Other Pets are Available", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        petNameText = (EditText) findViewById(R.id.name_text_id);
        genderSpinner = (Spinner) findViewById(R.id.gender_text_id);
        ageText = (EditText) findViewById(R.id.age_text_id);
        ageMonthText = (EditText) findViewById(R.id.ageMonth_text_id);
        doneButton = (Button) findViewById(R.id.done_button_id);
        skipButton = (Button) findViewById(R.id.skip_button_id);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(StartPageActivity.this, DashboardActivity.class);
                startActivity(i1);
                finish();
            }
        });

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StartPageActivity.this);
                alertDialog.setTitle("Select option");
                alertDialog.setMessage("Please upload your pet photo...");
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

                ageVal = ageText.getText().toString();
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

                String genderPosString = genderSpinner.getSelectedItem().toString();


                Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                img.setBounds(0, 0, 30, 30);

                if (petNameText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter pet name", Toast.LENGTH_SHORT).show();
                } else if (breedauto.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter breed name", Toast.LENGTH_SHORT).show();
                } else if (genderPosString.equalsIgnoreCase("Gender")) {
                    Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
                }/* else if (ageText.getText().toString().length() == 0) {
                    ageText.setCompoundDrawables(null, null, img, null);
                } else if (((Integer.parseInt(ageVal)) > 20)) {
                    ageText.setCompoundDrawables(null, null, img, null);
                } else if (profileimg.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                }*/ else if (ageVal.length() > 0 && ((Integer.parseInt(ageVal)) > 40)) {
                    Toast.makeText(getApplicationContext(), "Please Enter age between 0 to 40 yrs.", Toast.LENGTH_SHORT).show();
                } else if (ageMonthVal.length() > 0 && ((Integer.parseInt(ageMonthVal)) > 11)) {
                    Toast.makeText(getApplicationContext(), "Please Enter month between 0 to 11.", Toast.LENGTH_SHORT).show();
                } else if (encodedImage == null) {
                    Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.petprofile);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                    byte[] ba = bao.toByteArray();
                    encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
                } else {
                    cd = new ConnectionDetector(getApplicationContext());
                    Isinternetpresent = cd.isConnectingToInternet();
                    if (!Isinternetpresent) {
                        showAlertDialog(StartPageActivity.this,
                                "No Internet Connection", "You don't have internet connection.", false);
                    } else {


                        petName = petNameText.getText().toString();
                        //     age = ageText.getText().toString().trim();
                        dogCatSpinner1 = dogCatSpinner.getSelectedItem().toString();
                        genderSpinner1 = genderSpinner.getSelectedItem().toString();
                        breedAutoString = breedauto.getText().toString();

                        cd = new ConnectionDetector(getApplicationContext());
                        Isinternetpresent = cd.isConnectingToInternet();
                        if (Isinternetpresent) {
                            PetRegisterTask task = new PetRegisterTask();
                            task.execute("http://petsapp.petsappindia.co.in/pat.asmx/petMate");
                        } else {
                            //  Toast.makeText(StartPageActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
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

    public void showAlertDialog1(Context context, String title, String message,
                                 Boolean status) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.alert_tick);
        //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent in = new Intent(getApplicationContext(), FindAdoptActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(in);

                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            super.onPreExecute();
           /* dialog = ProgressDialog.show(StartPageActivity.this, "Loading",
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
                urlParameters = "&pcat=" + URLEncoder.encode(dogCatSpinner1, "UTF-8") +
                        "&pname=" + URLEncoder.encode(petName, "UTF-8") +
                        "&breed=" + URLEncoder.encode(breedAutoString, "UTF-8") +
                        "&gndr=" + URLEncoder.encode(genderSpinner1, "UTF-8") +
                        "&age=" + URLEncoder.encode(age, "UTF-8") +
                        "&wght=" + URLEncoder.encode("", "UTF-8") +
                        "&hght=" + URLEncoder.encode("", "UTF-8") +
                        "&size=" + URLEncoder.encode("", "UTF-8") +
                        "&vac=" + URLEncoder.encode("", "UTF-8") +
                        "&abt=" + URLEncoder.encode("", "UTF-8") +
                        "&img=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&pid=" + URLEncoder.encode(idString, "UTF-8") +
                        "&state=" + URLEncoder.encode("0", "UTF-8") +
                        "&adoptstate=" + URLEncoder.encode("0", "UTF-8") +
                        "&lfstate=" + URLEncoder.encode("0", "UTF-8") +
                        "&date=" + URLEncoder.encode("0", "UTF-8") +
                        "&adate=" + URLEncoder.encode("0", "UTF-8");
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

            try {
                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    System.out.println("--" + arr);
                    JSONObject obj = arr.getJSONObject(i);
                    System.out.println("--" + obj);
                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {

                        petNameText.setText("");
                        ageText.setText("");

                      /*  showAlertDialog1(StartPageActivity.this,
                                "Successfull !", "Successfully Registered..", false);*/

                        Intent i1 = new Intent(StartPageActivity.this, DashboardActivity.class);
                        startActivity(i1);
                        finish();

                        //    Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                        //  dialog.dismiss();
                    } else {
                        //       Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        //    dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
            //     dialog.dismiss();

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
}
