package su.maibat.mon3tr.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public class MyDeadlinesCommand implements Command {
    private static final int OFFSET = 1000;
    private final DataBaseLinker db;

    public MyDeadlinesCommand(final DataBaseLinker inputLinker) {
        this.db = inputLinker;
    }

    /**
     * @return Command name
     */
    public String getName() {
        return "mydeadlines";
    }

    /**
     * @return Help info about command
     */
    public String getHelp() {
        return "This command show list of your deadlines";
    }


    public State execute(int userId, String[] args, State currentState,
                        BlockingQueue<NumberedString> responseQueue) throws CommandException {


        try {
            DeadlineQuery[] queryList = db.getDeadlinesForUser(userId);
            if (queryList.length == 0) {
                NumberedString answer = new NumberedString(userId, "You have not any deadlines");
                responseQueue.add(answer);
                return null;
            }
            NumberedString answer = new NumberedString(userId, printTable(queryList));
            responseQueue.add(answer);
            return null;
        } catch (DeadlineNotFound dnf) {
            NumberedString answer = new NumberedString(userId, "You have not any deadlines");
            responseQueue.add(answer);
            return null;
        }

    }

    protected final String printTable(final DeadlineQuery[] queryList) {
        String answer = "";
        String answerFragment = "";
        for (int i = 0; i < queryList.length; i++) {
            answerFragment = answerFragment.concat((i + 1) + " : " + queryList[i].getName() + " : "
                    + new SimpleDateFormat("dd/MM/yyyy").
                    format(new Date(queryList[i].getExpireTime() * OFFSET)));
            if (queryList[i].isBurning()) {
                answerFragment = answerFragment + "\uD83D\uDD25";
            }
            if (queryList[i].isDead()) {
                answerFragment = answerFragment + "\uD83D\uDC80";
            }
            answer = answer.concat(answerFragment + "\n");
            answerFragment = "";

        }
        return answer;
    }
}
