package com.alttd.boosterapi.util;

import com.alttd.boosterapi.data.Booster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class BoosterParser {

    private final static MiniMessage miniMessage = MiniMessage.miniMessage();

    public static List<Component> parseBoosters(Logger logger, List<Booster> boosters, String message, boolean active) {
        return boosters.stream().map(booster -> {
            logger.debug("processing booster: " + booster);
            TagResolver resolver = TagResolver.resolver(
                    Placeholder.unparsed("type", booster.getBoosterType().getBoosterName()),
                    Placeholder.unparsed("activator", booster.getActivatorName()),
                    Placeholder.unparsed("start_time", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(booster.getStartingTime().toEpochMilli())),
                    Placeholder.unparsed("duration", booster.getDuration().toHours() + " hours"),
                    Placeholder.unparsed("multiplier", String.valueOf(booster.getMultiplier())),
                    Placeholder.unparsed("end_time", active ? DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(booster.getStartingTime().toEpochMilli() + booster.getDuration().toMillis()) : "unknown")
            );
            return miniMessage.deserialize(message, resolver);
        }).collect(Collectors.toList());
    }

}
