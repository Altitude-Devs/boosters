package com.alttd.vboosters.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;

import java.util.List;

public class Vutils {

    public static Component parseMiniMessage(String message, List<Template> templates) {
        MiniMessage miniMessage = MiniMessage.get();
        if (templates == null) {
            return miniMessage.deserialize(message);
        } else {
            return miniMessage.parse(message, TemplateResolver.templates(templates));
        }
    }
}
