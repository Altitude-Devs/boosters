package com.alttd.boosterapi.config;

import com.alttd.boosterapi.data.Booster;
import com.alttd.boosterapi.data.BoosterType;
import com.alttd.boosterapi.util.Logger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BoosterFileStorage {

    private File CONFIG_FILE;
    private final Logger logger;
    public BoosterFileStorage(Logger logger) {
        this.logger = logger;
        logger.info("Preparing booster file storage...");
        init();
    }

    private void init() {
        File CONFIG_PATH = new File(System.getProperty("user.home") + File.separator + "share" + File.separator + "configs" + File.separator + "Boosters");
        if (!CONFIG_PATH.exists()) {
            if (!CONFIG_PATH.mkdir())
                logger.severe("Unable to create json storage directory");
        }
        CONFIG_FILE = new File(CONFIG_PATH, "storage.json");
        if (!CONFIG_FILE.exists()) {
            try {
                if (!CONFIG_FILE.createNewFile())
                    logger.severe("Unable to create json storeage file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public synchronized List<Booster> reload() {
        logger.debug("Reloading boosters...");
        return loadBoosters();
    }

    private List<Booster> loadBoosters()  {
        List<Booster> boosters = new LinkedList<>();

        try {
            JsonParser parser = new JsonFactory().createParser(CONFIG_FILE);
            if (parser == null) {
                logger.warning("Unable to load in boosters from storage file.");
                return boosters;
            }
            parser.nextToken();
            while (parser.currentToken() != null && parser.currentToken().isStructStart()) {
                Optional<Booster> optionalBooster = loadBooster(parser);
                if (optionalBooster.isEmpty())
                    continue;
                Booster booster = optionalBooster.get();
                logger.debug("Loading booster:" + booster.getBoosterType());
                boosters.add(booster);
                if (parser.nextToken() != null && !parser.currentToken().isStructEnd()) {
                    logger.warning("Last loaded booster had more data than expected, skipping it...");
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

    public Optional<Booster> loadBooster(JsonParser parser) throws IOException {
        JsonToken jsonToken = parser.getCurrentToken();
        if (!jsonToken.isStructStart())
            return error("Didn't find struct start");

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"boosterUUID".equals(parser.getCurrentName()))
            return error("Didn't find boosterUUID at expected location");
        parser.nextValue();
        UUID boosterUUID = UUID.fromString(parser.getValueAsString());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"activatorName".equals(parser.getCurrentName()))
            return error("Didn't find activatorName at expected location");
        parser.nextValue();
        String activatorName = parser.getValueAsString();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"boosterType".equals(parser.getCurrentName()))
            return error("Didn't find boosterType at expected location");
        parser.nextValue();
        BoosterType boosterType = BoosterType.getByName(parser.getValueAsString());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"startingTime".equals(parser.getCurrentName()))
            return error("Didn't find startingTime at expected location");
        parser.nextValue();
        Instant startingTime = Instant.ofEpochMilli(parser.getValueAsLong());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"duration".equals(parser.getCurrentName()))
            return error("Didn't find duration at expected location");
        parser.nextValue();
        Duration duration = Duration.ofMillis(parser.getValueAsLong());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"multiplier".equals(parser.getCurrentName()))
            return error("Didn't find multiplier at expected location");
        parser.nextValue();
        double multiplier = parser.getValueAsDouble();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"running".equals(parser.getCurrentName()))
            return error("Didn't find running at expected location");
        parser.nextValue();
        boolean running = parser.getValueAsBoolean();
        parser.nextValue();

        return Optional.of(new Booster(boosterUUID, activatorName, boosterType, startingTime, duration, multiplier, running));
    }

    private Optional<Booster> error(String error) {
        logger.severe(error);
        return Optional.empty();
    }

    public synchronized void saveBoosters(List<Booster> boosters) {
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

        generator.writeStringField("boosterUUID", booster.getBoosterUUID().toString());
        generator.writeStringField("activatorName", booster.getActivatorName());
        generator.writeStringField("boosterType", booster.getBoosterType().getBoosterName());
        generator.writeNumberField("startingTime", booster.getStartingTime().toEpochMilli());
        generator.writeNumberField("duration", booster.getDuration().toMillis());
        generator.writeNumberField("multiplier", booster.getMultiplier());
        generator.writeBooleanField("running", booster.getRunning());

        generator.writeEndObject();
    }
}
