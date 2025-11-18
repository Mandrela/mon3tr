package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public class UpdateOffsetCommand extends MyDeadlinesCommand {

    private final SQLiteLinker linker;

    public UpdateOffsetCommand(final SQLiteLinker inputLinker) {
        super(inputLinker);
        this.linker = inputLinker;
    }

    public final String getName() {
        return "offset";
    }

    public final String getHelp() {
        return "You can use this command to set how long before "
                + "the deadline will start to burn";
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

                while (!isValidId(arg, queryList.length)) {
                    arg = chat.getMessage("Please enter a valid deadline id (number)");
                }

                int updateId = Integer.parseInt(arg) - 1;

                DeadlineQuery updateQuery = queryList[updateId];

                String offsetArg = "";
                while (!isValidOffset(arg)) {
                    offsetArg = chat.getMessage("Please enter a offset");
                }
                updateQuery.setOffset(Long.getLong(offsetArg));
                chat.sendAnswer("Offset has been updated");

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

    private boolean isValidId(final String arg, final int maxValue) {
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

    private boolean isValidOffset(final String arg) {
        try {
            int intArg = Integer.parseInt(arg);
            return intArg >= 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}

