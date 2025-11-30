package su.maibat.mon3tr;

public final class NumberedString {
    private final int mNumber;
    private final String mString;

    public NumberedString(final int number, final String string) {
        mNumber = number;
        mString = string;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getString() {
        return mString;
    }
}
