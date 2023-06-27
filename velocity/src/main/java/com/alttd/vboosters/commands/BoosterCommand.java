package com.alttd.vboosters.commands;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.util.Logger;
import com.alttd.vboosters.commands.boosterSubcommands.Activate;
import com.alttd.vboosters.commands.boosterSubcommands.ListBoosters;
import com.alttd.vboosters.commands.boosterSubcommands.Reload;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoosterCommand extends Command implements SimpleCommand {
    private final List<SubCommand> subCommands;
    private final ProxyServer proxyServer;
    private final ListBoosters listBoosters;

    public BoosterCommand(ProxyServer proxyServer, BoosterCache boosterCache, Logger logger) {
        this.proxyServer = proxyServer;
        listBoosters = new ListBoosters(logger, boosterCache);
        subCommands = Arrays.asList(
                new Activate(proxyServer, boosterCache, logger),
                new Reload(logger),
                listBoosters);
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

        if (!source.hasPermission("booster.use")) {
            source.sendMessage(parseMessage(Config.GENERIC_MESSAGES.NO_PERMISSION));
            return;
        }

        if (args.length == 0) {
            listBoosters.execute(args, source);
            return;
        }

        subCommands.stream()
                .filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .ifPresentOrElse(subCommand -> {
                    if (source.hasPermission(subCommand.getPermission()))
                        subCommand.execute(args, source);
                    else
                        source.sendMessage(parseMessage(Config.GENERIC_MESSAGES.NO_PERMISSION));
                }, () -> {
                    if (!source.hasPermission(listBoosters.getPermission())) {
                        source.sendMessage(parseMessage(Config.GENERIC_MESSAGES.NO_PERMISSION));
                        return;
                    }
                    listBoosters.execute(args, source);
                });
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggest = new ArrayList<>();

        if (!invocation.source().hasPermission("party.use"))
            return suggest;
        else if (args.length == 0) {
            subCommands.stream()
                    .filter(subCommand -> invocation.source().hasPermission(subCommand.getPermission()))
                    .forEach(subCommand -> suggest.add(subCommand.getName()));
        } else if (args.length == 1) {
            subCommands.stream()
                    .filter(subCommand -> invocation.source().hasPermission(subCommand.getPermission()))
                    .filter(subCommand -> subCommand.getName().startsWith(args[0].toLowerCase()))
                    .forEach(subCommand -> suggest.add(subCommand.getName()));
        } else {
            subCommands.stream()
                    .filter(subCommand -> invocation.source().hasPermission(subCommand.getPermission()))
                    .filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .ifPresent(subCommand -> suggest.addAll(subCommand.suggest(args, invocation.source())));
        }

        if (args.length == 0)
            return suggest;
        else
            return finalizeSuggest(suggest, args[args.length - 1]);
    }

    public List<String> finalizeSuggest(List<String> possibleValues, String remaining) {
        List<String> finalValues = new ArrayList<>();

        for (String str : possibleValues) {
            if (str.toLowerCase().startsWith(remaining.toLowerCase())) {
                finalValues.add(StringArgumentType.escapeIfRequired(str));
            }
        }

        return finalValues;
    }

}