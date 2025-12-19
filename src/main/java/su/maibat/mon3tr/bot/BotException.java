package su.maibat.mon3tr.bot;

public class BotException extends RuntimeException {
    private final String message;

    public BotException(final String messageArg) {
        message = messageArg;
    }

    public final String getMessage() {
        return message;
    }
}
