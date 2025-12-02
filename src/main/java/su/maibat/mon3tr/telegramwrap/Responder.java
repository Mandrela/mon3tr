package su.maibat.mon3tr.telegramwrap;

import static su.maibat.mon3tr.Main.CRITICAL;
import static su.maibat.mon3tr.Main.ERROR;
import static su.maibat.mon3tr.Main.INFO;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.exceptions.LinkerException;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


/** Wrapping for Bot to send messages through telegram. */
public class Responder implements Runnable {
    private static final int REPLY_AMOUNT = 3;

    private final DataBaseLinker db;
    private final TelegramClient tgClient;
    private final Map<Integer, Long> tempIds;
    private final BlockingQueue<NumberedString> responseQueue;


    /**
     * @param telegramClient
     * @param messageQueue Queue to work on.
     * @param database Source of uid {@literal <=>} chatId mapping.
     * @param temporaryIdentifiers Temporary uid {@literal <=>} chatId mapping.
     * More preferable than database. Can be null.
     * @throws IllegalArgumentException If one of the first three arguments is null.
     */
    public Responder(final TelegramClient telegramClient,
            final BlockingQueue<NumberedString> messageQueue, final DataBaseLinker database,
            final Map<Integer, Long> temporaryIdentifiers) throws IllegalArgumentException {
        if (telegramClient == null || messageQueue == null || database == null) {
            throw new IllegalArgumentException("Three first arguments shouldn't be null");
        }
        responseQueue = messageQueue;
        db = database;
        tgClient = telegramClient;
        tempIds = temporaryIdentifiers;
    }


    /** Works on specified queue infinitely*.
     * @implNote *Infinitely = while not interrupted.
     * @implNote Won't quit while queue is not empty.
     */
    @Override
    public void run() {
        while (true) {
            try {
                if (responseQueue.isEmpty()) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    Thread.yield();
                } else {
                    NumberedString message = responseQueue.poll();
                    int uid = message.getNumber();
                    try {
                        Long chatId;
                        if (tempIds != null && tempIds.containsKey(uid)) {
                            chatId = tempIds.get(uid);
                        } else {
                            chatId = db.getChatIdByUserId(uid);
                        }

                        assert message.getString() != null : "Strings in queue shouldn't be null";
                        sendToTelegram(chatId, mutate(message.getString()));
                    } catch (UserNotFound e) {
                        System.err.println(ERROR + "[Responder] User with id " + uid
                            + " wasn't found, skipping malformed message.");
                    }
                }
                continue;
            } catch (InterruptedException e) {
                System.out.println(INFO + "[Responder] Got interrupted, exiting.");

            } catch (LinkerException e) {
                System.err.println(CRITICAL + "[Responder] Database failed with message: "
                    + e.getMessage());

            } catch (Exception e) {
                System.err.println(CRITICAL + "[Responder] Failed with exception: "
                    + e.getMessage());
            }
            break;
        }
    }

    /** Interacton with Telegram API.
     * @implNote Will try to resend message couple of time if fails.
     * @param chatId Telegram chat id.
     * @param answer Message string.
     * @throws TelegramApiException If couldn't send message due to TelegramAPI issue.
     * @throws InterruptedException If interrupted while sleeping between replies.
     */
    private void sendToTelegram(final Long chatId, final String answer)
            throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId.toString(), answer);
        for (int counter = 0; counter < REPLY_AMOUNT; counter++) {
            try {
                tgClient.execute(sendMessage);
                return;
            } catch (TelegramApiException e) {
                System.err.println(ERROR + "[Responder] Couldn't send message (" + (counter + 1)
                    + "): " + e);
            }
        }
        throw new TelegramApiException("Telegram API seems unavailable after " + REPLY_AMOUNT
            + " tries.");
    }

    /** Method for integration of platform specific features.
     * @param string Actual response from bot.
     * @return Mutated response (for the sake of beauty).
     * @implNote Currently doing nothing.
     */
    private String mutate(final String string) {
        return string; // expansion point
    }
}
