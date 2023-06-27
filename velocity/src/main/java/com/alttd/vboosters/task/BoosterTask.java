package com.alttd.vboosters.task;

import com.alttd.boosterapi.data.Booster;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.Logger;
import com.alttd.vboosters.VelocityBoosters;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BoosterTask {

    private VelocityBoosters plugin;
    private final Logger logger;
    private final BoosterCache boosterCache;

    public BoosterTask(Logger logger, BoosterCache boosterCache) {
        super();
        this.logger = logger;
        this.boosterCache = boosterCache;
        plugin = VelocityBoosters.getPlugin();
    }

    private void run() {
        boolean update = false;
        List<Booster> values = boosterCache.getAllActiveBoosters();
        for (Booster booster : values) {
            logger.debug("Handling booster: " + booster);
            Instant currentTime = Instant.now();
            Duration elapsedTime = Duration.between(booster.getStartingTime(), currentTime);
            if (elapsedTime.compareTo(booster.getDuration()) >= 0) {
                logger.debug("No time remaining, finishing booster: " + booster);
                boosterCache.finishBooster(booster);
                update = true;
            }
        }
        if (!update)
            return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("reload");
        plugin.getProxy().getAllServers()
                .forEach(registeredServer -> registeredServer.sendPluginMessage(VelocityBoosters.getPlugin().getChannelIdentifier(), out.toByteArray()));
    }

    public void init() {
        plugin.getProxy().getScheduler().buildTask(plugin, this::run).repeat(Config.SETTINGS.UPDATE_FREQUENCY_MINUTES, TimeUnit.MINUTES).schedule();
    }

}
