package com.mickymaus209.msg.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MsgRegistry {
    private static final Map<UUID, UUID> MSG_MAP = new HashMap<>();

    public static void registerPlayerInteraction(UUID sender, UUID receiver) {
        MSG_MAP.put(sender, receiver);
    }

    public static UUID getLastInteraction(UUID sender) {
        return MSG_MAP.get(sender);
    }

    public static boolean hasMessagedBefore(UUID sender) {
        return MSG_MAP.containsKey(sender);
    }
}
