package com.alttd.boosterapi.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;

import java.util.List;
import java.util.Locale;

public class Utils {

    public static Component parseMiniMessage(String message, List<Template> templates) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        if (templates == null) {
            return miniMessage.deserialize(message);
        } else {
            return miniMessage.deserialize(message, TemplateResolver.templates(templates));
        }
    }

    public static String capitalize(String string) {
        if (string.length() <= 1)
            return string.toUpperCase();
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.toLowerCase().substring(1);
    }
}
