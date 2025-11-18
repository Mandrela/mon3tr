package su.maibat.mon3tr.chat;

import static su.maibat.mon3tr.Main.ERROR;

import java.util.LinkedList;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


/**
 * Chat with two neat features: can send messages through telegram
 * and can be frozen - meaning no new messages can be available for
 * chat reader while old aren't read.
 */
public final class TelegramChat implements Chat, MessageSink {
    private static final int REPLY_AMOUNT = 3;

    private volatile boolean isInterrupted = false;
    private final TelegramClient telegramClient;
    private final Long chatId;

    private final LinkedList<String> messages = new LinkedList<>();
    private final LinkedList<String> addBuffer = new LinkedList<>();
    private volatile boolean isFrozen = false;

    private final Object bufferLock = new Object();

    public TelegramChat(final Long chatIdArgument, final TelegramClient telegramClientArgument) {
        chatId = chatIdArgument;
        telegramClient = telegramClientArgument;
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty() && addBuffer.isEmpty();
    }

    @Override
    public long getChatId() {
        return chatId;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void freeze() {
        isFrozen = true;
    }

    public void unfreeze() {
        isFrozen = false;
        transfer();
    }

    private void transfer() {
        synchronized (bufferLock) {
            while (!addBuffer.isEmpty()) {
                messages.add(addBuffer.poll());
            }
        }
    }

    @Override
    public String getMessage() throws InterruptedException {
        if (isFrozen && messages.isEmpty()) {
            unfreeze();
        }

        while (messages.isEmpty()) {
            if (isInterrupted) {
                throw new InterruptedException("Chat " + chatId + " was interrupted");
            }
            Thread.yield();
        }

        synchronized (bufferLock) {
            return messages.poll();
        }
    }

    /**
     * @return Array of strings. If chat was in frozen state, return array will
     * consist only from messages from first addMessage[s];
     */
    @Override
    public String[] getAllMessages() {
        // https://stackoverflow.com/questions/44310226/what-does-stringnew-mean
        synchronized (bufferLock) {
            String[] result = messages.toArray(String[]::new);
            messages.clear();

            if (isFrozen) {
                unfreeze();
            }

            return result;
        }
    }

    @Override
    public void addMessage(final String message) {
        synchronized (bufferLock) {
            if (isFrozen) {
                addBuffer.add(message);
            } else {
                messages.add(message);
            }
        }
    }

    @Override
    public void sendAnswer(final String answer) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), answer);
        for (int counter = 0; counter < REPLY_AMOUNT; counter++) {
            try {
                telegramClient.execute(sendMessage);
                return;
            } catch (TelegramApiException e) {
                System.out.println(ERROR + "Couldn't send message " + answer + "\n" + e);
            }
        }
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
    }
}
