package su.maibat.mon3tr.db;

import su.maibat.mon3tr.DateUtils;

public final class DeadlineQuery extends DBQuery {
    // State enumerator: 0 - normal, 1 - burning, 2 - completed, 3 - dead, -1 - deleted

    private String name = "";
    private long expireTime = 0;
    private long remindOffset = DateUtils.SEC_IN_DAYS;

    private int ownerId = 0;
    private int[] assignedGroups;

    private boolean notified = false;
    private int state = 0;

    // For future
    private int notifCounter = 0;


    public DeadlineQuery() {
        super();
    }

    /**
     * @param idArg
     * @param deadlineName Display name of the deadline
     * @param burnAtTime Time at wich deadline will be burned
     * @param triggerOffset Relative to burnAtTime. Deadlines between burnAtTime and offset are
     * considered burning
     * @param ownerUserId Id of user to notify
     * @param isNotifiedAbout Flag set if this deadline was notified about
     * @param state
     */
    public DeadlineQuery(final int idArg, final String deadlineName, final long burnAtTime,
            final long triggerOffset, final int ownerUserId, final boolean isNotifiedAbout,
            final int stateArg) {
        super(idArg);
        name = deadlineName;
        expireTime = burnAtTime;
        remindOffset = triggerOffset;
        ownerId = ownerUserId;
        notified = isNotifiedAbout;
        state = stateArg;
    }

    public String getName() {
        return name;
    }
    public void setName(final String arg) {
        name = arg;
    }

    public long getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(final long arg) {
        expireTime = arg;
        notified = false;
    }

    public int getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(final int arg) {
        ownerId = arg;
    }

    public long getRemindOffset() {
        return remindOffset;
    }
    public void setRemindOffset(final long arg) {
        remindOffset = arg;
        notified = false;
    }


    public boolean isBurning() {
        return state == 1;
    }

    public boolean isCompleted() {
        return state == 2;
    }

    public boolean isDead() {
        return state == 3;
    }


    public boolean isNotified() {
        return notified;
    }

    public void setNotified() {
        notified = true;
    }


    public int[] getAssignedGroups() {
        return assignedGroups;
    }

    public void setAssignedGroups(final int[] arg) {
        assignedGroups = arg;
    }


    public int getNotifCounter() {
        return notifCounter;
    }

    public void setNotifCounter(final int arg) {
        notifCounter = arg;
    }
}
