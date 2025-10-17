package su.maibat.mon3tr.chat;

import java.util.LinkedList;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public final class TelegramChat implements Chat {
    private static final int REPLY_AMOUNT = 3;

    private final Long chatId;
    private final TelegramClient telegramClient;

    private LinkedList<String> messages;

    public TelegramChat(final Long chatIdArgument, final TelegramClient telegramClientArgument) {
        chatId = chatIdArgument;
        telegramClient = telegramClientArgument;
    }

    @Override
    public String getMessage() {
        while (messages.isEmpty()) {
            messages.peek();
        } // probably there is a way to give it up
        return messages.poll();
    }

    @Override
    public String[] getAllMessages() {
        String[] result = (String[]) messages.toArray();
        messages.clear();
        return result;
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

    @Override
    public void addMessage(final String message) {
        messages.add(message);
    }
}
