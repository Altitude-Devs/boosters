package com.alttd.boosterapi.data;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.Objects;

public class Booster implements Comparable<Booster> {

    private final UUID boosterUUID;
    private final String activatorName;
    private Instant startingTime;
    private Duration duration;
    private final BoosterType boosterType;
    private final Double multiplier;

    public Booster(UUID boosterUUID, BoosterType boosterType, String reason, Duration duration, double multiplier) {
        this.boosterUUID = boosterUUID;
        this.boosterType = boosterType;
        this.activatorName = reason;
        this.duration = duration;
        this.multiplier = multiplier;
        this.startingTime = Instant.now();
    }

    public Booster(BoosterType type, String playerName, Duration duration, double multiplier) {
        this(UUID.randomUUID(), type, playerName, duration, multiplier);
    }

    public Booster(UUID boosterUUID, String activatorName, BoosterType boosterType, Instant startingTime,
                   Duration duration, double multiplier) {
        this.boosterUUID = boosterUUID;
        this.activatorName = activatorName;
        this.boosterType = boosterType;
        this.startingTime = startingTime;
        this.duration = duration;
        this.multiplier = multiplier;
    }

    public void updateDuration() {
        Instant stopTime = Instant.now();
        Duration elapsedTime = Duration.between(startingTime, stopTime);
        duration = duration.minus(elapsedTime);
    }

    public double useMultiplier(double exp) {
        return exp * (multiplier + 1);
    }

    public UUID getBoosterUUID() {
        return boosterUUID;
    }

    public String getActivatorName() {
        return activatorName;
    }

    public Instant getStartingTime() {
        return startingTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public BoosterType getBoosterType() {
        return boosterType;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Booster other = (Booster) o;
        return Objects.equals(boosterUUID, other.boosterUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boosterUUID);
    }

    @Override
    public int compareTo(@NotNull Booster other) {
        int multiplierComparison = Double.compare(other.multiplier, this.multiplier);
        if (multiplierComparison != 0) {
            return multiplierComparison;
        }

        return this.duration.compareTo(other.duration);
    }

    @Override
    public String toString() {
        return "Booster{" +
                "boosterUUID=" + boosterUUID +
                ", activatorName='" + activatorName + '\'' +
                ", startingTime=" + startingTime +
                ", duration=" + duration +
                ", boosterType=" + boosterType +
                ", multiplier=" + multiplier +
                '}';
    }
}
