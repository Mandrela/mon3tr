package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public class DeadlineRemoveCommand extends MyDeadlinesCommand {

    private final SQLiteLinker linker;

    public DeadlineRemoveCommand(final SQLiteLinker inputLinker) {
        super(inputLinker);
        this.linker = inputLinker;
    }

    public final String getName() {
        return "remove";
    }

    public final String getHelp() {
        return "This command remove your deadline";
    }

    public final void execute(final Chat chat) {

        try {
            UserQuery user = linker.getUserByChatId(chat.getChatId());
            try {
                DeadlineQuery[] queryList = linker.getDeadlinesForUser(user.getId());
                if (queryList.length == 0) {
                    chat.sendAnswer("You have not any deadlines");
                    return;
                }

                super.printTable(chat, queryList);

                String[] arguments = chat.getAllMessages();
                String arg = "";
                if (arguments.length > 0) {
                    arg = arguments[0];
                }

                while (!isValid(arg, queryList.length)) {
                    arg = chat.getMessage("Please enter a valid deadline id (number)");
                }

                int removeId = Integer.parseInt(arg);

                linker.removeDeadline(queryList[removeId].getId());

                chat.sendAnswer("You have closed this gestalt!!!");

            } catch (DeadlineNotFound dnf) {
                chat.sendAnswer("You have not any deadlines");
            }
        } catch (UserNotFound unf) {
            UserQuery userQuery = new UserQuery(-1, chat.getChatId());
            try {
                linker.addUser(userQuery);
            } catch (MalformedQuery me) {
                chat.sendAnswer("Something went wrong");
            }
            chat.sendAnswer("You have not any deadlines");
        } catch (InterruptedException ie) {

        }
    }

    private boolean isValid(final String arg, final int maxValue) {
        //Не число
        //Больше предела
        //Меньше 1
        try {
            int intArg = Integer.parseInt(arg);
            return intArg <= maxValue && intArg >= 1;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
