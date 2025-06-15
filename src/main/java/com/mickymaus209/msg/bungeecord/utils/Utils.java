package com.mickymaus209.msg.bungeecord.utils;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Utils {

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

    public static TextComponent getClickAbleUrlMessage(String text, String url, String hoverText) {
        TextComponent textComponent = new TextComponent(text);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        return textComponent;
    }

        public static void sendConsoleMessage (String message){
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(message));
        }

        private static String formatLeftAligned (String key, String value, int totalWidth, int keyWidth){
            String paddedKey = key + repeat(" ", Math.max(0, keyWidth - key.length()));
            String line = " " + paddedKey + value;
            return line + repeat(" ", Math.max(0, totalWidth - line.length()));
        }

        public static String center (String text,int width){
            int padding = (width - text.length()) / 2;
            if (padding <= 0) return text;
            return repeat(" ", padding) + text + repeat(" ", width - text.length() - padding);
        }

        private static String repeat (String str,int times){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < times; i++) {
                sb.append(str);
            }
            return sb.toString();
        }
    }
