package com.mickymaus209.msg.bungeecord.data;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.common.Data;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


import java.util.*;
import java.util.regex.Pattern;

public class ConfigData implements Data {
    private final Msg msg;
    private final Map<String, Object> configData;
    private final CustomFile configFile;
    private static final String CONFIG_FILE_NAME = "config";
    private boolean checkForUpdates, spy;

    public ConfigData(Msg msg) {
        this.msg = msg;
        configData = new HashMap<>();
        configFile = new CustomFile(msg, CONFIG_FILE_NAME, this);
        configFile.setup();
        setDefaultConfigData();
    }

    /**
     * Initializing config data.
     * defaultConfigData is used to save all default keys and values that are set by default
     * After the actual data/values are loaded
     */
    private void setDefaultConfigData() {
        final Map<String, Object> defaultConfigData = new LinkedHashMap<>();
        //settings
        defaultConfigData.put("check_for_updates", true);
        defaultConfigData.put("spy_function", true);

        //messages
        defaultConfigData.put("prefix", "&7[&eYourServer.com&7]");
        defaultConfigData.put("msg_usage", "%prefix% &cUsage: %command% <player> <message>");
        defaultConfigData.put("reply_usage", "%prefix% &cUsage: %command% <message>");
        defaultConfigData.put("ignore_usage", "%prefix% &cUsage: %command% <player>");
        defaultConfigData.put("spy_usage", "%prefix% &cUsage: %command% <player, all, list>");
        defaultConfigData.put("no_permission", "%prefix% &cYou do not have permission to execute this command.");
        defaultConfigData.put("no_message_received", "%prefix% &cYou have not received any messages yet.");
        defaultConfigData.put("player_offline", "%prefix% &cThis player is not online.");
        defaultConfigData.put("player_not_found", "%prefix% &cThis player could not be found.");
        defaultConfigData.put("target_can_not_be_sender", "%prefix% &cYou cannot send a message to yourself.");
        defaultConfigData.put("sender_message", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultConfigData.put("receiver_message", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");

        //SPY
        defaultConfigData.put("spy_prefix", "&7[&4Spy&7]");
        defaultConfigData.put("spy_disabled", "%prefix% &cSpying is not allowed. Turn on in config.yml");
        defaultConfigData.put("spying_list", "%prefix% &aYou are currently spying the following players &c%spy_list%");
        defaultConfigData.put("can_not_spy_yourself", "%prefix% &cYou cannot spy yourself.");
        defaultConfigData.put("already_spying_player", "%prefix% &cYou are already spying &a%targetName%");
        defaultConfigData.put("already_spying_everyone", "%prefix% &cYou are currently not spying anyone.");
        defaultConfigData.put("not_spying_anyone", "%prefix% &cYou are currently not spying anyone.");
        defaultConfigData.put("not_spying_player", "%prefix% &cYou are currently not spying &a%targetName%");
        defaultConfigData.put("spy_activated_player", "%prefix% &aYou are now spying private messages from &c%targetName%");
        defaultConfigData.put("spy_deactivated_player", "%prefix% &aYou are no longer spying private messages from &c%targetName%");
        defaultConfigData.put("spy_activated_all", "%prefix% &aYou are now spying private messages from everyone");
        defaultConfigData.put("spy_deactivated_all", "%prefix% &aYou are no longer spying private messages from anyone");
        defaultConfigData.put("spy_deactivated_everyone", "%prefix% &aYou are no longer spying private messages from anyone");
        defaultConfigData.put("spy_message", "%spy_prefix% &c%senderName% &8➔ &c%targetName% &8➔ &7%message%");


        defaultConfigData.put("receiver_deactivated", "%prefix% &cThe messaged player has deactivated private messages.");
        defaultConfigData.put("receiver_ignored_you", "%prefix% &cThe messaged player has ignored you.");
        defaultConfigData.put("you_ignored_receiver", "%prefix% &cYou have ignored this player.");
        //defaultConfigData.put("ignored_players", "%prefix% &aYou have ignored the following players: &7%ignored_list%");
        defaultConfigData.put("activated", "%prefix% &aYou successfully activated private messages.");
        defaultConfigData.put("deactivated", "%prefix% &cYou successfully deactivated private messages.");
        defaultConfigData.put("ignored", "%prefix% &aYou are no longer receiving private messages from &c%targetName%");
        defaultConfigData.put("un_ignored", "%prefix% &aYou are now able to receive messages from &c%targetName% &aagain.");
        defaultConfigData.put("player_already_ignored", "%prefix% &cYou have already ignored this player.");
        defaultConfigData.put("player_is_not_ignored", "%prefix% &cYou have not ignored this player.");
        defaultConfigData.put("can_not_ignore_yourself", "%prefix% &cYou cannot ignore yourself.");
        defaultConfigData.put("reloaded", "%prefix% &aMSG has been reloaded.");
        defaultConfigData.put("update_notify", "%prefix% &aUpdate available for MSG&7: %currentVersion% &8→ &a%latestVersion%");
        defaultConfigData.put("update_download_button", "%prefix% &bClick here to download");

        //NEW
        defaultConfigData.put("aliases.msg", Arrays.asList("tell", "dm", "pm"));
        defaultConfigData.put("aliases.reply", Collections.singletonList("r"));

        configFile.addDefaults(defaultConfigData);

        //Loading the actual data from the file
        loadConfigData(defaultConfigData);
    }

    /**
     * Loading modified data from config into Map (RAM).
     * This method requires Map object. The keys of the map are used to get values of config
     *
     * @param data is supposed to be the default data for determining and setting the keys with the data that has been edited in the config.yml
     */
    private void loadConfigData(Map<String, Object> data) {
        configData.clear();
        for (String key : data.keySet()) {
            if (configFile.getConfig().get(key) == null) continue;
            configData.put(key, configFile.getConfig().get(key));
        }
        checkForUpdates = (boolean) getData("check_for_updates");
        spy = (boolean) getData("spy_function");
    }


    /**
     * Replace all defined placeholders as well as other placeholders for messages.
     *
     * @param key          in the file
     * @param player       to replace values for placeholders
     * @param placeholders define more placeholders (1: placeholder, 2: value...)
     * @return formated message that has replaced placeholder
     */
    public TextComponent getFormatedMessage(String key, ProxiedPlayer player, Object... placeholders) {
        String msg = configData.get(key).toString();

        //There is an undefined amount of placeholders which are processed here. It will replace the first object with the second, the third with the fourth and so on.
        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                String placeholder = placeholders[i].toString();
                String value = placeholders[i + 1] != null ? placeholders[i + 1].toString() : "";
                msg = msg.replaceAll("(?i)" + Pattern.quote(placeholder), value);
            }
        }

        //The players name will automatically be replaced in every message and also the players placeholders from the Placeholder API will be set.
        msg = msg.replaceAll("(?i)" + Pattern.quote("%senderName%"), player.getName()).replaceAll("(?i)" + Pattern.quote("%playerName%"), player.getName());

        //The prefix configured in the config will be replaced in every message automatically. The Minecraft color codes are replaced in every msg asw.
        msg = msg.replaceAll("(?i)" + Pattern.quote("%prefix%"), configData.get("prefix").toString());
        msg = msg.replaceAll("(?i)" + Pattern.quote("%spy_prefix%"), configData.get("spy_prefix").toString());
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return new TextComponent(msg);
    }

