package su.maibat.mon3tr.db;

public interface DataBaseLinker {
    /*protected void addQuery(String tableName, DBQuery inputQuery);
    protected void removeByID(String tableName, int id);
    protected void updateQuery(String tableName, DBQuery inputQuery);
    protected DBQuery get(String tableName, int id);
    protected DBQuery[] find(String tableName, String fieldName, String value);*/

    // Deadline
    void addDeadline(DeadlineQuery inputQuery);
    void removeDeadline(int id);
    void updateDeadline(DeadlineQuery inputQuery);
    DeadlineQuery getDeadline(int id);
    //DeadlineQuery[] find(String fieldName, String value);

    // User
    void addUser(UserQuery inputQuery);
    void deactivateUser(int id);
    void updateUser(UserQuery inputQuery);
    UserQuery getUserById(int id);
    UserQuery getUserByChatId(long chatId);
    //UserQuery[] findUsersByQuery(UserQuery searchQuery);
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
