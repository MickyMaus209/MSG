package com.mickymaus209.msg.spigot.spy;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Spy {
    private final Player player;
    private final Set<Player> spyingPlayers;
    private boolean spyAll;
    public static Map<Player, Spy> SPIES = new HashMap<>();

    public Spy(Player player) {
        this.player = player;
        spyingPlayers = new HashSet<>();
        SPIES.put(player, this);
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Player> getSpyingPlayers() {
        return spyingPlayers;
    }

    public void addPlayerToSpyingList(Player player) {
        spyingPlayers.add(player);
    }

    public void removePlayerFromSpyingList(Player player) {
        spyingPlayers.remove(player);
    }

    public static Spy getInstance(Player player) {
        if (SPIES.containsKey(player))
            return SPIES.get(player);
        else
            return new Spy(player);
    }

    public boolean isSpying(Player player){
        return spyingPlayers.contains(player);
    }

    public void setSpyAll(boolean spyAll) {
        this.spyAll = spyAll;
    }

    public boolean isSpyAll() {
        return spyAll;
    }
}
