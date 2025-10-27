package su.maibat.mon3tr.db;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


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

        assertTrue(!new File(coolName).exists());
        assertTrue(new File(coolName + ".db").exists());

        linker = new SQLiteLinker(coolName + ".db");
        assertTrue(new File(coolName + ".db").exists());
        assertTrue(!new File(coolName + ".db.db").exists());

        new File("dir.db").mkdir();
        try {
            throw assertThrows(FileAlreadyExistsException.class, () -> new SQLiteLinker("dir"));
        } catch (FileAlreadyExistsException e) { } // Avoiding vs code linting
    }


}