package su.maibat.mon3tr.commands.group;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;


public final class DeleteGroup extends ListGroups {
    private final SQLiteLinker db;


    public DeleteGroup(final SQLiteLinker linker) {
        super(linker);
        this.db = linker;
    }


    public String getName() {
        return "delete";
    }

    public String getHelp() {
        return "Deletes one of your owned group.\nInteractive mode only";
    }


    public State execute(final int userId, final String[] argsArg, final State currentStateArg,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        State currentState = currentStateArg;
        String[] args = argsArg != null ? argsArg : new String[0];
        if (currentState == null) {
            currentState = new State(0, new String[]{}, this);
        }
        switch (currentState.getStateId()) {
            case (0):
                return groupTable(userId, args, currentState, responseQueue);
            case (1):
                return selectIndex(userId, args, currentState, responseQueue);
            default:
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }


    private State groupTable(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        GroupQuery[] queryList;
        try {
            queryList = db.getOwnedGroups(userId);
        } catch (UserNotFound e) {
            throw new CommandException("User not found");
        }
        if (queryList.length == 0) {
            NumberedString answer = new NumberedString(userId, "You have no groups");
            responseQueue.add(answer);
            return null;
        }

        NumberedString answer = new NumberedString(userId, super.printTable(queryList));
        responseQueue.add(answer);

        String[] idList = new String[queryList.length];
        for (int i = 0; i < queryList.length; i++) {
            idList[i] = Integer.toString(queryList[i].getId());
        }
        currentState.setMemory(idList);

        return selectIndex(userId, args, currentState, responseQueue);

    }

    private State selectIndex(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        if (args.length > 0 && isValid(args[0], currentState.getMemory().length)) {

            int removeId = Integer.parseInt(args[0]) - 1;

            int removeQueryId = Integer.parseInt(currentState.getMemory()[removeId]);
            db.removeGroup(removeQueryId);

            NumberedString answer = new NumberedString(userId, "Group deleted");
            responseQueue.add(answer);
            return null;

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
