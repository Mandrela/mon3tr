package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.TelegramChat;


public interface Command {
    String getName();
    String getHelp();

    void execute(TelegramChat telegramChat);
}
