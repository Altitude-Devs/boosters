package com.alttd.boosterapi.database;

import com.alttd.boosterapi.booster.Booster;
import com.alttd.boosterapi.util.ALogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Queries {

    public static void createTables() {
        List<String> tables = new ArrayList<>();
        /* // TODO check for missed values, fix types
        CREATE TABLE `boosters` (
            `uuid` VARCHAR(36) NOT NULL,
            `type` TEXT NOT NULL,
            `state` TEXT NOT NULL,
            `multiplier` INT NOT NULL,
            `duration` BIGINT NOT NULL,
            `activator` TEXT NOT NULL,
            `timeremaining` BIGINT NOT NULL,
            PRIMARY KEY (`uuid`)
        );
         */
        tables.add("CREATE TABLE IF NOT EXISTS `boosters` (`uuid` VARCHAR(36) NOT NULL,`type` TEXT NOT NULL,`state` TEXT NOT NULL,`multiplier` INT NOT NULL,`duration` BIGINT NOT NULL,`activator` TEXT NOT NULL,`timeremaining` BIGINT NOT NULL, PRIMARY KEY (`uuid`));");
        try {
            Connection connection = Database.getConnection();

            for (String query : tables) {
                connection.prepareStatement(query).execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveBooster(Booster booster) {
        String query = "INSERT INTO boosters (uuid, type, state, multiplier, duration, activator, timeremaining) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE state = ?, timeremaining = ?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            // 1 - 7 insert
            statement.setString(1, booster.getUUID().toString());
            statement.setString(2, booster.getType().getBoosterName());
            statement.setString(3, booster.getState().getName());
            statement.setInt(4, booster.getMultiplier());
            statement.setLong(5, booster.getDuration());
            statement.setString(6, booster.getActivator());
            statement.setLong(7, booster.getTimeRemaining());
            // 8 - 9 update
            statement.setString(8, booster.getState().getName());
            statement.setLong(9, booster.getTimeRemaining());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getNonFinishedBoosters() {
        try {
            String query = "SELECT * FROM boosters WHERE state <> finished";
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
//            statement.setString(1, boosterState);
            return statement.executeQuery();
        } catch (SQLException ex) {
            ALogger.fatal("Error loading booster", ex);
            return null;
        }
    }

}
