package su.maibat.mon3tr.chat;

import java.util.LinkedList;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


/**
 * Chat with two neat features: can send messages through telegram
 * and can be frozen - meaning no new messages can be available for
 * chat reader while old aren't read.
 */
public final class TelegramChat implements Chat {
    private static final int REPLY_AMOUNT = 3;

    private final Long chatId;
    private final TelegramClient telegramClient;

    private final LinkedList<String> messages = new LinkedList<>();
    private final LinkedList<String> addBuffer = new LinkedList<>();
    private boolean isFrozen = false;

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

    public void froze() {
        isFrozen = true;
    }

    public void unfroze() {
        isFrozen = false;
        transfer();
    }

    private void transfer() {
        while (!addBuffer.isEmpty()) {
            messages.add(addBuffer.poll());
        }
    }

    @Override
    public String getMessage() {
        if (isFrozen && messages.isEmpty()) {
            unfroze();
        }

        while (messages.isEmpty()) {
            return ""; // Multithreading
        }
        return messages.poll();
    }

    /**
     * @return Array of strings. If chat was in frozen state, return array will
     * consist only from messages from first addMessage[s];
     */
    @Override
    public String[] getAllMessages() {
        // https://stackoverflow.com/questions/44310226/what-does-stringnew-mean
        String[] result = messages.toArray(String[]::new);
        messages.clear();

        if (isFrozen) {
            unfroze();
        }

        return result;
    }

    @Override
    public void addMessage(final String message) {
        if (isFrozen) {
            addBuffer.add(message);
        } else {
            messages.add(message);
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
                System.out.println("Couldn't send message " + answer + "\n" + e);
            }
        }
    }
}
