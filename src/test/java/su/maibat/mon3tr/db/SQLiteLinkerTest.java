package su.maibat.mon3tr.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;


public class SQLiteLinkerTest {
    @Test
    @DisplayName("Constructor test")
    void constructorTest() throws IOException {
        String coolName = "totally cool database name";

        new File(coolName).delete();
        new File(coolName + ".db").delete();
        new File(coolName + ".db.db").delete();
        new File("dir.db").delete();

        SQLiteLinker linker = new SQLiteLinker(coolName);

        try {
            assertTrue(!new File(coolName).exists());
            assertTrue(new File(coolName + ".db").exists());

            linker.close();
            linker = new SQLiteLinker(coolName + ".db");

            assertTrue(new File(coolName + ".db").exists());
            assertTrue(!new File(coolName + ".db.db").exists());

            new File("dir.db").mkdir();
            assertThrows(FileAlreadyExistsException.class,
                () -> new SQLiteLinker("dir"));
        } finally {
            linker.close();
        }
    }

    @Test
    @DisplayName("User interactions test")
    void userInteractionsTest() throws FileAlreadyExistsException {
        new File("user-test.db").delete();
        try (SQLiteLinker linker = new SQLiteLinker("user-test")) {
            assertThrows(UserNotFound.class, () -> linker.getUserById(100000));
            assertThrows(UserNotFound.class, () -> linker.getUserByChatId(200202020));

            UserQuery query = new UserQuery(-1, 1238388182L, null);
            assertDoesNotThrow(() -> linker.addUser(query));

            int id = 0;
            try {
                UserQuery output = linker.getUserByChatId(1238388182L);
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
    void deadlineInteractionsTest() throws FileAlreadyExistsException {
        new File("deadline-test.db").delete();
        try (SQLiteLinker linker = new SQLiteLinker("deadline-test")) {
            assertThrows(DeadlineNotFound.class, () -> linker.getDeadline(100000));
            assertThrows(DeadlineNotFound.class, () -> linker.getDeadlinesForUser(12345678));

            DeadlineQuery query = new DeadlineQuery(-1, "testname", 123, 123, 2020, null,
                false, 0);
            assertDoesNotThrow(() -> linker.addDeadline(query));

            int id = 0;
            try {
                DeadlineQuery[] output = linker.getDeadlinesForUser(2020);
                assertEquals(1, output.length);
                assertEquals(query.getName(), output[0].getName());
                assertEquals(query.getExpireTime(), output[0].getExpireTime());
                assertEquals(query.getRemindOffset(), output[0].getRemindOffset());
                assertEquals(query.getOwnerId(), output[0].getOwnerId());

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
                DeadlineQuery query2 = new DeadlineQuery(-1, "testname 2", 133, 23, 2020,
                    null, false, id);
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

    @Test
    @DisplayName("Group interactions test")
    void groupIneractionTest() throws Exception {
        new File("group-test.db").delete();
        try (SQLiteLinker linker = new SQLiteLinker("group-test")) {
            linker.addGroup(new GroupQuery("name", 1));

            // assertThrows(UserNotFound.class, () -> linker.getOwnedGroups(2));
            GroupQuery[] groupQueries = linker.getOwnedGroups(1);
            assertEquals(1, groupQueries.length);

            GroupQuery retakenGroup = linker.getGroups(new int[]{groupQueries[0].getId()})[0];
            assertEquals(groupQueries[0].getName(), retakenGroup.getName());
            assertEquals(groupQueries[0].getOwnerId(), retakenGroup.getOwnerId());
            assertEquals(groupQueries[0].getId(), retakenGroup.getId());

            retakenGroup.setName("new name");
            retakenGroup.setToken("kokrush");
            linker.updateGroup(retakenGroup);
            assertEquals(
                "new name",
                linker.getGroups(new int[]{retakenGroup.getId()})[0].getName()
            );

            assertThrows(TokenNotFound.class, () -> linker.tryFindToken("lol"));
            assertEquals(
                retakenGroup.getId(),
                linker.tryFindToken(retakenGroup.getToken()).getId()
            );

            int id = retakenGroup.getId();
            assertThrows(
                GroupNotFound.class,
                () -> linker.getGroups(new int[]{id, 2})
            );
            linker.removeGroup(id);
            assertThrows(
                GroupNotFound.class,
                () -> linker.getGroups(new int[]{id})
            );
        }
    }

    @Test
    @DisplayName("Group + Deadline test")
    void groupDeadlineTest() throws Exception {
        new File("gd-test.db").delete();
        try (SQLiteLinker linker = new SQLiteLinker("gd-test")) {
            linker.addGroup(new GroupQuery("group1", 1));
            linker.addGroup(new GroupQuery("group2", 1));
            int[] groupIds = new int[2];
            int i = 0;
            for (GroupQuery query : linker.getOwnedGroups(1)) {
                groupIds[i++] = query.getId();
            }

            linker.addDeadline(new DeadlineQuery(-1, "dead1", 1024, 1024, 1,
                null, false, 0));
            linker.addDeadline(new DeadlineQuery(-1, "dead2", 1024, 1024, 1,
                null, false, 0));
            int[] deadlineIds = new int[2];
            DeadlineQuery[] deadlines = linker.getDeadlinesForUser(1);
            i = 0;
            for (DeadlineQuery query : deadlines) {
                deadlineIds[i++] = query.getId();
            }
            deadlines[0].setAssignedGroups(groupIds);
            deadlines[1].setAssignedGroups(new int[]{groupIds[0]});
            linker.updateDeadline(deadlines[0]);
            linker.updateDeadline(deadlines[1]);



            String[] names = linker.getGroupNamesForDeadline(deadlineIds[0]);
            assertEquals("group1", names[0]);
            assertEquals("group2", names[1]);

            DeadlineQuery[] deadlineslinker = linker.getGroupsDeadlines(new int[]{groupIds[1]});
            assertEquals(1, deadlineslinker.length);
            assertEquals(deadlineIds[0], deadlineslinker[0].getId());

            assertEquals(3, linker.getGroupsDeadlines(groupIds).length);
        }

    }
}
