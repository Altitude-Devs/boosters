package com.alttd.vboosters.data;

import com.alttd.boosterapi.config.ServerConfig;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.UUID;

public class ServerWrapper {

    private final RegisteredServer registeredServer;
    private final String serverName;

    private final boolean useBoosters;

    public ServerWrapper(RegisteredServer registeredServer, ServerConfig serverConfig) {
        this.registeredServer = registeredServer;
        this.serverName = registeredServer.getServerInfo().getName();

        useBoosters = serverConfig.useBoosters;
    }

    public RegisteredServer getRegisteredServer() {
        return registeredServer;
    }

    public String serverName() {
        return serverName;
    }

    public boolean useBoosters() {
        return useBoosters;
    }

}
