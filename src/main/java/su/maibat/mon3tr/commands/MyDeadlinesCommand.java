package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;

;

public class MyDeadlinesCommand implements Command {

    DataBaseLinker linker;
    MyDeadlinesCommand(DataBaseLinker linker) {
        this.linker = linker;
    }

    public final String getName() {return "mydeadlines";}
    public final String getHelp() {return "This command show list of your deadlines";}

    public void execute(Chat chat) {


        UserQuery user = new UserQuery(-1, chat.getChatId());
        DeadlineQuery findQuery = new DeadlineQuery();
        findQuery.setUserId(user.getId());
        DeadlineQuery[] queryList = linker.findDeadline(findQuery);

        String answer = "";

        for (DeadlineQuery query : queryList){
            answer = answer.concat(query.getId() + " : " + query.getName() + " : " +
                    query.getBurnTime() + "\n");
        }
        chat.sendAnswer(answer);
    }
}
