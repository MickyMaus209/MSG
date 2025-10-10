package com.mickymaus209.msg.spigot.data.playerdata.data;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.playerdata.DataManger;
import com.mickymaus209.msg.spigot.data.playerdata.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;

import static com.mickymaus209.msg.spigot.data.playerdata.data.PlayerDataMySQL.DATA;

public class PlayerDataMySQLManager extends DataManger {

    public PlayerDataMySQLManager(Msg msg) {
        super(msg);
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        if(DATA.get(uuid) != null) return DATA.get(uuid);
        return new PlayerDataMySQL(getMsg(), uuid);
    }

    @Override
    public void reloadAllPlayerData() {
        DATA.clear();
        Bukkit.getOnlinePlayers().forEach(player -> new PlayerDataMySQL(msg, player.getUniqueId()).loadPlayerData());
    }

    @Override
    public void saveAllPlayerData() {
        DATA.values().forEach(PlayerDataMySQL::savePlayerData);
    }
}
