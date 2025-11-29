package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.chat.Chat;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public final class DeadlineAddCommand implements Command {

    private final DataBaseLinker db;

    public DeadlineAddCommand(final DataBaseLinker inputLinker) {
        this.db = inputLinker;
    }

    public String getName() {
        return "add";
    }

    public String getHelp() {
        return "this command add your deadline";
    }

    public void execute(final Chat chat) {
        try {
            try {
                db.getUserByChatId(chat.getChatId());
            } catch (UserNotFound e) {
                UserQuery userQuery = new UserQuery(-1, chat.getChatId());
                db.addUser(userQuery);
            }
            UserQuery user = db.getUserByChatId(chat.getChatId());
            DeadlineQuery inputQuery = new DeadlineQuery();

            inputQuery.setOwnerId(user.getId());

            String[] arguments = chat.getAllMessages();

            String name;
            String stringBurnTime;

            if (arguments.length >= 2) {

                if (isDate(arguments[1])) {
                    stringBurnTime = arguments[1];
                    if (isCorrectName(arguments[0])) {
                        name = arguments[0];
                    } else {
                        name = getArgument(chat, "name");
                    }

                } else if (isDate(arguments[0])) {
                    stringBurnTime = arguments[0];
                    if (isCorrectName(arguments[1])) {
                        name = arguments[1];
                    } else {
                        name = getArgument(chat, "name");
                    }

                } else {
                    if (isCorrectName(arguments[0])) {
                        name = arguments[0];
                    } else if (isCorrectName(arguments[1])) {
                        name = arguments[1];
                    } else {
                        name = getArgument(chat, "name");
                    }
                    stringBurnTime = getArgument(chat, "date");
                }

            } else if (arguments.length == 0) {

                name = getArgument(chat, "name");
                stringBurnTime = getArgument(chat, "date");

            } else {
                if (isDate(arguments[0])) {
                    name = getArgument(chat, "name");
                    stringBurnTime = arguments[0];
                } else if (arguments[0].isEmpty()) {
                    name = getArgument(chat, "name");
                    stringBurnTime = getArgument(chat, "date");
                } else {
                    name = arguments[0];
                    stringBurnTime = getArgument(chat, "date");
                }
            }

            long burnTime = stringToTime(stringBurnTime);

            inputQuery.setName(name);
            inputQuery.setExpireTime(burnTime);

            if (user.getLimit() == 0) {
                chat.sendAnswer("You have used up all your deadline cells, "
                        + "please close one or more deadlines before add a new one.");
            } else {
                user.setLimit(user.getLimit() - 1);
                db.addDeadline(inputQuery);
                chat.sendAnswer("Deadline added successfully");
            }

        } catch (UserNotFound e) {

        } catch (InterruptedException ie) {

        } catch (MalformedQuery e) {
            throw new RuntimeException(e);
        }

    }

    private String getArgument(final Chat chat, final String flag) throws InterruptedException {
        String answer = "";
        if (flag.equals("name")) {
            while (!isCorrectName(answer)) {
                answer = chat.getMessage("Please enter valid name for your deadline "
                    + "(not empty, not date)");
            }
            return answer;
        } else if (flag.equals("date")) {
            while (!isDate(answer)) {
                answer = chat.getMessage("Please enter correct date");
            }
            return answer;
        } else {
            return "";
        }
    }

    private boolean isDate(final String argument) {
        final Pattern pattern = compile("^\\d{1,2}[./]\\d{1,2}[./]\\d{1,4}$");
        Matcher matcher = pattern.matcher(argument);
        return matcher.find();
    }

    private boolean isCorrectName(final String name) {
        return !(isDate(name) || name.isEmpty());
    }

    private Long stringToTime(final String dateString) throws DateTimeParseException,
            IllegalArgumentException {
        String normalStringDate;
        try {
            normalStringDate = normalizeDate(dateString);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = LocalDate.parse(normalStringDate, formatter);
        LocalTime timeToAdd = LocalTime.MIDNIGHT;
        java.time.LocalDateTime localDateTime = date.atTime(timeToAdd);

        java.time.ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.of("UTC+5"));
        return zoneDateTime.toInstant().toEpochMilli() / 1000;
    }

    private String normalizeDate(final String dateArg) {

        int[] maxDayInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        String[] stringMaxDayInMonth = {"31", "28", "31", "30", "31", "30", "31",
                "31", "30", "31", "30", "31"};

        String[] dateFragments = dateArg.split("[./]");

        if (dateFragments.length != 3) {
            throw new IllegalArgumentException("Incorrect input date");
        } else {

            int month = Integer.parseInt(dateFragments[1]);
            if (month < 1) {
                month = 1;
                dateFragments[1] = "1";
            } else if (month > 12) {
                month = 12;
                dateFragments[1] = "12";
            }
            if (month <= 9 && dateFragments[1].length() == 1) {
                dateFragments[1] = "0" + dateFragments[1];
            }

            int day = Integer.parseInt(dateFragments[0]);
            if (day < 1) {
                dateFragments[0] = "01";
            } else if (day > maxDayInMonth[month - 1]) {
                dateFragments[0] = stringMaxDayInMonth[month - 1];
            }
            if (day <= 9 && dateFragments[1].length() == 1) {
                dateFragments[0] = "0" + dateFragments[0];
            }

            String year = dateFragments[2];
            if (year.length() < 4) {
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
