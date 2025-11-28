package su.maibat.mon3tr.db;

import java.io.Closeable;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static su.maibat.mon3tr.Main.INFO;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.LinkerException;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

// TODO Tests
public final class SQLiteLinker extends AbstractDataBaseLinker implements Closeable {
    private static final String URLPREFIX = "jdbc:sqlite:";

    private static final String USER_SELECT_BY_ID = "SELECT* FROM users WHERE id = ?";
    private static final String USER_SELECT_BY_CHAT_ID = "SELECT* FROM users WHERE chatID = ?";
    private static final String USER_SELECT_ALL = "SELECT* FROM users";
    private static final String USER_INSERT = "INSERT INTO users (chatId) VALUES (?)";
    private static final String USER_UPDATE = "UPDATE users SET chatId = ?, queryLimit = ?, "
        + "hpsfwn = ? WHERE id = ?";
    private static final String USER_UPDATE_ACTIVE = "UPDATE users SET active = 0 WHERE id = ?";


    private static final String DEADLINE_SELECT_BY_ID = "SELECT* FROM deadlines WHERE id = ? AND "
        + "active = 1";
    private static final String DEADLINE_SELECT_BY_USER_ID = "SELECT* FROM deadlines WHERE "
        + "userId = ? AND active = 1";
    private static final String DEADLINE_SELECT_ALL = "SELECT* FROM deadlines WHERE active = 1";
    private static final String DEADLINE_INSERT = "INSERT INTO deadlines (name, burns, "
        + "offsetValue, userId, notified) VALUES (?, ?, ?, ?, ?)";
    private static final String DEADLINE_UPDATE = "UPDATE deadlines SET name = ?, burns = ?, "
        + "offsetValue = ?, userId = ?, notified = ? WHERE id = ?";
    private static final String DEADLINE_UPDATE_ACTIVE = "UPDATE deadlines SET active = 0 "
        + "WHERE id = ?";


    private final String dbName;

    private final PreparedStatement user_get_by_id;
    private final PreparedStatement user_get_by_chat_id;
    private final PreparedStatement user_get_all;
    private final PreparedStatement user_add;
    private final PreparedStatement user_update;
    private final PreparedStatement user_deactivate;

    private final PreparedStatement deadline_get_by_id;
    private final PreparedStatement deadline_get_by_user_id;
    private final PreparedStatement deadline_get_all;
    private final PreparedStatement deadline_add;
    private final PreparedStatement deadline_update;
    private final PreparedStatement deadline_remove;

    private Connection conn = null;

