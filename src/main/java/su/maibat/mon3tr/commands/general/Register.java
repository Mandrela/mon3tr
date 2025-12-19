package su.maibat.mon3tr.commands.general;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.State;
import su.maibat.mon3tr.commands.StatelessCommand;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;


public final class Register implements StatelessCommand {
    private final DataBaseLinker db;
    private final Map<Integer, Long> tempIds;


    public Register(final DataBaseLinker dataBase, final Map<Integer, Long> idMap) {
        db = dataBase;
        tempIds = idMap;
    }


    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getHelp() {
        return "Try and see";
    }


    @Override
    public State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue)
            throws CommandException {
        executeWithoutState(userId, args, responseQueue);
        return null;
    }

    @Override
    public void executeWithoutState(final int userId, final String[] args,
            final BlockingQueue<NumberedString> responseQueue) {
        if (!db.checkUserExists(userId)) {
            UserQuery newUser = new UserQuery();
            newUser.setChatId(tempIds.get(userId));
            try {
                try {
                    db.addUser(newUser);
                    responseQueue.put(new NumberedString(userId, "Registered succesfully"));
                } catch (MalformedQuery e) {
                    responseQueue.put(new NumberedString(userId, "Couldn't register"));
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
