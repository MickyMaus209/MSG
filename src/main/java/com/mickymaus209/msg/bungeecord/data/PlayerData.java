package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.common.Data;
import net.md_5.bungee.api.ProxyServer;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerData implements Data {
    private final UUID playerUUID;
    private final Msg msg;
    private boolean deactivated;
    private List<UUID> ignoredPlayers;
    private CustomFile dataFile;
    private static final Map<UUID, PlayerData> DATA = new HashMap<>();
    //public static final long MAX_FILE_SIZE = 1024 * 10;

    public PlayerData(Msg msg, UUID playerUUID) {
        this.msg = msg;
        this.playerUUID = playerUUID;
        DATA.put(playerUUID, this);
    }

    /**
     * Setting up a unique file with the UUID of the Player (for each player own file)
     * Loading settings from file of Player into RAM
     */
    public void loadPlayerData() {
        dataFile = new CustomFile(msg, "/playerData/" + playerUUID.toString() + ".yml", this);
        dataFile.setup();

        setDeactivated(dataFile.getConfig().getBoolean("deactivated"));
        setIgnoredPlayers(dataFile.getConfig().getStringList("ignored_players").stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    /**
     * Saving Player settings by setting into their file
     */
    public void savePlayerData() {
        //SPAM PROTECTION
        /*if (dataFile.getFile().length() > MAX_FILE_SIZE) {
            Bukkit.getConsoleSender().sendMessage("§4DATA OF §a" + Bukkit.getPlayer(player.getName()) + " §4COULD NOT BE SAVED BECAUSE THE PLAYER'S FILE EXCEEDED THE MAXIMUM SIZE OF " + MAX_FILE_SIZE + "KB");
        }
         */
        dataFile.getConfig().set("deactivated", isDeactivated());
        dataFile.getConfig().set("ignored_players", ignoredPlayers.stream().map(UUID::toString).collect(Collectors.toList()));
        dataFile.save();
    }

    /**
     * Checking if Player has ignored a specific UUID (Player)
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
     *
     * @param uuid of player to remove from IgnoredList
     */
    public void unIgnore(UUID uuid) {
        getIgnoredPlayers().remove(uuid);
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
        ProxyServer.getInstance().getPlayers().forEach(p -> new PlayerData(msg, p.getUniqueId()).loadPlayerData());
    }

    /**
     * @return CustomFile object of player data
     */
    public CustomFile getDataFile() {
        return dataFile;
    }

    /**
     * Setting ignored players
     *
     * @param ignoredPlayers list to set as ignored players
     */
    public void setIgnoredPlayers(List<UUID> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    /**
     * Getting {@link List} UUIDs of Players
     *
     * @return List of Ignored Players
     */
    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public static PlayerData getPlayerData(UUID playerUUID, Msg msg) {
        if(DATA.get(playerUUID) != null) return DATA.get(playerUUID);
        return new PlayerData(msg, playerUUID);
    }

    @Override
    public void onFileCreate() {}
}
