package com.alttd.vboosters.data;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.storage.VelocityBoosterStorage;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Long getEndTime() {
        return startingTime + duration;
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
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("finish");
            out.writeUTF(uuid.toString());
            VelocityBoosters.getPlugin().getProxy().getAllServers()
                    .forEach(registeredServer -> registeredServer.sendPluginMessage(VelocityBoosters.getPlugin().getChannelIdentifier(), out.toByteArray()));
        }
    }

    @Override
    public void saveBooster() {
        VelocityBoosterStorage vbs = VelocityBoosterStorage.getVelocityBoosterStorage();
        vbs.getBoosters().put(uuid, this);
        vbs.saveBoosters(vbs.getBoosters().values());
        updateQueue();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("reload");
        VelocityBoosters.getPlugin().getProxy().getAllServers()
                .forEach(registeredServer -> registeredServer.sendPluginMessage(VelocityBoosters.getPlugin().getChannelIdentifier(), out.toByteArray()));
    }

    public void finish() { //TODO finish it on the servers as well
        finished = true;
        stopBooster();
        saveBooster(); //Deletes inactive boosters
        List<Booster> collect = VelocityBoosterStorage.getVelocityBoosterStorage().getBoosters(boosterType).stream().sorted().collect(Collectors.toList());
        if (collect.size() <= 1)
            return;
        Booster booster = collect.get(1);
        booster.setActive(true);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("activate");
        out.writeUTF(booster.getUUID().toString());
        VelocityBoosters.getPlugin().getProxy().getAllServers()
                .forEach(registeredServer -> registeredServer.sendPluginMessage(VelocityBoosters.getPlugin().getChannelIdentifier(), out.toByteArray()));
        //TODO send plugin message that this is finished
    }

    @Override
    public boolean finished() {
        return finished;
    }

    private void updateQueue() {
        Collection<Booster> boosters = VelocityBoosterStorage.getVelocityBoosterStorage().getBoosters(getType());
        if (boosters.isEmpty())
            return;
        List<Booster> collect = boosters.stream().sorted().collect(Collectors.toList());
        Booster booster = collect.get(0);
        if (!booster.isActive()) {
            booster.setActive(true);
            booster.setStartingTime(new Date().getTime());
        }
        if (collect.size() > 1)
            fixTimes(collect);
    }

    private void fixTimes(List<Booster> sorted) {
        for (int i = 0; i < sorted.size() - 1; i++) {
            Booster booster = sorted.get(i + 1);
            if (booster.isActive()) { //Disable active boosters that shouldn't be active and update their duration
                booster.setActive(false);
                booster.setDuration(booster.getEndTime() - booster.getStartingTime());
            }
            booster.setStartingTime(sorted.get(i).getEndTime());
        }
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
