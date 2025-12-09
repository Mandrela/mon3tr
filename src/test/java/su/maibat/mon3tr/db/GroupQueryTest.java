package su.maibat.mon3tr.db;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class GroupQueryTest {
    @Test
    void tokenGenerationTest() {
        GroupQuery group = new GroupQuery("name1", 2);
        assertTrue(group.generateToken() != group.generateToken());
    }
}
