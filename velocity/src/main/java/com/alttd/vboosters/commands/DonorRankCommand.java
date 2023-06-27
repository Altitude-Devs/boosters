package com.alttd.vboosters.commands;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Logger;
import com.alttd.boosterapi.util.StringModifier;
import com.alttd.vboosters.VelocityBoosters;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.ArrayList;
import java.util.Collection;

public class DonorRankCommand {

    private final MiniMessage miniMessage;
    private final Logger logger;

    public DonorRankCommand(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        miniMessage = MiniMessage.miniMessage();

        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("donorrank")
                .requires(ctx -> ctx.hasPermission("command.proxy.donorrank"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Collection<String> possibleValues = new ArrayList<>();
                            for (Player player : proxyServer.getAllPlayers()) {
                                possibleValues.add(player.getGameProfile().getName());
                            }
                            if(possibleValues.isEmpty()) return Suggestions.empty();
                            String remaining = builder.getRemaining().toLowerCase();
                            for (String str : possibleValues) {
                                if (str.toLowerCase().startsWith(remaining)) {
                                    builder.suggest(StringArgumentType.escapeIfRequired(str));
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("action", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    Collection<String> possibleValues = new ArrayList<>();
                                    possibleValues.add("promote");
                                    possibleValues.add("demote");
                                    String remaining = builder.getRemaining().toLowerCase();
                                    for (String str : possibleValues) {
                                        if (str.toLowerCase().startsWith(remaining)) {
                                            builder.suggest(StringArgumentType.escapeIfRequired(str));
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("rank", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            Collection<String> possibleValues = new ArrayList<>(Config.SETTINGS.DONOR_RANKS);
                                            String remaining = builder.getRemaining().toLowerCase();
                                            for (String str : possibleValues) {
                                                if (str.toLowerCase().startsWith(remaining)) {
                                                    builder.suggest(StringArgumentType.escapeIfRequired(str));
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            CommandSource commandSource = context.getSource();
                                            String username = context.getArgument("username", String.class);
                                            String action = context.getArgument("action", String.class);
                                            String rank = context.getArgument("rank", String.class).toLowerCase();
                                            LuckPerms luckPerms = BoosterAPI.get(logger).getLuckPerms();
                                            User user = luckPerms.getUserManager().getUser(username); //TODO test if this works with username

                                            if (user == null) {
                                                commandSource.sendMessage(miniMessage.deserialize(
                                                        Config.DONOR_RANK_MESSAGES.INVALID_USER,
                                                        Placeholder.unparsed("player", username)));
                                                return 1;
                                            }

                                            if (!Config.SETTINGS.DONOR_RANKS.contains(rank)) {
                                                commandSource.sendMessage(miniMessage.deserialize(
                                                        Config.DONOR_RANK_MESSAGES.INVALID_DONOR_RANK,
                                                        Placeholder.unparsed("rank", rank)));
                                                return 1;
                                            }

                                            switch (action) {
                                                case "promote" -> promote(user, rank);
                                                case "demote" -> demote(user, rank);
                                                default -> commandSource.sendMessage(miniMessage.deserialize(Config.DONOR_RANK_MESSAGES.INVALID_ACTION));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .executes(context -> 1)
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }

    private void promote(User user, String rank) {
        LuckPerms luckPerms = BoosterAPI.get(logger).getLuckPerms();
        user.getNodes(NodeType.INHERITANCE).stream()
                .filter(Node::getValue)
                .forEach(node -> {
                    if (Config.SETTINGS.DONOR_RANKS.contains(node.getGroupName()))
                        user.data().remove(node);
                });
        user.data().add(InheritanceNode.builder(rank).build());
        VelocityBoosters.getPlugin().getProxy().getPlayer(user.getUniqueId()).ifPresent(player -> {
            if (player.isActive()) {
                player.sendMessage(miniMessage.deserialize(Config.DONOR_RANK_MESSAGES.PROMOTE_MESSAGE,
                        Placeholder.unparsed("rank", StringModifier.capitalize(rank)),
                        Placeholder.unparsed("player", player.getUsername())));
            }
        });
        luckPerms.getUserManager().saveUser(user);
    }

    private void demote(User user, String rank) {
        LuckPerms luckPerms = BoosterAPI.get(logger).getLuckPerms();
        user.getNodes(NodeType.INHERITANCE).stream()
                .filter(Node::getValue)
                .forEach(node -> {
                    if (Config.SETTINGS.DONOR_RANKS.contains(node.getGroupName()))
                        user.data().remove(node);
                });
        VelocityBoosters.getPlugin().getProxy().getPlayer(user.getUniqueId()).ifPresent(player -> {
            if (player.isActive()) {
                player.sendMessage(miniMessage.deserialize(Config.DONOR_RANK_MESSAGES.DEMOTE_MESSAGE,
                        Placeholder.unparsed("rank", StringModifier.capitalize(rank)),
                        Placeholder.unparsed("player", player.getUsername())));
            }
        });
        luckPerms.getUserManager().saveUser(user);
    }
}
