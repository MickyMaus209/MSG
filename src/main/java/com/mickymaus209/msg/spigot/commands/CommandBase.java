package com.mickymaus209.msg.spigot.commands;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandBase implements CommandExecutor {
    private final Msg msg;
    public static final Map<CommandExecutor, PluginCommand> COMMANDS = new HashMap<>();

    public CommandBase(Msg msg, String commandName) {
        this.msg = msg;
        PluginCommand command = msg.getCommand(commandName);
        command.setExecutor(this);
        Utils.registerAliases(msg.getConfigData().getAliasesFromConfig(command.getName()), this, msg);
        COMMANDS.put(this, command);
    }

    public Msg getMsg() {
        return msg;
    }
}
