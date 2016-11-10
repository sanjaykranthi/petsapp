package com.petsapp.android;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by ANDIROID on 18-07-2016.
 */
public class Notification_count_session {
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "0";
    public static final String KEY_ID = "id";
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;

    // Email address (make variable public to access from outside)
    // public static final String KEY_EMAIL = "email";
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public Notification_count_session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id) {

        editor.putString(KEY_NAME, id);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        // user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // return user
        return user;
    }


}
