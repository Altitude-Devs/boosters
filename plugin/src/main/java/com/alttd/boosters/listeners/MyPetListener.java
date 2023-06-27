package com.alttd.boosters.listeners;

import com.alttd.boosterapi.data.Booster;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.data.BoosterType;
import de.Keyle.MyPet.api.event.MyPetExpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class MyPetListener implements Listener {

    private final BoosterCache boosterCache;

    public MyPetListener(BoosterCache boosterCache) {
        this.boosterCache = boosterCache;
    }

    @EventHandler
    public void onMyPetExpEvent(MyPetExpEvent event) {
        double exp = event.getPet().getExp();
        if (exp == 0) {
            return;
        }

        Optional<Booster> myPetBooster = boosterCache.getActiveBooster(BoosterType.MYPET);
        if (myPetBooster.isEmpty())
            return;

        Booster booster = myPetBooster.get();
        event.setExp(booster.useMultiplier(event.getExp()));
    }
}
