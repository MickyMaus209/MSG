package com.mickymaus209.msg.spigot.listeners;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.PlayerData;
import com.mickymaus209.msg.spigot.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final Msg msg;

    public PlayerJoinListener(Msg msg) {
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(this, msg);
    }

    /**
     * Event is called when Player is joining server
     * Used to load player data from File into RAM
     * Sending Player update notify
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Loading PlayerData such as list of ignored players and whether MSG is turned off or not
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);
        playerData.loadPlayerData();

        //Checking for update and sending player update notify
        if (!msg.getConfigData().isCheckForUpdatesTurnedOn()) return;

        if (!msg.getUpdateChecker().isUpdateAvailable()) return;

        if (!player.hasPermission("msg.updatecheck")) return;

        String currentVersion = msg.getUpdateChecker().getCurrentVersion(),
                latestVersion = msg.getUpdateChecker().getLatestVersion();

        String updateNotify = msg.getConfigData().getFormatedMessage("update_notify", player,
                "%currentVersion%", currentVersion, "%latestVersion%", latestVersion);

        String buttonText = msg.getConfigData().getFormatedMessage("update_download_button", player);

        TextComponent updateButton = Utils.getClickAbleUrlMessage(buttonText, msg.getDescription().getDescription(), "§7Open link");

       player.sendMessage(updateNotify);
       player.spigot().sendMessage(updateButton);
    }
}
