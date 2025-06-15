package com.mickymaus209.msg.bungeecord.spy;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class Spy {
    private final ProxiedPlayer player;
    private final Set<ProxiedPlayer> spyingPlayers;
    private boolean spyAll;

    public Spy(ProxiedPlayer player) {
        this.player = player;
        spyingPlayers = new HashSet<>();
        SpyManager.SPIES.put(player, this);
    }

    /**
     * @return Player that is spying someone
     */
    public ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * @return {@link Set} of {@link ProxiedPlayer}s that are currently being spied
     */
    public Set<ProxiedPlayer> getSpyingPlayers() {
        return spyingPlayers;
    }

    /**
     * Adding {@link ProxiedPlayer} to {@link List} of Players that are being spied.
     * @param player Player to add on spying list
     */
    public void addPlayerToSpyingList(ProxiedPlayer player) {
        spyingPlayers.add(player);
    }

    /**
     * Removing {@link ProxiedPlayer} from {@link List} of Players that are being spied.
     * @param player Player to remove from spying list
     */
    public void removePlayerFromSpyingList(ProxiedPlayer player) {
        spyingPlayers.remove(player);
    }

    /**
     * Getting Instance of {@link Spy} based on {@link ProxiedPlayer}
     * If instance already exists for {@link ProxiedPlayer} then return otherwise create new Instance
     * @param player - Player to get/create instance from/for
     * @return Instance of {@link Spy} from Player
     */
    public static Spy getInstance(ProxiedPlayer player) {
        if (SpyManager.SPIES.containsKey(player))
            return SpyManager.SPIES.get(player);
        else
            return new Spy(player);
    }

    /**
     * @param player to check if being spied by this spy
     * @return true if player is being spied by this spy
     */
    public boolean isSpying(ProxiedPlayer player){
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
