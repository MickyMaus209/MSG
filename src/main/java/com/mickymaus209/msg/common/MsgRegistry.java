package com.mickymaus209.msg.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A registry that tracks private messaging interactions between players.
 * <p>
 * It stores the last receiver each sender has interacted with,
 * allowing features like quick reply commands.
 */
public class MsgRegistry {
    private static final Map<UUID, UUID> MSG_MAP = new HashMap<>();

    /**
     * Registers a messaging interaction between a sender and a receiver.
     *
     * @param sender   the UUID of the player who sent the message
     * @param receiver the UUID of the player who received the message
     */
    public static void registerPlayerInteraction(UUID sender, UUID receiver) {
        MSG_MAP.put(sender, receiver);
    }

    /**
     * Returns the last player the given sender interacted with.
     *
     * @param sender the UUID of the sender
     * @return the UUID of the last receiver, or {@code null} if none recorded
     */
    public static UUID getLastInteraction(UUID sender) {
        return MSG_MAP.get(sender);
    }

    /**
     * Checks whether the given sender has previously messaged anyone.
     *
     * @param sender the UUID of the sender
     * @return {@code true} if a previous interaction exists, otherwise {@code false}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasMessagedBefore(UUID sender) {
        return MSG_MAP.containsKey(sender);
    }
}
