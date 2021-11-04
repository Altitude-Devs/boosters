package com.alttd.vboosters.commands;

import com.alttd.proxydiscordlink.bot.api.DiscordSendMessage;
import com.alttd.vboosters.VelocityBoosters;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class BoosterCommand {

    public BoosterCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("booster")
                .requires(ctx -> ctx.hasPermission("command.proxy.booster"))
                .executes(context -> {
                    String channelid = "776590138296893483";
                    String msg = "CONSOLEUSER activated booster of type BoosterType.MMMCALL for 48 hours.";
                    DiscordSendMessage.sendEmbed(Long.parseLong(channelid),"Booster Activated" , msg);
                    VelocityBoosters.getPlugin().getLogger().info(msg);
                    return 1;
                })
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }
}
