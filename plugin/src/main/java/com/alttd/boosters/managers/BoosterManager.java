package com.alttd.boosters.managers;

import com.alttd.boosterapi.booster.Booster;
import com.alttd.boosterapi.booster.BoosterState;
import com.alttd.boosterapi.booster.BoosterType;
import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.database.Database;
import com.alttd.boosterapi.database.Queries;
import com.alttd.boosterapi.util.ALogger;
import com.alttd.boosterapi.util.Utils;
import com.alttd.boosters.BoostersPlugin;
import com.alttd.boosters.data.IBooster;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BoosterManager {

    public BoosterManager() {
        activeBoosters = new TreeMap<>();
        loadAllBoosters();
    }

    private static Map<UUID, Booster> activeBoosters;

    public boolean isBoosted(BoosterType type) {
        for (Booster b : activeBoosters.values()) {
            if (b.getType() == type && b.isActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBoosters() {
        return !activeBoosters.isEmpty();
    }

    public Collection<Booster> getBoosters() {
        return activeBoosters.values();
    }

    public Booster getBooster(BoosterType type) {
        for (Booster b : activeBoosters.values()) {
            if (b.getType() == type && b.isActive()) {
                return b;
            }
        }
        return null;
    }

    public void addBooster(Booster queuedBooster) {
        BoosterType type = queuedBooster.getType();
        if(isBoosted(type)) {
            Booster activeBooster = getBooster(type);
            if (queuedBooster.getMultiplier() > activeBooster.getMultiplier()) {
                activeBoosters.put(queuedBooster.getUUID(), queuedBooster);
                activeBoosters.remove(activeBooster.getUUID());
                queuedBooster.updateState(BoosterState.ACTIVE);
                queuedBooster.setStartingTime(System.currentTimeMillis());
            }
        } else {
            activeBoosters.put(queuedBooster.getUUID(), queuedBooster);
            queuedBooster.updateState(BoosterState.ACTIVE);
            queuedBooster.setStartingTime(System.currentTimeMillis());
        }
    }

    public Booster getBooster(UUID uuid) {
        for (Booster b : activeBoosters.values()) {
            if (b.getUUID().equals(uuid)) {
                return b;
            }
        }
        return null;
    }

    public void removeBooster(UUID uuid) {
        Booster booster = getBooster(uuid);
        if (booster == null) return;
        activeBoosters.remove(booster.getUUID());/*.removeIf(booster -> booster.getUUID().equals(uuid));*/

        if (booster.getType() == null || booster.getActivator() == null || booster.getDuration() == null) return;
        List<Template> templates = new ArrayList<>(List.of(
                Template.template("type", booster.getType().getBoosterName()),
                Template.template("reason", booster.getActivator()),
                Template.template("duration", booster.getTimeDuration()),
                Template.template("multiplier", booster.getMultiplier()+"")));
        Bukkit.broadcast(Utils.parseMiniMessage(Config.STOPBOOSTER, templates));
    }

    public void loadBooster(String uuidString) {
        String query = "SELECT * FROM boosters WHERE uuid = ?";
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    Connection connection = Database.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, uuidString);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        //uuid, type, state, multiplier, duration, activator, timeremaining
                        UUID uuid = UUID.fromString(uuidString);
                        BoosterType boosterType = BoosterType.getByName(rs.getString("type"));
                        BoosterState boosterState = BoosterState.getByName(rs.getString("state"));
                        int multiplier = rs.getInt("multiplier");
//                        long duration = rs.getLong("duration");
                        String activator = rs.getString("activator");
                        long timeremaining = rs.getLong("timeremaining");
                        addBooster(new IBooster(uuid, boosterType, boosterState, activator, timeremaining, multiplier));
                    }
                } catch (SQLException ex) {
                    ALogger.fatal("Error loading booster", ex);
                }
            }
        }.runTaskAsynchronously(BoostersPlugin.getInstance());
    }

    public void loadAllBoosters() {
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    ResultSet rs = Queries.getNonFinishedBoosters();
                    if (rs == null) return;
                    while (rs.next()) {
                        //uuid, type, state, multiplier, duration, activator, timeremaining
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        BoosterType boosterType = BoosterType.getByName(rs.getString("type"));
                        BoosterState boosterState = BoosterState.getByName(rs.getString("state"));
                        int multiplier = rs.getInt("multiplier");
//                        long duration = rs.getLong("duration");
                        String activator = rs.getString("activator");
                        long timeremaining = rs.getLong("timeremaining");
                        addBooster(new IBooster(uuid, boosterType, boosterState, activator, timeremaining, multiplier));
                    }
                } catch (SQLException ex) {
                    ALogger.fatal("Error loading booster", ex);
                }
            }
        }.runTaskAsynchronously(BoostersPlugin.getInstance());
    }

}
