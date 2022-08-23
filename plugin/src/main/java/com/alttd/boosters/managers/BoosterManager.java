package com.alttd.boosters.managers;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.BoosterStorage;
import com.alttd.boosters.storage.ServerBoosterStorage;

import java.util.List;

public class BoosterManager {

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
