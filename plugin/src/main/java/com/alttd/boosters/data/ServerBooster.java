package com.alttd.boosters.data;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.util.ALogger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerBooster implements Booster {

    private UUID uuid;
    private String activator;
    private Long startingTime;
    private long duration;
    private BoosterType boosterType;
    private Double multiplier;
    private Boolean active;
    private Boolean finished;

    public ServerBooster(UUID uuid, BoosterType boosterType, String reason, long duration, double multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.activator = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = false;
        this.finished = false;
    }

    public ServerBooster(BoosterType type, String playerName, long duration, double multiplier) {
        this(UUID.randomUUID(), type, playerName, duration, multiplier);
    }

    public ServerBooster(UUID uuid, String activator, BoosterType boosterType, long startingTime, long duration, double multiplier, boolean active, boolean finished) {
        this.uuid = uuid;
        this.activator = activator;
        this.boosterType = boosterType;
        this.startingTime = startingTime;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = active;
        this.finished = finished;
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
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(double multiplier) {
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
    public Long getEndTime() {
        return startingTime = duration;
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
    }

    @Override
    public void saveBooster() {
        ALogger.error("Tried saving booster from server instead of proxy, only proxy should handle saving boosters");
    }

    @Override
    public void finish() {
        finished = true;
        stopBooster();
    }

    @Override
    public boolean finished() {
        return false;
    }


    @Override
    public int compareTo(@NotNull Object o) {
        Booster booster = (Booster) o;
        if (booster.getMultiplier() < getMultiplier())
            return -1;
        if (booster.getMultiplier() > getMultiplier())
            return 1;
        return booster.isActive() ? 1 : -1;
    }
}
