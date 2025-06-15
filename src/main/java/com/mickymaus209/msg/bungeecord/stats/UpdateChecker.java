package com.mickymaus209.msg.bungeecord.stats;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final Plugin plugin;
    private final int resourceId;
    private boolean updateAvailable = false;
    private final String currentVersion;
    private String latestVersion;


    /**
     * @param plugin - main Plugin extending {@link Plugin}
     * @param resourceId - id of SpigotMC project to check version
     */
    public UpdateChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Getting latest public version
     * @param consumer - version
     */
    private void getVersion(final Consumer<String> consumer) {
        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
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
     * @return true if an update is available
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
