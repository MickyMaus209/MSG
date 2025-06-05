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


    public UpdateChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        currentVersion = plugin.getDescription().getVersion();
    }

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

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
