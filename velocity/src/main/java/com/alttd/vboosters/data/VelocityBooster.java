package com.alttd.vboosters.data;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;

import java.util.UUID;

public class VelocityBooster implements Booster {

    private UUID uuid;
    private String activator;
    private Long startingTime;
    private long duration;
    private BoosterType boosterType;
    private Integer multiplier;
    private Boolean active;
    private Boolean finished;

    public VelocityBooster(UUID uuid, BoosterType boosterType, String reason, long duration, int multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.activator = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = false;
        this.finished = false;
        saveBooster();
    }

    public VelocityBooster(BoosterType type, String playerName, long duration, int multiplier) {
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
        setDuration(getTimeRemaining());
        setActive(false);
        saveBooster();
    }

    @Override
    public void saveBooster() {
        // logic to save to yaml or to db
    }

    public void finish() {
        finished = true;
        stopBooster();
    }

    @Override
    public boolean finished() {
        return finished;
    }

}
