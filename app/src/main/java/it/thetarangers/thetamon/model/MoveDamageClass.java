package it.thetarangers.thetamon.model;

public enum MoveDamageClass {
    STATUS,
    PHYSICAL,
    SPECIAL;

    public int getValue() {
        return ordinal() + 1;
    }

}
