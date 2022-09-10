package com.alttd.vboosters.managers;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.VelocityBooster;
import com.alttd.vboosters.storage.VelocityBoosterStorage;
import com.mysql.cj.log.Log;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
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
        /*
         * This is mainly used to count down the active boosters and
         * let backend servers know if one should be activated/deactivated
         */
        boostersTask = plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            for (Booster booster: getActiveBoosters()) {
                if (booster.getTimeRemaining() > 0) continue;
                booster.finish();

                plugin.getProxy().sendMessage(MiniMessage.miniMessage().deserialize("<green>Booster <booster> ended!</green>", Placeholder.unparsed("booster", booster.getType().getBoosterName())));
                // send data to the backend servers to let them know the booster is no longer active
            }
            getActiveBoosters().removeIf(Booster::finished);
            for (BoosterType type : BoosterType.values()) {
                if (!isBoosted(type)) { // activate a queud booster if needed
                    Booster queuedBooster = getHighestBooster(type);
                    if (queuedBooster == null)
                        continue;
                    activateBooster(queuedBooster);
                    // send an update to the backend servers to let them know this booster is active
                }
            }
            getQueuedBoosters().removeIf(Booster::finished);
        }).repeat(Config.activeTaskCheckFrequency, TimeUnit.SECONDS).schedule();
    }

    public void loadBoosters() {
        // load boosters from datastorage and check them one by one to activate them
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
//        BoosterType type = booster.getType();
//        if (isBoosted(type)) {
//            Booster activeBooster = getBoosted(type);
//            Booster queuedBooster = getHighestBooster(type);
//            if (queuedBooster != null && queuedBooster.getMultiplier() > activeBooster.getMultiplier()) {
//                swapBooster(activeBooster, queuedBooster);
//            }
//        } else {
//            activateBooster(booster);
//        }
        VelocityBoosterStorage.getVelocityBoosterStorage().add(booster);
        if (booster instanceof VelocityBooster velocityBooster)
            velocityBooster.updateQueue();
        else
            ALogger.error("Tried to add a not velocity booster from velocity");
    }

    public void removeBooster(Booster booster) {
        activeBoosters.remove(booster);
        booster.stopBooster();
    }

    public void swapBooster(Booster activeBooster, Booster queuedBooster) {
        deactivateBooster(activeBooster);
        activateBooster(queuedBooster);
    }

    public void activateBooster(Booster booster) {
        queuedBoosters.remove(booster);
        activeBoosters.add(booster);
        booster.setActive(true);
    }

    public void deactivateBooster(Booster booster) {
        if (booster.isActive())
            queuedBoosters.add(booster);
        activeBoosters.remove(booster);
        booster.setActive(false);
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
            b.stopBooster();
        }
        for (Booster b : queuedBoosters) {
            b.saveBooster();
        }
        activeBoosters.clear();
        queuedBoosters.clear();
    }

}
