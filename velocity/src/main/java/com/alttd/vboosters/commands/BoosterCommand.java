package com.alttd.vboosters.commands;

import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Utils;
import com.alttd.proxydiscordlink.bot.api.DiscordSendMessage;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.VelocityBooster;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BoosterCommand {

    CompletableFuture<Suggestions> buildRemainingString(SuggestionsBuilder builder, Collection<String> possibleValues) {
        if (possibleValues.isEmpty())
            return Suggestions.empty();

        String remaining = builder.getRemaining().toLowerCase();
        for (String str : possibleValues) {
            if (str.toLowerCase().startsWith(remaining)) {
                builder.suggest(StringArgumentType.escapeIfRequired(str));
            }
        }

        return builder.buildFuture();
    }

    public BoosterCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("booster")
                .requires(ctx -> ctx.hasPermission("command.proxy.booster"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .suggests((context, builder) -> buildRemainingString(builder, proxyServer.getAllPlayers().stream()
                                .map(Player::getGameProfile)
                                .map(GameProfile::getName)
                                .collect(Collectors.toList())))
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("booster", StringArgumentType.string())
                                .suggests((context, builder) -> buildRemainingString(builder, Arrays.stream(BoosterType.values())
                                        .map(BoosterType::getBoosterName)
                                        .collect(Collectors.toList())))
                                .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("time", IntegerArgumentType.integer(0, 525960))
                                        .suggests((context, builder) -> buildRemainingString(builder, List.of("60", "120", "180", "240", "300", "360",
                                                "420", "480", "540", "600", "660", "720", "780", "840", "900", "960", "1020", "1080", "1140", "1200", "1260", "1320", "1380", "1440")))
                                        .then(RequiredArgumentBuilder.<CommandSource, Double>argument("multiplier", DoubleArgumentType.doubleArg(0, 10))
                                                .suggests((context, builder) -> buildRemainingString(builder, List.of("0.5", "1", "1.5", "2")))
                                            .executes(context -> {
                                                String username = context.getArgument("username", String.class);
                                                BoosterType boosterType = BoosterType.getByName(context.getArgument("booster", String.class));
                                                long duration = context.getArgument("time", Integer.class) * 60;
                                                double multiplier = context.getArgument("multiplier", Double.class);
                                                VelocityBoosters.getPlugin().getBoosterManager().addBooster(new VelocityBooster(boosterType, username, duration, multiplier));
                                                long expiryTime = new Date().getTime() + duration;
                                                String msg = "[" + username + "] activated booster of type [" + Utils.capitalize(boosterType.getBoosterName()) + "] until %date%."; //TODO check if there was a booster active already and change message based on that
                                                DiscordSendMessage.sendEmbed(Config.BOOST_ANNOUNCE_CHANNEL, "Booster Activated", msg.replaceAll("%date%", "<t:" + expiryTime + ":f>"));
                                                String mcMessage = msg.replaceAll("%date%", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(expiryTime));
                                                VelocityBoosters.getPlugin().getProxy().sendMessage(MiniMessage.markdown().parse(mcMessage));
                                                VelocityBoosters.getPlugin().getLogger().info(mcMessage);
                                                return 1;
                                            })
                                        )
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
}
