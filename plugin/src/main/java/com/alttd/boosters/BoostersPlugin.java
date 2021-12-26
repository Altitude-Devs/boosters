package com.alttd.boosters;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.BoosterImplementation;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosters.listeners.MCmmoListener;
import com.alttd.boosters.listeners.MyPetListener;
import com.alttd.boosters.listeners.PhantomSpawnListener;
import com.alttd.boosters.listeners.PluginMessage;
import com.alttd.boosters.managers.BoosterManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoostersPlugin extends JavaPlugin {

    private static BoostersPlugin instance;
    private static BoosterAPI boosterAPI;
    private static BoosterManager boosterManager;

    @Override
    public void onEnable() {
        instance = this;
        boosterAPI = new BoosterImplementation();
        boosterManager = new BoosterManager();

        if (getServer().getPluginManager().isPluginEnabled("MyPet")) {
            registerListener(new MyPetListener());
        }

        if (getServer().getPluginManager().isPluginEnabled("mcMMO")) {
            registerListener(new MCmmoListener());
        }

        registerListener(new PhantomSpawnListener());

        getServer().getMessenger().registerOutgoingPluginChannel(this, Config.pluginMessageChannel);
        getServer().getMessenger().registerIncomingPluginChannel(this, Config.pluginMessageChannel, new PluginMessage());
    }

    @Override
    public void onDisable() {
        instance = null;
        boosterAPI = null;
    }

    public void registerListener(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void registerCommand(String commandName, CommandExecutor CommandExecutor) {
        getCommand(commandName).setExecutor(CommandExecutor);
    }

    public static BoostersPlugin getInstance() {
        return instance;
    }

    public BoosterAPI getAPI() {
        return boosterAPI;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }
}
