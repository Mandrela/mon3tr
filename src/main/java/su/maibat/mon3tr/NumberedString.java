package su.maibat.mon3tr;


/** Immutable class dedicated to holding a pair of int and String. */
public final class NumberedString {
    private final int mNumber;
    private final String mString;

    /**
     * @param number Number to hold.
     * @param string String to hold.
     */
    public NumberedString(final int number, final String string) {
        mNumber = number;
        mString = string;
    }

    /** @return Holded number. */
    public int getNumber() {
        return mNumber;
    }

    /** @return Holded string. */
    public String getString() {
        return mString;
    }
}
