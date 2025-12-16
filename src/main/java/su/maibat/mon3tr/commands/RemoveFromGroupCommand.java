package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;

import java.util.concurrent.BlockingQueue;

public final class RemoveFromGroupCommand extends ListGroupTaskCommand {
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
                return super.showGroups(userId, args, currentState, responseQueue);
            case (1):
                return super.selectGroup(userId, args, currentState, responseQueue);
            case (2):
                return showDeadlines(userId, args, currentState, responseQueue);
            case (2 + 1):
                return selectDeadlineIndex(userId, args[0], currentState, responseQueue);
            default:
                System.out.println("Out state");
                NumberedString answer = new NumberedString(userId, "Something went wrong");
                responseQueue.add(answer);
                return currentState;
        }
    }
    @Override
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

            System.out.println(queryList[0].getId());
            System.out.println(currentState.getMemory()[0]);

            String[] idList = new String[queryList.length + 1];
            idList[0] = currentState.getMemory()[0];
            for (int i = 1; i < queryList.length + 1; i++) {
                idList[i] = Integer.toString(queryList[i - 1].getId());
            }

            currentState.setMemory(idList);
            if (args.length == 1) {
                return selectDeadlineIndex(userId, "", currentState, responseQueue);
            }
            return selectDeadlineIndex(userId, args[1], currentState, responseQueue);

        } catch (GroupNotFound e) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }
    }

    private State selectDeadlineIndex(final int userId, final String arg,
            final State currentState, final BlockingQueue<NumberedString> responseQueue) {
        if (!(arg.isEmpty()) && (arg != "")
                && super.isValid(arg, currentState.getMemory().length)) {
            try {
                int deadlineId = Integer.parseInt(arg);
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
                currentState.setStateId(2 + 2);
                return currentState;
            }
        } else {
            NumberedString answer = new NumberedString(userId,
                    "Please enter a valid deadline id (number)");
            responseQueue.add(answer);
            currentState.setStateId(2 + 1);
            return currentState;
        }
    }

}
