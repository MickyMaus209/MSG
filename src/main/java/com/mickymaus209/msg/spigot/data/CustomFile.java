package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.common.Data;
import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomFile {
    private final Msg msg;
    private final String fileName;
    private File file;
    private FileConfiguration config;
    private final Data data;

    public CustomFile(Msg msg, String fileName, Data data) {
        this.msg = msg;
        this.fileName = fileName;
        this.data = data;
    }

    /**
     * Setting up file
     * Creating all necessary dirs to create file and ultimately creating the file
     * Initializing {@link org.bukkit.configuration.Configuration}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setup() {
        boolean newFile = false;
        if (!msg.getDataFolder().exists())
            msg.getDataFolder().mkdirs();

        file = new File(msg.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                newFile = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        //Config must be loaded before
        if (newFile)
            data.onFileCreate();
    }

    /**
     * Saving file using {@link org.bukkit.configuration.Configuration}
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reloading file using {@link org.bukkit.configuration.Configuration}
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @return the {@link org.bukkit.configuration.Configuration} which got useful methods such as get, set, contains...
     */
    public FileConfiguration getConfig() {
        return config;
    }
}