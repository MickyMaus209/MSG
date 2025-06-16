package com.mickymaus209.msg.spigot.utils;

import com.mickymaus209.msg.spigot.Msg;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class Utils {

    /**
     * Sending formatted Start/Stop console message that displays information
     *
     * @param msg - main class extending {@link JavaPlugin}
     */
    public static void sendStartStopMessage(Msg msg) {
        int width = 60, keyWidth = 20;
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", width));
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + center("MSG Plugin", width));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", width));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Mode:", ChatColor.GREEN + "Single Server", width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Version:", ChatColor.GREEN + msg.getDescription().getVersion(), width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Config:", ChatColor.GREEN + "Edit 'config.yml' in plugin dir", width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("PlaceholderAPI:", PlaceholderAPIManager.isInstalled() ? "§aDetected" : "§cNot found", width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Download Page:", ChatColor.YELLOW + msg.getDescription().getDescription(), width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(formatLeftAligned("Developer:", ChatColor.GREEN + "MickyMaus209", width, keyWidth));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + repeat("-", width));
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

    /**
     * Building clickable url message {@link TextComponent}
     *
     * @param text      - String that is displayed as the actual message
     * @param url       - url of clickable message
     * @param hoverText - text that is shown when hovering over message
     * @return net.md_5.bungee.api.chat.TextComponent with added {@link ClickEvent} and {@link HoverEvent}
     */
    public static TextComponent getClickAbleUrlMessage(String text, String url, String hoverText){
        TextComponent textComponent = new TextComponent(text);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        return textComponent;
    }

    /**
     * Checking if name is valid Sound enum
     * @param name - name of sound to be checked
     * @return boolean whether sound enum exists or not
     */
    public static boolean isValidSound(String name) {
        try {
            Sound.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Formats a key-value pair into a left-aligned string with fixed widths.
     * <p>
     * The key is padded with spaces to fit the specified {@code keyWidth}, followed by the value,
     * and the whole line is padded with spaces to reach the total specified {@code totalWidth}.
     * This is useful for creating nicely aligned tabular or column-style output.
     *
     * @param key        the key string to be left-aligned and padded to {@code keyWidth}
     * @param value      the value string to be placed right after the key
     * @param totalWidth the total width of the final string line
     * @param keyWidth   the fixed width to assign to the key (including padding)
     * @return a formatted string where the key is left-aligned and the line reaches {@code totalWidth}
     */
    @SuppressWarnings("SameParameterValue")
    private static String formatLeftAligned(String key, String value, int totalWidth, int keyWidth) {
        String paddedKey = key + repeat(" ", Math.max(0, keyWidth - key.length()));
        String line = " " + paddedKey + value;
        return line + repeat(" ", Math.max(0, totalWidth - line.length()));
    }

    /**
     * Centers the given text in a field of the specified width using spaces.
     * If the text is too long, it is returned unchanged.
     *
     * @param text  the text to center
     * @param width the total width of the output
     * @return the centered text with space padding
     */
    public static String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        if (padding <= 0) return text;
        return repeat(" ", padding) + text + repeat(" ", width - text.length() - padding);
    }

    /**
     * Repeats the given string a specified number of times.
     *
     * @param str   the string to repeat
     * @param times how many times to repeat it
     * @return the concatenated result of repeating {@code str} {@code times} times
     */
    private static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
