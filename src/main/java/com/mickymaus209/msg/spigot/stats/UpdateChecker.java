package com.mickymaus209.msg.spigot.stats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;
    private boolean updateAvailable = false;
    private final String currentVersion;
    private String latestVersion;


    /**
     * @param plugin - main Plugin extending {@link JavaPlugin}
     * @param resourceId - id of SpigotMC project to check version
     */
    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Getting latest public version
     * @param consumer - version
     */
    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    System.out.println("Checking for updates...");
                    latestVersion = scanner.next();
                    consumer.accept(latestVersion);
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }

    /**
     * Check if update is available, setting boolean updateAvailable
     */
    public void check() {
        getVersion(version -> {
            if (!plugin.getDescription().getVersion().equals(version))
                updateAvailable = true;
        });
    }

    /**
     * @return if an update is available
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * @return current version of this build
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * @return latest public version released on SpigotMC.org
     */
    public String getLatestVersion() {
        return latestVersion;
    }
}
