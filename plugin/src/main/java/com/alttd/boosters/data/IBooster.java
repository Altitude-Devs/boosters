package com.alttd.boosters.data;

import com.alttd.boosterapi.booster.BoosterType;
import com.alttd.boosterapi.config.BoosterConfig;

import java.util.UUID;

public class Booster implements com.alttd.boosterapi.booster.Booster {

    private UUID uuid;
    private String reason;
    private Long startingTime;
    private long duration;
    private BoosterType boosterType;
    private Integer multiplier;
    private Boolean active;
    private Boolean finished;

    public Booster(BoosterConfig boosterConfig) {
        this.uuid = boosterConfig.getUuid();
        this.boosterType = boosterConfig.getBoosterType();
        this.reason = boosterConfig.getReason();
        this.duration = boosterConfig.getDuration();
        this.multiplier = boosterConfig.getLevel();
        this.active = false;
        this.finished = boosterConfig.isFinished();
    }

    public Booster(UUID uuid, BoosterType boosterType, String reason, long duration, int multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.reason = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = false;
        this.finished = false;
    }

    @Override
    public boolean isActive() {
        return active;
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
    public String getTimeDuration() {
        long second = (getTimeRemaining() / 1000) % 60;
        long minute = (getTimeRemaining() / (1000 * 60)) % 60;
        long hour = (getTimeRemaining() / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hour, minute, second);
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
    public void setActivator(String activationReason) {
        this.reason = activationReason;
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
    public String toString() {
        return "BoosterType: " + boosterType + ", reason: " + reason + ", duration: " + getTimeRemaining() + ", Level: " + multiplier + ", Startingtime " + startingTime + ", duration " + duration + ", end time: " + System.currentTimeMillis();
    }

}
