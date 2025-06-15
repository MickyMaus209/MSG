package com.mickymaus209.msg.bungeecord.customevents;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerRepliedEvent extends Event {
    private final ProxiedPlayer sender;
    private final ProxiedPlayer receiver;
    private final String message;

    /**
     * This event is called when a player replies on a private message using {@link com.mickymaus209.msg.bungeecord.command.commands.ReplyCommand}
     * @param sender - sender of reply
     * @param receiver - receiver of reply
     * @param message - message that is replied
     */
    public PlayerRepliedEvent(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public ProxiedPlayer getReceiver() {
        return receiver;
    }

    public ProxiedPlayer getSender() {
        return sender;
    }
}
