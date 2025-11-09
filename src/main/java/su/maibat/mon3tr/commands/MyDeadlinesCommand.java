package su.maibat.mon3tr.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public final class MyDeadlinesCommand implements Command {
    private static final int OFFSET = 1000;
    private final DataBaseLinker linker;

    public MyDeadlinesCommand(final DataBaseLinker inputLinker) {
        this.linker = inputLinker;
    }

    public String getName() {
        return "mydeadlines";
    }
    public String getHelp() {
        return "This command show list of your deadlines";
    }

    public void execute(final Chat chat) {

        try {
            UserQuery user = linker.getUserByChatId(chat.getChatId());
            try {
                DeadlineQuery[] queryList = linker.getDeadlinesForUser(user.getId());
                if (queryList.length == 0) {
                    chat.sendAnswer("You have not any deadlines");
                    return;
                }

                String answer = "";

                for (DeadlineQuery query : queryList) {
                    answer = answer.concat(query.getId() + " : " + query.getName() + " : "
                            + new SimpleDateFormat("dd/MM/yyyy").
                            format(new Date(query.getBurnTime().longValue() * OFFSET)) + "\n");
                } // TODO: ADEQUATUS TIME PARSING
                chat.sendAnswer(answer);
            } catch (DeadlineNotFound dnf) {
                chat.sendAnswer("You have not any deadlines");
            }

        } catch (UserNotFound unf) {
            UserQuery userQuery = new UserQuery(-1, chat.getChatId());
            try {
                linker.addUser(userQuery);
                chat.sendAnswer("You have not any deadlines");
            } catch (MalformedQuery me) {
                chat.sendAnswer("Something went wrong");
            }


        }
    }
}
