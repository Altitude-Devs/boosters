package com.alttd.boosters.listeners;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.Logger;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessage implements PluginMessageListener {

    private final Logger logger;
    private final BoosterCache boosterCache;

    public PluginMessage(Logger logger, BoosterCache boosterCache) {
        this.logger = logger;
        this.boosterCache = boosterCache;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals(Config.SETTINGS.PLUGIN_MESSAGE_CHANNEL))
            return;
        logger.debug("Received plugin message");
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equals("reload")) {
            boosterCache.reloadBoosters();
        } else {
            logger.severe("Received invalid plugin message");
        }
    }
}
