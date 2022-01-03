package com.alttd.vboosters.commands;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.util.Utils;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.VelocityBooster;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.ArrayList;
import java.util.Collection;

public class BoosterCommand {

    public BoosterCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("booster")
                .requires(ctx -> ctx.hasPermission("command.proxy.booster"))
                .then(LiteralArgumentBuilder
                        .<CommandSource>literal("reload")
                        .requires(ctx -> ctx.hasPermission("command.proxy.booster.reload"))
                        .executes(context -> {
                            // TODO : reload config?
                            return 1;
                        })
                )
                .then(RequiredArgumentBuilder
                        .<CommandSource, String>argument("booster", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Collection<String> possibleValues = new ArrayList<>();
                            for (BoosterType boosterType: BoosterType.values()) {
                                possibleValues.add(boosterType.getBoosterName());
                            }
                            if(possibleValues.isEmpty()) return Suggestions.empty();
                            String remaining = builder.getRemaining().toLowerCase();
                            for (String str : possibleValues) {
                                if (str.toLowerCase().startsWith(remaining)) {
                                    builder.suggest(str = StringArgumentType.escapeIfRequired(str));
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder
                                .<CommandSource, String>argument("player", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Collection<String> possibleValues = new ArrayList<>();
                                    for (Player player : proxyServer.getAllPlayers()) {
                                        possibleValues.add(player.getGameProfile().getName());
                                    }
                                    if(possibleValues.isEmpty()) return Suggestions.empty();
                                    String remaining = builder.getRemaining().toLowerCase();
                                    for (String str : possibleValues) {
                                        if (str.toLowerCase().startsWith(remaining)) {
                                            builder.suggest(str = StringArgumentType.escapeIfRequired(str));
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(RequiredArgumentBuilder
                                        .<CommandSource, Integer>argument("duration", IntegerArgumentType.integer())
                                        .then(RequiredArgumentBuilder
                                                .<CommandSource, Integer>argument("level", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    createBooster(context);
                                                    return 1;
                                                })
                                        )
                                        .executes(context -> {
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    return 1;
                                })
                        )
                        .executes(context -> {
                            return 1;
                        })
                )
                .executes(context -> {
                    return 1;
                })
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }

    public void createBooster(CommandContext<CommandSource> commandContext) {
        CommandSource source = commandContext.getSource();
        BoosterType boosterType = BoosterType.getByName(commandContext.getArgument("booster", String.class));
        if (boosterType == null) {
            source.sendMessage(Utils.parseMiniMessage("Invalid booster type", null));
        }
        String playerName = commandContext.getArgument("player", String.class);
        int duration = commandContext.getArgument("duration", Integer.class);
        int level = commandContext.getArgument("level", Integer.class);
        Booster booster = new VelocityBooster(boosterType, playerName, duration, level);
        VelocityBoosters.getPlugin().getBoosterManager().addBooster(booster);
    }

}
