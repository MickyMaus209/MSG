package com.mickymaus209.msg.spigot.spy;

import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.entity.Player;

public class SpyManager {
    private final Msg msg;

    public SpyManager(Msg msg) {
        this.msg = msg;
    }

    public void logSpies(Player sender, Player receiver, String rawMessage) {
        for (Spy spy : Spy.SPIES.values()) {
            String spyMessage = msg.getConfigData().getFormatedMessage("spy_message", spy.getPlayer(),
                    "%senderName%", sender.getName(),
                    "%targetName%", receiver.getName(),
                    "%message%", rawMessage);

            if (spy.isSpyAll() && !spy.getPlayer().equals(sender) && !spy.getPlayer().equals(receiver)) {
                spy.getPlayer().sendMessage(spyMessage);
                continue;
            }

            if ((!spy.isSpying(sender) && !spy.isSpying(receiver)) || (spy.getPlayer().equals(sender) || spy.getPlayer().equals(receiver))) return;
            spy.getPlayer().sendMessage(spyMessage);
        }
    }

    public void reload() {
        Spy.SPIES.forEach((player, spy) -> spy.getSpyingPlayers().clear());
        Spy.SPIES.clear();
    }

    public void removePlayer(Player player) {
        for (Spy spy : Spy.SPIES.values()) {
            if (spy.isSpying(player))
                spy.removePlayerFromSpyingList(player);
            if (spy.getPlayer().equals(player))
                Spy.SPIES.remove(player);
        }
    }
}
