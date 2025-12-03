package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.notifier.Reactor;

import java.util.concurrent.BlockingQueue;

public class UpdateOffsetCommand extends MyDeadlinesCommand {
    private final Reactor reactor;
    private final SQLiteLinker db;
    private static final int SECONDS_IN_DAY = 86400;

    public UpdateOffsetCommand(final SQLiteLinker inputLinker, final Reactor reactorArg) {
        super(inputLinker);
        this.db = inputLinker;
        this.reactor = reactorArg;
    }

    public final String getName() {
        return "offset";
    }

    public final String getHelp() {
        return "You can use this command to set how long before "
                + "the deadline will start to burn";
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
            case (2):
                if (args.length == 0) {
                    return selectOffset(userId, "", currentState, responseQueue);
                }
                return selectOffset(userId, args[0], currentState, responseQueue);
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
                    NumberedString answer = new NumberedString(userId,
                        "You have not any deadlines");
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
            if (args.length != 0 && isValidId(args[0], currentState.getMemory().length)) {
                int updateId = Integer.parseInt(args[0]) - 1;
                String updateQueryId = currentState.getMemory()[updateId];
                currentState.setMemory(new String[]{updateQueryId});
                if (args.length > 1) {
                    return selectOffset(userId, args[1], currentState, responseQueue);
                }
                return selectOffset(userId, "", currentState, responseQueue);

            } else {
                NumberedString answer = new NumberedString(userId,
                        "Please enter a valid deadline id (number)");
                responseQueue.add(answer);
                currentState.setStateId(1);
                return currentState;
            }
        }

        private State selectOffset(final int userId, final String arg, final State currentState,
        final BlockingQueue<NumberedString> responseQueue) {
            if (isValidOffset(arg)) {
                try {
                    int updateId = Integer.parseInt(currentState.getMemory()[0]);

                    db.getDeadline(updateId).setRemindOffset(Long.parseLong(arg)
                            * SECONDS_IN_DAY);
                    NumberedString answer = new NumberedString(userId,
                            "Offset has been updated");
                    responseQueue.add(answer);
                    return null;
                } catch (DeadlineNotFound e) {
                    NumberedString answer = new NumberedString(userId,
                            "Please enter a offset (days before final date)");
                    responseQueue.add(answer);
                    currentState.setStateId(2);
                    return currentState;
                }
            } else {
                NumberedString answer = new NumberedString(userId,
                        "Please enter a offset (days before final date)");
                responseQueue.add(answer);
                currentState.setStateId(2);
                return currentState;
            }
        }


    private boolean isValidId(final String arg, final int maxValue) {
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

    private boolean isValidOffset(final String arg) {
        try {
            int intArg = Integer.parseInt(arg);
            return intArg >= 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}

