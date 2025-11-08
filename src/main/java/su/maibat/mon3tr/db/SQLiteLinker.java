package su.maibat.mon3tr.db;

import java.io.Closeable;
import java.io.File;
import java.math.BigDecimal;
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


public final class SQLiteLinker extends AbstractDataBaseLinker implements Closeable {
    private static final String URLPREFIX = "jdbc:sqlite:";

    private static final String USER_SELECT_BY_ID = "SELECT* FROM users WHERE id = ?";
    private static final String USER_SELECT_BY_CHAT_ID = "SELECT* FROM users WHERE chatID = ?";
    private static final String USER_INSERT = "INSERT INTO users (chatId) VALUES (?)";
    private static final String USER_UPDATE = "UPDATE users SET chatId = ?, queryLimit = ?, "
        + "hpsfwn = ? WHERE id = ?";
    private static final String USER_UPDATE_ACTIVE = "UPDATE users SET active = 0 WHERE id = ?";


    private static final String DEADLINE_SELECT_BY_ID = "SELECT* FROM deadlines WHERE id = ?";
    private static final String DEADLINE_SELECT_BY_USER_ID = "SELECT* FROM deadlines WHERE "
        + "userId = ? AND active = 1";
    private static final String DEADLINE_INSERT = "INSERT INTO deadlines (name, burns, "
        + "offsetValue, userId) VALUES (?, ?, ?, ?)";
    private static final String DEADLINE_UPDATE = "UPDATE deadlines SET name = ?, burns = ?, "
        + "offsetValue = ?, userId = ? WHERE id = ?";
    private static final String DEADLINE_UPDATE_ACTIVE = "UPDATE deadlines SET active = 0 "
        + "WHERE id = ?";


    private final String dbName;

    private final PreparedStatement user_get_by_id;
    private final PreparedStatement user_get_by_chat_id;
    private final PreparedStatement user_add;
    private final PreparedStatement user_update;
    private final PreparedStatement user_deactivate;

    private final PreparedStatement deadline_get_by_id;
    private final PreparedStatement deadline_get_by_user_id;
    private final PreparedStatement deadline_add;
    private final PreparedStatement deadline_update;
    private final PreparedStatement deadline_remove;

    private Connection conn = null;

    /**
     * @param databaseName Name (path in general) of database file to open.
     * If not exists - will be created
     * @throws FileAlreadyExistsException if databaseName equals to some directory name
     */
    public SQLiteLinker(final String databaseName) throws FileAlreadyExistsException {
        String postfix = "";
        if (!databaseName.endsWith(".db")) {
            postfix = ".db";
        }
        dbName = databaseName + postfix;

        File f = new File(dbName);
        if (f.isDirectory()) {
            throw new FileAlreadyExistsException("Database name collide with directory");
        }

        if (!f.exists()) {
            System.out.println(INFO + " Database file wasn't found, will create...");
        }

        try {
            conn = DriverManager.getConnection(URLPREFIX + dbName);
            System.out.println(INFO + " Database connection established.");

            String create_deadlines = "CREATE TABLE IF NOT EXISTS deadlines "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, "
                + "burns INTEGER NOT NULL, offsetValue INTEGER DEFAULT 10000, "
                + "userId INTEGER NOT NULL, groupId INTEGER, active INTEGER DEFAULT 1);";
            String create_users = "CREATE TABLE IF NOT EXISTS users "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, chatId REAL NOT NULL, "
                + "queryLimit INTEGER DEFAULT 32, hpsfwn INTEGER DEFAULT 0, "
                + "active INTEGER DEFAULT 1);";
            Statement statement = conn.createStatement();
            statement.execute(create_deadlines);
            statement.execute(create_users);

            user_get_by_id = conn.prepareStatement(USER_SELECT_BY_ID);
            user_get_by_chat_id = conn.prepareStatement(USER_SELECT_BY_CHAT_ID);
            user_add = conn.prepareStatement(USER_INSERT);
            user_update = conn.prepareStatement(USER_UPDATE);
            user_deactivate = conn.prepareStatement(USER_UPDATE_ACTIVE);

            deadline_get_by_id = conn.prepareStatement(DEADLINE_SELECT_BY_ID);
            deadline_get_by_user_id = conn.prepareStatement(DEADLINE_SELECT_BY_USER_ID);
            deadline_add = conn.prepareStatement(DEADLINE_INSERT);
            deadline_update = conn.prepareStatement(DEADLINE_UPDATE);
            deadline_remove = conn.prepareStatement(DEADLINE_UPDATE_ACTIVE);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String collectInfo(final String methodName) {
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
            throw new LinkerException("Couldn't collect conn info");
        }
        return "Current Linker state:\nConn: " + connState + "\nName: " + dbName
            + "\nMethod name: " + methodName + "\n";
    }

    private void checkUserQuery(final UserQuery inputQuery) throws MalformedQuery {
        if (inputQuery.getId() != -1) {
            throw new MalformedQuery("Id field of input UserQuery should be -1");
        } else if (inputQuery.getChatId() == 0) {
            throw new MalformedQuery(
                "ChatId field of input UserQuery should not be default value");
        }
    }

    private void checkDeadlineQuery(final DeadlineQuery inputQuery) throws MalformedQuery {
        String reason = "";

        if (inputQuery.getId() != -1) {
            reason = "Id field of input DeadlineQuery should be -1";
        } else if (inputQuery.getName().equals("")) {
            reason = "Name field of input DeadlineQuery should not be empty";
        } else if (inputQuery.getBurnTime().compareTo(new BigDecimal(0)) <= 0) {
            reason = "Burn time field of input DeadlineQuery should be positive";
        } else if (inputQuery.getUserId() <= 0) {
            reason = "User Id field of input DeadlineQuery should be positive";
        }

        if (!reason.equals("")) {
            throw new MalformedQuery(reason);
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("close") + e.getMessage());
        }
    }

