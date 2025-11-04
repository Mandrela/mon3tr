package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;

public class DeadlineUpdateCommand implements Command {

    public final String getName() {return "update";}

    public final String getHelp() {return "This command update your deadline";}

    public void execute(Chat chat) {
        String[] arguments = chat.getAllMessages();

        if (arguments.length == 0) {
            chat.sendAnswer("Something went wrong, try again with input some arguments");
        } else {
            chat.sendAnswer("Work in progress");
        }
    }
}
