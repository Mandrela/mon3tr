package su.maibat.mon3tr.db;

import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
import su.maibat.mon3tr.db.exceptions.GroupNotFound;
import su.maibat.mon3tr.db.exceptions.LinkerException;
import su.maibat.mon3tr.db.exceptions.MalformedQuery;
import su.maibat.mon3tr.db.exceptions.TokenNotFound;
import su.maibat.mon3tr.db.exceptions.UserNotFound;

public interface DataBaseLinker {
    // User
    /**
     * @param inputQuery I do assume, that id field is -1, else I will shoot!
     * @throws MalformedQuery when, for example, id != -1
     */
    void addUser(UserQuery inputQuery) throws MalformedQuery, LinkerException;
    void deactivateUser(int id) throws LinkerException;

    /**
     * @param inputQuery Query not only contains updated fields, but also and id by which
     * correct database row will be found
     * @throws MalformedQuery when, for example, id == -1
     */
    void updateUser(UserQuery inputQuery) throws MalformedQuery, LinkerException;

    boolean checkUserExists(int id) throws LinkerException;
    int getUserIdByChatId(long chatId) throws UserNotFound, LinkerException;
    long getChatIdByUserId(int id) throws UserNotFound, LinkerException;
    UserQuery getUserById(int id) throws UserNotFound, LinkerException;
    UserQuery getUserByChatId(long chatId) throws UserNotFound, LinkerException;
    UserQuery[] getUsersForGroups(int[] id) throws LinkerException;
    UserQuery[] getAllUsers() throws LinkerException;


    // Deadlines
    /**
     * @param inputQuery I do assume, that id field is -1, else I will shoot!
     * @throws MalformedQuery when, for example, id != -1
     */
    void addDeadline(DeadlineQuery inputQuery) throws MalformedQuery, LinkerException;
    void removeDeadline(int id) throws LinkerException;

    /**
     * @param inputQuery Query not only contains updated fields, but also and id by which
     * correct database row will be found
     * @throws MalformedQuery when, for example, id == -1
     */
    void updateDeadline(DeadlineQuery inputQuery) throws MalformedQuery, LinkerException;

    DeadlineQuery getDeadline(int id) throws DeadlineNotFound, LinkerException;
    DeadlineQuery[] getDeadlinesForUser(int userId) throws DeadlineNotFound, LinkerException;
    DeadlineQuery[] getGroupsDeadlines(int[] groupsId) throws GroupNotFound, LinkerException;
    DeadlineQuery[] getBurningDeadlines() throws LinkerException;
    DeadlineQuery[] getAllDeadlines() throws LinkerException;


    // User Settings
    boolean toggleNews(int userId) throws UserNotFound, LinkerException;
    boolean toggleLeaderboard(int userId) throws UserNotFound, LinkerException;


    // Groups
    void addGroup(GroupQuery inputQuery) throws MalformedQuery, LinkerException;
    void updateGroup(GroupQuery inputQuery) throws MalformedQuery, LinkerException;
    void removeGroup(int id) throws LinkerException;

    GroupQuery[] getGroups(int[] groupsId) throws GroupNotFound, LinkerException;
    GroupQuery[] getOwnedGroups(int userId) throws UserNotFound, LinkerException;
    String[] getGroupNamesForDeadline(int deadlineId) throws DeadlineNotFound, LinkerException;

    GroupQuery tryFindToken(String token) throws TokenNotFound, LinkerException;
}
