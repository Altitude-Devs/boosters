package com.alttd.boosterapi.config;

import com.alttd.boosterapi.util.Logger;

import java.io.File;
import java.util.List;

public final class Config extends AbstractConfig {

    private static Config config;

    Config(Logger logger) {
        super(
                new File(System.getProperty("user.home") + File.separator
                        + "share" + File.separator
                        + "configs" + File.separator
                        + "Boosters"),
                "config.yml", logger, Config.class);
    }

    public static void reload(Logger logger) {
        config = new Config(logger);
        config.readConfig(Config.class, null);
    }

    public static class LOGGING {
        private static final String prefix = "logging.";

        public static boolean PRINT_DEBUG = false;
        public static boolean PRINT_WARNINGS = true;

        private static void load() {
            PRINT_DEBUG = config.getBoolean(prefix, "print-debug", PRINT_DEBUG);
            PRINT_WARNINGS = config.getBoolean(prefix, "print-warnings", PRINT_WARNINGS);
        }
    }

    public static class SETTINGS {
        private static final String prefix = "settings.";

        public static int UPDATE_FREQUENCY_MINUTES = 1;
        public static String PLUGIN_MESSAGE_CHANNEL = "altitude:boosterplugin";
        public static long BOOST_ANNOUNCE_CHANNEL = -1L;
        public static List<String> DONOR_RANKS = List.of("count", "viceroy", "duke", "archduke");

        private static void load() {
            UPDATE_FREQUENCY_MINUTES = config.getInt(prefix, "update-frequency-minutes", UPDATE_FREQUENCY_MINUTES);
            BOOST_ANNOUNCE_CHANNEL = config.getLong(prefix, "boost-announce-channel", BOOST_ANNOUNCE_CHANNEL);
            PLUGIN_MESSAGE_CHANNEL = config.getString(prefix, "plugin-message-channel", PLUGIN_MESSAGE_CHANNEL);
//            DONOR_RANKS = config.getStringList(prefix, "donor-ranks", DONOR_RANKS);
        }
    }

    public static class GENERIC_MESSAGES {
        private static final String prefix = "generic-messages";

        public static String NO_PERMISSION = "<red>You do not have permission to do that command.</red>";
        public static String PLAYER_NOT_FOUND = "<red><player> is not a valid player.</red>";
        public static String RELOADED = "<green>Reloaded config.</green>";

        private static void load() {
            NO_PERMISSION = config.getString(prefix, "no-permission", NO_PERMISSION);
            PLAYER_NOT_FOUND = config.getString(prefix, "player-not-found", PLAYER_NOT_FOUND);
            RELOADED = config.getString(prefix, "reloaded", RELOADED);
        }
    }

    public static class BOOSTER_MESSAGES {
        private static final String prefix = "booster-messages";

        public static String LIST_BOOSTER_MESSAGE = "Active boosters:\n<active_boosters>\n\nQueued boosters:\n<queued_boosters>";
        public static String ACTIVE_BOOSTER_PART = "<type> activated by <activator> until <end_time> [UTC], boosts <multiplier> times";
        public static String QUEUED_BOOSTER_PART = "<type> queued by <activator> will be active for <duration>, boosts <multiplier> times";
        public static String BOOST_SERVER_MESSAGE = "<light_purple>* <player> activated an <dark_purple><booster></dark_purple> booster!</light_purple>";

        private static void load() {
            LIST_BOOSTER_MESSAGE = config.getString(prefix, "list-booster-message", LIST_BOOSTER_MESSAGE);
            ACTIVE_BOOSTER_PART = config.getString(prefix, "active-booster-part", ACTIVE_BOOSTER_PART);
            QUEUED_BOOSTER_PART = config.getString(prefix, "queued-booster-part", QUEUED_BOOSTER_PART);
            BOOST_SERVER_MESSAGE = config.getString(prefix, "boost-server-message", BOOST_SERVER_MESSAGE);
        }
    }

    public static class DONOR_RANK_MESSAGES {
        private static final String prefix = "donor-rank-messages";

