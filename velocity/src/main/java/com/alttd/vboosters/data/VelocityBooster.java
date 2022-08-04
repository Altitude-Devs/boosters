package com.alttd.vboosters.data;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.vboosters.storage.VelocityBoosterStorage;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.UUID;

public class VelocityBooster implements Booster {

    private UUID uuid;
    private String activator;
    private Long startingTime;
    private long duration;
    private BoosterType boosterType;
    private Double multiplier;
    private Boolean active;
    private Boolean finished;

    public VelocityBooster(UUID uuid, BoosterType boosterType, String reason, long duration, double multiplier) {
        this.uuid = uuid;
        this.boosterType = boosterType;
        this.activator = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.active = false;
        this.finished = false;
        this.startingTime = new Date().getTime();
        saveBooster();
    }

    public VelocityBooster(BoosterType type, String playerName, long duration, double multiplier) {
        this(UUID.randomUUID(), type, playerName, duration, multiplier);
    }

    public VelocityBooster(UUID uuid, String activator, BoosterType boosterType, long startingTime,
                           long duration, double multiplier, boolean active, boolean finished) {
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
    public void stopBooster() { //TODO stop it on the servers as well
        setDuration(getTimeRemaining());
        setActive(false);
        saveBooster();
        if (!finished) {
            //TODO send plugin message that its stopped
        }
    }

    @Override
    public void saveBooster() {
        VelocityBoosterStorage vbs = VelocityBoosterStorage.getVelocityBoosterStorage();
        vbs.getBoosters().put(uuid, this);
        vbs.saveBoosters(vbs.getBoosters().values());
    }

    public void finish() { //TODO finish it on the servers as well
        finished = true;
        stopBooster();
        //TODO send plugin message that this is finished
    }

    @Override
    public boolean finished() {
        return finished;
    }

}