    /**
     * @param databaseName Name (path in general) of database file to open.
     * If not exists - will be created
     * @throws FileAlreadyExistsException if databaseName equals to some directory name
     */
    public SQLiteLinker(final String databaseName)
            throws FileAlreadyExistsException, LinkerException {
        String postfix = "";
        if (!databaseName.endsWith(".db")) {
            postfix = ".db";
        }
        dbName = databaseName + postfix;

        File f = new File(dbName);
        if (f.isDirectory()) {
            throw new FileAlreadyExistsException("Database name collide with directory.");
        }

        if (!f.exists()) {
            System.out.println(INFO + "Database file wasn't found, will create...");
        }

        try {
            conn = DriverManager.getConnection(URLPREFIX + dbName);
            System.out.println(INFO + "Database connection established.");

            String create_deadlines = "CREATE TABLE IF NOT EXISTS deadlines "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, "
                + "burns INTEGER NOT NULL, offsetValue INTEGER DEFAULT 10000, "
                + "userId INTEGER NOT NULL, groupId INTEGER, active INTEGER DEFAULT 1, "
                + "notified INTEGER DEFAULT 0);";
            String create_users = "CREATE TABLE IF NOT EXISTS users "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, chatId REAL NOT NULL, "
                + "queryLimit INTEGER DEFAULT 32, hpsfwn INTEGER DEFAULT 0, "
                + "active INTEGER DEFAULT 1);";
            Statement statement = conn.createStatement();
            statement.execute(create_deadlines);
            statement.execute(create_users);

            user_get_by_id = conn.prepareStatement(USER_SELECT_BY_ID);
            user_get_by_chat_id = conn.prepareStatement(USER_SELECT_BY_CHAT_ID);
            user_get_all = conn.prepareStatement(USER_SELECT_ALL);
            user_add = conn.prepareStatement(USER_INSERT);
            user_update = conn.prepareStatement(USER_UPDATE);
            user_deactivate = conn.prepareStatement(USER_UPDATE_ACTIVE);

            deadline_get_by_id = conn.prepareStatement(DEADLINE_SELECT_BY_ID);
            deadline_get_by_user_id = conn.prepareStatement(DEADLINE_SELECT_BY_USER_ID);
            deadline_get_all = conn.prepareStatement(DEADLINE_SELECT_ALL);
            deadline_add = conn.prepareStatement(DEADLINE_INSERT);
            deadline_update = conn.prepareStatement(DEADLINE_UPDATE);
            deadline_remove = conn.prepareStatement(DEADLINE_UPDATE_ACTIVE);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("constr") + e.getMessage());
        }
    }

    private synchronized String collectInfo(final String methodName) {
        String connState = "null";
        try {
            if (conn != null) {
                if (conn.isClosed()) {
                    connState = "close";
                } else {
                    connState = "open";
                }
            }
        } catch (SQLException e) {
            throw new LinkerException("Couldn't collect conn info.");
        }
        return "Current Linker state:\nConn: " + connState + "\nName: " + dbName
            + "\nMethod name: " + methodName + "\n";
    }

    private void checkUserQuery(final UserQuery inputQuery, final boolean anyId)
            throws MalformedQuery {
        if (!anyId && inputQuery.getId() != -1) {
            throw new MalformedQuery("Id field of input UserQuery should be -1.");
        } else if (inputQuery.getChatId() == 0) {
            throw new MalformedQuery(
                "ChatId field of input UserQuery should not be default value.");
        }
    }

    private void checkDeadlineQuery(final DeadlineQuery inputQuery, final boolean anyId)
            throws MalformedQuery {
        String reason = "";

        if (!anyId && inputQuery.getId() != -1) {
            reason = "Id field of input DeadlineQuery should be -1.";
        } else if (inputQuery.getName().equals("")) {
            reason = "Name field of input DeadlineQuery should not be empty.";
        } else if (inputQuery.getBurnTime() <= 0) {
            reason = "Burn time field of input DeadlineQuery should be positive.";
        } else if (inputQuery.getUserId() <= 0) {
            reason = "User Id field of input DeadlineQuery should be positive.";
        }

        if (!reason.equals("")) {
            throw new MalformedQuery(reason);
        }
    }

    @Override
    public synchronized void close() throws LinkerException {
        try {
            synchronized (conn) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("close") + e.getMessage());
        }
    }

    // User
    @Override
    public void addUser(final UserQuery inputQuery)
            throws MalformedQuery, LinkerException {
        checkUserQuery(inputQuery, false);
        try {
            synchronized (user_add) {
                user_add.setLong(1, inputQuery.getChatId());
                synchronized (conn) {
                    user_add.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("addUser") + e.getMessage());
        }
    }

    @Override
    public void deactivateUser(final int id) throws LinkerException {
        try {
            synchronized (user_deactivate) {
                user_deactivate.setInt(1, id);
                synchronized (conn) {
                    user_deactivate.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("deactivateUser") + e.getMessage());
        }
    }

    @Override
    public void updateUser(final UserQuery inputQuery)
            throws MalformedQuery, LinkerException {
        try {
            checkUserQuery(inputQuery, true);
            synchronized (user_update) {
                user_update.setLong(1, inputQuery.getChatId());
                user_update.setInt(2, inputQuery.getLimit());
                user_update.setBoolean(3, inputQuery.isHasPaidSubscribeForWeatherNews());
                user_update.setInt(4, inputQuery.getId());
                synchronized (conn) {
                    user_update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("updateUser") + e.getMessage());
        }
    }


    /**
     * @param result Result set, assuming not toched
     * @return UserQuery parsed from result, @throws UserNotFound if result is empty
     */
    private UserQuery parseUserFromResult(final ResultSet result)
            throws UserNotFound, SQLException {
        if (!result.next()) {
            throw new UserNotFound(0); // Ment to be rethrowed
        }
        return new UserQuery(result.getInt("id"), result.getLong("chatId"));
    }


    @Override
    public UserQuery getUserById(final int id) throws UserNotFound, LinkerException {
        try {
            synchronized (user_get_by_id) {
                user_get_by_id.setInt(1, id);
                synchronized (conn) {
                return parseUserFromResult(user_get_by_id.executeQuery());
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getUserById") + "Id: " + id + "\n"
                + e.getMessage());
        } catch (UserNotFound e) {
            throw new UserNotFound(id);
        }
    }

    @Override
    public UserQuery getUserByChatId(final long chatId)
            throws UserNotFound, LinkerException {
        try {
            synchronized (user_get_by_chat_id) {
                user_get_by_chat_id.setLong(1, chatId);
                synchronized (conn) {
                    return parseUserFromResult(user_get_by_chat_id.executeQuery());
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getUserByChatId") + "ChatId: " + chatId + "\n"
                + e.getMessage());
        } catch (UserNotFound e) {
            throw new UserNotFound(chatId);
        }
    }

    public UserQuery[] getAllUsers() throws LinkerException {
        try {
            ResultSet result;
            synchronized (conn) {
                result = user_get_all.executeQuery();
            }

            java.util.ArrayList<UserQuery> userQuerys = new java.util.ArrayList<>();
            try {
                while (true) {
                    userQuerys.add(parseUserFromResult(result));
                }
            } catch (UserNotFound e) { }

            return userQuerys.toArray(UserQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getAllUsers") + e.getMessage());
        }
    }


    // Deadline
    @Override
    public void addDeadline(final DeadlineQuery inputQuery)
            throws MalformedQuery, LinkerException {
        try {
            checkDeadlineQuery(inputQuery, false);
            synchronized (deadline_add) {
                deadline_add.setString(1, inputQuery.getName());
                deadline_add.setLong(2, inputQuery.getBurnTime());
                deadline_add.setLong(3, inputQuery.getOffset());
                deadline_add.setInt(4, inputQuery.getUserId());
                deadline_add.setBoolean(5, inputQuery.isNotified());
                synchronized (conn) {
                    deadline_add.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("addDeadline") + e.getMessage());
        }
    }

    @Override
    public void removeDeadline(final int id) throws LinkerException {
        try {
            synchronized (deadline_remove) {
                deadline_remove.setInt(1, id);
                synchronized (conn) {
                    deadline_remove.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("removeDeadline") + e.getMessage());
        }
    }

    @Override
    public void updateDeadline(final DeadlineQuery inputQuery)
            throws MalformedQuery, LinkerException {
        try {
            checkDeadlineQuery(inputQuery, true);
            synchronized (deadline_update) {
                deadline_update.setString(1, inputQuery.getName());
                deadline_update.setLong(2, inputQuery.getBurnTime());
                deadline_update.setLong(3, inputQuery.getOffset());
                deadline_update.setInt(4, inputQuery.getUserId());
                deadline_update.setBoolean(5, inputQuery.isNotified());
                deadline_update.setInt(6, inputQuery.getId());
                synchronized (conn) {
                    deadline_update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("updateDeadline") + e.getMessage());
        }
    }

    private synchronized DeadlineQuery parseDeadlineFromResult(final ResultSet result)
            throws SQLException {
        return new DeadlineQuery(result.getInt("id"), result.getString("name"),
            result.getLong("burns"), result.getLong("offsetValue"), result.getInt("userId"),
            result.getBoolean("notified"));
    }

    @Override
    public synchronized DeadlineQuery getDeadline(final int id)
            throws DeadlineNotFound, LinkerException {
        try {
            ResultSet result;
            synchronized (deadline_get_by_id) {
                deadline_get_by_id.setInt(1, id);
                synchronized (conn) {
                    result = deadline_get_by_id.executeQuery();
                }
            }

            if (!result.next()) {
                throw new DeadlineNotFound(id);
            }
            return parseDeadlineFromResult(result);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getDeadline") + "Id: " + id + "\n"
                + e.getMessage());
        }
    }

    @Override
    public synchronized DeadlineQuery[] getDeadlinesForUser(final int userId)
            throws DeadlineNotFound, LinkerException {
        try {
            ResultSet result;
            synchronized (deadline_get_by_user_id) {
                deadline_get_by_user_id.setInt(1, userId);
                synchronized (conn) {
                    result = deadline_get_by_user_id.executeQuery();
                }
            }

            if (!result.next()) {
                throw new DeadlineNotFound(userId);
            }
            java.util.ArrayList<DeadlineQuery> deadlineQuerys = new java.util.ArrayList<>();
            do {
                deadlineQuerys.add(parseDeadlineFromResult(result));
            } while (result.next());

            return deadlineQuerys.toArray(DeadlineQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getDeadlines") + "User id: " + userId + "\n"
                + e.getMessage());
        }
    }

    public DeadlineQuery[] getAllDeadlines() throws LinkerException {
        try {
            ResultSet result;
            synchronized (conn) {
                result = deadline_get_all.executeQuery();
            }

            java.util.ArrayList<DeadlineQuery> deadlineQuerys = new java.util.ArrayList<>();
            while (result.next()) {
                deadlineQuerys.add(parseDeadlineFromResult(result));
            }

            return deadlineQuerys.toArray(DeadlineQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getAllDeadlines") + e.getMessage());
        }
    }

    public static void main(final String[] args) {
        System.out.println("Hello");
        try {
            SQLiteLinker db = new SQLiteLinker("kek");

            long start_e = System.currentTimeMillis();
            for (int i = 1; i <= 1000; i++) {
                db.getDeadlinesForUser(i);
            }
            long end_e = System.currentTimeMillis();

            double time_e = end_e - start_e;
            System.out.println(time_e / 1000 + " seconds to execute devastating thing, "
                + Math.round(time_e / 1000) + " ms on item");


            db.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
