package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {
    private final ProxiedPlayer player;
    private final Msg msg;
    private boolean deactivated;
    private List<String> ignoredPlayers;
    private CustomFile dataFile;
    private static final Map<ProxiedPlayer, PlayerData> DATA = new HashMap<>();
    //public static final long MAX_FILE_SIZE = 1024 * 10;

    public PlayerData(Msg msg, ProxiedPlayer player) {
        this.msg = msg;
        this.player = player;
        DATA.put(player, this);
    }

    /**
     * Setting up a unique file with the UUID of the Player (for each player own file)
     * Loading settings from file of Player into RAM
     */
    public void loadPlayerData() {
        dataFile = new CustomFile(msg, "/playerData/" + player.getUniqueId().toString() + ".yml");

        deactivated = dataFile.getConfig().getBoolean("deactivated");
        setIgnoredPlayers(dataFile.getConfig().getStringList("ignored_players"));
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
        dataFile.getConfig().set("ignored_players", ignoredPlayers);
        dataFile.save();
    }

    /**
     * Checking if Player has ignored a specific UUID (Player)
     * @param uuid of player to check
     * @return true if specified player is Ignored
     */
    public boolean hasIgnored(String uuid) {
        return getIgnoredPlayers().contains(uuid);
    }

    /**
     * Ignoring player's private messages
     * Adding UUID of Player to IgnoredList
     * @param uuid of player to set ignored
     */
    public void ignore(String uuid) {
        getIgnoredPlayers().add(uuid);
    }

    /**
     * To no longer ignore a specific Player (UUID) of sending private messages
     * Removing UUID of Player from IgnoredList
     * @param uuid of player to remove from IgnoredList
     */
    public void unIgnore(String uuid){
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
     * @param msg object of main (extends Java Plugin)
     */
    public static void reloadAllPlayerData(Msg msg) {
        DATA.values().forEach(playerData -> playerData.getDataFile().reload());
        DATA.clear();
        ProxyServer.getInstance().getPlayers().forEach(p -> new PlayerData(msg, p).loadPlayerData());
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
    public void setIgnoredPlayers(List<String> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    /**
     * Getting List<String> (stores UUIDs)
     * @return List of Ignored Players
     */
    public List<String> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public static PlayerData getPlayerData(ProxiedPlayer player) {
        return DATA.get(player);
    }
}
