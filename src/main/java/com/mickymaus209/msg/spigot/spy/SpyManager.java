package com.mickymaus209.msg.spigot.spy;

import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SpyManager {
    public static final Map<Player, Spy> SPIES = new HashMap<>();
    private final Msg msg;

    public SpyManager(Msg msg) {
        this.msg = msg;
    }

    /**
     * Logging message for spying
     * @param sender - of message
     * @param receiver of message
     * @param rawMessage - unformatted message
     */
    public void logSpies(Player sender, Player receiver, String rawMessage) {
        for (Spy spy : SPIES.values()) {
            String spyMessage = msg.getConfigData().getFormatedMessage("spy_message", spy.getPlayer(),
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
    public void removePlayer(Player player) {
        for (Spy spy : SPIES.values()) {
            if (spy.isSpying(player))
                spy.removePlayerFromSpyingList(player);
            if (spy.getPlayer().equals(player))
                SPIES.remove(player);
        }
    }
}
