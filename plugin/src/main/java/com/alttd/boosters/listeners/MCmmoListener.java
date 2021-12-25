package com.alttd.boosters.listeners;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.managers.BoosterManager;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MCmmoListener implements Listener {

    @EventHandler
    public void onMcMMOExperienceEvent(McMMOPlayerXpGainEvent event) {
        BoosterManager bm = BoostersPlugin.getInstance().getBoosterManager();
        if(bm.isBoosted(BoosterType.MCMMO)) {
            Booster b = bm.getBoosted(BoosterType.MCMMO);
            int multiplier = b.getMultiplier();
            event.setRawXpGained(event.getRawXpGained() * multiplier);
        }
    }
    // TODO : add individual mcmmo skill boosters
}
