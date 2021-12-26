package com.alttd.boosters.data;

import com.alttd.boosterapi.BoosterType;

import java.util.UUID;

public class Booster implements com.alttd.boosterapi.Booster {

    private UUID uuid;
    private String activator;
    private Long startingTime;
    private long duration;
    private BoosterType boosterType;
    private Integer multiplier;
    private Boolean active;
    private Boolean finished;

    public Booster(UUID uuid, BoosterType boosterType, String reason, long duration, int multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.activator = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = false;
        this.finished = false;
        saveBooster();
    }

    public Booster(BoosterType type, String playerName, long duration, int multiplier) {
        this(UUID.randomUUID(), type, playerName, duration, multiplier);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public BoosterType getType() {
        return boosterType;
    }

    @Override
    public void setType(BoosterType boosterType) {
        this.boosterType = boosterType;
    }

    @Override
    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
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
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String getActivator() {
        return activator;
    }

    @Override
    public void setActivator(String activationReason) {
        this.activator = activationReason;
    }

    @Override
    public long getTimeRemaining() {
        if(active) {
            return startingTime + duration - System.currentTimeMillis();
        }
        return duration;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void stopBooster() {

    }

    @Override
    public void saveBooster() {

    }

    @Override
    public void finish() {

    }

    @Override
    public boolean finished() {
        return false;
    }



}
