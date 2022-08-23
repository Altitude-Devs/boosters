package com.alttd.boosterapi;

import java.util.UUID;

public interface Booster extends Comparable {

    boolean isActive();

    void setActive(Boolean active);

    BoosterType getType();

    void setType(BoosterType boosterType);

    double getMultiplier();

    void setMultiplier(double multiplier);

    Long getStartingTime();

    void setStartingTime(long startingTime);

    Long getEndTime();

    Long getDuration();

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
