package com.alttd.vboosters.managers;

import com.alttd.boosterapi.config.ServerConfig;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.ServerWrapper;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerManager {

    private VelocityBoosters plugin;

    private static List<ServerWrapper> servers;

    public ServerManager(VelocityBoosters velocityBoosters) {
        plugin = velocityBoosters;
        initialize();
    }

    public void cleanup() {
        servers.clear();
        initialize();
    }

    public void initialize() {
        servers = new ArrayList<>();

        for (RegisteredServer registeredServer : plugin.getProxy().getAllServers()) {
            servers.add(new ServerWrapper(registeredServer, new ServerConfig(registeredServer.getServerInfo().getName())));
        }
    }

    public List<ServerWrapper> getServers()
    {
        return Collections.unmodifiableList(servers);
    }

    public ServerWrapper getWrapper(String serverName) {
        for(ServerWrapper wrapper : servers) {
            if(wrapper.serverName().equalsIgnoreCase(serverName)) {
                return wrapper;
            }
        }
        return null;
    }
}
