package com.alttd.boosterapi.config;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.util.ALogger;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BoosterStorage {

    private File CONFIG_FILE;
    private final Map<UUID, Booster> boosters;
    protected BoosterStorage() {
        ALogger.info("Loading boosters...");
        init();
        boosters = loadBoosters();
        if (Config.DEBUG) {
            for (Booster value : boosters.values()) {
                ALogger.info(value.getType().BoosterName);
            }
        }
    }
    private void init() {
        File CONFIG_PATH = new File(System.getProperty("user.home") + File.separator + "share" + File.separator + "configs" + File.separator + "Boosters");
        if (!CONFIG_PATH.exists()) {
            if (!CONFIG_PATH.mkdir())
                ALogger.error("Unable to create json storage directory");
        }
        CONFIG_FILE = new File(CONFIG_PATH, "storage.json");
        if (!CONFIG_FILE.exists()) {
            try {
                if (!CONFIG_FILE.createNewFile())
                    ALogger.error("Unable to create json storeage file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void reload() {
        if (Config.DEBUG)
            ALogger.info("Reloading boosters...");
        boosters.clear();
        boosters.putAll(loadBoosters());
    }

    public synchronized Map<UUID, Booster> getBoosters() {
        return boosters;
    }

    public synchronized Map<UUID, Booster> loadBoosters()  {
        Map<UUID, Booster> boosters = new HashMap<>();

        try {
            JsonParser parser = new JsonFactory().createParser(CONFIG_FILE);
            if (parser == null) {
                ALogger.warn("Unable to load in boosters from storage file.");
                return boosters;
            }
            parser.nextToken();
            while (parser.currentToken() != null && parser.currentToken().isStructStart()) {
                Booster booster = loadBooster(parser);
                if (Config.DEBUG)
                    ALogger.info("Loading booster [" + booster.getType() + "] activated by [" + booster.getActivator()+ "].");
                if (booster.getTimeRemaining() < 1)
                    continue;
                boosters.put(booster.getUUID(), booster);
                if (parser.nextToken() != null && !parser.currentToken().isStructEnd()) {
                    ALogger.warn("Last loaded booster had more data than expected, skipping it...");
                    while (!parser.nextToken().isStructEnd())
                        ;
                }
                parser.nextToken();
            }
            parser.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return boosters;
    }

    public abstract Booster loadBooster(JsonParser parser) throws IOException;

    public synchronized void saveBoosters(Collection<Booster> boosters) {
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(CONFIG_FILE, JsonEncoding.UTF8);
            Date date = new Date();
            for (Booster booster : boosters) {
                if (booster.finished() || (booster.isActive() && new Date(booster.getEndTime()).before(date)))
                    continue;
                saveBooster(booster, generator);
            }
            generator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveBoosters() {
        saveBoosters(boosters.values());
    }

    private void saveBooster(Booster booster, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("uuid", booster.getUUID().toString());
        generator.writeStringField("activator", booster.getActivator());
        generator.writeStringField("type", booster.getType().getBoosterName());
        generator.writeNumberField("startingTime", booster.getStartingTime());
        generator.writeNumberField("duration", booster.getDuration());
        generator.writeNumberField("multiplier", booster.getMultiplier());
        generator.writeBooleanField("active", booster.isActive());
        generator.writeBooleanField("finished", booster.finished());

        generator.writeEndObject();
    }

    public synchronized Collection<Booster> getBoosters(BoosterType type) {
        return boosters.values().stream().filter(booster -> booster.getType().equals(type)).collect(Collectors.toList());
    }

    public synchronized void add(Booster booster) {
        boosters.put(booster.getUUID(), booster);
    };
}
