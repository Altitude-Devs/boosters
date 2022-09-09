package com.alttd.vboosters.commands;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.Utils;
import com.alttd.proxydiscordlink.bot.api.DiscordSendMessage;
import com.alttd.proxydiscordlink.lib.net.dv8tion.jda.api.entities.templates.Template;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.data.VelocityBooster;
import com.alttd.vboosters.managers.BoosterManager;
import com.alttd.vboosters.storage.VelocityBoosterStorage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

    private static MiniMessage miniMessage = MiniMessage.miniMessage();
    public BoosterCommand(ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder
                .<CommandSource>literal("booster")
                .requires(ctx -> ctx.hasPermission("command.proxy.booster"))
                .executes(context -> { //TODO put these messages in config
                    String message = "Active boosters:\n<active_boosters>\n\nQueued boosters:\n<queued_boosters>";
                    String activeBooster = "<type> activated by <activator> until <end_time> [UTC], boosts <multiplier> times";
                    String queuedBooster = "<type> queued by <activator> starts at <start_time> [UTC] and will be active for <duration>, boosts <multiplier> times";
                    List<Component> activeBoosterComponents = new ArrayList<>();
                    List<Component> queuedBoosterComponents = new ArrayList<>();
                    for (Booster booster : VelocityBoosterStorage.getVelocityBoosterStorage().getBoosters().values()) {
                        long expiryTime = new Date().getTime() + booster.getDuration();
                        TagResolver.Builder tagResolver = TagResolver.builder();

                        List<TagResolver> templates = List.of(
                                Placeholder.unparsed("type", booster.getType().getBoosterName()),
                                Placeholder.unparsed("activator", booster.getActivator()),
                                Placeholder.unparsed("start_time", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(booster.getStartingTime())),
                                Placeholder.unparsed("duration", TimeUnit.MILLISECONDS.toHours(booster.getDuration()) + " hours"),
                                Placeholder.unparsed("multiplier", String.valueOf(booster.getMultiplier())));
                        for (TagResolver tagResolver1 : templates)
                            tagResolver.resolver(tagResolver1); // cheaty and lazy way I know

                        if (booster.isActive())
                            templates.add(Template.of("end_time", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(expiryTime)));
                        else
                            templates.add(Template.of("end_time", "unknown"));
                        if (booster.isActive())
                            activeBoosterComponents.add(miniMessage.parse(activeBooster, templates));
                        else if (!booster.finished())
                            queuedBoosterComponents.add(miniMessage.parse(queuedBooster, templates));
                    }
                    Component separator = miniMessage.parse("\n");
                    context.getSource().sendMessage(miniMessage.parse(message, List.of(
                            Template.of("active_boosters", Component.join(separator, activeBoosterComponents)),
                            Template.of("queued_boosters", Component.join(separator, queuedBoosterComponents))
                    )));
                    return 1;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .requires(ctx -> ctx.hasPermission("command.proxy.booster.manage"))
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
                                            .executes(context -> { //TODO make messages configurable
                                                String username = context.getArgument("username", String.class);
                                                BoosterType boosterType = BoosterType.getByName(context.getArgument("booster", String.class));
                                                long duration = TimeUnit.MINUTES.toMillis(context.getArgument("time", Integer.class));
                                                double multiplier = context.getArgument("multiplier", Double.class);
                                                if (boosterType.equals(BoosterType.MCMMO))
                                                    addAllMcMMOBoosters(username, duration, multiplier);
                                                else
                                                    VelocityBoosters.getPlugin().getBoosterManager().addBooster(new VelocityBooster(boosterType, username, duration, multiplier));
                                                String msg = "[" + username + "] purchased booster of type [" + Utils.capitalize(boosterType.getBoosterName()) + "]"; //TODO check if there was a booster active already and change message based on that
                                                DiscordSendMessage.sendEmbed(Config.BOOST_ANNOUNCE_CHANNEL, "Booster Purchased", msg);
                                                VelocityBoosters.getPlugin().getProxy().sendMessage(MiniMessage.miniMessage().deserialize(msg));
                                                VelocityBoosters.getPlugin().getLogger().info(msg);
                                                return 1;
                                            })
                                        )
                                )
                        )
                )
//                .executes(context -> 1)
                .build();

        BrigadierCommand brigadierCommand = new BrigadierCommand(command);

        CommandMeta.Builder metaBuilder = proxyServer.getCommandManager().metaBuilder(brigadierCommand);

        CommandMeta meta = metaBuilder.build();

        proxyServer.getCommandManager().register(meta, brigadierCommand);
    }

    private void addAllMcMMOBoosters(String username, long duration, double multiplier) {
        BoosterManager boosterManager = VelocityBoosters.getPlugin().getBoosterManager();
        for (BoosterType boosterType : BoosterType.getAllMcMMOBoosters()) {
            boosterManager.addBooster(new VelocityBooster(boosterType, username, duration, multiplier));
        }
    }
}
