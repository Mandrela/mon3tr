package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class ListGroupTaskCommand implements Command {
    private static final int OFFSET = 1000;
    private final SQLiteLinker db;
    public ListGroupTaskCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    /**
     * @return kek.
     */
    public String getName() {
        return "listGroupTask";
    }

    /**
     * @return kek.
     */
    public String getHelp() {
        return "This command show tasks of some group";
    }

    /**
     * @param userId kek.
     * @param args kek.
     * @param state kek.
     * @param responseQueue kek.
     * @return  kek.
     */
    public State execute(final int userId, final String[] args, final State state,
            final BlockingQueue<NumberedString> responseQueue) throws CommandException {
        State currentState;
        if (state == null) {
            currentState = new State(0, new String[]{}, (Command) this);
        } else {
            currentState = state;
        }
        switch (currentState.getStateId()) {
            case (0):
                return showGroups(userId, args, currentState, responseQueue);
            case (1):
                return selectGroup(userId, args, currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }


    /**
     * @param userId
     * @param args
     * @param currentState
     * @param responseQueue
     * @return kek
     */
    protected State showGroups(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        try {
            GroupQuery[] groupList = db.getOwnedGroups(userId);
            if (groupList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any groups");
                responseQueue.add(answer);
                return null;
            }
            NumberedString answer = new NumberedString(userId, printGroupTable(groupList));
            responseQueue.add(answer);

            String[] idList = new String[groupList.length];
            for (int i = 0; i < groupList.length; i++) {
                idList[i] = Integer.toString(groupList[i].getId());
            }
            currentState.setMemory(idList);
            return selectGroup(userId, args, currentState, responseQueue);

        } catch (UserNotFound unf) {
            NumberedString answer = new NumberedString(userId, "You have not any groups");
            responseQueue.add(answer);
            return null;
        }
    }


    /**
     * @param userId
     * @param args
     * @param currentState
     * @param responseQueue
     * @return kek
     */
    protected State selectGroup(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        if (args.length != 0) {
            if (isValid(args[0], currentState.getMemory().length)) {
                int reqId = Integer.parseInt(args[0]) - 1;
                String reqGroup = currentState.getMemory()[reqId];
                currentState.setMemory(new String[]{reqGroup});
                return showDeadlines(userId, args, currentState, responseQueue);
            } else {
                NumberedString answer = new NumberedString(userId, "Please enter a"
                        + " valid group id (number)");
                responseQueue.add(answer);
                currentState.setStateId(1);
                return currentState;
            }
        } else {
            NumberedString answer = new NumberedString(userId, "Please enter a"
                    + " valid group id (number)");
            responseQueue.add(answer);
            currentState.setStateId(1);
            return currentState;
        }
    }


    protected State showDeadlines(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        try {
            int groupId = Integer.parseInt(currentState.getMemory()[0]);

            DeadlineQuery[] queryList = db.getGroupsDeadlines(new int[]{groupId});
            if (queryList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any deadlines");
                responseQueue.add(answer);
                return null;
            }

            NumberedString answer = new NumberedString(userId, printDeadlineTable(queryList));
            responseQueue.add(answer);

            String[] idList = new String[queryList.length + 1];
            idList[0] = currentState.getMemory()[0];
            for (int i = 1; i < queryList.length; i++) {
                idList[i] = Integer.toString(queryList[i].getId());
            }
            currentState.setMemory(idList);

            return null;
        } catch (GroupNotFound e) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }
    }


    /**
     * @param queryList
     * @return kek
     */
    protected final String printDeadlineTable(final DeadlineQuery[] queryList) {
        String answer = "";
        String answerFragment = "";
        for (int i = 0; i < queryList.length; i++) {
            answerFragment = answerFragment.concat((i + 1) + " : " + queryList[i].getName() + " : "
                    + new SimpleDateFormat("dd/MM/yyyy").
                    format(new Date(queryList[i].getExpireTime() * OFFSET)));
            if (queryList[i].isBurning()) {
                answerFragment = answerFragment + "\uD83D\uDD25";
            }
            if (queryList[i].isDead()) {
                answerFragment = answerFragment + "\uD83D\uDC80";
            }
            answer = answer.concat(answerFragment + "\n");
            answerFragment = "";

        }
        return answer;
    }

    protected String printGroupTable(final GroupQuery[] groupList) {
        String answer = "Your own groups: \n\n";
        for (int i = 0; i < groupList.length; i++) {
            answer = answer.concat((i + 1) + " : " + groupList[i].getName());
        }
        return answer;
    }

    /**
     * @param arg
     * @param maxValue
     * @return kek
     */
    protected boolean isValid(final String arg, final int maxValue) {
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
