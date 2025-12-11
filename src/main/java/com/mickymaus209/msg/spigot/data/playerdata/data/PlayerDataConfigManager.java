package com.mickymaus209.msg.spigot.data.playerdata.data;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.playerdata.DataManger;
import com.mickymaus209.msg.spigot.data.playerdata.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;

import static com.mickymaus209.msg.spigot.data.playerdata.data.PlayerDataConfig.DATA;

public class PlayerDataConfigManager extends DataManger {
    public PlayerDataConfigManager(Msg msg) {
        super(msg);
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        if (DATA.get(uuid) != null) return DATA.get(uuid);
        else return new PlayerDataConfig(msg, uuid);
    }

    @Override
    public void reloadAllPlayerData() {
        DATA.values().forEach(playerData -> playerData.getDataFile().reload());
        DATA.clear();
        Bukkit.getOnlinePlayers().forEach(player -> new PlayerDataConfig(msg, player.getUniqueId()).loadPlayerData());
    }

    @Override
    public void saveAllPlayerData() {
        DATA.values().forEach(PlayerDataConfig::savePlayerData);
    }

    @Override
    public void stop() {

    }
}
