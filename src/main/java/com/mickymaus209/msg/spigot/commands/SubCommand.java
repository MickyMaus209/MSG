package com.mickymaus209.msg.spigot.commands;

import org.bukkit.entity.Player;

public interface SubCommand {
    void execute(Player player, String[] args, String label);
}
