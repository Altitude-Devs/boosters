package com.alttd.boosterapi.data;

import com.alttd.boosterapi.config.BoosterFileStorage;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoosterCache {

    private final HashMap<BoosterType, LinkedList<Booster>> boosters = new HashMap<>();
    private final BoosterFileStorage boosterFileStorage;

    public BoosterCache(BoosterFileStorage boosterFileStorage) {
        this.boosterFileStorage = boosterFileStorage;
        reloadBoosters();
    }

    public synchronized void reloadBoosters() {
        boosters.clear();
        List<Booster> allBoosters = boosterFileStorage.reload();
        for (Booster booster : allBoosters) {
            LinkedList<Booster> list = boosters.getOrDefault(booster.getBoosterType(), new LinkedList<>());
            list.add(booster);
            boosters.put(booster.getBoosterType(), list);
        }
        updateOrder();
    }

    private void updateOrder() {
        for (BoosterType boosterType : boosters.keySet()) {
            updateOrder(boosterType);
        }
    }

    private void updateOrder(BoosterType boosterType) {
        if (!boosters.containsKey(boosterType)) {
            return;
        }
        LinkedList<Booster> list = boosters.get(boosterType);
        list.sort(Booster::compareTo);
    }

    public synchronized Optional<Booster> getActiveBooster(BoosterType boosterType) {
        if (!boosters.containsKey(boosterType))
            return Optional.empty();
        LinkedList<Booster> list = boosters.get(boosterType);
        if (list.isEmpty())
            return Optional.empty();
        return Optional.of(list.get(0));
    }

    public synchronized List<Booster> getAllActiveBoosters() {
        return boosters.values().stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .collect(Collectors.toList());
    }

    public synchronized List<Booster> getAllQueuedBoosters() {
        return boosters.values().stream()
                .filter(list -> list.size() > 1)
                .map(list -> list.subList(1, list.size()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public synchronized void addNewBooster(BoosterType boosterType, String activatorName, Duration duration, double multiplier) {
        List<BoosterType> childBoosters = boosterType.getChildBoosters();
        if (!childBoosters.isEmpty()) {
            addNewBoosters(childBoosters, activatorName, duration, multiplier);
            return;
        }
        Booster booster = new Booster(boosterType, activatorName, duration, multiplier);
        LinkedList<Booster> list = boosters.getOrDefault(boosterType, new LinkedList<>());
        list.addLast(booster);
        boosters.put(boosterType, list);
        updateOrder(boosterType);
        boosterFileStorage.saveBoosters(boosters.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private void addNewBoosters(List<BoosterType> boosterTypes, String activatorName, Duration duration, double multiplier) {
        for (BoosterType boosterType : boosterTypes) {
            Booster booster = new Booster(boosterType, activatorName, duration, multiplier);
            LinkedList<Booster> list = boosters.getOrDefault(boosterType, new LinkedList<>());
            list.addLast(booster);
            boosters.put(boosterType, list);
            updateOrder(boosterType);
        }
        boosterFileStorage.saveBoosters(boosters.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    public synchronized void finishBooster(Booster booster) {
        BoosterType boosterType = booster.getBoosterType();
        LinkedList<Booster> list = boosters.get(boosterType);
        if (list == null)
            return;
        list.removeIf(filterBooster -> filterBooster.getBoosterUUID().equals(booster.getBoosterUUID()));
        boosters.put(boosterType, list);
        updateOrder(boosterType);
        boosterFileStorage.saveBoosters(boosters.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    public synchronized void updateAndSave() {
        boosterFileStorage.saveBoosters(boosters.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }
}
