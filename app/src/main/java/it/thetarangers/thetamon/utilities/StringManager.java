package it.thetarangers.thetamon.utilities;

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
}
