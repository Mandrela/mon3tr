package su.maibat.mon3tr.db;

public interface DataBaseLinker {
    // User
    /**
     * @param inputQuery I do assume, that id field is -1, else I will shoot!
     * @throws MalformedQuery when, for example, id != -1
     */
    void addUser(UserQuery inputQuery) throws MalformedQuery;
    void deactivateUser(int id);

    /**
     * @param inputQuery Query not only contains updated fields, but also and id by which
     * correct database row will be found
     * @throws MalformedQuery when, for example, id == -1
     */
    void updateUser(UserQuery inputQuery) throws MalformedQuery;

    UserQuery getUserById(int id);
    UserQuery getUserByChatId(long chatId);
    //UserQuery[] findUsersByQuery(UserQuery searchQuery);

    // Deadline
    /**
     * @param inputQuery I do assume, that id field is -1, else I will shoot!
     * @throws MalformedQuery when, for example, id != -1
     */
    void addDeadline(DeadlineQuery inputQuery) throws MalformedQuery;
    void removeDeadline(int id);

    /**
     * @param inputQuery Query not only contains updated fields, but also and id by which
     * correct database row will be found
     * @throws MalformedQuery when, for example, id == -1
     */
    void updateDeadline(DeadlineQuery inputQuery) throws MalformedQuery;

    DeadlineQuery getDeadline(int id);
    //DeadlineQuery[] find(String fieldName, String value);
}
