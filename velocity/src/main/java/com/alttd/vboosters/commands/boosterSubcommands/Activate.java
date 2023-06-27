package com.alttd.vboosters.commands.boosterSubcommands;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.data.BoosterCache;
import com.alttd.boosterapi.data.BoosterType;
import com.alttd.boosterapi.util.Logger;
import com.alttd.boosterapi.util.StringModifier;
import com.alttd.proxydiscordlink.bot.api.DiscordSendMessage;
import com.alttd.vboosters.VelocityBoosters;
import com.alttd.vboosters.commands.Command;
import com.alttd.vboosters.commands.SubCommand;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Activate extends Command implements SubCommand {

    private final ProxyServer proxyServer;
    private final BoosterCache boosterCache;
    private final Logger logger;

    public Activate(ProxyServer proxyServer, BoosterCache boosterCache, Logger logger) {
        this.proxyServer = proxyServer;
        this.boosterCache = boosterCache;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return "activate";
    }

    @Override
    public void execute(String[] args, CommandSource source) {
        if (args.length != 5) {
            source.sendMessage(parseMessage(getHelpMessage()));
            return;
        }
        String activatorName = args[1];
        BoosterType boosterType = BoosterType.getByName(args[2]);
        Duration duration;
        try {
            int minuteDuration = Integer.parseInt(args[3]);
            duration = Duration.ofMinutes(minuteDuration);
        } catch (NumberFormatException e) {
            source.sendMessage(parseMessage(getHelpMessage()));
            return;
        }
        double multiplier;

        try {
            multiplier = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            source.sendMessage(parseMessage(getHelpMessage()));
            return;
        }

        boosterCache.addNewBooster(boosterType, activatorName, duration, multiplier);

        String boosterName = StringModifier.capitalize(boosterType.getBoosterName());
        String msg = "[" + activatorName + "] purchased booster of type [" + boosterName + "]"; //Add to config for discord only

        DiscordSendMessage.sendEmbed(Config.SETTINGS.BOOST_ANNOUNCE_CHANNEL, "Booster Purchased", msg);
        VelocityBoosters.getPlugin().getProxy().sendMessage(parseMessage(Config.BOOSTER_MESSAGES.BOOST_SERVER_MESSAGE,
                Placeholder.unparsed("player", activatorName), Placeholder.unparsed("booster", boosterName)));
        logger.info(msg);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("reload");
        VelocityBoosters.getPlugin().getProxy().getAllServers()
                .forEach(registeredServer -> registeredServer.sendPluginMessage(VelocityBoosters.getPlugin().getChannelIdentifier(), out.toByteArray()));
    }

    @Override
    public List<String> suggest(String[] args, CommandSource source) {
        switch (args.length) {
            case 2 -> {
                return proxyServer.getAllPlayers().stream()
                        .map(Player::getUsername)
                        .collect(Collectors.toList());
            }
            case 3 -> {
                return Arrays.stream(BoosterType.values())
                        .map(BoosterType::getBoosterName)
                        .collect(Collectors.toList());
            }
            case 4 -> {
                return IntStream.iterate(60, i -> i <= 1440, i -> i + 60)
                        .boxed()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
            case 5 -> {
                return List.of("0.5", "1", "1.5", "2");
            }
            default -> {
                return List.of();
            }
        }
    }

    @Override
    public String getHelpMessage() {
        return "<red>Invalid arg length</red>"; //TODO implement
    }
}
