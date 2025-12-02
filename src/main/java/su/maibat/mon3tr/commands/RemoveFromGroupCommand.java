package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;

import java.util.concurrent.BlockingQueue;

public class RemoveFromGroupCommand extends ListGroupTaskCommand {
    private static final int OFFSET = 1000;
    private final SQLiteLinker db;
    public RemoveFromGroupCommand(final SQLiteLinker linker) {
        super(linker);
        this.db = linker;
    }

    public String getName() {
        return "removeFromGroup";
    }
    public String getHelp() {
        return "This command unlink your deadline from group";
    }

    public State execute(int userId, String[] args, State currentState,
                         BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        switch (currentState.getStateId()) {
            case (0):
                return super.showGroups(userId, args, currentState, responseQueue);
            case (1):
                return super.selectGroup(userId, args, currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }

    private State showDeadlines(int userId, String[] args, State currentState,
                                BlockingQueue<NumberedString> responseQueue) {
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

            return selectDeadlineIndex(userId, args, currentState, responseQueue);

        } catch (GroupNotFound e) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectDeadlineIndex (int userId, String[] args, State currentState,
                                       BlockingQueue<NumberedString> responseQueue) {
        if (super.isValid(args[0], currentState.getMemory().length)) {
            try {
                int deadlineId = Integer.parseInt(args[0]);

                int deadlineQueryId = Integer.parseInt(currentState.getMemory()[deadlineId]);
                int groupQueryId = Integer.parseInt(currentState.getMemory()[0]);

                DeadlineQuery deadline = db.getDeadline(deadlineQueryId);

                int[] oldGroups = deadline.getAssignedGroups();
                int[] newGroups = new int[oldGroups.length - 1];
                for (int i = 0; i < oldGroups.length; i++) {
                    if (oldGroups[i] != groupQueryId) {
                        newGroups[i] = oldGroups[i];
                    }
                }

                deadline.setAssignedGroups(newGroups);
                db.updateDeadline(deadline);

                NumberedString answer = new NumberedString(userId, "We unlink your deadline");
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

}
