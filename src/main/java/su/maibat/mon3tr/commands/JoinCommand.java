package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;

import java.util.concurrent.BlockingQueue;

public class JoinCommand implements Command {
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
        System.out.println("enter");
        if (state == null) {
            currentState = new State(0, new String[]{}, (Command) this);
        } else {
            currentState = state;
        }
        if (args.length == 0) {
            System.out.println("No args");
            NumberedString answer = new NumberedString(userId, "Enter token");
            responseQueue.add(answer);
            return currentState;
        } else {
            System.out.println("args: " + args[0]);
            try {
                String token = args[0];
                GroupQuery group = db.tryFindToken(token);
                UserQuery user = db.getUserById(userId);
                int[] groupList = user.getMembership();
                int[] newGroupList = new int[groupList.length + 1];
                newGroupList[0] = group.getId();
                System.arraycopy(groupList, 0, newGroupList, 1, groupList.length);
                user.setMembership(newGroupList);
                db.updateUser(user);
                NumberedString answer = new NumberedString(userId, "You are new member now");
                responseQueue.add(answer);
                return null;

            } catch (TokenNotFound e) {
                NumberedString answer = new NumberedString(userId,
                    "This token is not valid, please enter again"
                );
                responseQueue.add(answer);
                return currentState;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
