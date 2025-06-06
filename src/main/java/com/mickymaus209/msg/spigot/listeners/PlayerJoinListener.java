package com.mickymaus209.msg.spigot.listeners;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.PlayerData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Loading PlayerData such as list of ignored players and whether MSG is turned off or not
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);
        playerData.loadPlayerData();

        if (!msg.getConfigData().isCheckForUpdatesTurnedOn()) return;

        if (!msg.getUpdateChecker().isUpdateAvailable()) return;

        if (!player.hasPermission("msg.updatecheck")) return;

        String currentVersion = msg.getUpdateChecker().getCurrentVersion(),
                latestVersion = msg.getUpdateChecker().getLatestVersion();

        String updateNotify = msg.getConfigData().getFormatedMessage("update_notify", player,
                "%currentVersion%", currentVersion, "%latestVersion%", latestVersion);

        String buttonText = msg.getConfigData().getFormatedMessage("update_download_button", player);

        TextComponent updateButton = new TextComponent(buttonText);
        updateButton.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, msg.getDescription().getDescription()));
        updateButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Open link")));

       player.sendMessage(updateNotify);
       player.spigot().sendMessage(updateButton);
    }
}
