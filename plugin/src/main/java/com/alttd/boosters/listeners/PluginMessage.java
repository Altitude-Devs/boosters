package com.alttd.boosters.listeners;

import com.alttd.boosterapi.booster.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.data.IBooster;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessage implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals(Config.pluginMessageChannel)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        switch (subChannel) {
            case "booster-update":
                String uuid = in.readUTF();
                String status = in.readUTF();
                switch (status) {
                    case "start":
                        BoostersPlugin.getInstance().getBoosterManager().loadBooster(uuid);
                        break;
                    case "stop":
                        BoostersPlugin.getInstance().getBoosterManager().removeBooster(UUID.fromString(uuid));
                        // more stuff
                        break;
                }
                break;
            default:
                break;
        }
    }
}
