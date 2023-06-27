package com.alttd.boosterapi;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Logger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class BoosterImplementation implements BoosterAPI {

    private static BoosterAPI instance;
    private final Logger logger;

    private LuckPerms luckPerms;

    private BoosterImplementation(Logger logger) {
        instance = this;
        this.logger = logger;
        reloadConfig();

        luckPerms = getLuckPerms();
    }

    public static BoosterAPI get(Logger logger) {
        if (instance == null)
            instance = new BoosterImplementation(logger);
        return instance;
    }

    @Override
    public LuckPerms getLuckPerms() {
        if(luckPerms == null)
            luckPerms = LuckPermsProvider.get();
        return luckPerms;
    }

    @Override
    public void reloadConfig() {
        Config.reload(logger);
    }

}
