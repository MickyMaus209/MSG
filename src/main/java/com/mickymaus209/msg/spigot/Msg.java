package com.mickymaus209.msg.spigot;

import com.mickymaus209.msg.spigot.commands.MsgCommand;
import com.mickymaus209.msg.spigot.commands.ReplyCommand;
import com.mickymaus209.msg.spigot.data.ConfigData;
import com.mickymaus209.msg.spigot.data.GroupsData;
import com.mickymaus209.msg.spigot.data.PlayerData;
import com.mickymaus209.msg.spigot.listeners.PlayerJoinListener;
import com.mickymaus209.msg.spigot.listeners.PlayerQuitListener;
import com.mickymaus209.msg.spigot.listeners.PlayerSendMessageListener;
import com.mickymaus209.msg.spigot.spy.SpyManager;
import com.mickymaus209.msg.spigot.stats.UpdateChecker;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Msg extends JavaPlugin {
    private ConfigData configData;
    private UpdateChecker updateChecker;
    private GroupsData groupsData;
    private SpyManager spyManager;

    @Override
    public void onEnable() {
        register();
        Utils.sendStartStopMessage(this);
        updateChecker.check();
        PlayerData.reloadAllPlayerData(this);
    }

    @Override
    public void onDisable() {
        Utils.sendStartStopMessage(this);
        PlayerData.saveAllPlayerData();
    }

    public void register() {
        updateChecker = new UpdateChecker(this, 80931);
        configData = new ConfigData(this);
        groupsData = new GroupsData(this);

        spyManager = new SpyManager(this);

        new MsgCommand(this, "msg");
        new ReplyCommand(this, "reply");
        new Metrics(this, 12516).addCustomChart(new SingleLineChart("online_players", () -> Bukkit.getOnlinePlayers().size()));
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerSendMessageListener(this);
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public GroupsData getGroupsData() {
        return groupsData;
    }

    public SpyManager getSpyManager() {
        return spyManager;
    }
}
