package su.maibat.mon3tr.telegramwrap;

import java.util.WeakHashMap;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import su.maibat.mon3tr.bot.BotBackend;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


/** Wrapping for bot to recieve commands from telegram chats. */
public class Gate implements LongPollingSingleThreadUpdateConsumer {
    private static final char COMMAND_PREFIX = '/';

    private final BotBackend bot;
    private final DataBaseLinker db;
    private final WeakHashMap<Integer, Long> tempIds;

    private int lastTempUid = 0;


    /**
     * @param botBackend Will wrap around this
     * @param dataBase Source of chatId {@literal <=>} uid mapping.
     * @param temporaryIdentifiers Where to put temporal uid {@literal <=>} chatId mappings.
     * @throws IllegalArgumentException When any of the arguments is null.
     */
    public Gate(final BotBackend botBackend, final DataBaseLinker dataBase,
            final WeakHashMap<Integer, Long> temporaryIdentifiers)
            throws IllegalArgumentException {
        if (botBackend == null || dataBase == null || temporaryIdentifiers == null) {
            throw new IllegalArgumentException("All arguments should be not null values");
        }
        bot = botBackend;
        db = dataBase;
        tempIds = temporaryIdentifiers;
    }


    /** Telegram update processing.
     * @param update Telegram abstraction.
     * @implNote If user isn't registered yet, will assign temporal id.
     */
    @Override
    public void consume(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            int uid;
            try {
                uid = db.getUserIdByChatId(chatId);
            } catch (UserNotFound e) {
                if (--lastTempUid > 0) {
                    lastTempUid = -1;
                }
                uid = lastTempUid;
                tempIds.put(uid, chatId);
            }

            bot.process(uid, mutate(update.getMessage().getText()));
        }
    }

    /** Method for integration of platform specific features.
     * @param string Platform specific command.
     * @return Actual command to bot.
     */
    private String mutate(final String string) {
<<<<<<< HEAD
        if (bot.getCommandPrefix() != COMMAND_PREFIX && string.length() > 1
                && string.charAt(0) == COMMAND_PREFIX) {
=======
        if (string.length() > 1 && string.charAt(0) == COMMAND_PREFIX) {
>>>>>>> feature-7
            return bot.getCommandPrefix() + string.substring(1);
        }
        return string;
    }
}
