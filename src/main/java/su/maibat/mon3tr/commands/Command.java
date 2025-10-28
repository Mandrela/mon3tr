package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;


public interface Command {
    String getName();
    String getHelp();


    void execute(Chat chat);
}
