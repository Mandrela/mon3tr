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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public class DeadlineAddCommand implements Command {

    DataBaseLinker linker;

    DeadlineAddCommand(DataBaseLinker inputLinker) {
        this.linker = inputLinker;
    }

    private final boolean isDate(String argument){
        Pattern pattern = compile("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$");
        Matcher matcher = pattern.matcher(argument);
        return matcher.find();
    }

    public final String getName() {return "add";}

    public final String getHelp() {return "this command add your deadline";}

    public void execute(Chat chat){
        String[] arguments = chat.getAllMessages();

        if (arguments.length == 0) {
            chat.sendAnswer("Something went wrong, try again with input some arguments");

        } else {
            try {

                /*
                if (linker.getUserByChatId(chat.getChatId())).getId() == -1) { //try - catch
                    UserQuery userQuery = new UserQuery();
                    userQuery.setChatId(chat.getChatId());
                    linker.addUser(userQuery);
                }*/
                try {
                    linker.getUserByChatId(chat.getChatId());
                }
                catch (UserNotFound e) {
                    UserQuery userQuery = new UserQuery(-1, chat.getChatId());
                    linker.addUser(userQuery);
                }

                DeadlineQuery inputQuery = new DeadlineQuery();

                if (isDate(arguments[0])) {
                    inputQuery.setName(arguments[1]);
                    try {
                        Time burnTime = StringToTime(arguments[0]);
                        inputQuery.setBurnTime(burnTime);
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    inputQuery.setName(arguments[0]);
                    try {
                        Time burnTime = StringToTime(arguments[1]);
                        inputQuery.setBurnTime(burnTime);
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                inputQuery.setUserId(linker.getUserByChatId(chat.getChatId()).getId());
                //Заполнение запроса для добавления

                linker.addDeadline(inputQuery);
                chat.sendAnswer("Deadline added successfully");
            }
            catch (Exception e) {
                throw new RuntimeException("Arguments not found");
            }
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
