package com.mickymaus209.msg.bungeecord.listeners;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.customevents.PlayerRepliedEvent;
import com.mickymaus209.msg.bungeecord.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        //Loading PlayerData such as IgnoredPlayerList and whether MSG is turned off or not
        PlayerData playerData = new PlayerData(msg, player);
        playerData.loadPlayerData();

        if (!msg.getConfigData().isCheckForUpdatesTurnedOn()) {
            return;
        }

        if (!msg.getUpdateChecker().isUpdateAvailable()) return;

        if (!player.hasPermission("msg.updatecheck")) return;

        String currentVersion = msg.getUpdateChecker().getCurrentVersion(),
                latestVersion = msg.getUpdateChecker().getLatestVersion();

        TextComponent updateButton = msg.getConfigData().getFormatedMessage("update_download_button", player);
        updateButton.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, msg.getDescription().getDescription()));
        updateButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Open link")));

        player.sendMessage(msg.getConfigData().getFormatedMessage("update_notify", player,
                "%currentVersion%", currentVersion,
                "%latestVersion%", latestVersion));
        player.sendMessage(updateButton);
    }
}