    // User
    @Override
    public void addUser(final UserQuery inputQuery) throws MalformedQuery, LinkerException {
        checkUserQuery(inputQuery);
        try {
            user_add.setLong(1, inputQuery.getChatId());
            user_add.executeUpdate();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("addUser") + e.getMessage());
        }
    }

    @Override
    public void deactivateUser(final int id) throws LinkerException {
        try {
            user_deactivate.setInt(1, id);
            user_deactivate.executeUpdate();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("deactivateUser") + e.getMessage());
        }
    }

    @Override
    public void updateUser(final UserQuery inputQuery) throws MalformedQuery, LinkerException {
        try {
            //checkUserQuery(inputQuery);
            user_update.setLong(1, inputQuery.getChatId());
            user_update.setInt(2, inputQuery.getLimit());
            user_update.setBoolean(3, inputQuery.isHasPaidSubscribeForWeatherNews());
            user_update.setInt(4, inputQuery.getId());
            user_update.executeUpdate();
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
            user_get_by_id.setInt(1, id);
            return parseUserFromResult(user_get_by_id.executeQuery());
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getUserById") + "Id: " + id + "\n"
                + e.getMessage());
        } catch (UserNotFound e) {
            throw new UserNotFound(id);
        }
    }

    @Override
    public UserQuery getUserByChatId(final long chatId) throws UserNotFound, LinkerException {
        try {
            user_get_by_chat_id.setLong(1, chatId);
            return parseUserFromResult(user_get_by_chat_id.executeQuery());
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getUserByChatId") + "ChatId: " + chatId + "\n"
                + e.getMessage());
        } catch (UserNotFound e) {
            throw new UserNotFound(chatId);
        }
    }

    // Deadline
    @Override
    public void addDeadline(final DeadlineQuery inputQuery) throws MalformedQuery, LinkerException {
        try {
            checkDeadlineQuery(inputQuery);
            deadline_add.setString(1, inputQuery.getName());
            deadline_add.setBigDecimal(2, inputQuery.getBurnTime());
            deadline_add.setBigDecimal(3, inputQuery.getOffset());
            deadline_add.setInt(4, inputQuery.getUserId());
            deadline_add.executeUpdate();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("addDeadline") + e.getMessage());
        }
    }

    @Override
    public void removeDeadline(final int id) throws LinkerException {
        try {
            deadline_remove.setInt(1, id);
            deadline_remove.executeUpdate();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("removeDeadline") + e.getMessage());
        }
    }

    @Override
    public void updateDeadline(final DeadlineQuery inputQuery)
            throws MalformedQuery, LinkerException {
        try {
            //checkDeadlineQuery(inputQuery);
            deadline_update.setString(1, inputQuery.getName());
            deadline_update.setBigDecimal(2, inputQuery.getBurnTime());
            deadline_update.setBigDecimal(3, inputQuery.getOffset());
            deadline_update.setInt(4, inputQuery.getUserId());
            deadline_update.setInt(5, inputQuery.getId());
            deadline_update.executeUpdate();
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("updateDeadline") + e.getMessage());
        }
    }

    private DeadlineQuery parseDeadlineFromResult(final ResultSet result) throws SQLException {
        return new DeadlineQuery(result.getInt("id"), result.getString("name"),
            result.getBigDecimal("burns"), result.getBigDecimal("offsetValue"),
            result.getInt("userId"));
    }

    @Override
    public DeadlineQuery getDeadline(final int id) throws DeadlineNotFound, LinkerException {
        try {
            deadline_get_by_id.setInt(1, id);
            ResultSet result = deadline_get_by_id.executeQuery();
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
    public DeadlineQuery[] getDeadlinesForUser(final int userId)
            throws DeadlineNotFound, LinkerException {
        try {
            deadline_get_by_user_id.setInt(1, userId);
            ResultSet result = deadline_get_by_user_id.executeQuery();
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
}
