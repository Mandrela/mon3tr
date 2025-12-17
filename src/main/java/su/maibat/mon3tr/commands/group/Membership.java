package su.maibat.mon3tr.commands.group;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;


public class Membership implements Command {
    private SQLiteLinker db;


    public Membership(final SQLiteLinker linker) {
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
        return "List memberships";
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
            // System.out.println(groupIdList.length);
            if (groupIdList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have no memberships");
                responseQueue.add(answer);
                return null;
            }
            GroupQuery[] groupList = db.getGroups(groupIdList);
            NumberedString answer = new NumberedString(userId, printTable(groupList));
            responseQueue.add(answer);
            return null;
        } catch (UserNotFound nf) {
            NumberedString answer = new NumberedString(userId, "You have no memberships");
            responseQueue.add(answer);
            return null;
        }
    }

    protected final String printTable(final GroupQuery[] groupList) {
        String answer = "Memberships: \n\n";
        for (int i = 0; i < groupList.length; i++) {
            answer = answer.concat((i + 1) + " : " + groupList[i].getName()) + "\n";
        }
        return answer;
    }
}
