package com.alttd.boosters.listeners;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.boosters.storage.ServerBoosterStorage;
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
            case "activate" -> {
                UUID uuid = UUID.fromString(in.readUTF());
                Booster booster = ServerBoosterStorage.getServerBoosterStorage().getBoosters().get(uuid);
                if (booster == null) {
                    ServerBoosterStorage.getServerBoosterStorage().reload();
                    booster = ServerBoosterStorage.getServerBoosterStorage().getBoosters().get(uuid);
                }
                if (booster == null) {
                    ALogger.warn("Unable to load and activate booster [" + uuid + "]");
                    break;
                }
                booster.setActive(true);
            }
            case "finish" -> {
                UUID uuid = UUID.fromString(in.readUTF());
                Booster booster = ServerBoosterStorage.getServerBoosterStorage().getBoosters().get(uuid);
                if (booster == null) {
                    ALogger.error("Tried to finish booster that was never loaded [" + uuid + "]");
                    break;
                }
                booster.finish();
            }
            case "stop" -> {
                UUID uuid = UUID.fromString(in.readUTF());
                Booster booster = ServerBoosterStorage.getServerBoosterStorage().getBoosters().get(uuid);
                if (booster == null) {
                    ALogger.error("Tried to stop booster that was never loaded [" + uuid + "]");
                    break;
                }
                booster.stopBooster();
            }
            case "reload" -> {
                ServerBoosterStorage.getServerBoosterStorage().reload();
            }
            default -> {}
        }
    }
}
