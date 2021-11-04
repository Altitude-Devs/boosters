package com.alttd.vboosters.managers;

import com.alttd.boosters.api.Booster;
import com.alttd.boosters.api.BoosterType;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.config.Config;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BoosterManager {

    private VelocityBoosters plugin;

    private static List<Booster> queuedBoosters;
    private static List<Booster> activeBoosters;
    private static ScheduledTask queueBoosterTask;
    private static ScheduledTask activeBoostersTask;

    public void init() {
        plugin = VelocityBoosters.getPlugin();
        activeBoosters = new ArrayList<>();
        queuedBoosters = new ArrayList<>();
        /*
         * This is mainly used to count down the active boosters and
         * let backend servers know if one should be activated/deactivated
         */
        activeBoostersTask = plugin.getProxy().getScheduler().buildTask(plugin, () -> {
            for (Booster booster: getActiveBoosters()) {
                if (booster.getTimeRemaining() <= 0) {

                }
            }
        }).repeat(Config.activeTaskCheckFrequency, TimeUnit.SECONDS).schedule();
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

    public void removeBooster(Booster booster) {
        activeBoosters.remove(booster);
        booster.stopBooster();
//        final TextComponent message = new TextComponent(booster.getType().name() + " by " + booster.getPlayerName() + " has ended.");
//        message.setColor(ChatColor.DARK_PURPLE);
//        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Duration" + booster.getTimeLeft()).create() ));
//        plugin.getServer().broadcast(message);

//        for(Booster qb : queuedBoosters) {
//            boolean active = false;
//            BoosterType qType = qb.getType();
//            if(qType == booster.getType()) {
//                for(Booster b : activeBoosters) {
//                    if(b.getType() == qType) {
//                        active = true;
//                        break;
//                    }
//                }
//                if(!active) {
//                    activateBooster(qb);
//                    break;
//                }
//            }
//        }
    }

    public void swapBooster(Booster activeBooster, Booster queuedBooster) {
        deactivateBooster(activeBooster);
        activateBooster(queuedBooster);
    }

    public void activateBooster(Booster booster) {
        queuedBoosters.remove(booster);
        activeBoosters.add(booster);
        booster.setActive(true);
//        final TextComponent message = new TextComponent(booster.getType().name() + " activated by " + booster.getPlayerName());
//        message.setColor(ChatColor.DARK_PURPLE);
//        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Duration" + booster.getTimeLeft()).create() ));
//        plugin.getServer().broadcast(message);
    }

    public void deactivateBooster(Booster booster) {
        queuedBoosters.add(booster);
        activeBoosters.remove(booster);
        booster.setActive(false);
//        final TextComponent message = new TextComponent(booster.getType().name() + " activated by " + booster.getPlayerName());
//        message.setColor(ChatColor.DARK_PURPLE);
//        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Duration" + booster.getTimeLeft()).create() ));
//        plugin.getServer().broadcast(message);
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
            b.saveBooster();
        }
        for (Booster b : queuedBoosters) {
            b.saveBooster();
        }
    }

}
