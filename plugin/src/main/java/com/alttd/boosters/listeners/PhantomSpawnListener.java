package com.alttd.boosters.listeners;

import com.alttd.boosterapi.BoosterType;
import com.alttd.boosters.BoostersPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PhantomSpawnListener implements Listener {

    @EventHandler
    public void onPhantomPreSpawn(com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent event) {
        Entity spawningEntity = event.getSpawningEntity();
        if (spawningEntity instanceof Player player
        && BoostersPlugin.getInstance().getBoosterManager().isBoosted(BoosterType.PHANTOM)) {
            event.setCancelled(true);
            event.setShouldAbortSpawn(true);
        }
    }

}
