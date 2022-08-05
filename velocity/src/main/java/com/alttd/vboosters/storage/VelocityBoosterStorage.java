package com.alttd.vboosters.storage;

import com.alttd.boosterapi.Booster;
import com.alttd.boosterapi.BoosterType;
import com.alttd.boosterapi.config.BoosterStorage;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.vboosters.data.VelocityBooster;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.UUID;

public class VelocityBoosterStorage extends BoosterStorage {

    private static VelocityBoosterStorage velocityBoosterStorage = null;

    public static VelocityBoosterStorage getVelocityBoosterStorage() {
        if (velocityBoosterStorage == null)
            velocityBoosterStorage = new VelocityBoosterStorage();
        return velocityBoosterStorage;
    }

    private VelocityBoosterStorage() {
        super();
    }

    @Override
    public Booster loadBooster(JsonParser parser) throws IOException {
        JsonToken jsonToken = parser.getCurrentToken();
        if (!jsonToken.isStructStart())
            return error("Didn't find struct start");

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"uuid".equals(parser.getCurrentName()))
            return error("Didn't find uuid at expected location");
        parser.nextValue();
        UUID uuid = UUID.fromString(parser.getValueAsString());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"activator".equals(parser.getCurrentName()))
            return error("Didn't find activator at expected location");
        parser.nextValue();
        String activator = parser.getValueAsString();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"type".equals(parser.getCurrentName()))
            return error("Didn't find type at expected location");
        parser.nextValue();
        BoosterType boosterType = BoosterType.getByName(parser.getValueAsString());

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"startingTime".equals(parser.getCurrentName()))
            return error("Didn't find startingTime at expected location");
        parser.nextValue();
        long startingTime = parser.getValueAsLong();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"duration".equals(parser.getCurrentName()))
            return error("Didn't find duration at expected location");
        parser.nextValue();
        long duration = parser.getValueAsLong();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"multiplier".equals(parser.getCurrentName()))
            return error("Didn't find multiplier at expected location");
        parser.nextValue();
        double multiplier = parser.getValueAsDouble();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"active".equals(parser.getCurrentName()))
            return error("Didn't find active at expected location");
        parser.nextValue();
        boolean active = parser.getValueAsBoolean();

        jsonToken = parser.nextToken();
        if (jsonToken != JsonToken.FIELD_NAME || !"finished".equals(parser.getCurrentName()))
            return error("Didn't find finished at expected location");
        parser.nextValue();
        boolean finished = parser.getValueAsBoolean();
        return new VelocityBooster(uuid, activator, boosterType, startingTime, duration, multiplier, active, finished);
    }

    private static Booster error(String error) {
        ALogger.error(error);
        return null;
    }
}