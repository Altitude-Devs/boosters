package com.alttd.boosters.listeners;

import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.data.BoosterType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PhantomSpawnListener implements Listener {

    private final BoosterCache boosterCache;

    public PhantomSpawnListener(BoosterCache boosterCache) {
        this.boosterCache = boosterCache;
    }

    @EventHandler
    public void onPhantomPreSpawn(com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent event) {
        Entity spawningEntity = event.getSpawningEntity();
        if (spawningEntity instanceof Player
                && boosterCache.getActiveBooster(BoosterType.PHANTOM).isPresent()) {
            event.setCancelled(true);
            event.setShouldAbortSpawn(true);
        }
    }

}
