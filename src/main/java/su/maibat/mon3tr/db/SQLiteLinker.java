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

     // Deadline
    @Override
    public void addDeadline(final DeadlineQuery inputQuery) {

    }

    @Override
    public void removeDeadline(final int id) {

    }

    @Override
    public void updateQuery(final DeadlineQuery inputQuery) {

    }

    @Override
    public DeadlineQuery getDeadline(final int id) {
        return new DeadlineQuery();
    }

    @Override
    public DeadlineQuery[] find(final String fieldName, final String value) {
        return new DeadlineQuery[1];
    }

    // User
    @Override
    public void addUser(final UserQuery inputQuery) {

    }

    @Override
    public void removeUser(final int id) {

    }

    @Override
    public void updateUser(final UserQuery inputQuery) {

    }

    @Override
    public UserQuery getUser(final int id) {
        return new UserQuery();
    }

    @Override
    public UserQuery[] findUser(final UserQuery searchQuery) {
        return new UserQuery[1];
    }
}
