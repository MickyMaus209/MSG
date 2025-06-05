package com.mickymaus209.msg.bungeecord.registries;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public static void registerCommand(String name, Command command, Msg msg) {
        COMMANDS.put(name.toLowerCase(), command);
        ProxyServer.getInstance().getPluginManager().registerCommand(msg, command);
    }

    public static Command get(String name) {
        return COMMANDS.get(name.toLowerCase());
    }

    public static Collection<Command> getAll() {
        return COMMANDS.values();
    }
}
