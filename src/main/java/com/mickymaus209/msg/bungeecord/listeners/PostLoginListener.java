package com.mickymaus209.msg.bungeecord.listeners;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.bungeecord.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {
    private final Msg msg;

    public PostLoginListener(Msg msg) {
        this.msg = msg;
        ProxyServer.getInstance().getPluginManager().registerListener(msg, this);
    }

    /**
     * Event is called when Player is logging in on ProxyServer
     * Used to load player data from File into RAM
     * Sending Player update notify
     */
    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        //Loading PlayerData such as IgnoredPlayerList and whether MSG is turned off or not
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);
        playerData.loadPlayerData();

        //Checking for update and sending player update notify
        if (!msg.getConfigData().isCheckForUpdatesTurnedOn()) {
            return;
        }

        if (!msg.getUpdateChecker().isUpdateAvailable()) return;

        if (!player.hasPermission("msg.updatecheck")) return;

        String currentVersion = msg.getUpdateChecker().getCurrentVersion(),
                latestVersion = msg.getUpdateChecker().getLatestVersion();

        TextComponent updateButton = Utils.getClickAbleUrlMessage(
                msg.getConfigData().getFormatedMessage("update_download_button", player).getText(),
                msg.getDescription().getDescription(), "§7Open link");
                msg.getConfigData().getFormatedMessage("update_download_button", player);


        player.sendMessage(msg.getConfigData().getFormatedMessage("update_notify", player,
                "%currentVersion%", currentVersion,
                "%latestVersion%", latestVersion));
        player.sendMessage(updateButton);
    }
}
