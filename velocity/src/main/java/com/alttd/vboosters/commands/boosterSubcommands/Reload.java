package com.alttd.vboosters.commands.boosterSubcommands;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Logger;
import com.alttd.vboosters.commands.Command;
import com.alttd.vboosters.commands.SubCommand;
import com.velocitypowered.api.command.CommandSource;

import java.util.List;

public class Reload extends Command implements SubCommand {

    private final Logger logger;

    public Reload(Logger logger) {
        this.logger = logger;
    }


    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public void execute(String[] args, CommandSource source) {
        Config.reload(logger);
        source.sendMessage(parseMessage(Config.GENERIC_MESSAGES.RELOADED));
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
