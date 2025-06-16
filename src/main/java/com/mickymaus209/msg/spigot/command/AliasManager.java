package com.mickymaus209.msg.spigot.command;

import com.mickymaus209.msg.spigot.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AliasManager {
    private final List<PluginCommand> registeredAliasWrappers = new ArrayList<>();
    private final Msg msg;

    public AliasManager(Msg msg) {
        this.msg = msg;
    }

    /**
     * Dynamically registers a list of command aliases with the given CommandExecutor.
     * <pr>
     * This method uses reflection to create PluginCommand instances and manually
     * registers them with the server's CommandMap. Each alias is associated with
     * the same executor and plugin.
     * <p>
     * Registered commands are stored in a local list (registeredAliasWrappers) so
     * they can later be unregistered.
     *
     * @param aliases   A list of command aliases to register (e.g., ["msg", "tell"]).
     * @param executor  The executor that should handle all the registered aliases.
     */
    public void registerAliases(List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            for (String alias : aliases) {
                PluginCommand command = constructor.newInstance(alias, msg);
                command.setExecutor(executor);
                commandMap.register(msg.getDescription().getName(), command);
                registeredAliasWrappers.add(command);
            }
        } catch (Exception e) {
            System.err.println("[MSG] Aliases for " + executor.getClass().getName() + " could not be registered.");
            System.err.println("[MSG] The following aliases could not be registered: " + aliases);
        }
    }

    /**
     * Unregisters all previously registered command aliases.
     * <p>
     * This method removes all aliases stored in the `registeredAliasWrappers` list
     * from Bukkit's internal CommandMap using reflection. After removal, the list
     * is cleared to avoid stale references.
     * <p>
     * This method should be called before re-registering or reload to remove old aliases
     */
    @SuppressWarnings("unchecked")
    public void unregisterAllAliases() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            if (!(commandMap instanceof SimpleCommandMap)) return;

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            for (Command alias : registeredAliasWrappers)
                knownCommands.remove(alias.getName());

            registeredAliasWrappers.clear();
        } catch (Exception e) {
            System.err.println("[MSG] Error: Could not unregister aliases. Please restart server");
        }
    }

    /**
     * Registering all aliases (set in config.yml) for all commands
     */
    public void registerAllAliases() {
        for (PluginCommand command : CommandRegistry.getAll()) {
            List<String> aliases = msg.getConfigData().getAliasesFromConfig(command.getName());
            registerAliases(aliases, command.getExecutor());
        }
    }

    /**
     * @return List of all registered alias as {@link PluginCommand} object
     */
    @SuppressWarnings("unused")
    public List<PluginCommand> getRegisteredAliasWrappers() {
        return registeredAliasWrappers;
    }
}
