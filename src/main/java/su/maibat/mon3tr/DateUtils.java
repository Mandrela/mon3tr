package su.maibat.mon3tr;

public final class DateUtils {
    private DateUtils() { }
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOURS = 60;
    public static final int MILLIS_IN_SEC = 1000;
    public static final int SECONDS_IN_30_MINUTES = 1800;

    // Shortcuts
    public static final int SEC_IN_DAYS = HOURS_IN_DAY * MINUTES_IN_HOURS * MILLIS_IN_SEC;
}
