package com.alttd.boosterapi.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;

import java.util.List;

public class Utils {

    public static Component parseMiniMessage(String message, List<Template> templates) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        if (templates == null) {
            return miniMessage.deserialize(message);
        } else {
            return miniMessage.deserialize(message, TemplateResolver.templates(templates));
        }
    }

}
