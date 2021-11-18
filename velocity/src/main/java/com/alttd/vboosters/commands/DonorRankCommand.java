package com.alttd.vboosters.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class DonorRankCommand {

    public DonorRankCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("donorrank")
                .requires(ctx -> ctx.hasPermission("command.proxy.donorrank"))
                .executes(context -> {
                    //EX: donorrank {username} promote archduke {transaction}
                    //File: {transaction}, cbccaa76-906d-458e-a24e-4b74322f2bb7, none -> archduke
                    //EX: donorrank {username} demote archduke {transaction}
                    //TODO command format: /donorrank user promote rank donate_id
                    //TODO store the command and before and after rank in a file
                    //TODO remove old donor ranks and add the new one

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
