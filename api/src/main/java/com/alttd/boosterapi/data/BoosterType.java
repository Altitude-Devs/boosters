package com.alttd.boosterapi.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BoosterType {

    /**
     * MCMMO - implies all mcmmo skills are boosted
     */
    MCMMO("mcmmo", null),

    ACROBATICS("acrobatics", MCMMO),
    ALCHEMY("alchemy", MCMMO),
    ARCHERY("archery", MCMMO),
    AXES("axes", MCMMO),
    EXCAVATION("excavation", MCMMO),
    FISHING("fishing", MCMMO),
    HERBALISM("herbalism", MCMMO),
    MINING("mining", MCMMO),
    REPAIR("repair", MCMMO),
    SALVAGE("salvage", MCMMO),
    SMELTING("smelting", MCMMO),
    SWORDS("swords", MCMMO),
    TAMING("taming", MCMMO),
    UNARMED("unarmed", MCMMO),
    WOODCUTTING("woodcutting", MCMMO),

    /**
     * MYPET - Boosts MyPet exp gains
     */
    MYPET("mypet", null),
    /**
     * VANILLAXP - increases exp gained by killing mobs
     */
    VANILLAXP("vanillaxp", null),
    /**
     * LUCK - Boosts luck based vanilla features
     * Caps at max vanilla enchant + 1
     * Boosts:
     *  - Mining with Fortune
     *  - Adds 1 extra looting level to any mob kills
     *  - Boosts luck of the sea by 1
     */
    LUCK("luck", null),
    /**
     * PHANTOM - Disables phantom spawns while this booster is active
     */
    PHANTOM("phantom", null),
    /**
     * IDK
     */
    UNKNOWN("unknown", null);

    public final String BoosterName;
    public final BoosterType parent;
    BoosterType(String BoosterName, BoosterType parent) {
        this.BoosterName = BoosterName;
        this.parent = parent;
    }

    public String getBoosterName() {
        return this.BoosterName;
    }

    public static BoosterType getByName(String text) {
        for (BoosterType type : BoosterType.values()) {
            if (type.BoosterName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public List<BoosterType> getChildBoosters() {
        return Arrays.stream(BoosterType.values()).filter(boosterType -> boosterType.parent == this).collect(Collectors.toList());
    }

}
