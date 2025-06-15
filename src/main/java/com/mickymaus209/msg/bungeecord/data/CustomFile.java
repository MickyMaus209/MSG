package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.common.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

public class CustomFile {
    private final Msg msg;
    private File file;
    private Configuration config;
    private final String fileName;
    private final Data data;

    protected CustomFile(Msg msg, String fileName, Data data) {
        this.msg = msg;
        this.fileName = fileName + ".yml";
        this.data = data;
    }

    /**
     * Setting up file
     * Creating all necessary dirs to create file and ultimately creating the file
     * Initializing {@link Configuration}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void setup() {
        boolean newFile = false;
        if (!msg.getDataFolder().exists())
            msg.getDataFolder().mkdirs();

        file = new File(msg.getDataFolder().getPath(), fileName);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                newFile = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Config must be loaded before
        if (newFile)
            data.onFileCreate();
    }

    /**
     * @return the {@link Configuration} which got useful methods such as get, set, contains...
     */
    protected Configuration getConfig() {
        return config;
    }

    /**
     * Saving file using {@link Configuration}
     */
    protected void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Could not save file " + file.getName());
        }
    }

    /**
     * Reloading file using {@link Configuration}
     */
    protected void reload() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setting default data (key -> value) in the file
     *
     * @param defaultDataMap data (key -> value) as Map object for default data
     */
    protected void addDefaults(Map<String, Object> defaultDataMap) {
        for (String key : defaultDataMap.keySet()) {
            if (config.contains(key)) continue;
            config.set(key, defaultDataMap.get(key));
        }
        save();
    }


    /**
     * Set a single "data" (key -> value) as default data which will be set when file is created or missing key.
     *
     * @param key   default key to be set
     * @param value default value to be set
     */
    @SuppressWarnings("unused")
    protected void addDefault(String key, Object value) {
        if (config.contains(key)) return;
        config.set(key, value);
        save();
    }
}
