package com.mickymaus209.msg.bungeecord.registries;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<String, Command> COMMANDS = new HashMap<>();

    /**
     * Registering commands for {@link Map} and {@link ProxyServer}
     * @param name - main name of command (used for execution)
     * @param command - Command object for command class
     * @param msg - main class (extends {@link net.md_5.bungee.api.plugin.Plugin})
     */
    public static void registerCommand(String name, Command command, Msg msg) {
        COMMANDS.put(name.toLowerCase(), command);
        ProxyServer.getInstance().getPluginManager().registerCommand(msg, command);
    }

    /**
     * Getting {@link Command} object from COMMANDS Map through name
     * @param name - name of command to find
     * @return Command object of specified Command name
     */
    public static Command get(String name) {
        return COMMANDS.get(name.toLowerCase());
    }

    /**
     * Get all registered Commands
     * @return values of Collection which stores all registered Commands
     */
    public static Collection<Command> getAll() {
        return COMMANDS.values();
    }
}
