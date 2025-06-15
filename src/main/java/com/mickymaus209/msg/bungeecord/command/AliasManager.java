package com.mickymaus209.msg.bungeecord.command;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class AliasManager {
    private final List<Command> registeredAliasWrappers = new ArrayList<>();
    private final Msg msg;

    public AliasManager(Msg msg) {
        this.msg = msg;
    }

    /**
     * Registering aliases for command which are set in config.yml
     * @param commandName - name of command to register aliases for
     */
    public void registerAliases(String commandName) {
        List<String> aliases = msg.getConfigData().getAliasesFromConfig(commandName);
        Command originalCommand = CommandRegistry.get(commandName);
        if (originalCommand == null) return;

        for (String alias : aliases) {
            Command wrapper = new Command(alias) {
                @Override
                public void execute(CommandSender sender, String[] args) {
                   originalCommand.execute(sender, args);
                }
            };
            registeredAliasWrappers.add(wrapper);
            ProxyServer.getInstance().getPluginManager().registerCommand(msg, wrapper);
        }
    }

    /**
     * Unregistering all aliases for all commands
     */
    public void unregisterAllAliases() {
        for (Command command : registeredAliasWrappers)
            ProxyServer.getInstance().getPluginManager().unregisterCommand(command);
        registeredAliasWrappers.clear();
    }

    /**
     * Registering all aliases (set in config.yml) for all commands
     */
    public void registerAllAliases() {
        for (Command command : CommandRegistry.getAll())
            registerAliases(command.getName());
    }

    /**
     * @return List of all registered alias as Command Object
     */
    public List<Command> getRegisteredAliasWrappers() {
        return registeredAliasWrappers;
    }
}
