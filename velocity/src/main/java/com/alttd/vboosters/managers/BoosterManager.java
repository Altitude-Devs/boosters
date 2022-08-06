package com.alttd.vboosters.managers;

import com.alttd.boosterapi.booster.Booster;
import com.alttd.boosterapi.booster.BoosterState;
import com.alttd.boosterapi.booster.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.database.Queries;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.VelocityBooster;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BoosterManager {

    private VelocityBoosters plugin;

    private static List<Booster> queuedBoosters;
    private static List<Booster> activeBoosters;
    private static ScheduledTask boostersTask;

    public BoosterManager(VelocityBoosters velocityBoosters) {
        plugin = velocityBoosters;
        activeBoosters = new ArrayList<>();
        queuedBoosters = new ArrayList<>();
        loadAllBoosters();
        /*
         * This is mainly used to count down the active boosters and
         * let backend servers know if one should be activated/deactivated
         */
        boostersTask = plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            for (Booster booster: getActiveBoosters()) {
                if (booster.getTimeRemaining() > 0) continue;
                booster.updateState(BoosterState.FINISHED);
                // send data to the backend servers to let them know the booster is no longer active
            }
            getActiveBoosters().removeIf(booster -> booster.getState() == BoosterState.FINISHED);
            for (BoosterType type : BoosterType.values()) {
                if (!isBoosted(type)) { // activate a queud booster if needed
                    Booster queuedBooster = getHighestBooster(type);
                    if (queuedBooster == null)
                        continue;
                    activateBooster(queuedBooster);
                    // send an update to the backend servers to let them know this booster is active
                }
            }
            getActiveBoosters().removeIf(booster -> booster.getState() == BoosterState.FINISHED);
        }).repeat(Config.activeTaskCheckFrequency, TimeUnit.SECONDS).schedule();
    }

    public void loadAllBoosters() {
        try {
            ResultSet rs = Queries.getNonFinishedBoosters();
            if (rs == null) return;
            while (rs.next()) {
                //uuid, type, state, multiplier, duration, activator, timeremaining
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                BoosterType boosterType = BoosterType.getByName(rs.getString("type"));
                BoosterState boosterState = BoosterState.getByName(rs.getString("state"));
                int multiplier = rs.getInt("multiplier");
//                        long duration = rs.getLong("duration");
                String activator = rs.getString("activator");
                long timeremaining = rs.getLong("timeremaining");
                addBooster(new VelocityBooster(uuid, boosterType, boosterState, activator, timeremaining, multiplier));
            }
        } catch (SQLException ex) {
            ALogger.fatal("Error loading booster", ex);
        }
        loadBoosters();
    }

    public void loadBoosters() {
        for (BoosterType type : BoosterType.values()) {
            if (isBoosted(type)) {
                Booster activeBooster = getBoosted(type);
                Booster queuedBooster = getHighestBooster(type);
                if (queuedBooster != null && queuedBooster.getMultiplier() > activeBooster.getMultiplier()) {
                    swapBooster(activeBooster, queuedBooster);
                }
            } else {
                Booster queuedBooster = getHighestBooster(type);
                if(queuedBooster == null)
                    continue;
                activateBooster(queuedBooster);
            }
        }
    }

    public void addBooster(Booster booster) {
        BoosterType type = booster.getType();
        if(isBoosted(type)) {
            Booster activeBooster = getBoosted(type);
            Booster queuedBooster = getHighestBooster(type);
            if (queuedBooster != null && queuedBooster.getMultiplier() > activeBooster.getMultiplier()) {
                swapBooster(activeBooster, queuedBooster);
            }
        } else {
            activateBooster(booster);
        }
    }

    public void swapBooster(Booster activeBooster, Booster queuedBooster) {
        deactivateBooster(activeBooster);
        activateBooster(queuedBooster);
    }

    public void activateBooster(Booster booster) {
        queuedBoosters.remove(booster);
        activeBoosters.add(booster);
        booster.setStartingTime(System.currentTimeMillis());
        booster.updateState(BoosterState.ACTIVE);
        ServerManager.sendBoosterUpdate(booster, "start");
    }

    public void deactivateBooster(Booster booster) {
        queuedBoosters.add(booster);
        activeBoosters.remove(booster);
        booster.updateState(BoosterState.PAUSED);
        ServerManager.sendBoosterUpdate(booster, "stop");
    }

    public void removeBooster(Booster booster) {
        activeBoosters.remove(booster);
        queuedBoosters.remove(booster);
        booster.updateState(BoosterState.FINISHED);
        booster.saveBooster();
        ServerManager.sendBoosterUpdate(booster, "stop");
    }

    public boolean isBoosted(BoosterType type) {
        for (Booster b : activeBoosters) {
            if (b.getType() == type && b.isActive()) {
                return true;
            }
        }
        return false;
    }

    public Booster getBoosted(BoosterType type) {
        for (Booster b : activeBoosters) {
            if (b.getType() == type && b.isActive()) {
                return b;
            }
        }
        return null;
    }

    public Booster getHighestBooster(BoosterType type) {
        return getQueuedBooster(type).stream().max(Comparator.comparing(Booster::getMultiplier)).orElse(null);
    }

    public List<Booster> getActiveBoosters() {
        return activeBoosters;
    }

    public List<Booster> getQueuedBoosters() {
        return queuedBoosters;
    }

    public List<Booster> getQueuedBooster(BoosterType type) {
        return getQueuedBoosters().stream().filter(booster -> booster.getType() == type).collect(Collectors.toList());
    }

    public void saveAllBoosters() {
        for (Booster b : activeBoosters) {
            b.updateState(BoosterState.PAUSED);
        }
        queuedBoosters.addAll(activeBoosters);
        activeBoosters = null;
        for (Booster b : queuedBoosters) {
            b.saveBooster();
        }
        queuedBoosters = null;
    }

}
