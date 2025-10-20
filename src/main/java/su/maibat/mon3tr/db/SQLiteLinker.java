package su.maibat.mon3tr.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class SQLiteLinker extends AbstractDataBaseLinker {
    private static final String URLPREFIX = "jdbc:sqlite:";
    private final String dbName;

    public SQLiteLinker(final String databaseName) {
        dbName = databaseName;

        try (Connection conn = DriverManager.getConnection(URLPREFIX + dbName)) {
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // User
    @Override
    public void addUser(final UserQuery inputQuery) throws MalformedQuery {

    }

    @Override
    public void deactivateUser(final int id) {

    }

    @Override
    public void updateUser(final UserQuery inputQuery) throws MalformedQuery {

    }

    @Override
    public UserQuery getUserById(final int id) {
        return new UserQuery(-1);
    }

    @Override
    public UserQuery getUserByChatId(final long chatId) {
        return new UserQuery(-1);
    }

    // Deadline
    @Override
    public void addDeadline(final DeadlineQuery inputQuery) throws MalformedQuery {

    }

    @Override
    public void removeDeadline(final int id) {

    }

    @Override
    public void updateDeadline(final DeadlineQuery inputQuery) throws MalformedQuery {

    }

    @Override
    public DeadlineQuery getDeadline(final int id) {
        return new DeadlineQuery(-1);
    }
}
