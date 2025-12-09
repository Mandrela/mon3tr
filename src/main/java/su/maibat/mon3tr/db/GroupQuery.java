package su.maibat.mon3tr.db;

public final class GroupQuery extends DBQuery {
    private static int salt = 0;
    private String name;
    private int ownerId;
    private String token;
    private static final int MAGIC_NUMBER_4 = 4;
    private static final int MAGIC_NUMBER_15 = 15;


    public GroupQuery() {
        super();
    }

    public GroupQuery(final String groupName, final int ownerUserId) {
        name = groupName;
        ownerId = ownerUserId;
    }

    public GroupQuery(final int id, final String groupName, final int ownerUserId,
            final String tokenArg) {
        super(id);
        name = groupName;
        ownerId = ownerUserId;
        token = tokenArg;
    }


    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        name = newName;
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final int newOwnerId) {
        ownerId = newOwnerId;
    }


    public String getToken() {
        return token;
    }

    public void setToken(final String newToken) {
        token = newToken;
    }

    public String generateToken() {
        return String.valueOf((name.hashCode() << MAGIC_NUMBER_15)
                + (ownerId << MAGIC_NUMBER_4) + salt++);
    }
}
