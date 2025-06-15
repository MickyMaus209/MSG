package com.mickymaus209.msg.bungeecord.spy;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpyManager {
    private final Msg msg;

    public SpyManager(Msg msg) {
        this.msg = msg;
    }

    public void logSpies(ProxiedPlayer sender, ProxiedPlayer receiver, String rawMessage) {
        for (Spy spy : Spy.SPIES.values()) {
            TextComponent spyMessage = msg.getConfigData().getFormatedMessage("spy_message", spy.getPlayer(),
                    "%senderName%", sender.getName(),
                    "%targetName%", receiver.getName(),
                    "%message%", rawMessage);

            if(spy.getPlayer().getUniqueId().equals(sender.getUniqueId()) || spy.getPlayer().getUniqueId().equals(receiver.getUniqueId())) continue;

            if (spy.isSpyAll()) {
                spy.getPlayer().sendMessage(spyMessage);
                continue;
            }

            if (!spy.isSpying(sender) && !spy.isSpying(receiver)) continue;
            spy.getPlayer().sendMessage(spyMessage);
        }
    }

    public void reload() {
        Spy.SPIES.forEach((player, spy) -> spy.getSpyingPlayers().clear());
        Spy.SPIES.clear();
    }

    public void removePlayer(ProxiedPlayer player) {
        for (Spy spy : Spy.SPIES.values()) {
            if (spy.isSpying(player))
                spy.removePlayerFromSpyingList(player);
            if (spy.getPlayer().equals(player))
                Spy.SPIES.remove(player);
        }
    }
}