    /**
     * Registering all Aliases for all commands.
     * Commands are saved in a map and aliases are saved in config
     */

    /**
     * This method is used to get all Aliases for a specific command set in the config.yml
     *
     * @param keyCommand - command to get aliases from
     * @return - List<String> with all Aliases set in the config (as String list)
     */
    public List<String> getAliasesFromConfig(String keyCommand) {
        String path = "aliases" + "." + keyCommand;
        Object aliasesObject = getData(path);

        if (!(aliasesObject instanceof List<?>)) return Collections.emptyList();
        List<?> rawList = (List<?>) aliasesObject;

        List<String> aliases = new ArrayList<>();
        for (Object obj : rawList) {
            if (!(obj instanceof String)) continue;
            aliases.add((String) obj);
        }
        return aliases;
    }


    /**
     * Reloading Config
     * The config file will be reloaded and the data will be loaded again from Config file into Map (RAM).
     */
    public void reload() {
        configFile.reload();
        setDefaultConfigData();
        msg.getAliasManager().unregisterAllAliases();
        msg.getAliasManager().registerAllAliases();
    }

    /**
     * Getting data from configData Map which stores the data from the config.yml
     * @param key
     * @return
     */
    public Object getData(String key) {
        return configData.get(key);
    }

    /**
     * @return getting boolean of whether the update notifier is turned on or off
     */
    public boolean isCheckForUpdatesTurnedOn() {
        return checkForUpdates;
    }

    /**
     * @return getting boolean of whether the spy function is enabled
     */
    public boolean isSpyEnabled() {
        return spy;
    }

    @Override
    public void onFileCreate() {

    }
}
