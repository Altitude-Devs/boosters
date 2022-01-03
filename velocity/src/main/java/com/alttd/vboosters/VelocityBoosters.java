package com.alttd.vboosters;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.BoosterImplementation;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.proxydiscordlink.DiscordLink;
import com.alttd.proxydiscordlink.bot.api.DiscordSendMessage;
import com.alttd.vboosters.commands.BoosterCommand;
import com.alttd.vboosters.commands.DonorRankCommand;
import com.alttd.vboosters.listeners.PluginMessageListener;
import com.alttd.vboosters.managers.BoosterManager;
import com.alttd.vboosters.managers.ServerManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

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
    private BoosterManager boosterManager;
    private ServerManager serverManager;

    private ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from("altitude:boosterplugin");

    @Inject
    public VelocityBoosters(ProxyServer proxyServer, Logger proxyLogger) {
        plugin = this;
        server = proxyServer;
        logger = proxyLogger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        ALogger.init(logger);
        boosterAPI = new BoosterImplementation();
        boosterManager = new BoosterManager(this);
        serverManager = new ServerManager(this);
        server.getChannelRegistrar().register(channelIdentifier);
        server.getEventManager().register(this, new PluginMessageListener(channelIdentifier));

        loadCommands();
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        boosterManager.saveAllBoosters();
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
        new BoosterCommand(server);
        new DonorRankCommand(server);
    }

    public ChannelIdentifier getChannelIdentifier() {
        return channelIdentifier;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

}
