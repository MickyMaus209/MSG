package com.mickymaus209.msg.spigot.command;

import java.util.HashMap;
import java.util.Map;

public class SubCommandRegistry {
    private static final Map<String, SubCommand> SUB_COMMAND_MAP = new HashMap<>();

    public static void register(String name, SubCommand subCommand, String... aliases) {
        SUB_COMMAND_MAP.put(name, subCommand);
        for (String alias : aliases)
            SUB_COMMAND_MAP.put(alias, subCommand);
    }

    public static Map<String, SubCommand> getSubCommandMap() {
        return SUB_COMMAND_MAP;
    }
}
