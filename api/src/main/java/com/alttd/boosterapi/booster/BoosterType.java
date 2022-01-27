package com.alttd.boosterapi;

public enum BoosterType {

    /**
     * MCMMO - implies all mcmmo skills are boosted
     */
    MCMMO("mcmmo"),

    ACROBATICS("acrobatics"),
    ALCHEMY("alchemy"),
    ARCHERY("archery"),
    AXES("axes"),
    EXCAVATION("excavation"),
    FISHING("fishing"),
    HERBALISM("herbalism"),
    MINING("mining"),
    REPAIR("repair"),
    SALVAGE("salvage"),
    SMELTING("smelting"),
    SWORDS("swords"),
    TAMING("taming"),
    UNARMED("unarmed"),
    WOODCUTTING("woodcutting"),

    /**
     * MYPET - Boosts MyPet exp gains
     */
    MYPET("mypet"),
    /**
     * VANILLAXP - increases exp gained by killing mobs
     */
    VANILLAXP("vanillaxp"),
    /**
     * LUCK - Boosts luck based vanilla features
     * Caps at max vanilla enchant + 1
     * Boosts:
     *  - Mining with Fortune
     *  - Adds 1 extra looting level to any mob kills
     *  - Boosts luck of the sea by 1
     */
    LUCK("luck"),
    /**
     * PHANTOM - Disables phantom spawns while this booster is active
     */
    PHANTOM("phantom"),
    ERROR("error");

    public final String BoosterName;
    BoosterType(String BoosterName) {
        this.BoosterName = BoosterName;
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
        return ERROR;
    }

}
