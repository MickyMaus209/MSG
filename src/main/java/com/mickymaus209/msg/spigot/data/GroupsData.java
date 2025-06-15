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

    public void loadGroupFormats() {
        isEnabled = file.getConfig().getBoolean("enabled");
        if (!isEnabled) return;
        ConfigurationSection groupsSection = file.getConfig().getConfigurationSection("groups");

        if (groupsSection != null) {
            for (String group : groupsSection.getKeys(false)) {
                String senderFormat = getData("groups." + group + ".sender");
                String receiverFormat = getData("groups." + group + ".receiver");
                String permission = getData("groups." + group + ".permission");
                GroupFormat groupFormat = new GroupFormat(senderFormat, receiverFormat, group, permission);
                groupFormats.add(groupFormat);
            }
        }
    }

    public GroupFormat findGroupFormat(Player player) {
        for (GroupFormat groupFormat : groupFormats) {
            if (!player.hasPermission(groupFormat.getPermission())) continue;
            return groupFormat;
        }
        return null;
    }

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

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getData(String key) {
        return file.getConfig().getString(key);
    }

    public void reload() {
        file.reload();
        groupFormats.clear();
        loadGroupFormats();
    }
}
