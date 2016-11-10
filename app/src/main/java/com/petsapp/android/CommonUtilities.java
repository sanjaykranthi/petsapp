package com.petsapp.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import com.petsapp.android.Model.AdoptRequestModel;
import com.petsapp.android.Model.Adoption_filter_Data;
import com.petsapp.android.Model.Allpetlist;
import com.petsapp.android.Model.BreedModel;
import com.petsapp.android.Model.ItemObject;
import com.petsapp.android.Model.LostFoundRequestModel;
import com.petsapp.android.Model.PetBookData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ANDIROID on 16-03-2016.
 */
public class CommonUtilities {

    public static List<AdoptRequestModel> adoptionreglist = new ArrayList<AdoptRequestModel>();
    public static List<Adoption_filter_Data> adoptionfilterlist = new ArrayList<Adoption_filter_Data>();
    public static List<BreedModel> adoptionbreed = new ArrayList<BreedModel>();

    public static List<Allpetlist> allpetslist = new ArrayList<Allpetlist>();

    public static List<PetBookData> petbooks = new ArrayList<PetBookData>();


    public static List<LostFoundRequestModel> lostnfoundreglist = new ArrayList<LostFoundRequestModel>();

    public static List<ItemObject> playdatereglist = new ArrayList<ItemObject>();


    public CommonUtilities(Context context) {
    }

    public static boolean isConnectingToInternet(Context cxt) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) cxt
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showInterntSettingsAlert(final Context cxt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                cxt);
        alertDialog.setTitle("No Internet...");
        alertDialog.setMessage("Still We Can Help You, Please Call Our HelpLine...");
        alertDialog.setPositiveButton("CALL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String customercare = "08067335555";
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + customercare));
                        cxt.startActivity(intent);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //((Activity) cxt).finish();
                        //finish();
                    }
                });
        alertDialog.show();
    }

    public static void showInterntOnlySettingsAlert(final Context cxt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                cxt);
        alertDialog.setTitle("Internet not enabled");
        alertDialog.setMessage("Please go to settings menu and enable it..");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIRELESS_SETTINGS);
                        cxt.startActivity(intent);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //((Activity) cxt).finish();
                    }
                });
        alertDialog.show();
    }

    public static void showLocationSettingsAlert(final Context cxt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                cxt);
        alertDialog.setTitle("Location Service not enabled");
        alertDialog.setMessage("Please go to settings to enable it..");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        cxt.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //((Activity) cxt).finish();
                    }
                });
        alertDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDiff_Ts(String spot_ts) throws ParseException {
        String elapsed = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date d = sdf.parse(spot_ts);
        long ts = d.getTime();
        long cur_ts = System.currentTimeMillis();
        long diff = cur_ts - ts; // Time difference in milliseconds
        int diffDays = (int) diff / (24 * 60 * 60 * 1000);
        int diffMinutes = (int) (diff / (1000 * 60) % 60);
        int diffHours = (int) diff / (60 * 60 * 1000);
        if (diffDays > 0) {
            elapsed = diffDays + " day";
            if (diffDays > 1)
                elapsed = elapsed + "s ";
        } else if (diffHours > 0) {
            elapsed = diffHours + " hr";
            if (diffHours > 1)
                elapsed = elapsed + "s";
            if (diffMinutes != 0) {
                elapsed = elapsed + " " + diffMinutes + " min";
                if (diffMinutes > 1)
                    elapsed = elapsed + "s";
            }
        } else {
            elapsed = elapsed + diffMinutes + " min";
            if (diffMinutes > 1)
                elapsed = elapsed + "s";
        }
        return elapsed;
    }
}
