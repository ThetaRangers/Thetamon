package it.thetarangers.thetamon.utilities;

import android.content.Context;

public class StringManager {
    public static String capitalize(String in) {
        String[] tmp = in.split("-");

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < tmp.length; i++) {
            String str = tmp[i];
            out.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));

            if (!(i == tmp.length - 1)) {
                out.append(" ");
            }
        }

        return out.toString();
    }

    public static String decapitalize(String in) {
        return in.toLowerCase().replace(" ", "-");
    }

    public static String format(String in) {
        return in.replace("\n", " ");
    }

    public static String formatFromR(Context context, int id, int value) {
        return String.format("%s: %d", context.getResources().getString(id), value);
    }

    public static String formatFromR(Context context, int id, String value) {
        return String.format("%s: %s", context.getResources().getString(id), value);
    }
}
