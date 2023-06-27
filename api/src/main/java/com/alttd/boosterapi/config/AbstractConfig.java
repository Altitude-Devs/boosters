package com.alttd.boosterapi.config;

import com.alttd.boosterapi.util.Logger;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "SameParameterValue"})
abstract class AbstractConfig {
    File file;
    private static final Pattern PATH_PATTERN = Pattern.compile("\\.");
    private static YamlConfigurationLoader configLoader;
    public static ConfigurationNode config;
    private static Logger logger = null;
    private static File CONFIG_FILE;

    AbstractConfig(File file, String filename, Logger logger, Class clazz) {
        AbstractConfig.logger = logger;
        init(new File(file.getPath()), filename, clazz);
    }

    private void init(File file, String filename, Class clazz) {
        this.file = file;
        CONFIG_FILE = new File(file, "config.yml");
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
            config = configLoader.load(ConfigurationOptions.defaults().header("").shouldCopyDefaults(false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        readConfig(clazz, null);
        try {
            configLoader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void readConfig(Class<?> clazz, Object instance) {
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            for (Method method : declaredClass.getDeclaredMethods()) {
                if (Modifier.isPrivate(method.getModifiers())) {
                    if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                        try {
                            method.setAccessible(true);
                            method.invoke(instance);
                        } catch (InvocationTargetException ex) {
                            throw new RuntimeException(ex.getCause());
                        } catch (Exception ex) {
                            if (logger != null)
                                logger.severe("Error invoking %.", method.toString());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        saveConfig();
    }

    private static void saveConfig() {
        try {
            configLoader.save(config);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Object[] splitPath(String key) {
        return PATH_PATTERN.split(key);
    }

    private static void set(String prefix, String path, Object def) {
        path = prefix + path;
        if(config.node(splitPath(path)).virtual()) {
            try {
                config.node(splitPath(path)).set(def);
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }
        saveConfig();
    }

    protected static void setString(String path, String def) {
        try {
            if(config.node(splitPath(path)).virtual())
                config.node(splitPath(path)).set(io.leangen.geantyref.TypeToken.get(String.class), def);
        } catch(SerializationException ex) {
            ex.printStackTrace();
        }
    }

    protected static boolean getBoolean(String prefix, String path, boolean def) {
        set(prefix, path, def);
        return config.node(splitPath(path)).getBoolean(def);
    }

    protected static double getDouble(String prefix, String path, double def) {
        set(prefix, path, def);
        return config.node(splitPath(path)).getDouble(def);
    }

    protected static int getInt(String prefix, String path, int def) {
        set(prefix, path, def);
        return config.node(splitPath(path)).getInt(def);
    }

    protected static String getString(String prefix, String path, String def) {
        setString(path, def);
        return config.node(splitPath(path)).getString(def);
    }

    protected static Long getLong(String prefix, String path, Long def) {
        set(prefix, path, def);
        return config.node(splitPath(path)).getLong(def);
    }

    protected static <T> List<String> getList(String prefix, String path, T def) {
        try {
            set(prefix, path, def);
            return config.node(splitPath(path)).getList(TypeToken.get(String.class));
        } catch(SerializationException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

}