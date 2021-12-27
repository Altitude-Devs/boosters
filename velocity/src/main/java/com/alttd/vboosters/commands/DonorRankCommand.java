package com.alttd.vboosters.commands;

import com.alttd.boosterapi.BoosterAPI;
import com.alttd.boosterapi.config.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.UUID;

public class DonorRankCommand {

    public DonorRankCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("donorrank")
                .requires(ctx -> ctx.hasPermission("command.proxy.donorrank"))
                .then(RequiredArgumentBuilder.argument("username", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("action", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("rank", StringArgumentType.word()))
                .then(RequiredArgumentBuilder.argument("transaction-id", StringArgumentType.word()))
                .executes(context -> {
                    //EX: donorrank {username} promote archduke {transaction}
                    //File: {transaction}, cbccaa76-906d-458e-a24e-4b74322f2bb7, none -> archduke
                    //EX: donorrank {username} demote archduke {transaction}
                    //TODO command format: /donorrank user promote rank donate_id
                    //TODO store the command and before and after rank in a file
                    //TODO remove old donor ranks and add the new one

                    if (!Config.donorRanks.contains(context.getInput())) //TODO validate group from command is donor
                        return 0;
                    LuckPerms luckPerms = BoosterAPI.get().getLuckPerms();
                    User user = luckPerms.getUserManager().getUser(UUID.fromString(context.getInput())); //TODO context.getInput needs to get uuid
                    if (user == null) {
                        context.getSource().sendMessage(MiniMessage.get().parse("string")); //TODO configurable message
                        return 0;
                    }
                    user.getNodes(NodeType.INHERITANCE).stream()
                            .filter(Node::getValue)
                            .forEach(node -> {
                                if (Config.donorRanks.contains(node.getKey()))
                                    user.data().remove(node);
                            });
                    user.data().add(InheritanceNode.builder(context.getInput()).build()); //TODO this needs to add the group from the command
                    //TODO command format: /donorrank user promote rank donate_id
                    //TODO get command and before and after rank from a file
                    //TODO remove the command and before and after rank from a file
                    //TODO remove current donor ranks and add the old one back (or remove them all if there was no old one)
//                    VelocityBoosters.getPlugin().getLogger().info(stuff);
                    return 1;
                })
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }
}
