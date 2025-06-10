package com.mickymaus209.msg.bungeecord;

import com.mickymaus209.msg.bungeecord.command.CommandHandler;
import com.mickymaus209.msg.bungeecord.data.ConfigData;
import com.mickymaus209.msg.bungeecord.data.GroupsData;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.bungeecord.listeners.PlayerDisconnectListener;
import com.mickymaus209.msg.bungeecord.spy.SpyManager;
import com.mickymaus209.msg.bungeecord.stats.UpdateChecker;
import com.mickymaus209.msg.bungeecord.command.AliasManager;
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
    private CommandHandler commandHandler;

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
        groupsData = new GroupsData(this);

        aliasManager = new AliasManager(this);
        spyManager = new SpyManager(this);

        //Commands
        commandHandler = new CommandHandler(this);
        commandHandler.registerCommands();
        commandHandler.registerSubCommands();

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

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
