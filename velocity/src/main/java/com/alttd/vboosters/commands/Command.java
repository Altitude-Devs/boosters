package com.alttd.vboosters.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public abstract class Command {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    protected Component parseMessage(String message, TagResolver.Single... placeholders) {
        if (placeholders.length == 0)
            return miniMessage.deserialize(message);
        return miniMessage.deserialize(message, placeholders);
    }

}
