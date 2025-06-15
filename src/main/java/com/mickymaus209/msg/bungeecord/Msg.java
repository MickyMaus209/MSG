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

    /**
     * Called when the plugin is enabled.
     * <p>
     * Calling method to register components, checks for updates, reloads player data,
     * and registers a shutdown hook to save data for unexpected shutdown.
     */
    @Override
    public void onEnable() {
        register();
        Utils.sendStartStopMessage(this);
        updateChecker.check();
        PlayerData.reloadAllPlayerData(this);
        Runtime.getRuntime().addShutdownHook(new Thread(PlayerData::saveAllPlayerData));
    }

    /**
     * Called when the plugin is disabled.
     * <p>
     * Sends a shutdown message and saves all player data.
     */
    @Override
    public void onDisable() {
        Utils.sendStartStopMessage(this);
        PlayerData.saveAllPlayerData();
    }

    /**
     * Initializes and registers all core components of the plugin.
     * <p>
     * This includes:
     * <ul>
     *   <li>Update checking and configuration loading</li>
     *   <li>Manager classes (aliases, spying)</li>
     *   <li>Command and subcommand registration</li>
     *   <li>Event listeners</li>
     *   <li>Metrics reporting</li>
     * </ul>
     */
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
        //Doesn't work --> new TabCompleteListener(this);

        //Utils
        new Metrics(this, 12516).addCustomChart(new SingleLineChart("online_players", () -> getProxy().getOnlineCount()));
    }

    /**
     * Stores all configuration data from config.yml
     *
     * @return the {@link ConfigData} object
     */
    public ConfigData getConfigData() {
        return configData;
    }

    /**
     * Checking for updates, getting versions
     * @return the {@link UpdateChecker} object
     */
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    /**
     * Managing aliases for {@link net.md_5.bungee.api.plugin.Command}
     * @return {@link AliasManager} object
     */
    public AliasManager getAliasManager() {
        return aliasManager;
    }

    /**
     * Stores all data from group_format.yml
     * Used for group formats
     * @return {@link GroupsData}
     */
    public GroupsData getGroupsData() {
        return groupsData;
    }

    /**
     * Managing spies for private messages
     * @return {@link SpyManager}
     */
    public SpyManager getSpyManager() {
        return spyManager;
    }

    /**
     * Handling Commands including SubCommands
     * @return {@link CommandHandler}
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
