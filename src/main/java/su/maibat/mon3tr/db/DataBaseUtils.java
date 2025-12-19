package su.maibat.mon3tr.db;

public final class DataBaseUtils {
    private DataBaseUtils() { }

    private static final String DELIMITER = ":";


    public static String arrayToString(final int[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        String string = DELIMITER;
        for (int i : array) {
            string += String.valueOf(i) + DELIMITER;
        }
        return string;
    }

    public static int[] stringToArray(final String string) {
        if (string == null || string.length() == 0) {
            return new int[0];
        }
        String[] elements = string.split(DELIMITER);
        int[] array = new int[elements.length - 1];
        for (int i = 1; i < elements.length; i++) {
            array[i - 1] = Integer.valueOf(elements[i]);
        }
        return array;
    }
}
