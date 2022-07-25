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
        if (bm.isBoosted(BoosterType.MCMMO)) {
            Booster b = bm.getBooster(BoosterType.MCMMO);
            double multiplier = b.getMultiplier();
            event.setRawXpGained(Math.round(event.getRawXpGained() * multiplier));
            return;
        }
        String skillName = event.getSkill().name();
        BoosterType type = BoosterType.getByName(skillName);
        if (bm.isBoosted(type)) {
            Booster b = bm.getBooster(type);
            double multiplier = b.getMultiplier();
            event.setRawXpGained(Math.round(event.getRawXpGained() * multiplier));
            return;
        }
    }
    // TODO : add individual mcmmo skill boosters
}
