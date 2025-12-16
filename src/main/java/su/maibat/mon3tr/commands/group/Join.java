package su.maibat.mon3tr.commands.group;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;

import java.util.concurrent.BlockingQueue;


public class Join implements Command {
    private final SQLiteLinker db;


    public Join(final SQLiteLinker linker) {
               this.db = linker;
    }


    public final String getName() {
        return "join";
    }

    public final String getHelp() {
        return "Become member of group by token.\nSyntax: /join <token>"
            + "\nToken can expire, so if you expirience troubles ask group owner to re"
            + "generate token";
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
                UserQuery user = db.getUserById(userId);
                int[] groupList = user.getMembership();
                int[] newGroupList = new int[groupList.length + 1];
                newGroupList[0] = group.getId();
                System.arraycopy(groupList, 0, newGroupList, 1, groupList.length);
                user.setMembership(newGroupList);
                db.updateUser(user);
                NumberedString answer = new NumberedString(userId, "Membership aquired");
                responseQueue.add(answer);
                return null;

            } catch (TokenNotFound e) {
                NumberedString answer = new NumberedString(userId,
                    "Token is not valid"
                );
                responseQueue.add(answer);
                return currentState;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
