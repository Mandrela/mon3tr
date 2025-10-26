package su.maibat.mon3tr.db;

import lombok.Getter;

@Getter // Will generate getId() method automatically
public abstract class DBQuery {
    private final int id;

    public DBQuery() {
        id = -1;
    }

    public DBQuery(final int idArg) {
        id = idArg;
    }
}
