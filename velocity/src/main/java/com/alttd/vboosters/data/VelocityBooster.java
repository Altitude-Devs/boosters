package com.alttd.vboosters.data;

import com.alttd.boosterapi.booster.Booster;
import com.alttd.boosterapi.booster.BoosterState;
import com.alttd.boosterapi.booster.BoosterType;
import com.alttd.boosterapi.database.Queries;

import java.util.UUID;

public class VelocityBooster implements Booster {

    private final UUID uuid;
    private final String reason;
    private Long startingTime;
    private long duration;
    private final BoosterType boosterType;
    private BoosterState boosterState;
    private final Integer multiplier;

    public VelocityBooster(UUID uuid, BoosterType boosterType, BoosterState boosterState , String reason, long duration, int multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.boosterState = boosterState;
        this.reason = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.saveBooster();
    }

    @Override
    public boolean isActive() {
        return boosterState == BoosterState.ACTIVE;
    }

    @Override
    public BoosterType getType() {
        return boosterType;
    }

    @Override
    public BoosterState getState() {
        return boosterState;
    }

    @Override
    public void updateState(BoosterState state) {
        this.boosterState = state;
    }

    @Override
    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public Long getStartingTime() {
        return startingTime;
    }

    @Override
    public void setStartingTime(long startingTime) {
        this.startingTime = startingTime;
    }

    @Override
    public Long getDuration() {
        return duration;
    }

    @Override
    public String getTimeDuration() {
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        long day = (duration / (1000 * 60 * 60 * 24));
        return String.format("%02d:%02d:%02d:%02d", day, hour, minute, second);
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String getActivator() {
        return reason;
    }

    @Override
    public long getTimeRemaining() {
        if(boosterState == BoosterState.ACTIVE) {
            return startingTime + duration - System.currentTimeMillis();
        }
        return duration;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void saveBooster() {
        Queries.saveBooster(this);
    }

    @Override
    public String toString() {
        return "BoosterType: " + boosterType + ", reason: " + reason + ", duration: " + getTimeRemaining() + ", Level: " + multiplier + ", Startingtime " + startingTime + ", duration " + duration + ", end time: " + System.currentTimeMillis();
    }
}
