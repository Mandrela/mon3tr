package su.maibat.mon3tr.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class DataBaseUtilsTest {
    // arrayToString fucked array, no array, giant numbers
    // is adequatous result of method
    // stringToArray abnormal string
    @Test
    @DisplayName("Array to String test")
    void arrayToStringTest() {
        assertEquals("", DataBaseUtils.arrayToString(null));
        assertEquals("", DataBaseUtils.arrayToString(new int[0]));

        assertEquals(":1:2:3:10:2031001:",
            DataBaseUtils.arrayToString(new int[]{1, 2, 3, 10, 2031001}));
    }

    @Test
    @DisplayName("String to Array test")
    void stringToArrayTest() {
        assertEquals(0, DataBaseUtils.stringToArray(null).length);
        assertEquals(0, DataBaseUtils.stringToArray("").length);

        int[] array = new int[]{1, 2, 10, 230};
        int[] resultingArray = DataBaseUtils.stringToArray(":1:2:10:230:");
        assertEquals(array.length,
            resultingArray.length);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], resultingArray[i]);
        }
    }
}
