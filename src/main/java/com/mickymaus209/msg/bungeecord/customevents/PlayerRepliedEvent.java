package com.mickymaus209.msg.bungeecord.customevents;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerRepliedEvent extends Event {
    private final ProxiedPlayer sender;
    private final ProxiedPlayer receiver;
    private final String message;

    public PlayerRepliedEvent(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ProxiedPlayer getReceiver() {
        return receiver;
    }

    public ProxiedPlayer getSender() {
        return sender;
    }
}
