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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.net.URL;
import java.net.URLEncoder;

public class PetBookPetRegActivity extends AppCompatActivity {

    static String encodedImage;
    final Context context = this;
    EditText petNameText, detailsText;
    ImageView img;
    FloatingActionButton uploadButton;
    File file;
    ConnectionDetector cd;
    String petName, petDetails;
    JSONArray arr = null;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_CAMERA_REQUEST = 2;
    private Bitmap bitmap;
    private Uri filePath;
    private String urlParameters;
    private Boolean Isinternetpresent = false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_book_pet_reg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), InfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#4e4b92"));
        }

        petNameText = (EditText) findViewById(R.id.petname_text_id);
        detailsText = (EditText) findViewById(R.id.petdetails_text_id);
        img = (ImageView) findViewById(R.id.upload_img_id);
        uploadButton = (FloatingActionButton) findViewById(R.id.submit);

       /* img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PetBookPetRegActivity.this);
        alertDialog.setTitle("Select option");
        alertDialog.setMessage("Please upload your pet photo...").setCancelable(false);
/*********Camera********/
        // alertDialog.setIcon(R.drawable.delete);
        alertDialog.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                        /*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, 2);*/
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 2);
                //finish();

            }
        });
/*********Gallery********/
        alertDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            }
        });
        /*Cancel Button*/

        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PetBookPetRegActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        alertDialog.show();


         /*   }
        });*/

        Drawable imgPb = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
        imgPb.setBounds(0, 0, 20, 20);
        //   petNameText.setCompoundDrawables(null, null, imgPb, null);
        //   detailsText.setCompoundDrawables(null, null, imgPb, null);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mate_fade);
                uploadButton.startAnimation(animFadein);

                Drawable imgDrw = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                imgDrw.setBounds(0, 0, 20, 20);

               /* if (petNameText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter pet name", Toast.LENGTH_SHORT).show();
                } else if (detailsText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter details about pet", Toast.LENGTH_SHORT).show();
                } else if (img.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else if (encodedImage == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {*/
                cd = new ConnectionDetector(getApplicationContext());
                Isinternetpresent = cd.isConnectingToInternet();
                if (!Isinternetpresent) {
                    showAlertDialog(PetBookPetRegActivity.this,
                            "No Internet Connection", "You don't have internet connection.", false);
                } else {
                    petName = petNameText.getText().toString();
                    petDetails = detailsText.getText().toString().trim();

                    cd = new ConnectionDetector(getApplicationContext());
                    Isinternetpresent = cd.isConnectingToInternet();
                    if (Isinternetpresent) {
                        PetBookTask task = new PetBookTask();
                        task.execute("http://petsapp.petsappindia.co.in/pat.asmx/addpetbook");
                    } else {
                        //     Toast.makeText(PetBookPetRegActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                // }
            }
        });
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

   /* private File getTempFile(Context context) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
        if (!file.exists()) {
            file.mkdir();
        }

        return new File(file, "myImage.png");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //   file = getTempFile(this);
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


            file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
            //   file = getTempFile(this);
            try {
                Uri uri = Uri.fromFile(file);
                cropCapturedImage(uri);
            } catch (ActivityNotFoundException aNFE) {
                String errorMessage = "Sorry - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }

        } else {
            // img.setImageResource(R.drawable.blue_image);
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                img.setImageBitmap(thePic);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                System.out.println("base^^^^$$$$$$$" + encodedImage);
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }

        if (resultCode != RESULT_OK) {
            Intent i = new Intent(getApplicationContext(), InfoActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        //   img.setImageResource(R.drawable.blue_image);
        super.onPause();
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

    /* public void showAlertDialog1(Context context, String title, String message,
                                  Boolean status) {
         final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
         alertDialog.setTitle(title);
         alertDialog.setMessage(message);
         alertDialog.setIcon(R.drawable.alert_tick);
         //    alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
         alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {

                 Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                 in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                 startActivity(in);
                 finish();
                 dialog.cancel();
             }
         });

         alertDialog.show();
     }*/
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

    private class PetBookTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //  reg();
            super.onPreExecute();
          /*  dialog = ProgressDialog.show(PetBookPetRegActivity.this, "Uploading",
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
                urlParameters = "&name=" + URLEncoder.encode("petbook", "UTF-8") +
                        "&image=" + URLEncoder.encode(encodedImage, "UTF-8") +
                        "&details=" + URLEncoder.encode(petDetails, "UTF-8");

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

            try {
                arr = new JSONArray(result);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String state = obj.getString("status");
                    System.out.println("--" + state);
                    if (state.contains("success")) {
                        petNameText.setText("");
                        detailsText.setText("");

                        encodedImage = null;
                        img.setImageDrawable(null);

                      /*  showAlertDialog1(PetBookPetRegActivity.this,
                                "Successfull !", "Successfully Registered..", false);*/
                        //  Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                        showAlertDialog1(PetBookPetRegActivity.this, false);
                        Intent in = new Intent(getApplicationContext(), InfoActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(in);
                        finish();

                        //   dialog.dismiss();
                    } else {
                        //   Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        //  dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException ee) {
                ee.printStackTrace();
            }
            //    dialog.dismiss();

        }
    }
  /*  @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }*/
}
