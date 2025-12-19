package su.maibat.mon3tr.db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class GroupQuery extends DBQuery {
    private static final String TOKEN_PREFIX = "t0k3n_";
    private static final int TOKEN_LENGTH = 16;
    private static final int MASK = 0xff;
    private static int salt = 0;
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
        try {
            token = TOKEN_PREFIX + bytesToHex(
                MessageDigest.getInstance("SHA-256").digest(
                    (name + (salt++ << 1)).getBytes()
                )
            ).substring(0, TOKEN_LENGTH);
            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String bytesToHex(final byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(MASK & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