        public static String INVALID_USER = "<red><player> does not exist.</red>";
        public static String INVALID_ACTION = "<red><action> is not a valid action user promote or demote.</red>";
        public static String INVALID_DONOR_RANK = "<red><rank> is not a valid donor rank.</red>";
        public static String DEMOTE_MESSAGE = "<red>Your <rank> rank was refunded and removed. Contact staff if you're unsure what caused this.</red>";
        public static String PROMOTE_MESSAGE = "<green>Thank you for your support! We applied the <rank> rank to your account.</green>";

        private static void load() {
            INVALID_USER = config.getString(prefix, "invalid-user", INVALID_USER);
            INVALID_ACTION = config.getString(prefix, "invalid-action", INVALID_ACTION);
            INVALID_DONOR_RANK = config.getString(prefix, "invalid-donor-rank", INVALID_DONOR_RANK);
            DEMOTE_MESSAGE = config.getString(prefix, "demote-message", DEMOTE_MESSAGE);
            PROMOTE_MESSAGE = config.getString(prefix, "promote-message", PROMOTE_MESSAGE);
        }
    }

//    private static final Pattern PATH_PATTERN = Pattern.compile("\\.");
//    private static final String HEADER = "";
//
//    private static File CONFIG_FILE;
//    public static ConfigurationNode config;
//    public static YamlConfigurationLoader configLoader;
//
//    static int version;
//    static boolean verbose;
//
//    public static File CONFIGPATH;
//    public static void init() {
//        CONFIGPATH = new File(System.getProperty("user.home") + File.separator + "share" + File.separator + "configs" + File.separator + "Boosters");
//        CONFIG_FILE = new File(CONFIGPATH, "config.yml");
//        configLoader = YamlConfigurationLoader.builder()
//                .file(CONFIG_FILE)
//                .nodeStyle(NodeStyle.BLOCK)
//                .build();
//        if (!CONFIG_FILE.getParentFile().exists()) {
//            if(!CONFIG_FILE.getParentFile().mkdirs()) {
//                return;
//            }
//        }
//        if (!CONFIG_FILE.exists()) {
//            try {
//                if(!CONFIG_FILE.createNewFile()) {
//                    return;
//                }
//            } catch (IOException error) {
//                error.printStackTrace();
//            }
//        }
//
//        try {
//            config = configLoader.load(ConfigurationOptions.defaults().header(HEADER));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        verbose = getBoolean("verbose", true);
//        version = getInt("config-version", 1);
//
//        readConfig(Config.class, null);
//        try {
//            configLoader.save(config);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void readConfig(Class<?> clazz, Object instance) {
//        for (Method method : clazz.getDeclaredMethods()) {
//            if (Modifier.isPrivate(method.getModifiers())) {
//                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
//                    try {
//                        method.setAccessible(true);
//                        method.invoke(instance);
//                    } catch (InvocationTargetException | IllegalAccessException ex) {
//                        ALogger.fatal("Error reading config", ex);
//                    }
//                }
//            }
//        }
//        saveConfig();
//    }
//
//    public static void saveConfig() {
//        try {
//            configLoader.save(config);
//        } catch (IOException ex) {
//            ALogger.fatal("Error saving config", ex);
//        }
//    }
//
//    private static Object[] splitPath(String key) {
//        return PATH_PATTERN.split(key);
//    }
//
//    private static void set(String path, Object def) {
//        if(config.node(splitPath(path)).virtual()) {
//            try {
//                config.node(splitPath(path)).set(def);
//            } catch (SerializationException e) {
//            }
//        }
//    }
//
//    private static void setString(String path, String def) {
//        try {
//            if(config.node(splitPath(path)).virtual())
//                config.node(splitPath(path)).set(TypeToken.get(String.class), def);
//        } catch(SerializationException ex) {
//        }
//    }
//
//    private static boolean getBoolean(String path, boolean def) {
//        set(path, def);
//        return config.node(splitPath(path)).getBoolean(def);
//    }
//
//    private static double getDouble(String path, double def) {
//        set(path, def);
//        return config.node(splitPath(path)).getDouble(def);
//    }
//
//    private static int getInt(String path, int def) {
//        set(path, def);
//        return config.node(splitPath(path)).getInt(def);
//    }
//
//    private static String getString(String path, String def) {
//        setString(path, def);
//        return config.node(splitPath(path)).getString(def);
//    }
//
//    private static Long getLong(String path, Long def) {
//        set(path, def);
//        return config.node(splitPath(path)).getLong(def);
//    }
//
//    private static <T> List<String> getList(String path, T def) {
//        try {
//            set(path, def);
//            return config.node(splitPath(path)).getList(TypeToken.get(String.class));
//        } catch(SerializationException ex) {
//        }
//        return new ArrayList<>();
//    }
//
//    /** ONLY EDIT ANYTHING BELOW THIS LINE **/
//    public static String driver = "com.mysql.cj.jdbc.Driver";
//    public static String host = "13.11.1.78";
//    public static String port = "3306";
//    public static String database = "McTestSql";
//    public static String user = "root";
//    public static String password = "foobar";
//    public static String options = "?MaxPooledStatements=250&useSSL=false&autoReconnect=true&maxReconnects=3";
//    private static void databaseSettings() {
//        String path = "database.";
//        driver = getString(path + "driver", driver);
//        host = getString(path + "host", host);
//        port = getString(path + "port", port);
//        database = getString(path + "database", database);
//        user = getString(path + "user", user);
//        password = getString(path + "password", password);
//        options = getString(path + "options", options);
//    }
//
//    public static Long activeTaskCheckFrequency = 1L;
//    public static Long taskCheckFrequency = 1L;
//    private static void boosterTaskSettings() {
//        activeTaskCheckFrequency = getLong("task.queue-frequency", activeTaskCheckFrequency);
//        taskCheckFrequency = getLong("task.check-frequency", taskCheckFrequency);
//    }
//
//    public static String pluginMessageChannel = "altitude:boosterplugin";
//    private static void pluginMessageSettings() {
//        pluginMessageChannel = getString("settings.message-channel", pluginMessageChannel);
//    }
//
//    public static long BOOST_ANNOUNCE_CHANNEL = -1;
//    private static void announceChannels() {
//        BOOST_ANNOUNCE_CHANNEL = getLong("settings.boost-announce-channel", BOOST_ANNOUNCE_CHANNEL);
//    }
//
//    public static List<String> donorRanks = new ArrayList<>();
//    private static void loadDonorStuff() {
//        donorRanks = getList("donor.ranks", donorRanks);
//    }
//
//    public static String INVALID_USER = "<red><player> does not exist.</red>";
//    public static String INVALID_ACTION = "<red><action> is not a valid action user promote or demote.</red>";
//    public static String INVALID_DONOR_RANK = "<red><rank> is not a valid donor rank.</red>";
//    public static String DEMOTE_MESSAGE = "<red>Your <rank> rank was refunded and removed. Contact staff if you're unsure what caused this.</red>";
//    public static String PROMOTE_MESSAGE = "<green>Thank you for your support! We applied the <rank> rank to your account.</green>";
//    public static String BOOST_SERVER_MESSAGE = "<light_purple>* <player> activated an <dark_purple><booster></dark_purple> booster!</light_purple>";
//    private static void loadMessages() {
//        INVALID_USER = getString("messages.invalid-user", INVALID_USER);
//        INVALID_ACTION = getString("messages.invalid-action", INVALID_ACTION);
//        INVALID_DONOR_RANK = getString("messages.invalid-donor-rank", INVALID_DONOR_RANK);
//        DEMOTE_MESSAGE = getString("messages.demote", DEMOTE_MESSAGE);
//        PROMOTE_MESSAGE = getString("messages.promote", PROMOTE_MESSAGE);
//        BOOST_SERVER_MESSAGE = getString("messages.boost-server", BOOST_SERVER_MESSAGE);
//    }
//
//    public static boolean DEBUG = false;
//    private static void loadSettings() {
//        DEBUG = getBoolean("settings.debug", DEBUG);
//    }
}
