package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;

public class MembershipListCommand implements Command {
    private SQLiteLinker db;
    public MembershipListCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    /**
     * @return Yes.
     */
    public String getName() {
        return "membership";
    }

    /**
     * @return Yes.
     */
    public String getHelp() {
        return "This command show your memberships";
    }

    /**
     * @param userId kek.
     * @param args kek.
     * @param currentState kek.
     * @param responseQueue kek.
     * @return  kek.
     */
    public State execute(final int userId, final String[] args, final State currentState,
                         final BlockingQueue<NumberedString> responseQueue)
            throws CommandException {
        try {
            UserQuery user = db.getUserById(userId);
            int[] groupIdList = user.getMembership();
            if (groupIdList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any memberships");
                responseQueue.add(answer);
                return null;
            }
            GroupQuery[] groupList = db.getGroups(groupIdList);
            NumberedString answer = new NumberedString(userId, printTable(groupList));
            responseQueue.add(answer);
            return null;
        } catch (GroupNotFound | UserNotFound nf) {
            NumberedString answer = new NumberedString(userId, "You have not any memberships");
            responseQueue.add(answer);
            return null;
        }
    }
    protected final String printTable(final GroupQuery[] groupList) {
        String answer = "Your memberships: \n\n";
        for (int i = 0; i < groupList.length; i++) {
            answer = answer.concat((i + 1) + " : " + groupList[i].getName()) + "\n";
        }
        return answer;
    }
}
