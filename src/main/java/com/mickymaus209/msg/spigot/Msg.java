package com.mickymaus209.msg.spigot;

import com.mickymaus209.msg.spigot.command.AliasManager;
import com.mickymaus209.msg.spigot.command.CommandHandler;
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
    private CommandHandler commandHandler;
    private AliasManager aliasManager;

    /**
     * Called when the plugin is enabled.
     * <p>
     * Calling method to register components, checks for updates, reloads player data,
     */
    @Override
    public void onEnable() {
        register();
        Utils.sendStartStopMessage(this);
        updateChecker.check();
        PlayerData.reloadAllPlayerData(this);
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
    public void register() {
        updateChecker = new UpdateChecker(this, 80931);
        configData = new ConfigData(this);
        groupsData = new GroupsData(this);

        spyManager = new SpyManager(this);
        aliasManager = new AliasManager(this);

        commandHandler = new CommandHandler(this);
        commandHandler.registerCommands();
        commandHandler.registerSubCommands();

        new Metrics(this, 12516).addCustomChart(new SingleLineChart("online_players", () -> Bukkit.getOnlinePlayers().size()));
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerSendMessageListener(this);
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

    /**
     * Managing aliases for {@link org.bukkit.command.Command}
     * @return {@link AliasManager} object
     */
    public AliasManager getAliasManager() {
        return aliasManager;
    }
}
