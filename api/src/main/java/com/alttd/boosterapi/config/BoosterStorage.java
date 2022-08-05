package com.alttd.boosterapi.config;

import com.alttd.boosterapi.Booster;
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

public abstract class BoosterStorage {

    private File CONFIG_FILE;
    private final Map<UUID, Booster> boosters;
    protected BoosterStorage() {
        ALogger.info("Loading boosters...");
        init();
        boosters = loadBoosters();
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
        boosters.clear();
        boosters.putAll(loadBoosters());
    }

    public Map<UUID, Booster> getBoosters() {
        return boosters;
    }

    public Map<UUID, Booster> loadBoosters()  {
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

    public void saveBoosters(Collection<Booster> boosters) {
        try {
            JsonGenerator generator = new JsonFactory().createGenerator(CONFIG_FILE, JsonEncoding.UTF8);
            for (Booster booster : boosters) {
                saveBooster(booster, generator);
            }
            generator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

}