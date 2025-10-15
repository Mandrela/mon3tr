package su.maibat.mon3tr.db;

public interface DataBaseLinker {
    void addQuery(String tableName, DBQuery inputQuery);
    void removeByID(String tableName, int id);
    DBQuery get(String tableName, int id);
    void updateQuery(String tableName, DBQuery inputQuery);
    DBQuery find(String tableName, String fieldName, String value);
}

/**
find("Deadline", "UserID", find("User", "ChatId", "123")[0].id);

class DBQuery {
    int ID = -1;
    String Name;
    int Remind offset;
}


Deadlines:
ID  | Burn Date | Name  | Remind offset | UserID    | GroupID



User:
ID  | ChatId    | Deadline Query limit  | HPSFWN
                  32


Groups:
ID  | List of UserIDs
*/
