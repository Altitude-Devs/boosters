package com.alttd.boosterapi;

import java.util.UUID;

public interface Booster {

    boolean isActive();

    void setActive(Boolean active);

    BoosterType getType();

    void setType(BoosterType boosterType);

    int getMultiplier();

    void setMultiplier(int multiplier);

    Long getStartingTime();

    void setStartingTime(long startingTime);

    Long getDuration();

    String getTimeDuration();

    void setDuration(long duration);

    String getActivator();

    void setActivator(String activationReason);

    long getTimeRemaining();

    UUID getUUID();

    void stopBooster();

    void saveBooster();

    void finish();

    boolean finished();
}
