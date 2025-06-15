package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.common.Data;
import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData implements Data {
    private final Msg msg;
    private boolean deactivated;
    private List<UUID> ignoredPlayers;
    private CustomFile dataFile;
    private static final Map<UUID, PlayerData> DATA = new HashMap<>();
    private final String fileName;

    public PlayerData(Msg msg, UUID playerUUID) {
        this.msg = msg;
        fileName = playerUUID.toString();
        DATA.put(playerUUID, this);
    }

    /**
     * Setting up a unique file with the UUID of the Player (for each player own file)
     * Loading settings from file of Player into RAM
     */
    public void loadPlayerData() {
        dataFile = new CustomFile(msg, "/playerData/" + fileName + ".yml", this);
        dataFile.setup();

        setDeactivated(dataFile.getConfig().getBoolean("deactivated"));
        setIgnoredPlayers(dataFile.getConfig().getStringList("ignored_players").stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    /**
     * Saving Player settings by setting into their file
     */
    public void savePlayerData() {
        dataFile.getConfig().set("deactivated", isDeactivated());
        dataFile.getConfig().set("ignored_players", ignoredPlayers.stream().map(UUID::toString).collect(Collectors.toList()));
        dataFile.save();
    }

    /**
     * Saving all player's player data in their individual file at once
     */
    public static void saveAllPlayerData() {
        DATA.values().forEach(PlayerData::savePlayerData);
    }

    /**
     * Reloading all player data at once including file, clearing list and loading again
     *
     * @param msg object of main (extends Java Plugin)
     */
    public static void reloadAllPlayerData(Msg msg) {
        DATA.values().forEach(playerData -> playerData.getDataFile().reload());
        DATA.clear();
        Bukkit.getOnlinePlayers().forEach(p -> new PlayerData(msg, p.getUniqueId()).loadPlayerData());
    }

    /**
     * Checking if Player has ignored a specific UUID (Player)
     *
     * @param uuid of player to check
     * @return true if specified player is Ignored
     */
    public boolean hasIgnored(UUID uuid) {
        return getIgnoredPlayers().contains(uuid);
    }

    /**
     * Ignoring player's private messages
     * Adding UUID of Player to IgnoredList
     * @param uuid of player to set ignored
     */
    public void ignore(UUID uuid) {
        getIgnoredPlayers().add(uuid);
    }

    /**
     * To no longer ignore a specific Player (UUID) of sending private messages
     * Removing UUID of Player from IgnoredList
     * @param uuid of player to remove from IgnoredList
     */
    public void unIgnore(UUID uuid) {
        getIgnoredPlayers().remove(uuid);
    }

    /**
     * @return boolean of whether player has deactivated private msg or not
     */
    public boolean isDeactivated() {
        return deactivated;
    }

    /**
     * Setting private msg deactivated or not deactivated for this specific player
     * @param deactivated - true = setting deactivated; false = setting not deactivated
     */
    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    /**
     * Getting List<UUID> UUIDs of Players
     *
     * @return List of Ignored Players
     */
    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    /**
     * Getting instance of {@link PlayerData}
     * @param uuid - player's uuid to get player data from
     * @param msg - main instance ({@link org.bukkit.plugin.java.JavaPlugin}
     * @return PlayerData instance which is stored in DATA Map or creating a new one if not exists
     */
    public static PlayerData getPlayerData(UUID uuid, Msg msg) {
        if (DATA.get(uuid) != null) return DATA.get(uuid);
        else return new PlayerData(msg, uuid);
    }

    /**
     * @return CustomFile object of player data
     */
    public CustomFile getDataFile() {
        return dataFile;
    }

    /**
     * Setting ignored players
     * @param ignoredPlayers list to set as ignored players
     */
    public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    @Override
    public void onFileCreate() {

    }
}
