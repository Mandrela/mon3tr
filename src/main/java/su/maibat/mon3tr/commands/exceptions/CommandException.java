package su.maibat.mon3tr.commands.exceptions;

public class CommandException extends Exception {
    private final String message;

    public CommandException(final String messageArg) {
        message = messageArg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
