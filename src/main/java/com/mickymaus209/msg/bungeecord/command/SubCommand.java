package com.mickymaus209.msg.bungeecord.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface SubCommand {
    void execute(ProxiedPlayer player, String[] args, String label);
}
