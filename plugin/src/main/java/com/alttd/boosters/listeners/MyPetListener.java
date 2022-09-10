package com.alttd.boosters.listeners;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.managers.BoosterManager;
import de.Keyle.MyPet.api.event.MyPetExpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyPetListener implements Listener {

    @EventHandler
    public void onMyPetExpEvent(MyPetExpEvent event) {
        BoosterManager bm = BoostersPlugin.getInstance().getBoosterManager();
        if(bm.isBoosted(BoosterType.MYPET)) {
            Booster b = bm.getBooster(BoosterType.MYPET);
            double multiplier = b.getMultiplier() + 1;
            event.setExp(event.getExp() * multiplier);
        }
    }
}
