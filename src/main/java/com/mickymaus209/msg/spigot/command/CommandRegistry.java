package com.mickymaus209.msg.spigot.command;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<CommandExecutor, PluginCommand> COMMANDS = new HashMap<>();

    /**
     * Registering commands for {@link Map} and {@link org.bukkit.Bukkit}
     *
     * @param commandName   - main name of command (used for execution)
     * @param commandExecutor - {@link CommandExecutor} object for command class
     * @param msg             - main class (extends {@link org.bukkit.plugin.java.JavaPlugin})
     */
    public static void registerCommand(String commandName, CommandExecutor commandExecutor, Msg msg) {
        PluginCommand pluginCommand = msg.getCommand(commandName);
        pluginCommand.setExecutor(commandExecutor);
        COMMANDS.put(commandExecutor, pluginCommand);
        Utils.registerAliases(msg.getConfigData().getAliasesFromConfig(pluginCommand.getName()), commandExecutor, msg);
    }

    /**
     * Getting {@link PluginCommand} object from COMMANDS Map through name
     *
     * @param name - name of command to find
     * @return PluginCommand object of specified Command name
     */
    public static PluginCommand get(String name) {
        for (PluginCommand pluginCommand : COMMANDS.values()){
            if(pluginCommand.getName().equalsIgnoreCase(name)) return pluginCommand;
        }
        return null;
    }

    /**
     * Get all registered Commands
     *
     * @return values of Collection which stores all registered Commands
     */
    public static Collection<PluginCommand> getAll() {
        return COMMANDS.values();
    }
}
