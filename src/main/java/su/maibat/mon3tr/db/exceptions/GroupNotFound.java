package su.maibat.mon3tr.db.exceptions;

public class GroupNotFound extends Exception {
    private final int groupId;

    public GroupNotFound(final int groupIdArg) {
        groupId = groupIdArg;
    }

    public final int getGroupId() {
        return groupId;
    }
}
