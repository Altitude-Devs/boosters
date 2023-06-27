package com.alttd.vboosters.commands.boosterSubcommands;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.BoosterParser;
import com.alttd.boosterapi.util.Logger;
import com.alttd.vboosters.commands.Command;
import com.alttd.vboosters.commands.SubCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

public class ListBoosters extends Command implements SubCommand {

    private final Logger logger;
    private final BoosterCache boosterCache;

    public ListBoosters(Logger logger, BoosterCache boosterCache) {
        this.logger = logger;
        this.boosterCache = boosterCache;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void execute(String[] args, CommandSource source) {
        List<Component> activeBoosters = BoosterParser.parseBoosters(logger, boosterCache.getAllActiveBoosters(),
                Config.BOOSTER_MESSAGES.ACTIVE_BOOSTER_PART, true);
        List<Component> queuedBoosters = BoosterParser.parseBoosters(logger, boosterCache.getAllQueuedBoosters(),
                Config.BOOSTER_MESSAGES.QUEUED_BOOSTER_PART, false);

        source.sendMessage(parseMessage(Config.BOOSTER_MESSAGES.LIST_BOOSTER_MESSAGE,
                Placeholder.component("active_boosters", Component.join(JoinConfiguration.newlines(), activeBoosters)),
                Placeholder.component("queued_boosters", Component.join(JoinConfiguration.newlines(), queuedBoosters))
        ));
    }

    @Override
    public List<String> suggest(String[] args, CommandSource source) {
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return ""; //TODO implement
    }
}
