package com.alttd.boosters.listeners;

import com.alttd.boosterapi.config.Config;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessage implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals(Config.pluginMessageChannel)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        // Listen to plugin messages from velocity to either activate or deactive a booster.
        switch (subChannel) {
            default:
                break;
        }
    }
}
