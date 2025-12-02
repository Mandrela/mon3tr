package su.maibat.mon3tr.commands.exceptions;

public class CommandException extends Exception {
    private final String message;

    public CommandException(final String messageArg) {
        message = messageArg;
    }

    /**
     * @return kek
     */
    @Override
    public String getMessage() {
        return message;
    }
}
