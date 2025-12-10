package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;

public class LeaveCommand extends MembershipListCommand {
    private final SQLiteLinker db;
    public LeaveCommand(final SQLiteLinker linker) {
        super(linker);
        this.db = linker;
    }

    public final String getName() {
        return "leave";
    }

    public final String getHelp() {
        return "This command let you leave from group";
    }

    public State execute(final int userId, final String[] args, final State state,
                         final BlockingQueue<NumberedString> responseQueue)
            throws CommandException {
        State currentState;
        if (state == null) {
            currentState = new State(0, new String[]{}, (Command) this);
        } else {
            currentState = state;
        }
        switch (currentState.getStateId()) {
            case(0):
                return groupTable(userId, args, currentState, responseQueue);
            case (1):
                return selectIndex(userId, args, currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }

    private State groupTable(final int userId, final String[] args, final State state,
                             final BlockingQueue<NumberedString> responseQueue) {
        try {
            UserQuery user = db.getUserById(userId);
            int[] groupIdList = user.getMembership();
            if (groupIdList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any groups");
                responseQueue.add(answer);
                return null;
            }
            GroupQuery[] groupList = db.getGroups(groupIdList);
            NumberedString answer = new NumberedString(userId, printTable(groupList));
            responseQueue.add(answer);

            String[] idList = new String[groupList.length];
            for (int i = 0; i < groupList.length; i++) {
                idList[i] = Integer.toString(groupIdList[i]);
            }
            state.setMemory(idList);
            return selectIndex(userId, args, state, responseQueue);
        } catch (UserNotFound | GroupNotFound unf) {
            NumberedString answer = new NumberedString(userId, "You have not any groups");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectIndex(final int userId, final String[] args, final State currentState,
                              final BlockingQueue<NumberedString> responseQueue) {
        if (args.length != 0 && isValid(args[0], currentState.getMemory().length)) {
            try {
                int selectId = Integer.parseInt(args[0]) - 1;
                int groupQueryId = Integer.parseInt(currentState.getMemory()[selectId]);
                UserQuery user = db.getUserById(userId);
                int[] groupIdList = user.getMembership();
                int[] newGroupIdList = new int[groupIdList.length];

                for (int i = 0, j = 0; i < groupIdList.length; i++) {
                    if (groupIdList[i] != groupQueryId) {
                        newGroupIdList[j] = groupIdList[i];
                        j++;
                    }
                }


                user.setMembership(newGroupIdList);

                NumberedString answer = new NumberedString(userId,"nothing" );
                responseQueue.add(answer);
                return null;
            } catch (NumberFormatException e) {
                NumberedString answer = new NumberedString(userId,
                        "Please enter a valid group id (number)");
                responseQueue.add(answer);
                currentState.setStateId(1);
                return currentState;
            } catch (UserNotFound e) {
                throw new RuntimeException(e);
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid group id (number)");
            responseQueue.add(answer);
            currentState.setStateId(1);
            return currentState;
        }
    }

    private boolean isValid(final String arg, final int maxValue) {
        //Не число
        //Больше предела
        //Меньше 1
        try {
            int intArg = Integer.parseInt(arg);
            return intArg <= maxValue && intArg >= 1;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
