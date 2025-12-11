package com.mickymaus209.msg.spigot.data.playerdata;

import com.mickymaus209.msg.spigot.Msg;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DataManger {
    public static final int CONFIG_FILE = 0, MYSQL = 1;
    protected final Msg msg;

    protected DataManger(Msg msg) {
        this.msg = msg;
    }

    public Msg getMsg() {
        return msg;
    }

    public abstract PlayerData getPlayerData(UUID uuid);
    public abstract void reloadAllPlayerData();
    public abstract void saveAllPlayerData();
    public abstract void stop();
}
