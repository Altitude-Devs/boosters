package com.alttd.boosterapi;

import com.alttd.boosterapi.util.Logger;
import net.luckperms.api.LuckPerms;

public interface BoosterAPI {

    static BoosterAPI get(Logger logger) {
        return BoosterImplementation.get(logger);
    }

    LuckPerms getLuckPerms();

    void reloadConfig();
}
