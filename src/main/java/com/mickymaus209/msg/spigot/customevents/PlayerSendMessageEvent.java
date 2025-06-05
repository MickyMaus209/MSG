package com.mickymaus209.msg.spigot.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSendMessageEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player sender;
    private final Player receiver;
    private final String message;

    public PlayerSendMessageEvent(Player sender, Player receiver, String message){
        this.sender = sender;
        this.receiver = receiver;
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
