package su.maibat.mon3tr.commands.group;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;

import java.util.concurrent.BlockingQueue;

public final class CreateGroup implements Command {
    private static final int MAX_GROUP_NAME_LENGTH = 50;
    private final SQLiteLinker db;


    public CreateGroup(final SQLiteLinker linker) {
        this.db = linker;
    }


    public String getName() {
        return "create";
    }

    public String getHelp() {
        return "Creates group.\nSyntax: /create <name>\n\nGroups is a mean to share your "
            + "personal tasks with a bunch of people you trust";
    }


    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null || args.length < 1 || args[0].isEmpty()
                || args[0].length() > MAX_GROUP_NAME_LENGTH) {
            NumberedString answer = new NumberedString(userId, "Enter group name");
            responseQueue.add(answer);
            return new State(0, new String[]{}, this);
        } else {
            String name = args[0];
            GroupQuery group = new GroupQuery(name, userId);
            db.addGroup(group);
            NumberedString answer = new NumberedString(userId, "Group created");
            responseQueue.add(answer);
            return null;
        }
    }
}
