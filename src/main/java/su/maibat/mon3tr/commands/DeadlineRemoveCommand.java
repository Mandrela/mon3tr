package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.SQLiteLinker;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public class DeadlineRemoveCommand implements Command{

    SQLiteLinker linker;

    DeadlineRemoveCommand(SQLiteLinker inputLinker) {
        this.linker = inputLinker;
    }

    public final String getName() {return "remove";}

    public final String getHelp() {return "This command remove your deadline";}

    public final void execute(Chat chat) {

        String[] arguments = chat.getAllMessages();
        if (arguments.length == 0) {
            chat.sendAnswer("Something went wrong, try again with input some arguments");
        } else  if (arguments.length == 1){

            int id = Integer.parseInt(arguments[0]);

            try {
                if (linker.getUserById(linker.getDeadline(id).getUserId()).getChatId() !=
                        chat.getChatId()) {
                    chat.sendAnswer("You do not have this deadline " +
                            "(do not take on more than you need to)");
                    return;
                }
            } catch (DeadlineNotFound dnf) {
                chat.sendAnswer("Deadline not found");
                return;
            } catch (UserNotFound unf) {
                UserQuery userQuery = new UserQuery(-1, chat.getChatId());
                try {
                    linker.addUser(userQuery);
                } catch (MalformedQuery me) {}
                chat.sendAnswer("You have not any deadlines");
                return;
            }

            linker.removeDeadline(id);


            /*
            //Фрагмент для случая нахождения множества записей

            if (deleteQueryArray.length == 0) {
                chat.sendAnswer("No records with this name or date were found");
            } else {
                //Если у нас есть несколько записей на одну дату с одним именем мы счтаем их одной
                for (DeadlineQuery deleteQuery : deleteQueryArray) {
                    linker.removeDeadline(deleteQuery.getId());
                }
                chat.sendAnswer("You have closed this gestalt!!!");
            }*/

        } else {
            chat.sendAnswer("Please use 1 argument with this command");
        }
    }
}
