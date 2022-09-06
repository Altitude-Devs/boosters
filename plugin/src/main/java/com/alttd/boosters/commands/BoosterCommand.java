package com.alttd.boosters.commands;

import com.alttd.boosterapi.Booster;
import com.alttd.boosters.storage.ServerBoosterStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.*;

public class BoosterCommand implements CommandExecutor {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("boosters.list")) {
            commandSender.sendMiniMessage("<red>You don't have permission for this command", null);
            return true;
        }
        String message = "Active boosters:\n<active_boosters>\n\nQueued boosters:\n<queued_boosters>";
        String activeBooster = "<type> activated by <activator> until <end_time> [UTC], boosts <multiplier> times";
        String queuedBooster = "<type> queued by <activator> starts at <start_time> [UTC] and will be active for <duration>, boosts <multiplier> times";
        List<Component> activeBoosterComponents = new ArrayList<>();
        List<Component> queuedBoosterComponents = new ArrayList<>();
        for (Booster booster : ServerBoosterStorage.getServerBoosterStorage().getBoosters().values()) {
            long expiryTime = new Date().getTime() + booster.getDuration();
            TagResolver templates = TagResolver.resolver(
                    Placeholder.unparsed("type", booster.getType().getBoosterName()),
                    Placeholder.unparsed("activator", booster.getActivator()),
                    Placeholder.unparsed("start_time", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(booster.getStartingTime())),
                    Placeholder.unparsed("duration", String.valueOf(booster.getDuration())),
                    Placeholder.unparsed("multiplier", String.valueOf(booster.getMultiplier())),
                    Placeholder.unparsed("end_time", booster.isActive() ? DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(expiryTime) : "unknown")
                    );
            if (booster.isActive())
                activeBoosterComponents.add(miniMessage.deserialize(activeBooster, templates));
            else if (!booster.finished())
                queuedBoosterComponents.add(miniMessage.deserialize(queuedBooster, templates));
        }

        commandSender.sendMiniMessage(message, TagResolver.resolver(
                Placeholder.component("active_boosters", Component.join(JoinConfiguration.newlines(), activeBoosterComponents)),
                Placeholder.component("queued_boosters", Component.join(JoinConfiguration.newlines(), queuedBoosterComponents))
        ));
        return true;
    }
}
