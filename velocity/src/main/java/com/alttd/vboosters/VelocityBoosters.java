package com.alttd.vboosters;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.BoosterImplementation;
import com.alttd.boosterapi.config.BoosterFileStorage;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.Logger;
import com.alttd.vboosters.commands.BoosterCommand;
import com.alttd.vboosters.commands.DonorRankCommand;
import com.alttd.vboosters.listeners.PluginMessageListener;
import com.alttd.vboosters.task.BoosterTask;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

// TODO use the version created in build.gradle.kts
@Plugin(id = "boosterplugin", name = "BoosterPlugin", version = "1.0.0",
        description = "Easily manage all boosters on the Altitude Minecraft Server Network.",
        authors = {"destro174", "teri"},
        dependencies = {@Dependency(id = "luckperms"),@Dependency(id = "proxydiscordlink")}
)
public class VelocityBoosters {

    private static VelocityBoosters plugin;
    private final ProxyServer server;
    private final Logger logger;

    private BoosterAPI boosterAPI;
    private BoosterCache boosterCache;

    private final ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from(
            Config.SETTINGS.PLUGIN_MESSAGE_CHANNEL);

    @Inject
    public VelocityBoosters(ProxyServer proxyServer, org.slf4j.Logger proxyLogger) {
        plugin = this;
        server = proxyServer;
        this.logger = new Logger(proxyLogger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        boosterAPI = BoosterImplementation.get(logger);
        this.boosterCache = new BoosterCache(new BoosterFileStorage(logger));

        server.getChannelRegistrar().register(channelIdentifier);
        server.getEventManager().register(this, new PluginMessageListener(channelIdentifier));

        loadCommands();
        reloadConfig();
        new BoosterTask(logger, boosterCache).init();
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        boosterCache.updateAndSave();
    }

    public void reloadConfig() {
        boosterAPI.reloadConfig();
    }

    public static VelocityBoosters getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxy() {
        return server;
    }

    public void loadCommands() {
        // all (proxy)commands go here
        server.getCommandManager().register("booster", new BoosterCommand(server, boosterCache, logger));
        new DonorRankCommand(server, logger);
    }

    public ChannelIdentifier getChannelIdentifier() {
        return channelIdentifier;
    }

}
