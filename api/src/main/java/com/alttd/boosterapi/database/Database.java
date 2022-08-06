package com.alttd.boosterapi.database;

import com.alttd.boosterapi.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Database instance;
    private static Connection connection;

    /**
     * Sets information for the database and opens the connection.
     */
    public Database() {
        instance = this;

        try {
            instance.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the connection if it's not already open.
     * @throws SQLException If it can't create the connection.
     */
    public void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database + "?autoReconnect=true"+
                            "&useSSL=false",
                    Config.user, Config.password);
        }
    }

    /**
     * Returns the connection for the database
     * @return Returns the connection.
     */
    public static Connection getConnection() {
        try {
            instance.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Sets the connection for this instance
     */
    public static boolean initialize() {
        instance = new Database();
        return connection != null;
    }

}