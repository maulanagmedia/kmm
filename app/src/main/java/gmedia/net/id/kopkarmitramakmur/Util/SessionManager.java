package gmedia.net.id.kopkarmitramakmur.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import gmedia.net.id.kopkarmitramakmur.LoginScreen;

/**
 * Created by Shin on 2/27/2017.
 */

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "GmediaKOPKARUser";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String TAG_UID = "uid";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_NIK = "nik";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_DEPT = "dept";
    public static final String TAG_SAVE = "save";

    // Constructor
    public SessionManager(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String uid, String nama, String nik, String email, String dept, String save){

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(TAG_UID, uid);

        editor.putString(TAG_NAMA, nama);

        editor.putString(TAG_NIK, nik);

        editor.putString(TAG_EMAIL, email);

        editor.putString(TAG_DEPT, dept);

        editor.putString(TAG_SAVE, save);

        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        //if(!this.isLoggedIn()){
        // user is not logged in redirect him to Login Activity
        //	Intent i = new Intent(context, Login.class);
        // Closing all the Activities
        //	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        //	context.startActivity(i);
        //}

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(TAG_UID, pref.getString(TAG_UID, null));

        user.put(TAG_NAMA, pref.getString(TAG_NAMA, null));

        user.put(TAG_NIK, pref.getString(TAG_NIK, null));

        user.put(TAG_EMAIL, pref.getString(TAG_EMAIL, null));

        user.put(TAG_DEPT, pref.getString(TAG_DEPT, null));

        user.put(TAG_SAVE, pref.getString(TAG_SAVE, null));

        return user;
    }

    public String getUid(){

        return pref.getString(TAG_UID, null);
    }

    public String getNama(){

        return pref.getString(TAG_NAMA, null);
    }

    public String getNik(){

        return pref.getString(TAG_NIK, null);
    }

    public String getEmail(){

        return pref.getString(TAG_EMAIL, null);
    }

    public String getDepartement(){

        return pref.getString(TAG_DEPT, null);
    }
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences

        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        if(getUserDetails().get(TAG_NAMA) != null){
            return true;
        }else{
            return false;
        }
		/*return pref.getBoolean(IS_LOGIN, false);*/
    }

    public boolean saveLogin(){
        if(getUserDetails().get(TAG_SAVE) != null){

            if(getUserDetails().get(TAG_SAVE).equals("1")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
		/*return pref.getBoolean(IS_LOGIN, false);*/
    }
}
