package su.maibat.mon3tr.db.exceptions;


public class DeadlineNotFound extends Exception {
    private final int userId;

    public DeadlineNotFound(final int userIdArg) {
        userId = userIdArg;
    }
    public int getUserId() {
        return userId;
    }
}
