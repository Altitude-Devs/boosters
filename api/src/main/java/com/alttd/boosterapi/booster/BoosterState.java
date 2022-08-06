package com.alttd.boosterapi.booster;

public enum BoosterState {
    /**
     * Implies the booster is created and waiting to be run
     */
    CREATED("created"),
    /**
     * Booster is currently active
     */
    ACTIVE("active"), // Booster is running
    /**
     * Booster is paused because a higher level of the same type is active
     */
    PAUSED("paused"), // Booster is paused because a higher level is running
    /**
     * Booster is has finished running.
     */
    FINISHED("finished"),
    /**
     * Something went wrong :shrug:
     */
    ERROR("error"); // Booster is finished

    private final String name;

    BoosterState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BoosterState getByName(String text) {
        for (BoosterState state : BoosterState.values()) {
            if (state.getName().equalsIgnoreCase(text)) {
                return state;
            }
        }
        return ERROR;
    }

}
