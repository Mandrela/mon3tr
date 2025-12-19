package su.maibat.mon3tr.db.exceptions;

public class TokenNotFound extends Exception {
    private final String token;

    public TokenNotFound(final String tokenArg) {
        token = tokenArg;
    }

    public final String getToken() {
        return token;
    }
}
