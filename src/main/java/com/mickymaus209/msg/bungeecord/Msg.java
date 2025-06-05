package com.mickymaus209.msg.bungeecord;

import com.mickymaus209.msg.bungeecord.commands.MsgCommand;
import com.mickymaus209.msg.bungeecord.commands.ReplyCommand;
import com.mickymaus209.msg.bungeecord.data.ConfigData;
import com.mickymaus209.msg.bungeecord.data.GroupsData;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.bungeecord.listeners.PlayerDisconnectListener;
import com.mickymaus209.msg.bungeecord.listeners.TabCompleteListener;
import com.mickymaus209.msg.bungeecord.spy.SpyManager;
import com.mickymaus209.msg.bungeecord.stats.UpdateChecker;
import com.mickymaus209.msg.bungeecord.commands.AliasManager;
import com.mickymaus209.msg.bungeecord.utils.Utils;
import net.md_5.bungee.api.plugin.Plugin;
import com.mickymaus209.msg.bungeecord.listeners.PostLoginListener;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SingleLineChart;

public class Msg extends Plugin {
    private ConfigData configData;
    private UpdateChecker updateChecker;
    private AliasManager aliasManager;
    private GroupsData groupsData;
    private SpyManager spyManager;

    @Override
    public void onEnable() {
        register();
        Utils.sendStartStopMessage(this);
        updateChecker.check();
        PlayerData.reloadAllPlayerData(this);
        Runtime.getRuntime().addShutdownHook(new Thread(PlayerData::saveAllPlayerData));
    }

    @Override
    public void onDisable() {
        Utils.sendStartStopMessage(this);
        PlayerData.saveAllPlayerData();
    }

    private void register() {
        updateChecker = new UpdateChecker(this, 80931);
        configData = new ConfigData(this);
        aliasManager = new AliasManager(this);
        groupsData = new GroupsData(this);
        spyManager = new SpyManager(this);

        //Commands
        new MsgCommand("msg", this);
        new ReplyCommand("reply", this);

        //Listeners
        new PostLoginListener(this);
        new PlayerDisconnectListener(this);
        //Doesnt work --> new TabCompleteListener(this);

        //Utils
        new Metrics(this, 12516).addCustomChart(new SingleLineChart("online_players", () -> getProxy().getOnlineCount()));
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public AliasManager getAliasManager() {
        return aliasManager;
    }

    public GroupsData getGroupsData() {
        return groupsData;
    }

    public SpyManager getSpyManager() {
        return spyManager;
    }
}
