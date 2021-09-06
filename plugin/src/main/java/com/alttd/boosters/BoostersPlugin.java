package com.alttd.boosters;

import com.alttd.boosters.api.BoosterAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoostersPlugin extends JavaPlugin {

    private static BoostersPlugin instance;
    private static BoosterAPI boosterAPI;

    @Override
    public void onEnable() {
        instance = this;
        boosterAPI = new BoosterAPIProvider();
    }

    @Override
    public void onDisable() {

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
}
