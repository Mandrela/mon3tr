package su.maibat.mon3tr.db;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public class SQLiteLinkerTest {
    @Test
    @DisplayName("Constructor test")
    void ConstructorTest() throws IOException {
        String coolName = "totally cool database name";

        new File(coolName).delete();
        new File(coolName + ".db").delete();
        new File(coolName + ".db.db").delete();
        new File("dir.db").delete();

        @SuppressWarnings("unused")
        SQLiteLinker linker = new SQLiteLinker(coolName);

        try {
            assertTrue(!new File(coolName).exists());
            assertTrue(new File(coolName + ".db").exists());
            
            linker.close();
            linker = new SQLiteLinker(coolName + ".db");

            assertTrue(new File(coolName + ".db").exists());
            assertTrue(!new File(coolName + ".db.db").exists());

            new File("dir.db").mkdir();
            assertThrows(FileAlreadyExistsException.class, () -> new SQLiteLinker("dir"));
        } finally {
            linker.close();
        }
    }

    @Test
    @DisplayName("User interactions test")
    void UserInteractionsTest() throws FileAlreadyExistsException {
        try (SQLiteLinker linker = new SQLiteLinker("user-test")) {
            assertThrows(UserNotFound.class, () -> linker.getUserById(100000));
            assertThrows(UserNotFound.class, () -> linker.getUserByChatId(200202020));

            UserQuery query = new UserQuery(-1, 123L);
            assertDoesNotThrow(() -> linker.addUser(query));

            int id = 0;
            try {
                UserQuery output = linker.getUserByChatId(123L);
                assertEquals(query.getChatId(), output.getChatId());
                id = output.getId();
            } catch (UserNotFound e) {
                assertTrue(false, "Should not throw");
            }

            try {
                UserQuery updateQuery = linker.getUserById(id);
                updateQuery.setChatId(1235L);
                assertDoesNotThrow(() -> linker.updateUser(updateQuery));

                UserQuery output = linker.getUserById(id);

                assertEquals(1235L, output.getChatId());
            } catch (UserNotFound e) {
                assertTrue(false, "Should find user");
            }
        }
    }

    @Test
    @DisplayName("Deadline interactions test")
    void DeadlineInteractionsTest() throws FileAlreadyExistsException {
        new File("deadline-test.db").delete();
        try (SQLiteLinker linker = new SQLiteLinker("deadline-test")) {
            assertThrows(DeadlineNotFound.class, () -> linker.getDeadline(100000));
            assertThrows(DeadlineNotFound.class, () -> linker.getDeadlinesForUser(12345678));

            DeadlineQuery query = new DeadlineQuery(-1, "testname", new BigDecimal(123), new BigDecimal(123), 2020);
            assertDoesNotThrow(() -> linker.addDeadline(query));

            int id = 0;
            try {
                DeadlineQuery[] output = linker.getDeadlinesForUser(2020);
                assertEquals(1, output.length);
                assertEquals(query.getName(), output[0].getName());
                assertEquals(query.getBurnTime(), output[0].getBurnTime());
                assertEquals(query.getOffset(), output[0].getOffset());
                assertEquals(query.getUserId(), output[0].getUserId());

                id = output[0].getId();
            } catch (DeadlineNotFound e) {
                assertTrue(false, "Should not throw");
            }

            try {
                DeadlineQuery updateQuery = linker.getDeadline(id);
                updateQuery.setName("another testname");
                assertDoesNotThrow(() -> linker.updateDeadline(updateQuery));

                DeadlineQuery output = linker.getDeadline(id);

                assertEquals("another testname", output.getName());
            } catch (DeadlineNotFound e) {
                assertTrue(false, "Should find deadline");
            }

            try {
                DeadlineQuery query2 = new DeadlineQuery(-1, "testname 2", new BigDecimal(133), new BigDecimal(23), 2020);
                assertDoesNotThrow(() -> linker.addDeadline(query2));
                
                DeadlineQuery[] output = linker.getDeadlinesForUser(2020);
                assertEquals(2, output.length, "Should have two deadlines by now");

                DeadlineQuery firstDeadline = output[0];
                assertDoesNotThrow(() -> linker.removeDeadline(firstDeadline.getId()));
                output = linker.getDeadlinesForUser(2020);
                assertEquals(1, output.length, "Should have one now");
            } catch (DeadlineNotFound e) {
                assertTrue(false, "Should find deadlines");
            }
        }
    }
}