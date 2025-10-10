package com.mickymaus209.msg.spigot.data.playerdata;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PlayerData {
    public static final int CONFIG_FILE = 0, MYSQL = 1;
    public abstract CompletableFuture<Void> loadPlayerData();
    public abstract CompletableFuture<Void>  savePlayerData();
    public abstract boolean hasIgnored(UUID uuid);
    public abstract void ignore(UUID uuid);
    public abstract void unIgnore(UUID uuid);
    public abstract boolean isDeactivated();
    public abstract void setDeactivated(boolean deactivated);
    public abstract List<UUID> getIgnoredPlayers();
    public abstract void setIgnoredPlayers(List<UUID> ignoredPlayers);
    public abstract void removeFromCache();
}

