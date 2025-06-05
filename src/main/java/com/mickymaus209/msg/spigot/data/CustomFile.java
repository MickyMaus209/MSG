package com.mickymaus209.msg.spigot.data;

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

    public CustomFile(Msg msg, String fileName) {
        this.msg = msg;
        this.fileName = fileName;
        setup();
    }

    private void setup() {
        if (!msg.getDataFolder().exists())
            msg.getDataFolder().mkdirs();

        file = new File(msg.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}