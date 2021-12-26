package com.alttd.boosters.managers;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;

import java.util.List;

public class BoosterManager {

    private static List<Booster> activeBoosters;

    public boolean isBoosted(BoosterType type) {
        for (Booster b : activeBoosters) {
            if (b.getType() == type && b.isActive()) {
                return true;
            }
        }
        return false;
    }

    public Booster getBooster(BoosterType type) {
        for (Booster b : activeBoosters) {
            if (b.getType() == type && b.isActive()) {
                return b;
            }
        }
        return null;
    }
}
