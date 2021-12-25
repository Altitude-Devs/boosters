package com.alttd.boosterapi.database;

import com.alttd.boosterapi.config.Config;
import com.alttd.boosterapi.util.ALogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(Config.driver);

                connection = DriverManager.getConnection("jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database + Config.options,  Config.user, Config.password);
            } catch (ClassNotFoundException | SQLException ex) {
                ALogger.fatal("Failed to connect to sql.", ex);
            }
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();

                connection = null;

            } catch (SQLException ex) {
                ALogger.fatal("Failed to disconnect from sql.", ex);
            }
        }
    }

}
