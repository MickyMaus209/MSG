package com.mickymaus209.msg.spigot.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerRepliedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player sender;
    private final Player receiver;
    private final String message;

    /**
     * This event is called when a player replies on a private message using {@link com.mickymaus209.msg.spigot.command.commands.ReplyCommand}
     * @param sender - sender of reply
     * @param receiver - receiver of reply
     * @param message - message that is replied
     */
    public PlayerRepliedEvent(Player receiver, Player sender, String message) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
