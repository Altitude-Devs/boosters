package com.alttd.boosters.commands;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.BoosterParser;
import com.alttd.boosterapi.util.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoosterCommand implements CommandExecutor {

    private final BoosterCache boosterCache;
    private final Logger logger;

    public BoosterCommand(BoosterCache boosterCache, Logger logger) {
        this.boosterCache = boosterCache;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("boosters.list")) {
            commandSender.sendMiniMessage("<red>You don't have permission for this command", null);
            return true;
        }

        commandSender.sendMiniMessage(Config.BOOSTER_MESSAGES.LIST_BOOSTER_MESSAGE, TagResolver.resolver(
                Placeholder.component("active_boosters", Component.join(JoinConfiguration.newlines(),
                        BoosterParser.parseBoosters(logger, boosterCache.getAllActiveBoosters(),
                                Config.BOOSTER_MESSAGES.ACTIVE_BOOSTER_PART, true))),
                Placeholder.component("queued_boosters", Component.join(JoinConfiguration.newlines(),
                        BoosterParser.parseBoosters(logger, boosterCache.getAllQueuedBoosters(),
                                Config.BOOSTER_MESSAGES.QUEUED_BOOSTER_PART, false))))
        );

        if (!(commandSender instanceof Player))
            boosterCache.reloadBoosters();
        return true;
    }

}
