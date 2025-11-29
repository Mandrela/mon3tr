package su.maibat.mon3tr.db.exceptions;

public class DeadlineNotFound extends Exception {
    private final int deadlineId;

    public DeadlineNotFound(final int deadlineArg) {
        deadlineId = deadlineArg;
    }

    public final int getDeadlineId() {
        return deadlineId;
    }
}
