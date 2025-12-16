package su.maibat.mon3tr.notifier;

import static su.maibat.mon3tr.Main.ERROR;

import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.DateUtils;
import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;


public final class Notifier implements Runnable, Reactor {
    private static final String OWN_TASK_BURN = "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\n "
        + "Personal: deadline %s is burning\n\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25";
    private static final String OWN_TASK_DEAD = "Presonal: deadline %s as good as dead now";
    private static final String MEM_TASK_BURN = "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\n "
            + "Membership: deadline %s is burning \n\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25";
    private static final String MEM_TASK_DEAD = "Membership: deadline %s as good as dead now";
    private static final int QUEUE_USE_THRESHOLD = 4;

    private final DataBaseLinker db;
    private final BlockingQueue<NumberedString> queue;

    public Notifier(final DataBaseLinker dataBase,
            final BlockingQueue<NumberedString> responseQueue) {
        db = dataBase;
        queue = responseQueue;
    }


    private void sendNotification(final int id, final String format, final String name,
            final boolean fast) throws InterruptedException {
        while (!fast && queue.remainingCapacity() < QUEUE_USE_THRESHOLD) {
            Thread.yield();
        }
        queue.put(new NumberedString(id, String.format(format, name)));
    }

    public void runOnDeadlines(final DeadlineQuery[] deadlines, final boolean fastExec)
                throws MalformedQuery, InterruptedException {
        System.out.println("Begun run for " + deadlines.length);
        for (DeadlineQuery deadline: deadlines) {
            long timeDiff = deadline.getExpireTime()
                - System.currentTimeMillis() / DateUtils.MILLIS_IN_SEC;
            if (timeDiff < 0) {
                sendNotification(
                    deadline.getOwnerId(),
                    OWN_TASK_DEAD,
                    deadline.getName(),
                    fastExec
                );

                for (UserQuery user: db.getUsersForGroups(deadline.getAssignedGroups())) {
                    sendNotification(user.getId(), MEM_TASK_DEAD, deadline.getName(), fastExec);
                }
                // normal -> burning
                // not notified -> notified

                deadline.setState(1 + 2);
                deadline.setNotified(true);
                db.updateDeadline(deadline);
            } else if (timeDiff <= deadline.getRemindOffset() && !deadline.isNotified()) {
                if (!deadline.isBurning()) {
                    deadline.setState(1);
                }

                sendNotification(
                    deadline.getOwnerId(),
                    OWN_TASK_BURN,
                    deadline.getName(),
                    fastExec
                );

                for (UserQuery user: db.getUsersForGroups(deadline.getAssignedGroups())) {
                    sendNotification(user.getId(), MEM_TASK_BURN, deadline.getName(), fastExec);
                }

                deadline.setNotified(true);
                db.updateDeadline(deadline);
            }

            // if (deadline.isBurning() && !deadline.isNotified()) {
            // System.out.println(DEBUG + "[notif]: Found burning deadline " + deadline.getName());
            //     try {
            //         queue.put(new NumberedString(
            //             deadline.getOwnerId(),
            //             String.format(OWN_TASK_BURN, deadline.getName())
            //         ));
            //         deadline.setNotified(true);
            //         db.updateDeadline(deadline);
            //     } catch (MalformedQuery e) {
            //         System.out.println(ERROR + "Impossible happened, wasn't able to update "
            //             + "deadline " + deadline.getName() + " with id " + deadline.getId());
            //     } catch (InterruptedException e) {
            //     }
            // }
        }
    }

    public void run() {
        // Align to 30 minutes
        while (true) {
            try {
                runOnDeadlines(db.getBurningDeadlines(), false);
                Thread.sleep(DateUtils.SECONDS_IN_30_MINUTES * DateUtils.MILLIS_IN_SEC);
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                System.out.println(ERROR + "Got unknown exception (" + e.getClass()
                    + ") with message: " + e.getMessage());
            }
        }
    }

    @Override
    public void trigger(final int id) {
        try {
            DeadlineQuery[] deadlines = new DeadlineQuery[]{db.getDeadline(id)};
            if (deadlines[0].isCompleted() || deadlines[0].isDead()) {
                return;
            }
            deadlines[0].setNotified(false);
            runOnDeadlines(deadlines, true);
        } catch (DeadlineNotFound | MalformedQuery e) {
            System.out.println(ERROR + "triggered on non existing or malformed deadline " + id
                + ": " + e.getMessage());
        } catch (InterruptedException e) {
            return;
        }
    }
}
