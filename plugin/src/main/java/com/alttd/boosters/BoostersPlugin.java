package com.alttd.boosters;

import com.alttd.boosterapi.config.BoosterFileStorage;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.Logger;
import com.alttd.boosters.commands.BoosterCommand;
import com.alttd.boosters.listeners.MyPetListener;
import com.alttd.boosters.listeners.PhantomSpawnListener;
import com.alttd.boosters.listeners.PluginMessage;
import com.alttd.boosters.listeners.mcMMOListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoostersPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = new Logger(getSLF4JLogger());
        BoosterCache boosterCache = new BoosterCache(new BoosterFileStorage(logger));

        if (getServer().getPluginManager().isPluginEnabled("MyPet")) {
            registerListener(new MyPetListener(boosterCache));
        }

        if (getServer().getPluginManager().isPluginEnabled("mcMMO")) {
            registerListener(new mcMMOListener(boosterCache));
        }

        registerListener(new PhantomSpawnListener(boosterCache));

        getServer().getMessenger().registerOutgoingPluginChannel(this, Config.SETTINGS.PLUGIN_MESSAGE_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, Config.SETTINGS.PLUGIN_MESSAGE_CHANNEL, new PluginMessage(logger, boosterCache));
        registerCommand("listboosters", new BoosterCommand(boosterCache, logger));
    }

    public void registerListener(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void registerCommand(String commandName, CommandExecutor CommandExecutor) {
        getCommand(commandName).setExecutor(CommandExecutor);
    }
}
