package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.common.GroupFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class GroupsData {
    private final Msg msg;
    private final CustomFile file;
    private final List<GroupFormat> groupFormats;
    private static final String FILE_NAME = "group_format";
    private boolean isEnabled;
    
    public GroupsData(Msg msg) {
        this.msg = msg;
        file = new CustomFile(msg, FILE_NAME);
        groupFormats = new ArrayList<>();
        loadDefaults();
        loadGroupFormats();
    }

    /**
     * Setting up default data which is set when key is missing
     * defaultConfigData is used to save all default keys and values that are set by default
     */
    private void loadDefaults(){
        Map<String, Object> defaultDataMap = new LinkedHashMap<>();
        defaultDataMap.put("enabled", false);
        defaultDataMap.put("groups.admin.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultDataMap.put("groups.admin.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        defaultDataMap.put("groups.admin.permission", "group.admin");
        defaultDataMap.put("groups.staff.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultDataMap.put("groups.staff.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        defaultDataMap.put("groups.staff.permission", "group.staff");
        defaultDataMap.put("groups.vip.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultDataMap.put("groups.vip.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        defaultDataMap.put("groups.vip.permission", "group.vip");
        defaultDataMap.put("groups.default.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultDataMap.put("groups.default.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        defaultDataMap.put("groups.default.permission", "group.default");

        file.addDefaults(defaultDataMap);
    }

    /**
     * Loading the groups sections from the file.
     * If group section was found, a GroupFormat object will be created and added to a List<GroupFormat>
     */
    public void loadGroupFormats() {
        isEnabled = file.getConfig().getBoolean("enabled");
        if (!isEnabled) return;

        Configuration groupsSection = file.getConfig().getSection("groups");

        if (groupsSection != null) {
            for (String group : groupsSection.getKeys()) {
                String senderFormat = getDataFromConfig("groups." + group + ".sender");
                String receiverFormat = getDataFromConfig("groups." + group + ".receiver");
                String permission = getDataFromConfig("groups." + group + ".permission");
                GroupFormat groupFormat = new GroupFormat(senderFormat, receiverFormat, group, permission);
                groupFormats.add(groupFormat);
            }
        }
    }

    /**
     * Finding the corresponding format for a player
     * @param player - player to find the group format for
     * @return GroupFormat object which stores all necessary information about the format.
     */
    public GroupFormat findGroupFormat(ProxiedPlayer player) {
        for (GroupFormat groupFormat : groupFormats) {
            if (!player.hasPermission(groupFormat.getPermission())) continue;
            return groupFormat;
        }
        return null;
    }

    /**
     * Replace all defined placeholders as well as other placeholders for messages.
     *
     * @param rawMessage          message to be formatted
     * @param player       to replace values for placeholders
     * @param placeholders define more placeholders (1: placeholder, 2: value...)
     * @return formated message that has replaced placeholder
     */
    public TextComponent formatMessage(String rawMessage, ProxiedPlayer player, Object... placeholders) {

        //There is an undefined amount of placeholders which are processed here. It will replace the first object with the second, the third with the fourth and so on.
        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                if (placeholders[i].toString().equalsIgnoreCase("%receiverName%"))
                    placeholders[i] = "%senderName%";
                String placeholder = placeholders[i].toString();
                String value = placeholders[i + 1] != null ? placeholders[i + 1].toString() : "";
                rawMessage = rawMessage.replaceAll("(?i)" + Pattern.quote(placeholder), value);
            }
        }

        //The players name will automatically be replaced in every message and also the players placeholders from the Placeholder API will be set.
        rawMessage = rawMessage.replaceAll("(?i)" + Pattern.quote("%senderName%"), player.getName()).replaceAll("(?i)" + Pattern.quote("%playerName%"), player.getName());


        //The prefix configured in the config will be replaced in every message automatically. The Minecraft color codes are replaced in every msg asw.
        rawMessage = rawMessage.replaceAll("(?i)" + Pattern.quote("%prefix%"), msg.getConfigData().getData("prefix").toString());
        rawMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);
        return new TextComponent(rawMessage);
    }

    /**
     * @return true if group formats are enabled in the file
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Getting String from Configuration
     * @param key - key set in file
     * @return value of key in file
     */
    public String getDataFromConfig(String key) {
        return file.getConfig().getString(key);
    }

    /**
     * Reloading group formats including file, list of Group Formats and loading Group formats again out of config
     */
    public void reload() {
        file.reload();
        groupFormats.clear();
        loadGroupFormats();
    }
}
