package com.alttd.boosterapi;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.database.Database;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class BoosterImplementation implements BoosterAPI {

    private static BoosterAPI instance;

    private LuckPerms luckPerms;

    public BoosterImplementation() {
        instance = this;
        reloadConfig();
        Database.initialize();
        luckPerms = getLuckPerms();
    }

    public static BoosterAPI get() {
        if (instance == null)
            instance = new BoosterImplementation();
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
        Config.init();
    }

}
