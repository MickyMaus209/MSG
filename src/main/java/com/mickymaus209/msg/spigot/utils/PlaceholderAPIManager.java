package com.mickymaus209.msg.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIManager {
    private static boolean installed;

    static {
        check();
    }

    /**
     * Checking if PlaceholderAPI is installed on server
     * Setting boolean whether it is installed or not
     */
    public static void check() {
        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        installed = placeholderApi != null && placeholderApi.isEnabled();
    }

    /**
     * @return true if PlaceholderAPI is installed and false if not
     */
    public static boolean isInstalled() {
        return installed;
    }
}
