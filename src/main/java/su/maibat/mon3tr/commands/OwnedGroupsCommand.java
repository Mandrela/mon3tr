package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;

public class OwnedGroupsCommand implements Command {
    private final SQLiteLinker db;
    public OwnedGroupsCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    /** This is javadoc.
     * @return kek.
    */
    public String getName() {
        return "listOwnedGroups";
    }

    /** This is javadoc.
     * @return kek.
    */
    public String getHelp() {
        return "This command shows your own groups";
    }

    /**
     * @param userId kek.
     * @param args kek.
     * @param currentState kek.
     * @param responseQueue kek.
     * @return  kek.
     */
    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        try {
            GroupQuery[] groupList = db.getOwnedGroups(userId);
            if (groupList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any groups");
                responseQueue.add(answer);
                return null;
            }
            NumberedString answer = new NumberedString(userId, printTable(groupList));
            responseQueue.add(answer);
            return null;
        } catch (UserNotFound unf) {
            NumberedString answer = new NumberedString(userId, "You have not any groups");
            responseQueue.add(answer);
            return null;
        }
    }

    /**
     * @param groupList kek.
     * @return kek.
    */
    protected final String printTable(final GroupQuery[] groupList) {
        String answer = "Your own groups: \n\n";
        for (int i = 0; i < groupList.length; i++) {
            answer = answer.concat((i + 1) + " : " + groupList[i].getName()) + "\n";
        }
        return answer;
    }
}
