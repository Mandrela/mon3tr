package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;


public interface Command {
    String getName();
    String getHelp();

    default boolean isDate(String argument){
        Pattern pattern = compile("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$");
        Matcher matcher = pattern.matcher(argument);
        return matcher.find();
    }
    void execute(Chat chat);
}
