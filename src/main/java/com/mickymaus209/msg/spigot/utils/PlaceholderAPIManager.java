package com.mickymaus209.msg.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIManager {
    private static boolean installed;

    static {
        check();
    }

    public static void check() {
        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        installed = placeholderApi != null && placeholderApi.isEnabled();
    }

    public static boolean isInstalled() {
        return installed;
    }
}
