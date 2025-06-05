package com.mickymaus209.msg.spigot.utils;

import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class Utils {
    private static final int WIDTH = 60, KEY_WIDTH = 20;

    public static void sendStartStopMessage(Msg msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", WIDTH));
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + center("MSG Plugin", WIDTH));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Mode:", ChatColor.GREEN + "Single Server", WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Version:", ChatColor.GREEN + msg.getDescription().getVersion(), WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Config:", ChatColor.GREEN + "Edit 'config.yml' in plugin dir", WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("PlaceholderAPI:", PlaceholderAPIManager.isInstalled() ? "§aDetected" : "§cNot found", WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Download Page:", ChatColor.YELLOW + msg.getDescription().getDescription(), WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Developer:", ChatColor.GREEN + "MickyMaus209", WIDTH, KEY_WIDTH));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", WIDTH));
    }

   /* public static void sendUpdateMessage(Msg msg, String currentVersion, String latestVersion) {
        Bukkit.getConsoleSender().sendMessage("§a" + repeat("-", WIDTH));
        Bukkit.getConsoleSender().sendMessage(center("§8MSG §7» §aA NEW UPDATE IS AVAILABLE!", WIDTH));
        Bukkit.getConsoleSender().sendMessage(center("§7Current Version: §e" + currentVersion, WIDTH));
        Bukkit.getConsoleSender().sendMessage(center("§7Latest Version: §e" + latestVersion, WIDTH));
        Bukkit.getConsoleSender().sendMessage(center("§aDownload from:", WIDTH));
        Bukkit.getConsoleSender().sendMessage(center("§b" + msg.getDescription().getDescription(), WIDTH));
        Bukkit.getConsoleSender().sendMessage("§a" + repeat("-", WIDTH));
    }
    */

    public static void registerAliases(List<String> aliases, CommandExecutor executor, JavaPlugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            for (String alias : aliases) {
                PluginCommand command = constructor.newInstance(alias, plugin);
                command.setExecutor(executor);
                commandMap.register(plugin.getDescription().getName(), command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidSound(String name) {
        try {
            Sound.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String formatLeftAligned(String key, String value, int totalWidth, int keyWidth) {
        String paddedKey = key + repeat(" ", Math.max(0, keyWidth - key.length()));
        String line = " " + paddedKey + value;
        return line + repeat(" ", Math.max(0, totalWidth - line.length()));
    }

    public static String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        if (padding <= 0) return text;
        return repeat(" ", padding) + text + repeat(" ", width - text.length() - padding);
    }

    private static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
