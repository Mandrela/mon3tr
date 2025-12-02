package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.SQLiteLinker;

import java.util.concurrent.BlockingQueue;

public class GroupJoinCommand implements Command{

    private final SQLiteLinker db;

    public GroupJoinCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    public String getName() {
        return "join";
    }

    public String getHelp() {
        return "You can join to other's groups by this command";
    }

    public State execute(int userId, String[] args, State currentState,
                         BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        if (args.length == 0 || !(isCorrectToken(args[0]))) {
            NumberedString answer = new NumberedString(userId, "Enter group token");
            responseQueue.add(answer);
            return currentState;
        }


    }
    private boolean isCorrectToken(String token) {
        return true;
    }
}
