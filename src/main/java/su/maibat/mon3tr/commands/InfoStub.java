package su.maibat.mon3tr.commands;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;

public final class InfoStub implements Command {
    private final String data;


    public InfoStub(final String info) {
        data = info;
    }


    @Override
    public String getName() {
        return data;
    }

    @Override
    public String getHelp() {
        return "Congratulations, you found easter egg!";
    }

    @Override
    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        return null;
    }
}
