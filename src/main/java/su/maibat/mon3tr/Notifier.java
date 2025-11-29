package su.maibat.mon3tr;

import static su.maibat.mon3tr.Main.DEBUG;
import static su.maibat.mon3tr.Main.ERROR;
import static su.maibat.mon3tr.Main.SEC_TO_MILLIS_FACTOR;

import su.maibat.mon3tr.chat.TelegramChat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import org.telegram.telegrambots.meta.generics.TelegramClient;


public final class Notifier {
    private static final int WAIT_TIME_SEC = 1 * 60;

    private final DataBaseLinker dataBase;
    private final TelegramClient telegramClient;

    public Notifier(final DataBaseLinker dataBaseArg, final TelegramClient telegramClientArg) {
        dataBase = dataBaseArg;
        telegramClient = telegramClientArg;
    }

    // TODO: Get all burning and not notified, Test, Arch
    public void run() {
        for (DeadlineQuery deadline: dataBase.getAllDeadlines()) {
            if (deadline.isBurning() && !deadline.isNotified()) {
                System.out.println(DEBUG + "[notif]: Found burning deadline " + deadline.getName());
                try {
                    long chatId = dataBase.getUserById(deadline.getOwnerId()).getChatId();
                    new TelegramChat(chatId, telegramClient).sendAnswer("\uD83D\uDD25\uD83D\uDD25"
                        + "\uD83D\uDD25\n Your deadline " + deadline.getName() + " is burning\n"
                        + "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25");
                    deadline.setNotified();
                    dataBase.updateDeadline(deadline);
                } catch (UserNotFound e) {
                    dataBase.removeDeadline(deadline.getId());
                } catch (MalformedQuery e) {
                    System.out.println(ERROR + "Impossible happened, wasn't able to update "
                        + "deadline " + deadline.getName() + " with id " + deadline.getId());
                }
            }
        }
    }

    public void runInfinitely() {
        while (true) {
            try {
                run();
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
