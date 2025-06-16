package com.mickymaus209.msg.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public abstract class CommandBase extends Command {
    public CommandBase(String name) {
        super(name);
    }

    public abstract void executeCommand(CommandSender commandSender, String[] args, String label);
}

