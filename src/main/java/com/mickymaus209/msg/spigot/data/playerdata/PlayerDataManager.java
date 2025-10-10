package com.mickymaus209.msg.spigot.data.playerdata;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.data.MySQLData;
import com.mickymaus209.msg.spigot.data.playerdata.data.PlayerDataConfigManager;
import com.mickymaus209.msg.spigot.data.playerdata.data.PlayerDataMySQLManager;
import com.mickymaus209.msg.spigot.data.sql.MySQLManager;

import java.util.UUID;

public class PlayerDataManager {
    private DataManger[] dataMangers;
    private DataManger currentDataMethod;
    private final Msg msg;
    private final MySQLData mySQLData;
    private MySQLManager mySQLManager;

    public PlayerDataManager(Msg msg) {
        this.msg = msg;
        this.mySQLData = msg.getMySQLData();
        dataMangers = new DataManger[2];
        dataMangers[DataManger.CONFIG_FILE] = new PlayerDataConfigManager(msg);
        dataMangers[DataManger.MYSQL] = new PlayerDataMySQLManager(msg);
        setup();
    }

    private void setup() {
        if (!mySQLData.isEnabled()) {
            setCurrentDataMethod(DataManger.CONFIG_FILE);
            return;
        }
        setCurrentDataMethod(DataManger.MYSQL);
        String host = mySQLData.getHost();
        String port = mySQLData.getPort();
        String username = mySQLData.getUsername();
        String password = mySQLData.getPassword();
        mySQLManager = new MySQLManager(host, port, username, password);
        mySQLManager.createTablesAsync();
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return currentDataMethod.getPlayerData(playerUUID);
    }

    public void reloadAllPlayerData() {
        currentDataMethod.reloadAllPlayerData();
    }

    public void saveAllPlayerData() {
        currentDataMethod.saveAllPlayerData();
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public void reload() {
        setup();
    }

    public DataManger getCurrentDataMethod() {
        return currentDataMethod;
    }

    public void setCurrentDataMethod(int id) {
        currentDataMethod = dataMangers[id];
    }
}
