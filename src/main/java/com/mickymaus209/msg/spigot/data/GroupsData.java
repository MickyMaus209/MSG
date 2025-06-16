package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.common.Data;
import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.utils.PlaceholderAPIManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GroupsData implements Data {
    private final Msg msg;
    private final CustomFile file;
    private final List<GroupFormat> groupFormats;
    private static final String FILE_NAME = "group_format.yml";
    private boolean isEnabled;

    public GroupsData(Msg msg) {
        this.msg = msg;
        file = new CustomFile(msg, FILE_NAME, this);
        file.setup();
        groupFormats = new ArrayList<>();
        loadGroupFormats();
    }

    /**
     * Setting up default data which is set when key is missing
     */
    private void setDefaults(){
        file.getConfig().addDefault("enabled", false);
        file.getConfig().addDefault("groups.admin.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        file.getConfig().addDefault("groups.admin.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        file.getConfig().addDefault("groups.admin.permission", "msg.group.admin");
        file.getConfig().addDefault("groups.staff.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        file.getConfig().addDefault("groups.staff.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        file.getConfig().addDefault("groups.staff.permission", "msg.group.staff");
        file.getConfig().addDefault("groups.vip.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        file.getConfig().addDefault("groups.vip.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        file.getConfig().addDefault("groups.vip.permission", "msg.group.vip");
        file.getConfig().addDefault("groups.default.sender", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        file.getConfig().addDefault("groups.default.receiver", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");
        file.getConfig().addDefault("groups.default.permission", "msg.group.default");

        file.getConfig().options().copyDefaults(true);
        file.save();
    }


    @Override
    public void onFileCreate() {
        setDefaults();
    }

    /**
     * Loading the groups sections from the file.
     * If group section was found, a {@link GroupFormat} object will be created and added to a {@link List}
     */
    public void loadGroupFormats() {
        isEnabled = file.getConfig().getBoolean("enabled");
        if (!isEnabled) return;
        ConfigurationSection groupsSection = file.getConfig().getConfigurationSection("groups");

        if (groupsSection != null) {
            for (String group : groupsSection.getKeys(false)) {
                String senderFormat = getStringFromConfig("groups." + group + ".sender");
                String receiverFormat = getStringFromConfig("groups." + group + ".receiver");
                String permission = getStringFromConfig("groups." + group + ".permission");
                GroupFormat groupFormat = new GroupFormat(senderFormat, receiverFormat, group, permission);
                groupFormats.add(groupFormat);
            }
        }
    }

    /**
     * Finding the corresponding format for a {@link Player}
     * @param player - player to find the group format for
     * @return GroupFormat object which stores all necessary information about the format.
     */
    public GroupFormat findGroupFormat(Player player) {
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
    public String formatMessage(String rawMessage, Player player, Object... placeholders){

        //There is an undefined amount of placeholders which are processed here. It will replace the first object with the second, the third with the fourth and so on.
        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                String placeholder = placeholders[i].toString();
                String value = placeholders[i + 1] != null ? placeholders[i + 1].toString() : "";
                rawMessage = rawMessage.replace(placeholder, value);
            }
        }

        //The players name will automatically be replaced in every message and also the players placeholders from the Placeholder API will be set.
        if (player != null) {
            rawMessage = rawMessage.replaceAll("%senderName%", player.getName()).replaceAll("%playerName%", player.getName());
            if (PlaceholderAPIManager.isInstalled())
                rawMessage = PlaceholderAPI.setPlaceholders(player, rawMessage);
        }

        //The prefix configured in the config will be replaced in every message automatically. The Minecraft color codes are replaced in every msg asw.
        rawMessage = rawMessage.replaceAll("%prefix%", msg.getConfigData().getFormatedMessage("prefix", player));
        rawMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);
        return rawMessage;
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
    public String getStringFromConfig(String key) {
        return file.getConfig().getString(key);
    }

    /**
     * Reloading group formats including {@link CustomFile}, {@link List} of {@link GroupFormat} and loading Group formats again out of config
     */
    public void reload() {
        file.reload();
        groupFormats.clear();
        loadGroupFormats();
    }
}
