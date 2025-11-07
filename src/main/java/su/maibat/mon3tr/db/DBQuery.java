package su.maibat.mon3tr.db;


public abstract class DBQuery {
    private final int id;

    public DBQuery() {
        id = -1;
    }

    public DBQuery(final int idArg) {
        id = idArg;
    }

    public int getId() {
        return id;
    }
}
