package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
                        Long burnTime = StringToTime(arguments[0]);
                        inputQuery.setBurnTime(burnTime);
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    inputQuery.setName(arguments[0]);
                    try {
                        Long burnTime = StringToTime(arguments[1]);
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

    boolean isDate(String argument){
        Pattern pattern = compile("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$");
        Matcher matcher = pattern.matcher(argument);
        return matcher.find();
    }

    private Long StringToTime(String dateString) throws DateTimeParseException {
        String normalStringDate = normalizeDate(dateString);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = LocalDate.parse(normalStringDate, formatter);
        LocalTime timeToAdd = LocalTime.MIDNIGHT;
        java.time.LocalDateTime localDateTime = date.atTime(timeToAdd);

        java.time.ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.of("UTC+5"));
        return zoneDateTime.toInstant().toEpochMilli()/1000;
    }

    private String normalizeDate (String dateArg) {

        int[] maxDayInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        String[] dateFragments = dateArg.split("[./]");

        if (dateFragments.length != 3) {
            throw new IllegalArgumentException("Неверно заданная дата");
        } else {

            int month = Integer.parseInt(dateFragments[1]);
            if (month < 1){
                month = 1;
                dateFragments[1] = "01";
            } else if (month > 12) {
                month = 12;
                dateFragments[1] = "12";
            }

            int day = Integer.parseInt(dateFragments[0]);
            if (day < 1) {
                dateFragments[0] = "01";
            } else if (day > maxDayInMonth[month-1]) {
                dateFragments[0] = Integer.toString(maxDayInMonth[month-1]);
            }

            String year = dateFragments[2];
            if (year.length() < 4){
                if (year.length() == 2) {
                    dateFragments[2] = "20".concat(year);
                } else if (year.length() == 3) {
                    dateFragments[2] = "2".concat(year);
                } else {
                    dateFragments[2] = "300".concat(year);
                }
            }

            return dateFragments[0] + '.' + dateFragments[1] + '.' + dateFragments[2];
        }
    }
}
