package com.mickymaus209.msg.spigot.spy;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Spy {
    private final Player player;
    private final Set<Player> spyingPlayers;
    private boolean spyAll;

    public Spy(Player player) {
        this.player = player;
        spyingPlayers = new HashSet<>();
        SpyManager.SPIES.put(player, this);
    }

    /**
     * @return Player that is spying someone
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return {@link Set} of {@link Player}s that are currently being spied
     */
    public Set<Player> getSpyingPlayers() {
        return spyingPlayers;
    }

    /**
     * Adding {@link Player} to {@link List} of Players that are being spied.
     * @param player Player to add on spying list
     */
    public void addPlayerToSpyingList(Player player) {
        spyingPlayers.add(player);
    }

    /**
     * Removing {@link Player} from {@link List} of Players that are being spied.
     * @param player Player to remove from spying list
     */
    public void removePlayerFromSpyingList(Player player) {
        spyingPlayers.remove(player);
    }

    /**
     * Getting Instance of {@link Spy} based on {@link Player}
     * If instance already exists for {@link Player} then return otherwise create new Instance
     * @param player - Player to get/create instance from/for
     * @return Instance of {@link Spy} from Player
     */
    public static Spy getInstance(Player player) {
        if (SpyManager.SPIES.containsKey(player))
            return SpyManager.SPIES.get(player);
        else
            return new Spy(player);
    }

    /**
     * @param player to check if being spied by this spy
     * @return true if player is being spied by this spy
     */
    public boolean isSpying(Player player){
        return spyingPlayers.contains(player);
    }

    /**
     * Spy everyone
     * @param spyAll true for making spy to spy everyone
     */
    public void setSpyAll(boolean spyAll) {
        this.spyAll = spyAll;
    }

    /**
     * @return if spy is spying everyone
     */
    public boolean isSpyAll() {
        return spyAll;
    }
}
