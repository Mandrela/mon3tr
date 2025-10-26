package su.maibat.mon3tr.db;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static su.maibat.mon3tr.Main.INFO;
import static su.maibat.mon3tr.Main.WARNING;
import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public final class SQLiteLinker extends AbstractDataBaseLinker {
    private static final String URLPREFIX = "jdbc:sqlite:";
    private final String dbName;

    private Connection conn = null;

    // TODO tests
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
            throw new FileAlreadyExistsException("Desired Database name collide with directory");
        }

        if (!f.exists()) {
            System.out.println(WARNING + " Database file wasn't found, will create...");
        }

        try {
            conn = DriverManager.getConnection(URLPREFIX + dbName);
            System.out.println(INFO + " Database connection established.");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
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
    public UserQuery getUserById(final int id) throws UserNotFound {
        return new UserQuery();
    }

    @Override
    public UserQuery getUserByChatId(final long chatId) throws UserNotFound {
        return new UserQuery();
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
    public DeadlineQuery getDeadline(final int id) throws DeadlineNotFound {
        return new DeadlineQuery();
    }

    @Override
    public DeadlineQuery[] getDeadlinesForUser(final int userId) throws DeadlineNotFound {
        return new DeadlineQuery[1];
    }
}
