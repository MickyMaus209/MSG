package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

public class CustomFile {
    private final Msg msg;
    private File file;
    private Configuration config;
    private final String fileName;

    public CustomFile(Msg msg, String fileName) {
        this.msg = msg;
        this.fileName = fileName + ".yml";
        setup();
    }

    /**
     * Setting up file
     * Creating all necessary dirs to create file and ultimately creating the file
     * Initializing Bukkit#Configuration
     */
    public void setup() {
        if (!msg.getDataFolder().exists())
            msg.getDataFolder().mkdirs();

        file = new File(msg.getDataFolder().getPath(), fileName);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the Bukkit#Configuration which got useful methods such as get, set, contains...
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Saving file using Bukkit#Configuration
     */
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save file " + file.getName());
        }
    }

    /**
     * Reloading file using Bukkit#Configuration
     */
    public void reload() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setting default data (key -> value) in the file
     * @param defaultDataMap data (key -> value) as Map object for default data
     */
    public void addDefaults(Map<String, Object> defaultDataMap) {
        for (String key : defaultDataMap.keySet()) {
            if (config.contains(key)) continue;
            config.set(key, defaultDataMap.get(key));
        }
        save();
    }


    /**
     * Set a single "data" (key -> value) as default data which will be set when file is created or missing key.
     * @param key default key to be set
     * @param value default value to be set
     */
    public void addDefault(String key, Object value) {
        if (config.contains(key)) return;
        config.set(key, value);
        save();
    }

    /**
     * @return - the actual Java file object
     */
    public File getFile() {
        return file;
    }
}
