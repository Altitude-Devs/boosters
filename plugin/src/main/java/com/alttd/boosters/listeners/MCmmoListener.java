package com.alttd.boosters.listeners;

import com.alttd.boosterapi.data.Booster;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.data.BoosterType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class mcMMOListener implements Listener {

    private final BoosterCache boosterCache;

    public mcMMOListener(BoosterCache boosterCache) {
        this.boosterCache = boosterCache;
    }


    @EventHandler
    public void onMcMMOExperienceEvent(McMMOPlayerXpGainEvent event) {
        String skillName = event.getSkill().name();
        BoosterType type = BoosterType.getByName(skillName);
        Optional<Booster> optionalBooster = boosterCache.getActiveBooster(type);
        if (optionalBooster.isEmpty())
            return;

        Booster booster = optionalBooster.get();
        event.setRawXpGained(Math.round(booster.useMultiplier(event.getRawXpGained())));
    }
}
