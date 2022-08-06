package com.alttd.boosterapi.booster;

import java.util.UUID;

public interface Booster {

    boolean isActive();

    BoosterType getType();

    BoosterState getState();

    void updateState(BoosterState state);

    int getMultiplier();

    Long getStartingTime();

    void setStartingTime(long startingTime);

    Long getDuration();

    String getTimeDuration();

    void setDuration(long duration);

    String getActivator();

    long getTimeRemaining();

    UUID getUUID();

    void saveBooster();

}
