package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.GroupQuery;
import su.maibat.mon3tr.db.SQLiteLinker;


import java.util.concurrent.BlockingQueue;

public class GroupDeleteCommand extends OwnedGroupsCommand {
    private final SQLiteLinker db;
    public GroupDeleteCommand(final SQLiteLinker linker) {
        super(linker);
        this.db = linker;
    }

    public String getName() {
        return "deleteGroup";
    }
    public String getHelp() {
        return "This command destroy one of yours troop";
    }

    public State execute(int userId, String[] args, State currentState,
                         BlockingQueue<NumberedString> responseQueue) throws CommandException {
        if (currentState == null) {
            return (new State(0, new String[]{}, this));
        }
        switch (currentState.getStateId()) {
            case (0):
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


    private State groupTable(final int userId, final String[] args, final State currentState,
    final BlockingQueue<NumberedString> responseQueue) {

        GroupQuery[] queryList = db.getOwnedGroups(userId);
        if (queryList.length == 0) {
            NumberedString answer = new NumberedString(userId, "You have not any groups");
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
        if (isValid(args[0], currentState.getMemory().length)) {

            int removeId = Integer.parseInt(args[0]) - 1;

            int removeQueryId = Integer.parseInt(currentState.getMemory()[removeId]);
            db.removeGroup(removeQueryId);

            NumberedString answer = new NumberedString(userId, "You have remove group");
            responseQueue.add(answer);
            return null;

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




