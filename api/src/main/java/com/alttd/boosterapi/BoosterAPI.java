package com.alttd.boosterapi;

import net.luckperms.api.LuckPerms;

public interface BoosterAPI {

    static BoosterAPI get() {
        return BoosterImplementation.get();
    }

    LuckPerms getLuckPerms();

    void reloadConfig();
}
