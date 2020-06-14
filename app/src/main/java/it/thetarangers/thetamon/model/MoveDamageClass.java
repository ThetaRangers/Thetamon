package it.thetarangers.thetamon.model;

public enum MoveDamageClass {
    STATUS,
    PHYSICAL,
    SPECIAL;

    public static String STATUS_NAME = "status";
    public static String PHYSICAL_NAME = "physical";
    public static String SPECIAL_NAME = "special";

    public int getValue() {
        return ordinal() + 1;
    }

}
