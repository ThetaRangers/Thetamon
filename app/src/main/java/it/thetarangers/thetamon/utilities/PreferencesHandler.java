package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHandler {

    public static final String FIRST_USE = "FirstUse";
    private static Boolean isFirstUse;

    private PreferencesHandler() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static synchronized Boolean isFirstUse(Context context) {
        if (isFirstUse == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            isFirstUse = sharedPreferences.getBoolean(FIRST_USE, true);
        }
        return isFirstUse;
    }

    public static synchronized void setIsFirstUse(Context context, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(FIRST_USE, value);
        editor.apply();
        isFirstUse = value;
    }

}
