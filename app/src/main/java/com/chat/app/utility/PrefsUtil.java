package com.chat.app.utility;

/*
 * Created by kopite on 27/3/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.chat.app.ui.activity.HomeActivity;

public class PrefsUtil {
    private static final String DEVICE_TOKEN = "device_token";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_ID = "user_id";
    private static final String LOGIN_STATUS = "login_status";

    private static SharedPreferences getSharePrefInstance(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void FcmToken(Context context, String value) {
        SharedPreferences.Editor editor = getSharePrefInstance(context).edit();
        editor.putString(DEVICE_TOKEN, value);
        editor.apply();
    }

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences.Editor editor = getSharePrefInstance(context).edit();
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context context) {
        return getSharePrefInstance(context).getString(USER_EMAIL, null);
    }

    public static String getFcm(Context context) {
        return getSharePrefInstance(context).getString(DEVICE_TOKEN, null);
    }

    public static void saveUserId(Context context, String uid) {
        SharedPreferences.Editor editor = getSharePrefInstance(context).edit();
        editor.putString(USER_ID, uid);
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getSharePrefInstance(context).getString(USER_ID, null);
    }

    public static void setLoginStatus(Context context, boolean status) {
        SharedPreferences.Editor editor = getSharePrefInstance(context).edit();
        editor.putBoolean(LOGIN_STATUS,status);
        editor.apply();
    }
    public static boolean getLogin(Context context){
        return getSharePrefInstance(context).getBoolean(LOGIN_STATUS,false);
    }
}
