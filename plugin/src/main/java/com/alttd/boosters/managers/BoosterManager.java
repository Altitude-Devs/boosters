package com.alttd.boosters.managers;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.storage.ServerBoosterStorage;
import org.bukkit.scheduler.BukkitRunnable;

public class BoosterManager {

    public BoosterManager() {
       new BukkitRunnable() {
           @Override
           public void run() {
               for (Booster booster: ServerBoosterStorage.getServerBoosterStorage().getBoosters().values()) {
                   if (!booster.isActive())
                       continue;
                   if (booster.getTimeRemaining() > 0) continue;
                        booster.finish();
               }
           }
       }.runTaskTimerAsynchronously(BoostersPlugin.getInstance(), 0, Config.activeTaskCheckFrequency * 20);
    }

    public boolean isBoosted(BoosterType type) {
        for (Booster b : ServerBoosterStorage.getServerBoosterStorage().getBoosters().values()) {
            if (b.getType() == type && b.isActive()) {
                return true;
            }
        }
        return false;
    }

    public Booster getBooster(BoosterType type) {
        for (Booster b : ServerBoosterStorage.getServerBoosterStorage().getBoosters().values()) {
            if (b.getType() == type && b.isActive()) {
                return b;
            }
        }
        return null;
    }
}
