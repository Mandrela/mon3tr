package su.maibat.mon3tr.commands;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public interface Command {
    String getName();
    String getHelp();

    State execute(int userId, String[] args, State currentState,
        BlockingQueue<NumberedString> responseQueue) throws CommandException, UserNotFound;
}
