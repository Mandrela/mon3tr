package su.maibat.mon3tr.commands;

import su.maibat.mon3tr.NumberedString;
import su.maibat.mon3tr.commands.exceptions.CommandException;
import su.maibat.mon3tr.db.DataBaseLinker;
import su.maibat.mon3tr.db.DeadlineQuery;
import su.maibat.mon3tr.db.UserQuery;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;
import su.maibat.mon3tr.notifier.Reactor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public final class DeadlineAddCommand implements Command {
    private final DataBaseLinker db;
    private final Reactor reactor;

    public DeadlineAddCommand(final DataBaseLinker inputLinker, final Reactor reactorArg) {
        this.db = inputLinker;
        this.reactor = reactorArg;
    }

    public String getName() {
        return "add";
    }

    public String getHelp() {
        return "this command add your deadline";
    }


    public State execute(final int userId, final String[] args, final State state,
                         final BlockingQueue<NumberedString> responseQueue)
            throws CommandException {
        try {
            State currentState;
            if (state == null) {
                currentState = new State(0, new String[]{}, (Command) this);
            } else {
                currentState = state;
            }
            if (db.getUserById(userId).getLimit() == 0) {
                NumberedString answer = new NumberedString(userId, "You have used up all your "
                        + "deadline cells, please close one or more deadlines before add a new"
                        + " one.");
                responseQueue.add(answer);
                return null;
            }

            switch (currentState.getStateId()) {
                case (0):
                    return nameCheck(userId, args, currentState, responseQueue);

                case (1):
                    if (args.length == 0) {
                        return dateCheck(userId, "", currentState, responseQueue);
                    }
                    return dateCheck(userId, args[0], currentState, responseQueue);

                case (2):
                    return addDeadline(userId, currentState, responseQueue);

                default:
                    System.out.println("Out state");
                    NumberedString answer = new NumberedString(userId, "Something went wrong");
                    responseQueue.add(answer);
                    return currentState;
            }

        } catch (UserNotFound unf) {
            System.out.println("UserNotFound");
            NumberedString answer = new NumberedString(userId, "Something went wrong");
            responseQueue.add(answer);
            return null;
        }
    }

    private State nameCheck(final int userId, final String[] args, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        if (args.length != 0 && isCorrectName(args[0])) {
            currentState.setMemory(new String[]{args[0], ""});
            if (args.length < 2) {
                return dateCheck(userId, "", currentState, responseQueue);
            }
            return dateCheck(userId, args[1], currentState, responseQueue);
        } else {
            NumberedString answer = new NumberedString(userId, "Please, enter a valid name");
            responseQueue.add(answer);
            currentState.setStateId(0);
            return currentState;
        }
    }

    private State dateCheck(final int userId, final String arg, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        if (isDate(arg)) {
            String[] outMem = currentState.getMemory();
            outMem[1] = arg;
            currentState.setMemory(outMem);
            return addDeadline(userId, currentState, responseQueue);
        } else {
            NumberedString answer = new NumberedString(userId, "Please, enter a valid date");
            responseQueue.add(answer);
            currentState.setStateId(1);
            return currentState;
        }

    }

    private State addDeadline(final int userId, final State currentState,
            final BlockingQueue<NumberedString> responseQueue) {
        try {
            DeadlineQuery inputQuery = new DeadlineQuery();
            String[] stateMemory = currentState.getMemory();

            long expireTime = stringToTime(stateMemory[1]);

            inputQuery.setName(stateMemory[0]);
            inputQuery.setExpireTime(expireTime);
            inputQuery.setOwnerId(userId);

            db.addDeadline(inputQuery);
            reactor.trigger(0);

            UserQuery user = db.getUserById(userId);
            user.setLimit(user.getLimit() - 1);

            NumberedString answer = new NumberedString(userId, "Deadline added successfully");
            responseQueue.add(answer);
            return null;
        } catch (MalformedQuery mq) {
            currentState.setStateId(2);
            return currentState;
        } catch (UserNotFound e) {
            throw new RuntimeException(e);
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
