package com.alttd.vboosters.task;

import com.alttd.boosterapi.config.Config;
import com.alttd.vboosters.VelocityBoosters;

import java.util.concurrent.TimeUnit;

public class BoosterTask {

    private VelocityBoosters plugin;

    public BoosterTask() {
        super();
        plugin = VelocityBoosters.getPlugin();
    }

    public void init() {
        plugin.getProxy().getScheduler().buildTask(plugin, () -> {

        }).repeat(Config.taskCheckFrequency, TimeUnit.SECONDS).schedule();
    }

}
