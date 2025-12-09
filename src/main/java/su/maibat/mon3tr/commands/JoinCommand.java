package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;

public class JoinCommand {
    private final SQLiteLinker db;
    public JoinCommand(final SQLiteLinker linker) {
               this.db = linker;
    }
    public final String getName() {
        return "join";
    }
    public final String getHelp() {
        return "By this command you can connect to someone's group with using token of this group";
    }

    public final State execute(final int userId, final String[] args, final State state,
                         final BlockingQueue<NumberedString> responseQueue) {
        State currentState;
        if (state == null) {
            currentState = new State(0, new String[]{}, (Command) this);
        } else {
            currentState = state;
        }
        if (args.length == 0) {
            NumberedString answer = new NumberedString(userId, "Enter token");
            responseQueue.add(answer);
            return currentState;
        } else {
            try {
                String token = args[0];
                GroupQuery group = db.tryFindToken(token);
                int[] groupList = db.getUserById(userId).getMembership();
                int[] newGroupList = new int[groupList.length + 1];
                newGroupList[0] = group.getId();
                System.arraycopy(groupList, 0, newGroupList, 1, groupList.length);
                db.getUserById(userId).setMembership(newGroupList);
                NumberedString answer = new NumberedString(userId, "You are new member now");
                responseQueue.add(answer);
                return null;

            } catch (TokenNotFound e) {
                NumberedString answer = new NumberedString(userId, "This token is not valid");
                responseQueue.add(answer);
                return currentState;
            } catch (UserNotFound e) {
                throw new RuntimeException(e);
            }
        }
    }
}
