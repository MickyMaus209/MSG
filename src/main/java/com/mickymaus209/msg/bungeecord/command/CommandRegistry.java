package com.mickymaus209.msg.bungeecord.command;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<String, CommandBase> COMMANDS = new HashMap<>();

    /**
     * Registering commands for {@link Map} and {@link ProxyServer}
     * @param command - Command object for command class
     * @param msg - main class (extends {@link net.md_5.bungee.api.plugin.Plugin})
     */
    public static void registerCommand(CommandBase command, Msg msg) {
        COMMANDS.put(command.getName(), command);
        ProxyServer.getInstance().getPluginManager().registerCommand(msg, command);
        msg.getAliasManager().registerAliases(command.getName());
    }

    /**
     * Getting {@link Command} object from COMMANDS Map through name
     * @param name - name of command to find
     * @return Command object of specified Command name
     */
    public static CommandBase get(String name) {
        return COMMANDS.get(name.toLowerCase());
    }

    /**
     * Get all registered Commands
     * @return values of Collection which stores all registered Commands
     */
    public static Collection<CommandBase> getAll() {
        return COMMANDS.values();
    }
}
