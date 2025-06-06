package com.mickymaus209.msg.bungeecord.listeners;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {
    private final Msg msg;

    public PlayerDisconnectListener(Msg msg) {
        this.msg = msg;
        ProxyServer.getInstance().getPluginManager().registerListener(msg, this);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);
        playerData.savePlayerData();
        msg.getSpyManager().removePlayer(player);
    }
}
