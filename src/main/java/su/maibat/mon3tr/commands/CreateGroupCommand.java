package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;

import java.util.concurrent.BlockingQueue;

public class CreateGroupCommand implements Command{

    private final SQLiteLinker db;
    public CreateGroupCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    public String getName() {
        return "createGroup";
    }
    public String getHelp() {
        return "This command create a new troop";
    }

    public State execute(int userId, String[] args, State currentState,
                         BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        if (args.length < 1 || args[0].isEmpty() || args[0].length() > 50) {
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
