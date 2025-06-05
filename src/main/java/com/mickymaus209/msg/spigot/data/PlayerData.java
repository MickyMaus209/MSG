package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {
    private final Msg msg;
    private boolean deactivated;
    private List<String> ignoredPlayers;
    private CustomFile dataFile;
    private static final Map<Player, PlayerData> DATA = new HashMap<>();
    private final String fileName;

    public PlayerData(Msg msg, Player player) {
        this.msg = msg;
        fileName = player.getUniqueId().toString();
        DATA.put(player, this);
    }

    public void loadPlayerData() {
        dataFile = new CustomFile(msg, "/playerData/" + fileName + ".yml");
        deactivated = dataFile.getConfig().getBoolean("deactivated");
        ignoredPlayers = dataFile.getConfig().getStringList("ignored_players");
    }

    public void savePlayerData() {
        dataFile.getConfig().set("deactivated", isDeactivated());
        dataFile.getConfig().set("ignored_players", ignoredPlayers);
        dataFile.save();
    }

    public static void saveAllPlayerData() {
        DATA.values().forEach(PlayerData::savePlayerData);
    }

    public static void reloadAllPlayerData(Msg msg) {
        DATA.values().forEach(playerData -> playerData.getDataFile().reload());
        DATA.clear();
        Bukkit.getOnlinePlayers().forEach(p -> new PlayerData(msg, p).loadPlayerData());
    }

    public boolean hasIgnored(String uuid) {
        return getIgnoredPlayers().contains(uuid);
    }

    public void ignore(String uuid) {
        getIgnoredPlayers().add(uuid);
    }

    public void unIgnore(String uuid) {
        getIgnoredPlayers().remove(uuid);
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public List<String> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public static PlayerData getPlayerData(Player player) {
        return DATA.get(player);
    }

    public CustomFile getDataFile() {
        return dataFile;
    }
}
