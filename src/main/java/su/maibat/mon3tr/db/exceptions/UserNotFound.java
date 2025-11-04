package su.maibat.mon3tr.db.exceptions;

import lombok.Getter;


@Getter
public class UserNotFound extends Exception {
    private final long id;

    /**
     * @param idArg either user id or chat id
     */
    public UserNotFound(final long idArg) {
        id = idArg;
    }
}
