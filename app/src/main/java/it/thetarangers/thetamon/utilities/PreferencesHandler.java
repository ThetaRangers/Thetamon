package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import it.thetarangers.thetamon.R;

public class PreferencesHandler {

    private static Boolean isFirstUse;
    private static String isNightMode;

    private PreferencesHandler() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preferences_name),
                Context.MODE_PRIVATE);
    }

    public static synchronized Boolean isFirstUse(Context context) {
        if (isFirstUse == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            isFirstUse = sharedPreferences.getBoolean(context.getString(R.string.first_use), true);
        }
        return isFirstUse;
    }

    public static synchronized void setIsFirstUse(Context context, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.first_use), value);
        editor.apply();
        isFirstUse = value;
    }

    public static synchronized String isNightMode(Context context) {
        if (isNightMode == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            isNightMode = sharedPreferences.getString(context.getString(R.string.night_mode), context.getString(R.string.system_enum));
        }
        return isNightMode;
    }

    public static synchronized void setIsNightMode(Context context, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.night_mode), value);
        editor.apply();
        isNightMode = value;
    }

}
