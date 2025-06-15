package com.mickymaus209.msg.bungeecord.command;

import java.util.HashMap;
import java.util.Map;

public class SubCommandRegistry {
    private static final Map<String, SubCommand> SUB_COMMAND_MAP = new HashMap<>();

    /**
     * Registering sub command
     * @param name - main name of sub command that is used as argument
     * @param subCommand - object of Class implementing {@link SubCommand} for the corresponding sub command
     * @param aliases - alternative ways for using this sub command
     */
    public static void register(String name, SubCommand subCommand, String... aliases) {
        SUB_COMMAND_MAP.put(name, subCommand);
        for (String alias : aliases)
            SUB_COMMAND_MAP.put(alias, subCommand);
    }

    /**
     * Getting all sub commands
     * @return - Map of all registered sub commands (Key: String, Value: Class implementing SubCommand)
     */
    public static Map<String, SubCommand> getSubCommandMap() {
        return SUB_COMMAND_MAP;
    }
}
