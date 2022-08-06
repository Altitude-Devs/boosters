package com.alttd.boosters.commands;

import com.alttd.boosterapi.booster.Booster;
import com.alttd.boosterapi.booster.BoosterState;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Utils;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.managers.BoosterManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BoosterList implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BoosterManager boosterManager = BoostersPlugin.getInstance().getBoosterManager();
        if (!boosterManager.hasBoosters()) {
            sender.sendMiniMessage("<red>There are no active boosters", null);
            return true;
        }
        Component component = Utils.parseMiniMessage("<gold>Active boosters: ", null);
        for (Booster booster : boosterManager.getBoosters()) {
            if (booster.getType() == null || booster.getActivator() == null || booster.getDuration() == null || booster.getStartingTime() == null) continue;
            List<Template> templates = new ArrayList<>(List.of(
                    Template.template("type", booster.getType().getBoosterName()),
                    Template.template("reason", booster.getActivator()),
                    Template.template("duration", booster.getTimeDuration()), // TODO add formatted time string
                    Template.template("multiplier", booster.getMultiplier()+"")));
            Component info = Utils.parseMiniMessage(Config.BOOSTERLI, templates);
            component = component.append(Component.newline()).append(info);
        }
        sender.sendMessage(component);
        return true;
    }
}
