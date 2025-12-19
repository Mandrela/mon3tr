package su.maibat.mon3tr.commands.task;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.Command;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.util.concurrent.BlockingQueue;


public class RemoveTask extends ListPersonalTasks {
    private final SQLiteLinker db;


    public RemoveTask(final SQLiteLinker inputLinker) {
        super(inputLinker);
        this.db = inputLinker;
    }


    public final String getName() {
        return "remove";
    }

    public final String getHelp() {
        return "Completely deletes tasks.\n"
            + "Interactive mode is recommended, although /remove <id> syntax is supported";
    }


    public final State execute(final int userId, final String[] args, final State state,
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
                return deadlineTable(userId, args, currentState, responseQueue);
            case (1):
                return selectIndex(userId, args, currentState, responseQueue);
            default:
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }


    private State deadlineTable(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        try {
            DeadlineQuery[] queryList = db.getDeadlinesForUser(userId);
            if (queryList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have no tasks");
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
        } catch (DeadlineNotFound dnf) {
            NumberedString answer = new NumberedString(userId, "You have no tasks");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectIndex(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (args.length != 0 && isValid(args[0], currentState.getMemory().length)) {
            try {
                int removeId = Integer.parseInt(args[0]) - 1;

                int removeQueryId = Integer.parseInt(currentState.getMemory()[removeId]);
                UserQuery user =  db.getUserById(userId);
                user.setLimit(user.getLimit() + 1);
                db.updateUser(user);

                db.removeDeadline(removeQueryId);

                NumberedString answer = new NumberedString(userId,
                    "Task removed");
                responseQueue.add(answer);
                return null;
            } catch (UserNotFound unf) {
                return currentState;
            } catch (MalformedQuery e) {
                throw new CommandException(e.getMessage());
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid task id");
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
