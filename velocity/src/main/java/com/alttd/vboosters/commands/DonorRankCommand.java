package com.alttd.vboosters.commands;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Utils;
import com.alttd.vboosters.VelocityBoosters;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

public class DonorRankCommand {

    private final MiniMessage miniMessage;

    public DonorRankCommand(ProxyServer proxyServer) {
        miniMessage = MiniMessage.get();
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("donorrank")
                .requires(ctx -> ctx.hasPermission("command.proxy.donorrank"))
                .then(RequiredArgumentBuilder.argument("username", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("action", StringArgumentType.word()))
                .then(RequiredArgumentBuilder//.argument("rank", StringArgumentType.word())
                        .<CommandSource, String>argument("rank", StringArgumentType.word())
                        .executes(
                                context -> {

                                    String username = context.getArgument("username", String.class);
                                    String action = context.getArgument("action", String.class);
                                    String rank = context.getArgument("rank", String.class);

                                    LuckPerms luckPerms = BoosterAPI.get().getLuckPerms();
                                    User user = luckPerms.getUserManager().getUser(username); //TODO test if this works with username

                                    if (user == null) {
                                        context.getSource().sendMessage(miniMessage.parse(
                                                Config.INVALID_USER,
                                                Template.of("player", username)));
                                        return 1;
                                    }

                                    if (!Config.donorRanks.contains(context.getInput())) {
                                        context.getSource().sendMessage(miniMessage.parse(
                                                Config.INVALID_DONOR_RANK,
                                                Template.of("rank", rank)));
                                        return 1;
                                    }

                                    switch (action) {
                                        case "promote" -> promote(user, rank);
                                        case "demote" -> demote(user, rank);
                                        default -> context.getSource().sendMessage(miniMessage.parse(Config.INVALID_ACTION));
                                    }
                                    return 1;
                                }
                        ))
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }

    private void promote(User user, String rank) {
        user.getNodes(NodeType.INHERITANCE).stream()
                .filter(Node::getValue)
                .forEach(node -> {
                    if (Config.donorRanks.contains(node.getKey()))
                        user.data().remove(node);
                });
        user.data().add(InheritanceNode.builder(rank).build());
        VelocityBoosters.getPlugin().getProxy().getPlayer(user.getUniqueId()).ifPresent(player -> {
            if (player.isActive()) {
                player.sendMessage(miniMessage.parse(Config.PROMOTE_MESSAGE,
                        Template.of("rank", Utils.capitalize(rank)),
                        Template.of("player", player.getUsername())));
            }
        });
    }

    private void demote(User user, String rank) {
        user.getNodes(NodeType.INHERITANCE).stream()
                .filter(Node::getValue)
                .forEach(node -> {
                    if (Config.donorRanks.contains(node.getKey()))
                        user.data().remove(node);
                });
        VelocityBoosters.getPlugin().getProxy().getPlayer(user.getUniqueId()).ifPresent(player -> {
            if (player.isActive()) {
                player.sendMessage(miniMessage.parse(Config.DEMOTE_MESSAGE,
                        Template.of("rank", Utils.capitalize(rank)),
                        Template.of("player", player.getUsername())));
            }
        });
    }
}
