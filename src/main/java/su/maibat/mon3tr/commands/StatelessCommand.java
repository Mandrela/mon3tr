package su.maibat.mon3tr.commands;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;

public interface StatelessCommand extends Command {
    void executeWithoutState(int userId, String[] args,
        BlockingQueue<NumberedString> responseQueue);
}
