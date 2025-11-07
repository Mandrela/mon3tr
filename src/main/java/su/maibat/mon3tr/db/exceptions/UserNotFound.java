package su.maibat.mon3tr.db.exceptions;


public class UserNotFound extends Exception {
    private final long id;

    /**
     * @param idArg either user id or chat id
     */
    public UserNotFound(final long idArg) {
        id = idArg;
    }
    public long getId() {
        return id;
    }
}
