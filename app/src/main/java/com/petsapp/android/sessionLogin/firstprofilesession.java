package com.petsapp.android.sessionLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.petsapp.android.FirstProfileActivity;

/**
 * Created by Android on 03-03-2016.
 */
public class firstprofilesession {
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    public static final String KEY_DOB = "DOB";
    public static final String KEY_ADDRESS = "address";
    // Sharedpref file name
    private static final String PREF_NAME = "first_profile";

    private static final String IS_CHECK_PROFILE = "IsCheckProfIn";

    // User name (make variable public to access from outside)
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Constructor
    public firstprofilesession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void createLoginSession(String add) {

        editor.putBoolean(IS_CHECK_PROFILE, true);

        // Storing name in pref
        //      editor.putString(KEY_EMAIL, email);

        // Storing email in pref
        //  editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_ADDRESS, add);

        // commit changes
        editor.commit();
    }

    public boolean checkProfile() {
        // Check login status
        if (!this.isProfIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, FirstProfileActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }

    public boolean isProfIn() {
        return pref.getBoolean(IS_CHECK_PROFILE, false);
    }
}
