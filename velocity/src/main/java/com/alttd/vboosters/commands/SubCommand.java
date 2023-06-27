package com.alttd.vboosters.commands;

import com.velocitypowered.api.command.CommandSource;

import java.util.List;

public interface SubCommand{

    String getName();

    default String getPermission() {
        return "boosters." + getName();
    }

    void execute(String[] args, CommandSource source);

    List<String> suggest(String[] args, CommandSource source);

    String getHelpMessage();

}
