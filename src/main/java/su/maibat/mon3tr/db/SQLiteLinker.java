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
import java.util.Arrays;

import static su.maibat.mon3tr.Main.INFO;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.LinkerException;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

// TODO Tests
public final class SQLiteLinker extends AbstractDataBaseLinker implements Closeable {
    private static final String URLPREFIX = "jdbc:sqlite:";

    private static final String USER_SELECT_BY_ID = "SELECT* FROM users WHERE id = ? AND "
        + "active = 1";
    private static final String USER_SELECT_BY_CHAT_ID = "SELECT* FROM users WHERE chatId = ?";
    private static final String USER_SELECT_ALL = "SELECT* FROM users";
    private static final String USER_INSERT = "INSERT INTO users (chatId) VALUES (?)";
    private static final String USER_UPDATE = "UPDATE users SET chatId = ?, dlimit = ?, "
        + "hpsfwn = ?, strat = ?, news = ?, leader = ?, burned = ?, completed = ?, "
        + "name = ?, groups = ? WHERE id = ?";
    private static final String USER_UPDATE_ACTIVE = "UPDATE users SET active = 0 WHERE id = ?";


    private static final String DEADLINE_SELECT_BY_ID = "SELECT* FROM deadlines WHERE id = ? AND "
        + "state != -1";
    private static final String DEADLINE_SELECT_BY_USER_ID = "SELECT* FROM deadlines WHERE "
        + "userId = ? AND state != -1";
    private static final String DEADLINE_SELECT_FOR_GROUP = "SELECT* FROM deadlines WHERE "
        + "instr(groups, \':?:\') > 0 AND state != -1";
    private static final String DEADLINE_SELECT_ALL = "SELECT* FROM deadlines WHERE state != -1";
    private static final String DEADLINE_INSERT = "INSERT INTO deadlines (name, burns, "
        + "offsetValue, userId) VALUES (?, ?, ?, ?)";
    private static final String DEADLINE_UPDATE = "UPDATE deadlines SET name = ?, burns = ?, "
        + "offsetValue = ?, userId = ?, groups = ?, notified = ?, state = ?, notifCounter = ? "
        + " WHERE id = ?";
    private static final String DEADLINE_UPDATE_ACTIVE = "UPDATE deadlines SET state = -1 "
        + "WHERE id = ?";


    private static final String GROUP_SELECT_BY_ID = "SELECT* FROM groups WHERE id = ? AND "
        + "ocflag != -1";
    private static final String GROUP_SELECT_BY_OWNER_ID = "SELECT* FROM groups WHERE owner = ?"
        + "AND ocflag != -1";
    private static final String GROUP_SELECT_BY_TOKEN = "SELECT* FROM groups WHERE token = ? AND "
        + "ocflag != -1";
    private static final String GROUP_INSERT = "INSERT INTO groups (name, owner) VALUES "
        + "(?, ?)";
    private static final String GROUP_UPDATE = "UPDATE groups SET name = ?, owner = ?, token = ? "
        + "WHERE id = ?";
    private static final String GROUP_UPDATE_ACTIVE = "UPDATE groups SET ocflag = -1 WHERE "
        + "id = ?";


    private final String dbName;

    private final PreparedStatement user_get_by_id;
    private final PreparedStatement user_get_by_chat_id;
    private final PreparedStatement user_get_all;
    private final PreparedStatement user_add;
    private final PreparedStatement user_update;
    private final PreparedStatement user_deactivate;

    private final PreparedStatement deadline_get_by_id;
    private final PreparedStatement deadline_get_by_user_id;
    private final PreparedStatement deadline_get_by_group_id;
    private final PreparedStatement deadline_get_all;
    private final PreparedStatement deadline_add;
    private final PreparedStatement deadline_update;
    private final PreparedStatement deadline_remove;

    private final PreparedStatement group_get_by_id;
    private final PreparedStatement group_get_by_owner_id;
    private final PreparedStatement group_get_by_token;
    private final PreparedStatement group_add;
    private final PreparedStatement group_update;
    private final PreparedStatement group_remove;

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
                + "burns INTEGER NOT NULL, offsetValue INTEGER NOT NULL, "
                + "userId INTEGER NOT NULL, groups TEXT, notified INTEGER DEFAULT 0, "
                + "state INTEGER DEFAULT 0, event INTEGER DEFAULT 0, notifCounter INTEGER "
                + " DEFAULT 0);";
            String create_users = "CREATE TABLE IF NOT EXISTS users "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, chatId INTEGER NOT NULL, "
                + "dlimit INTEGER DEFAULT 32, hpsfwn INTEGER DEFAULT 0, "
                + "strat INTEGER DEFAULT 0, news INTEGER DEFAULT 1, leader INTEGER DEFAULT 0, "
                + "burned INTEGER DEFAULT 0, completed INTEGER DEFAULT 0, name TEXT, groups "
                + "TEXT, active INTEGER DEFAULT 1);";
            String create_groups = "CREATE TABLE IF NOT EXISTS groups "
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, owner INTEGER, "
                + "token TEXT, ocflag INTEGER DEFAULT 0);";
            Statement statement = conn.createStatement();
            statement.execute(create_deadlines);
            statement.execute(create_users);
            statement.execute(create_groups);

