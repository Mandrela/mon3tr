package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;


public class DeadlineRemoveCommand extends MyDeadlinesCommand {

    private final SQLiteLinker db;

    public DeadlineRemoveCommand(final SQLiteLinker inputLinker) {
        super(inputLinker);
        this.db = inputLinker;
    }

    public final String getName() {
        return "remove";
    }

    public final String getHelp() {
        return "This command remove your deadline";
    }


    public final State execute(final int userId, final String[] args, final State currentState,
                               final BlockingQueue<NumberedString> responseQueue)
            throws CommandException {
        switch (currentState.getStateId()) {
            case(0):
                return deadlineTable(userId, args, currentState, responseQueue);
            case (1):
                return selectIndex(userId, args, currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }


    private State deadlineTable(final int userId, final String[] args, final State currentState,
                                final BlockingQueue<NumberedString> responseQueue) {
        try {
            DeadlineQuery[] queryList = db.getDeadlinesForUser(userId);
            if (queryList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any deadlines");
                responseQueue.add(answer);
                return null;
            }

            NumberedString answer = new NumberedString(userId, printTable(queryList));
            responseQueue.add(answer);

            String[] idList = new String[queryList.length];
            for (int i = 0; i < queryList.length; i++) {
                idList[i] = Integer.toString(queryList[i].getId());
            }
            currentState.setMemory(idList);

            return selectIndex(userId, args, currentState, responseQueue);
        } catch (DeadlineNotFound dnf) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectIndex(final int userId, final String[] args, final State currentState,
                              final BlockingQueue<NumberedString> responseQueue) {
        if (isValid(args[0], currentState.getMemory().length)) {
            try {
                int removeId = Integer.parseInt(args[0]) - 1;

                int removeQueryId = Integer.parseInt(currentState.getMemory()[removeId]);
                db.getUserById(userId).setLimit(db.getUserById(userId).getLimit() + 1);
                db.removeDeadline(removeQueryId);

                NumberedString answer = new NumberedString(userId, "You have closed this gestalt!!!");
                responseQueue.add(answer);
                return null;
            } catch (UserNotFound unf) {
                return currentState;
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid deadline id (number)");
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
