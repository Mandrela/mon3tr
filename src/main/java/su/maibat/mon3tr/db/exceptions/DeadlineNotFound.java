package su.maibat.mon3tr.db.exceptions;


import lombok.Getter;

@Getter
public class DeadlineNotFound extends Exception {
    private final int userId;

    public DeadlineNotFound(final int userIdArg) {
        userId = userIdArg;
    }
}
