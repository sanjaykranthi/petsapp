package com.petsapp.android.sessionLogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.petsapp.android.FirstPetRegistrationActivity;

/**
 * Created by WEB DESIGNING on 01-03-2016.
 */
public class MapSession {

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    // Sharedpref file name
    private static final String PREF_NAME = "Pet_Prof";
    // All Shared Preferences Keys
    private static final String IS_CheckPET_PROF = "IsFillIn";
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public MapSession(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name) {
        // Storing login value as TRUE
        editor.putBoolean(IS_CheckPET_PROF, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);


        // commit changes
        editor.commit();
    }

    public boolean checkPetProfile() {
        // Check login status
        if (!this.isProfIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, FirstPetRegistrationActivity.class);
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
        return pref.getBoolean(IS_CheckPET_PROF, false);
    }


}
