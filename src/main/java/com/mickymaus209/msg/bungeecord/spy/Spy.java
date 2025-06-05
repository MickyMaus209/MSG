package com.mickymaus209.msg.bungeecord.spy;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class Spy {
    private final ProxiedPlayer player;
    private final Set<ProxiedPlayer> spyingPlayers;
    private boolean spyAll;
    public static Map<ProxiedPlayer, Spy> SPIES = new HashMap<>();

    public Spy(ProxiedPlayer player) {
        this.player = player;
        spyingPlayers = new HashSet<>();
        SPIES.put(player, this);
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public Set<ProxiedPlayer> getSpyingPlayers() {
        return spyingPlayers;
    }

    public void addPlayerToSpyingList(ProxiedPlayer player) {
        spyingPlayers.add(player);
    }

    public void removePlayerFromSpyingList(ProxiedPlayer player) {
        spyingPlayers.remove(player);
    }

    public static Spy getInstance(ProxiedPlayer player) {
        if (SPIES.containsKey(player))
            return SPIES.get(player);
        else
            return new Spy(player);
    }

    public boolean isSpying(ProxiedPlayer player){
        return spyingPlayers.contains(player);
    }

    public void setSpyAll(boolean spyAll) {
        this.spyAll = spyAll;
    }

    public boolean isSpyAll() {
        return spyAll;
    }
}
