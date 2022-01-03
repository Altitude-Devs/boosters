package com.alttd.boosterapi.config;

import com.alttd.boosterapi.util.ALogger;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public final class Config {
    private static final Pattern PATH_PATTERN = Pattern.compile("\\.");
    private static final String HEADER = "";

    private static File CONFIG_FILE;
    public static ConfigurationNode config;
    public static YamlConfigurationLoader configLoader;

    static int version;
    static boolean verbose;

    public static File CONFIGPATH;
    public static void init() {
        CONFIGPATH = new File(System.getProperty("user.home") + File.separator + "share" + File.separator + "configs" + File.separator + "Boosters");
        CONFIG_FILE = new File(CONFIGPATH, "config.yml");
        configLoader = YamlConfigurationLoader.builder()
                .file(CONFIG_FILE)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        if (!CONFIG_FILE.getParentFile().exists()) {
            if(!CONFIG_FILE.getParentFile().mkdirs()) {
                return;
            }
        }
        if (!CONFIG_FILE.exists()) {
            try {
                if(!CONFIG_FILE.createNewFile()) {
                    return;
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        }

        try {
            config = configLoader.load(ConfigurationOptions.defaults().header(HEADER));
        } catch (IOException e) {
            e.printStackTrace();
        }

        verbose = getBoolean("verbose", true);
        version = getInt("config-version", 1);

        readConfig(Config.class, null);
        try {
            configLoader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException | IllegalAccessException ex) {
                        ALogger.fatal("Error reading config", ex);
                    }
                }
            }
        }
        saveConfig();
    }

    public static void saveConfig() {
        try {
            configLoader.save(config);
        } catch (IOException ex) {
            ALogger.fatal("Error saving config", ex);
        }
    }

    private static Object[] splitPath(String key) {
        return PATH_PATTERN.split(key);
    }

    private static void set(String path, Object def) {
        if(config.node(splitPath(path)).virtual()) {
            try {
                config.node(splitPath(path)).set(def);
            } catch (SerializationException e) {
            }
        }
    }

    private static void setString(String path, String def) {
        try {
            if(config.node(splitPath(path)).virtual())
                config.node(splitPath(path)).set(TypeToken.get(String.class), def);
        } catch(SerializationException ex) {
        }
    }

    private static boolean getBoolean(String path, boolean def) {
        set(path, def);
        return config.node(splitPath(path)).getBoolean(def);
    }

    private static double getDouble(String path, double def) {
        set(path, def);
        return config.node(splitPath(path)).getDouble(def);
    }

    private static int getInt(String path, int def) {
        set(path, def);
        return config.node(splitPath(path)).getInt(def);
    }

    private static String getString(String path, String def) {
        setString(path, def);
        return config.node(splitPath(path)).getString(def);
    }

    private static Long getLong(String path, Long def) {
        set(path, def);
        return config.node(splitPath(path)).getLong(def);
    }

    private static <T> List<String> getList(String path, T def) {
        try {
            set(path, def);
            return config.node(splitPath(path)).getList(TypeToken.get(String.class));
        } catch(SerializationException ex) {
        }
        return new ArrayList<>();
    }

    /** ONLY EDIT ANYTHING BELOW THIS LINE **/
    public static String driver = "com.mysql.cj.jdbc.Driver";
    public static String host = "13.11.1.78";
    public static String port = "3306";
    public static String database = "McTestSql";
    public static String user = "root";
    public static String password = "foobar";
    public static String options = "?MaxPooledStatements=250&useSSL=false&autoReconnect=true&maxReconnects=3";
    private static void databaseSettings() {
        String path = "database.";
        driver = getString(path + "driver", driver);
        host = getString(path + "host", host);
        port = getString(path + "port", port);
        database = getString(path + "database", database);
        user = getString(path + "user", user);
        password = getString(path + "password", password);
        options = getString(path + "options", options);
    }

    public static Long activeTaskCheckFrequency = 1L;
    public static Long taskCheckFrequency = 1L;
    private static void boosterTaskSettings() {
        activeTaskCheckFrequency = getLong("task.queue-frequency", activeTaskCheckFrequency);
        taskCheckFrequency = getLong("task.check-frequency", taskCheckFrequency);
    }

    public static String pluginMessageChannel = "altitude:boosterplugin";
    private static void pluginMessageSettings() {
        pluginMessageChannel = getString("settings.message-channel", pluginMessageChannel);
    }

    public static boolean announceInDiscord = false;
    public static String channelId = "776590138296893483";
    public static String messageTitle = "Booster Activated";
    public static String messageBody = "<user> activated booster of type <boostertype> for <time> hours.";
    private static void discordSettings() {
        announceInDiscord = getBoolean("settings.discord.enabled", announceInDiscord);
        channelId = getString("settings.discord.channel", channelId);
        messageTitle = getString("settings.discord.message-ttitle", messageTitle);
        messageBody = getString("settings.discord.message-body", messageBody);
    }

}
