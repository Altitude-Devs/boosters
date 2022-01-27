package com.alttd.boosterapi.database;

import com.alttd.boosterapi.Booster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Queries {

    public static void createTables() {
        List<String> tables = new ArrayList<>();
        /* // TODO check for missed values, fix types
        CREATE TABLE `boosters` (
            `uuid` VARCHAR(36) NOT NULL,
            `type` TEXT NOT NULL,
            `multiplier` INT NOT NULL,
            `duration` BIGINT NOT NULL,
            `activator` TEXT NOT NULL,
            `timeremaining` BIGINT NOT NULL,
            `finished` BIT NOT NULL'
            PRIMARY KEY (`uuid`)
        );
         */
        tables.add("CREATE TABLE `boosters` (`uuid` VARCHAR(36) NOT NULL,`type` TEXT NOT NULL,`multiplier` INT NOT NULL,`duration` BIGINT NOT NULL,`activator` TEXT NOT NULL,`timeremaining` BIGINT NOT NULL,`finished` BIT NOT NULL, PRIMARY KEY (`uuid`));");
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
        String query = "INSERT INTO boosters (uuid, type, multiplier, duraction, activator, timeremaining, finished) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE party_id = ?, toggled_channel = ?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            // 1 - 7 insert
            statement.setString(1, booster.getUUID().toString());
            statement.setString(2, booster.getType().getBoosterName());
            statement.setInt(3, booster.getMultiplier());
            statement.setLong(4, booster.getDuration());
            statement.setString(5, booster.getActivator());
            statement.setLong(6, booster.getTimeRemaining());
            statement.set(5, toggledChannel == null ? null : toggledChannel.getChannelName());
            // 8 - update
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
