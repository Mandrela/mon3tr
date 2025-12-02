package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class MoveToGroupCommand implements Command {
    private static final int OFFSET = 1000;
    private final SQLiteLinker db;
    public MoveToGroupCommand(final SQLiteLinker linker) {
        this.db = linker;
    }

    public String getName() {
        return "MoveToGroupGroup";
    }
    public String getHelp() {
        return "This command link your deadline to group";
    }

    public State execute(int userId, String[] args, State currentState,
                         BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        switch (currentState.getStateId()) {
            case(0):
                return showGroups(userId, args, currentState, responseQueue);
            case (1):
                return selectGroup(userId, args, currentState, responseQueue);
            case(2):
                return showDeadlines(userId, args, currentState, responseQueue);
            case (3):
                return selectDeadlineIndex(userId, args, currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }

    private State showGroups(int userId, String[] args, State currentState,
                             BlockingQueue<NumberedString> responseQueue) {
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


    private State selectGroup(int userId, String[] args, State currentState,
                              BlockingQueue<NumberedString> responseQueue) {
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

    private State showDeadlines(int userId, String[] args, State currentState,
                                BlockingQueue<NumberedString> responseQueue) {
        try {
            DeadlineQuery[] queryList = db.getDeadlinesForUser(userId);
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

            return selectDeadlineIndex(userId, args, currentState, responseQueue);
        } catch (DeadlineNotFound dnf) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }
    }


    private State selectDeadlineIndex (int userId, String[] args, State currentState,
                                       BlockingQueue<NumberedString> responseQueue) {
        if (isValid(args[0], currentState.getMemory().length)) {
            try {
                int deadlineId = Integer.parseInt(args[0]);

                int deadlineQueryId = Integer.parseInt(currentState.getMemory()[deadlineId]);
                int groupQueryId = Integer.parseInt(currentState.getMemory()[0]);

                DeadlineQuery deadline = db.getDeadline(deadlineQueryId);

                int[] oldGroups = deadline.getAssignedGroups();
                int[] newGroups = new int[oldGroups.length + 1];
                System.arraycopy(oldGroups,0,newGroups,0, oldGroups.length);
                newGroups[oldGroups.length] = groupQueryId;

                deadline.setAssignedGroups(newGroups);
                db.updateDeadline(deadline);

                NumberedString answer = new NumberedString(userId, "We link your deadline");
                responseQueue.add(answer);
                return null;
            } catch (DeadlineNotFound | MalformedQuery dnf) {
                currentState.setStateId(4);
                return currentState;
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid deadline id (number)");
            responseQueue.add(answer);
            currentState.setStateId(3);
            return currentState;
        }
    }


    private String printGroupTable(GroupQuery[] groupList) {
        String answer = "Your own groups: \n\n";
        for (int i = 0; i < groupList.length; i++) {
            answer = answer.concat((i + 1) + " : " + groupList[i].getName());
        }
        return answer;
    }

    private String printDeadlineTable(final DeadlineQuery[] queryList) {
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