            user_get_by_id = conn.prepareStatement(USER_SELECT_BY_ID);
            user_get_by_chat_id = conn.prepareStatement(USER_SELECT_BY_CHAT_ID);
            user_get_all = conn.prepareStatement(USER_SELECT_ALL);
            user_add = conn.prepareStatement(USER_INSERT);
            user_update = conn.prepareStatement(USER_UPDATE);
            user_deactivate = conn.prepareStatement(USER_UPDATE_ACTIVE);

            deadline_get_by_id = conn.prepareStatement(DEADLINE_SELECT_BY_ID);
            deadline_get_by_user_id = conn.prepareStatement(DEADLINE_SELECT_BY_USER_ID);
            deadline_get_by_group_id = conn.prepareStatement(DEADLINE_SELECT_FOR_GROUP);
            deadline_get_all = conn.prepareStatement(DEADLINE_SELECT_ALL);
            deadline_add = conn.prepareStatement(DEADLINE_INSERT);
            deadline_update = conn.prepareStatement(DEADLINE_UPDATE);
            deadline_remove = conn.prepareStatement(DEADLINE_UPDATE_ACTIVE);

            group_get_by_id = conn.prepareStatement(GROUP_SELECT_BY_ID);
            group_get_by_owner_id = conn.prepareStatement(GROUP_SELECT_BY_OWNER_ID);
            group_get_by_token = conn.prepareStatement(GROUP_SELECT_BY_TOKEN);
            group_add = conn.prepareStatement(GROUP_INSERT);
            group_update = conn.prepareStatement(GROUP_UPDATE);
            group_remove = conn.prepareStatement(GROUP_UPDATE_ACTIVE);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("constr") + e.getMessage());
        }
    }


    public static String arrayToString(final int[] array) {
        if (array == null) {
            return "";
        }
        String string = ":";
        for (int i : array) {
            string = string + String.valueOf(i) + ":";
        }
        return string;
    }

    public static int[] stringToArray(final String string) {
        if (string == null) {
            return new int[0];
        }
        int[] array = new int[string.length()];
        int i = 0;
        for (String number : string.split(":")) {
            array[i++] = Integer.valueOf(number);
        }
        return Arrays.copyOfRange(array, 0, i);
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
        } else if (inputQuery.getExpireTime() <= 0) {
            reason = "Burn time field of input DeadlineQuery should be positive.";
        } else if (inputQuery.getOwnerId() <= 0) {
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
                user_update.setInt(4, 0);
                user_update.setInt(5, 1);
                user_update.setInt(6, 0);
                user_update.setInt(7, 0);
                user_update.setInt(8, 0);
                user_update.setString(9, "kekname");
                user_update.setString(10, arrayToString(inputQuery.getMembership()));
                user_update.setInt(11, inputQuery.getId());
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
        return new UserQuery(result.getInt("id"), result.getLong("chatId"), stringToArray(
            result.getString("groups")));
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

    public long getChatIdByUserId(final int id) throws UserNotFound, LinkerException {
        return getUserById(id).getChatId();
    }

    public int getUserIdByChatId(final long chatId) throws UserNotFound, LinkerException {
        return getUserByChatId(chatId).getId();
    }

    public boolean checkUserExists(final int id) throws LinkerException {
        try {
            getUserById(id);
            return true;
        } catch (UserNotFound e) {
            return false;
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
                deadline_add.setLong(2, inputQuery.getExpireTime());
                deadline_add.setLong(3, inputQuery.getRemindOffset());
                deadline_add.setInt(4, inputQuery.getOwnerId());
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
                deadline_update.setLong(2, inputQuery.getExpireTime());
                deadline_update.setLong(3, inputQuery.getRemindOffset());
                deadline_update.setInt(4, inputQuery.getOwnerId());
                deadline_update.setString(5, arrayToString(inputQuery.getAssignedGroups()));
                deadline_update.setBoolean(6, inputQuery.isNotified());
                deadline_update.setInt(7, inputQuery.getState());
                deadline_update.setInt(8, 0);
                deadline_update.setInt(9, inputQuery.getId());
                synchronized (conn) {
                    deadline_update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("updateDeadline") + e.getMessage());
        }
    }

    private DeadlineQuery parseDeadlineFromResult(final ResultSet result)
            throws SQLException {
        return new DeadlineQuery(result.getInt("id"), result.getString("name"),
            result.getLong("burns"), result.getLong("offsetValue"), result.getInt("userId"),
            stringToArray(result.getString("groups")), result.getBoolean("notified"),
            result.getInt("state"));
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
            throw new LinkerException(collectInfo("getDeadlinesForUser") + "User id: " + userId
                + ": " + e.getMessage());
        }
    }

    @Override
    public DeadlineQuery[] getGroupsDeadlines(final int[] groupsId)
            throws GroupNotFound, LinkerException {
        try {
            java.util.ArrayList<DeadlineQuery> deadlineQuerys = new java.util.ArrayList<>();
            for (int groupId : groupsId) {
                ResultSet result;
                synchronized (deadline_get_by_group_id) {
                    deadline_get_by_group_id.setInt(1, groupId);
                    synchronized (conn) {
                        result = deadline_get_by_group_id.executeQuery();
                    }
                }

                while (result.next()) {
                    deadlineQuerys.add(parseDeadlineFromResult(result));
                }
            }

            return deadlineQuerys.toArray(DeadlineQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getGroupDeadlines") + ": " + e.getMessage());
        }
    }

    @Override
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


    // Group
    public void addGroup(final GroupQuery inputQuery) throws LinkerException {
        try {
            synchronized (group_add) {
                group_add.setString(1, inputQuery.getName());
                group_add.setInt(2, inputQuery.getOwnerId());
                synchronized (conn) {
                    group_add.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("addGroup") + e.getMessage());
        }
    }

    public void updateGroup(final GroupQuery inputQuery) throws MalformedQuery, LinkerException {
        try {
            synchronized (group_update) {
                group_update.setString(1, inputQuery.getName());
                group_update.setInt(2, inputQuery.getOwnerId());
                group_update.setString(3, inputQuery.getToken());
                group_update.setInt(4, inputQuery.getId());
                synchronized (conn) {
                    group_update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("updateGroup") + e.getMessage());
        }
    }

    public void removeGroup(final int id) throws LinkerException {
        try {
            synchronized (group_remove) {
                group_remove.setInt(1, id);
                synchronized (conn) {
                    group_remove.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("removeGroup") + e.getMessage());
        }
    }

    private GroupQuery parseGroupFromResult(final ResultSet result)
            throws SQLException {
        return new GroupQuery(result.getInt("id"), result.getString("name"),
            result.getInt("owner"), result.getString("token"));
    }

    public GroupQuery[] getGroups(final int[] groupsId) throws GroupNotFound, LinkerException {
        try {
            java.util.ArrayList<GroupQuery> groupQuerys = new java.util.ArrayList<>();
            for (int groupId : groupsId) {
                ResultSet result;
                synchronized (group_get_by_id) {
                    group_get_by_id.setInt(1, groupId);
                    synchronized (conn) {
                        result = group_get_by_id.executeQuery();
                    }
                }

                while (result.next()) {
                    groupQuerys.add(parseGroupFromResult(result));
                }
            }

            return groupQuerys.toArray(GroupQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getGroups") + e.getMessage());
        }
    }

    public GroupQuery[] getOwnedGroups(final int userId) throws UserNotFound, LinkerException {
        try {
            ResultSet result;
            synchronized (group_get_by_owner_id) {
                group_get_by_owner_id.setInt(1, userId);
                synchronized (conn) {
                    result = group_get_by_owner_id.executeQuery();
                }
            }
            java.util.ArrayList<GroupQuery> groupQuerys = new java.util.ArrayList<>();
            while (result.next()) {
                groupQuerys.add(parseGroupFromResult(result));
            }

            return groupQuerys.toArray(GroupQuery[]::new);
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("getOwnedGroups") + e.getMessage());
        }
    }

    public String[] getGroupNamesForDeadline(final int deadlineId)
            throws DeadlineNotFound, LinkerException {
        try {
            GroupQuery[] a = getGroups(getDeadline(deadlineId).getAssignedGroups());
            String[] answer = new String[a.length];

            for (int i = 0; i < answer.length; ++i) {
                answer[i] = a[i].getName();
            }

            return answer;
        } catch (Exception e) {
            throw new LinkerException(collectInfo("getGroupNamesForDeadline") + e.getMessage());
        }
    }

    public GroupQuery tryFindToken(final String token) throws TokenNotFound, LinkerException {
        try {
            ResultSet result;
            synchronized (group_get_by_token) {
                    group_get_by_token.setString(1, token);
                synchronized (conn) {
                    result = group_get_by_token.executeQuery();
                }
            }

            if (!result.next()) {
                throw new TokenNotFound(token);
            }

            GroupQuery answer = parseGroupFromResult(result);

            if (result.next()) { // ambigious token
                throw new TokenNotFound(token);
            }
            return answer;
        } catch (SQLException e) {
            throw new LinkerException(collectInfo("tryFindToken") + e.getMessage());
        }
    }

    // User Settings
    public boolean toggleNews(final int userId) throws UserNotFound, LinkerException {
        return true;
    }

    public boolean toggleLeaderboard(final int userId) throws UserNotFound, LinkerException {
        return true;
    }
}
