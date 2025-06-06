package com.mickymaus209.msg.spigot.listeners;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final Msg msg;

    public PlayerQuitListener(Msg msg){
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(this, msg);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);
        playerData.savePlayerData();
        msg.getSpyManager().removePlayer(player);
    }
}
