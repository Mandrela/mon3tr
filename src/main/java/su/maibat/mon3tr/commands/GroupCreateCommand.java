package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;

import java.util.concurrent.BlockingQueue;

public final class GroupCreateCommand implements Command {
    private static final int MAX_GROUP_NAME_LENGTH = 50;

    private final SQLiteLinker db;
    public GroupCreateCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    public String getName() {
        return "createGroup";
    }
    public String getHelp() {
        return "This command create a new troop";
    }

    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        if (args.length < 1 || args[0].isEmpty() || args[0].length() > MAX_GROUP_NAME_LENGTH) {
            NumberedString answer = new NumberedString(userId, "Enter group name");
            responseQueue.add(answer);
            return currentState;
        } else {
            String name = args[0];
            GroupQuery group = new GroupQuery(name, userId);
            db.addGroup(group);
            NumberedString answer = new NumberedString(userId, "Your group has been "
                    + "successfully created!");
            responseQueue.add(answer);
            return null;
        }

    }
}
