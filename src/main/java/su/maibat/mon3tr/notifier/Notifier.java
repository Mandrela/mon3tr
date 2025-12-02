package su.maibat.mon3tr.notifier;

import static su.maibat.mon3tr.Main.DEBUG;
import static su.maibat.mon3tr.Main.ERROR;
import static su.maibat.mon3tr.Main.SEC_TO_MILLIS_FACTOR;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;


public final class Notifier implements Runnable {
    private static final int WAIT_TIME_SEC = 1 * 60;

    private final DataBaseLinker db;
    private final BlockingQueue<NumberedString> queue;

    public Notifier(final DataBaseLinker dataBase, final BlockingQueue<NumberedString> responseQueue) {
        db = dataBase;
        queue = responseQueue;
    }

    // TODO: Get all burning and not notified, Test, Arch
    public void runOnce() {
        for (DeadlineQuery deadline: db.getAllDeadlines()) {
            if (deadline.isBurning() && !deadline.isNotified()) {
                System.out.println(DEBUG + "[notif]: Found burning deadline " + deadline.getName());
                try {
                    queue.put(new NumberedString(deadline.getOwnerId(),
                        "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\n Your deadline " + deadline.getName()
                        + " is burning\n\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25"));
                    deadline.setNotified();
                    db.updateDeadline(deadline);
                } catch (MalformedQuery e) {
                    System.out.println(ERROR + "Impossible happened, wasn't able to update "
                        + "deadline " + deadline.getName() + " with id " + deadline.getId());
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void run() {
        while (true) {
            try {
                runOnce();
                Thread.sleep(WAIT_TIME_SEC * SEC_TO_MILLIS_FACTOR);
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                System.out.println(ERROR + "Got unknown exception (" + e.getClass()
                    + ") with message: " + e.getMessage());
            }
        }
    }
}
