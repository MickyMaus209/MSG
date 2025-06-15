package com.mickymaus209.msg.bungeecord.utils;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Utils {

    /**
     * Sending formatted Start/Stop console message that displays information
     *
     * @param msg - main class extending {@link net.md_5.bungee.api.plugin.Plugin}
     */
    public static void sendStartStopMessage(Msg msg) {
        int width = 60, keyWidth = 20;
        sendConsoleMessage(ChatColor.GRAY + repeat("-", width));
        sendConsoleMessage(ChatColor.AQUA + center("MSG Plugin", width));
        sendConsoleMessage(ChatColor.GRAY + repeat("-", width));
        sendConsoleMessage(formatLeftAligned("Mode:", ChatColor.GREEN + "BungeeCord", width, keyWidth));
        sendConsoleMessage(formatLeftAligned("Version:", ChatColor.GREEN + msg.getDescription().getVersion(), width, keyWidth));
        sendConsoleMessage(formatLeftAligned("Config:", ChatColor.GREEN + "Edit 'config.yml' in plugin dir", width, keyWidth));
        sendConsoleMessage(formatLeftAligned("Download Page:", ChatColor.YELLOW + msg.getDescription().getDescription(), width, keyWidth));
        sendConsoleMessage(formatLeftAligned("Developer:", ChatColor.GREEN + "MickyMaus209", width, keyWidth));
        sendConsoleMessage(ChatColor.GRAY + repeat("-", width));
    }

   /* public static void sendUpdateMessage(Msg msg, String currentVersion, String latestVersion) {
        sendConsoleMessage("§a" + repeat("-", WIDTH));
        sendConsoleMessage(center("§8MSG §7» §aA NEW UPDATE IS AVAILABLE!", WIDTH));
        sendConsoleMessage(center("§7Current Version: §e" + currentVersion, WIDTH));
        sendConsoleMessage(center("§7Latest Version: §e" + latestVersion, WIDTH));
        sendConsoleMessage(center("§aDownload from:", WIDTH));
        sendConsoleMessage(center("§b" + msg.getDescription().getDescription(), WIDTH));
        sendConsoleMessage("§a" + repeat("-", WIDTH));
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
    public static TextComponent getClickAbleUrlMessage(String text, String url, String hoverText) {
        TextComponent textComponent = new TextComponent(text);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        return textComponent;
    }

    /**
     * Sending message in Proxy console
     * @param message - message to send in console
     */
    public static void sendConsoleMessage(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(message));
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
