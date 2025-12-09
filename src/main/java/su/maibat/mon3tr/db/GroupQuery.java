package su.maibat.mon3tr.db;

public final class GroupQuery extends DBQuery {
    private String name;
    private int ownerId;
    private String token;


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
        return "hui";
    }
}
