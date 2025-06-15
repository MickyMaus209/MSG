package com.mickymaus209.msg.bungeecord.spy;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

public class SpyManager {
    private final Msg msg;
    public static final Map<ProxiedPlayer, Spy> SPIES = new HashMap<>();

    public SpyManager(Msg msg) {
        this.msg = msg;
    }

    /**
     * Logging message for spying
     * @param sender - of message
     * @param receiver of message
     * @param rawMessage - unformatted message
     */
    public void logSpies(ProxiedPlayer sender, ProxiedPlayer receiver, String rawMessage) {
        for (Spy spy : SPIES.values()) {
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

    /**
     * Clearing both spies and spying players of all spies
     */
    public void reload() {
        SPIES.forEach((player, spy) -> spy.getSpyingPlayers().clear());
        SPIES.clear();
    }

    /**
     * Removing Player from any spying list as well as from spies List
     * @param player - player to be removed
     */
    public void removePlayer(ProxiedPlayer player) {
        for (Spy spy : SPIES.values()) {
            if (spy.isSpying(player))
                spy.removePlayerFromSpyingList(player);
            if (spy.getPlayer().equals(player))
                SPIES.remove(player);
        }
    }
}
