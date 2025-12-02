package su.maibat.mon3tr.commands;

public final class State {
    private int id;
    private String[] memory;
    private final Command owner;

    public State(final int stateId, final String[] memoryArray, final Command stateOwner) {
        id = stateId;
        memory = memoryArray;
        owner = stateOwner;
    }

    public State(final State state) {
        id = state.getStateId();
        memory = state.getMemory();
        owner = state.getOwner();
    }


    public int getStateId() {
        return id;
    }

    public void setStateId(final int stateId) {
        id = stateId;
    }


    public String[] getMemory() {
        return memory;
    }

    public void setMemory(String[] newMemory) {
        memory = newMemory;
    }


    public Command getOwner() {
        return owner;
    }
}
