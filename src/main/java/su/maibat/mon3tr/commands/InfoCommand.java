package su.maibat.mon3tr.commands;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;


public abstract class InfoCommand implements StatelessCommand {
    protected String info = "";

    @Override
    public final State execute(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        executeWithoutState(userId, args, responseQueue);
        return null;
    }

    @Override
    public final void executeWithoutState(final int userId, final String[] args,
            final BlockingQueue<NumberedString> responseQueue) {
        try {
            responseQueue.put(new NumberedString(userId, info));
        } catch (InterruptedException e) {
        }
    }

    /**
     * @param newInfo new info property
    */
    public void setInfo(final String newInfo) {
        info = newInfo;
    }
}
