package it.thetarangers.thetamon.utilities;

public class StringManager {
    public static String capitalize(String in) { // TODO maybe put this somewhere else
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
