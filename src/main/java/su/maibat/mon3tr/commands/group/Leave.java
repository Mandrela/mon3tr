package su.maibat.mon3tr.commands.group;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;


public final class Leave extends Membership {
    private final SQLiteLinker db;


    public Leave(final SQLiteLinker linker) {
        super(linker);
        this.db = linker;
    }


    public String getName() {
        return "leave";
    }

    public String getHelp() {
        return "Lose membership.\nInteractive mode only";
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
                NumberedString answer = new NumberedString(userId, "You have no groups");
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
        } catch (UserNotFound unf) {
            NumberedString answer = new NumberedString(userId, "You have no groups");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectIndex(final int userId, final String[] args, final State currentState,
                              final BlockingQueue<NumberedString> responseQueue) {
        if (args.length != 0 && isValid(args[0], currentState.getMemory().length)) {
            try {
                int selectId = Integer.parseInt(args[0]) - 1;
                // int groupQueryId = Integer.parseInt(currentState.getMemory()[selectId]);
                UserQuery user = db.getUserById(userId);
                int[] groupIdList = user.getMembership();
                int[] newGroupIdList = new int[groupIdList.length - 1];

                System.arraycopy(groupIdList, 0, newGroupIdList, 0, selectId);
                System.arraycopy(groupIdList, selectId + 1, newGroupIdList,
                        selectId, groupIdList.length - 1);

                user.setMembership(newGroupIdList);
                db.updateUser(user);

                NumberedString answer = new NumberedString(userId, "Membership lost");
                responseQueue.add(answer);
                return null;
            } catch (NumberFormatException e) {
                NumberedString answer = new NumberedString(userId,
                        "Please enter a valid group id");
                responseQueue.add(answer);
                currentState.setStateId(1);
                return currentState;
            } catch (UserNotFound e) {
                throw new RuntimeException(e);
            } catch (MalformedQuery e) {
                throw new RuntimeException();
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid group id");
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
