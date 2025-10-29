package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;


public interface Command {
    String getName();
    String getHelp();


    void execute(Chat chat);
}
