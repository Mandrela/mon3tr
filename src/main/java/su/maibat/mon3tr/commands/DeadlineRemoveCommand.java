package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DeadlineRemoveCommand implements Command{

    DataBaseLinker linker;

    DeadlineRemoveCommand(DataBaseLinker inputLinker) {
        this.linker = inputLinker;
    }

    public final String getName() {return "remove";}

    public final String getHelp() {return "This command remove your deadline";}

    public final void execute(Chat chat) {
        String[] arguments = chat.getAllMessages();
        if (arguments.length == 0) {
            chat.sendAnswer("Something went wrong, try again with input some arguments");
        } else  if (arguments.length == 2){
            //Первый случай: переданы имя и дата (в таком порядке)

            DeadlineQuery findQuery = new DeadlineQuery();


            if (isDate(arguments[0])) {
                findQuery.setName(arguments[1]);
                try {
                    Time burnTime = StringToTime(arguments[0]);
                    findQuery.setBurnTime(burnTime);
                } catch (DateTimeParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                findQuery.setName(arguments[0]);
                try {
                    Time burnTime = StringToTime(arguments[1]);
                    findQuery.setBurnTime(burnTime);
                } catch (DateTimeParseException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                findQuery.setUserId(linker.getUserByChatId(chat.getChatId()).getId());
            } catch (UserNotFound e) {
                UserQuery userQuery = new UserQuery(-1, chat.getChatId());
                linker.addUser(userQuery);
                chat.sendAnswer("You have not any deadlines");
                return;
            }


            DeadlineQuery[] deleteQueryArray = linker.getDeadline(DeadlineQuery findQuery);
            //TODO настроить поиск

            if (deleteQueryArray.length == 0) {
                chat.sendAnswer("No records with this name or date were found");
            } else {
                //Если у нас есть несколько записей на одну дату с одним именем мы счтаем их одной
                for (DeadlineQuery deleteQuery : deleteQueryArray) {
                    linker.removeDeadline(deleteQuery.getId());
                }
                chat.sendAnswer("You have closed this gestalt!!!");
            }
        } else {
            //Второй случай: только 1 аргумент
            chat.sendAnswer("Please use 2 arguments with this command");
        }
    }

    private Time StringToTime(String dateString) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = LocalDate.parse(dateString, formatter);
        LocalTime timeToAdd = LocalTime.MIDNIGHT;
        java.time.LocalDateTime localDateTime = date.atTime(timeToAdd);

        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(localDateTime);

        Time sqlTime = new Time(timestamp.getTime());
        return sqlTime;
    }
}
