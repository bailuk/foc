package ch.bailu.foc.util;

/**
 * Boolean value that is `false` after initialisation
 * and can only be set to `true`
 */
public class Check {
    private Boolean check = null;

    public void set(boolean b) {
        if (check == null) {
            check = b;
        }
    }

    public boolean get() {
        return (check != null && check);
    }

    public  boolean isSet() {
        return check != null;
    }
}
