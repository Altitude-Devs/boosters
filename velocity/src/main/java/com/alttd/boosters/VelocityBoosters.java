package com.alttd.boosters;

import com.alttd.boosters.listeners.PluginMessageListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
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
        dependencies = {@Dependency(id = "luckperms")}
)
public class VelocityBoosters {

    private static VelocityBoosters plugin;
    private final ProxyServer server;
    private final Logger logger;

    private ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from("altitude:boosterplugin");

    @Inject
    public VelocityBoosters(ProxyServer proxyServer, Logger proxyLogger) {
        plugin = this;
        server = proxyServer;
        logger = proxyLogger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(channelIdentifier);
        server.getEventManager().register(this, new PluginMessageListener(channelIdentifier));

        loadCommands();
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
    }

    public ChannelIdentifier getChannelIdentifier() {
        return channelIdentifier;
    }

}
