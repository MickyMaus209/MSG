package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.common.Data;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.CommandBase;
import com.mickymaus209.msg.spigot.utils.PlaceholderAPIManager;
import com.mickymaus209.msg.spigot.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

public class ConfigData implements Data {
    private final Msg msg;
    private final Map<String, Object> configData;
    private final CustomFile configFile;
    private static final String CONFIG_FILE_NAME = "config.yml";
    private boolean checkForUpdates, playSound, spy;
    private String soundName;
    private int soundVolume, soundPitch;

    public ConfigData(Msg msg) {
        this.msg = msg;
        configFile = new CustomFile(msg, CONFIG_FILE_NAME, this);
        configFile.setup();
        configData = new HashMap<>();
        initialize();
    }

    /**
     * Initializing config data.
     * defaultData is used to save all default keys and values that are set by default
     * After the actual data/values are loaded
     */
    private void initialize() {
        final Map<String, Object> defaultData = new LinkedHashMap<>();

        //settings
        defaultData.put("check_for_updates", true);
        defaultData.put("spy_function", true);
        defaultData.put("play_sound", false);
        defaultData.put("sound", "ENTITY_PLAYER_LEVELUP");
        defaultData.put("sound_volume", 1);
        defaultData.put("sound_pitch", 1);

        //messages
        defaultData.put("prefix", "&7[&eYourServer.com&7]");
        defaultData.put("msg_usage", "%prefix% &cUsage: %command% <player> <message>");
        defaultData.put("reply_usage", "%prefix% &cUsage: %command% <message>");
        defaultData.put("ignore_usage", "%prefix% &cUsage: %command% <player>");
        defaultData.put("spy_usage", "%prefix% &cUsage: %command% <player, all, list>");
        defaultData.put("no_permission", "%prefix% &cYou do not have permission to execute this command.");
        defaultData.put("no_message_received", "%prefix% &cYou have not received any messages yet.");
        defaultData.put("player_offline", "%prefix% &cThis player is not online.");
        defaultData.put("player_not_found", "%prefix% &cThis player could not be found.");
        defaultData.put("target_can_not_be_sender", "%prefix% &cYou cannot send a message to yourself.");
        defaultData.put("sender_message", "%prefix% &cYou &8➔ &c%targetName% &8➔ &7%message%");
        defaultData.put("receiver_message", "%prefix% &c%senderName% &8➔ &cYou &8➔ &7%message%");

        //SPY
        defaultData.put("spy_prefix", "&7[&4Spy&7]");
        defaultData.put("spy_disabled", "%prefix% &cSpying is not allowed. Turn on in config.yml");
        defaultData.put("spying_list", "%prefix% &aYou are currently spying the following players &c%spy_list%");
        defaultData.put("can_not_spy_yourself", "%prefix% &cYou cannot spy yourself.");
        defaultData.put("already_spying_player", "%prefix% &cYou are already spying &a%targetName%");
        defaultData.put("already_spying_everyone", "%prefix% &cYou are currently not spying anyone.");
        defaultData.put("not_spying_anyone", "%prefix% &cYou are currently not spying anyone.");
        defaultData.put("not_spying_player", "%prefix% &cYou are currently not spying &a%targetName%");
        defaultData.put("spy_activated_player", "%prefix% &aYou are now spying private messages from &c%targetName%");
        defaultData.put("spy_deactivated_player", "%prefix% &aYou are no longer spying private messages from &c%targetName%");
        defaultData.put("spy_activated_all", "%prefix% &aYou are now spying private messages from everyone");
        defaultData.put("spy_deactivated_all", "%prefix% &aYou are no longer spying private messages from anyone");
        defaultData.put("spy_deactivated_everyone", "%prefix% &aYou are no longer spying private messages from anyone");
        defaultData.put("spy_message", "%spy_prefix% &c%senderName% &8➔ &c%targetName% &8➔ &7%message%");

        defaultData.put("receiver_deactivated", "%prefix% &cThe messaged player has deactivated private messages.");
        defaultData.put("receiver_ignored_you", "%prefix% &cThe messaged player has ignored you.");
        defaultData.put("you_ignored_receiver", "%prefix% &cYou have ignored this player.");
        //  defaultData.put("ignored_players", "%prefix% &aYou have ignored the following players: &7%ignored_list%");
        defaultData.put("activated", "%prefix% &aYou successfully activated private messages.");
        defaultData.put("deactivated", "%prefix% &cYou successfully deactivated private messages.");
        defaultData.put("ignored", "%prefix% &aYou are no longer receiving private messages from &c%targetName%");
        defaultData.put("un_ignored", "%prefix% &aYou are now able to receive messages from &c%targetName% &aagain.");
        defaultData.put("player_already_ignored", "%prefix% &cYou have already ignored this player.");
        defaultData.put("player_is_not_ignored", "%prefix% &cYou have not ignored this player.");
        defaultData.put("can_not_ignore_yourself", "%prefix% &cYou cannot ignore yourself.");
        defaultData.put("reloaded", "%prefix% &aMSG has been reloaded.");
        defaultData.put("update_notify", "%prefix% &aUpdate available for MSG&7: %currentVersion% &8→ &a%latestVersion%");
        defaultData.put("update_download_button", "%prefix% &bClick here to download");

        //Aliases
        defaultData.put("aliases.msg", Arrays.asList("tell", "dm", "pm"));
        defaultData.put("aliases.reply", Collections.singletonList("r"));

        for (String key : defaultData.keySet())
            configFile.getConfig().addDefault(key, defaultData.get(key));

        configFile.getConfig().options().copyDefaults(true);
        configFile.save();

        loadConfigData(defaultData);
    }


    /**
     * Loading modified data from config into Map (RAM).
     * This method requires Map object (of defaultData) because the default Keys are used to get the actual data set in the config.
     *
     * @param data is used for the getting the keys of the config and therefore getting the values set for the corresponding keys
     */
    private void loadConfigData(Map<String, Object> data) {
        configData.clear();
        for (String key : data.keySet()) {
            if (configFile.getConfig().get(key) == null) continue;
            configData.put(key, configFile.getConfig().get(key));
        }

        checkForUpdates = (boolean) getData("check_for_updates");
        playSound = (boolean) getData("play_sound");
        soundName = (String) getData("sound");
        soundVolume = (int) getData("sound_volume");
        soundPitch = (int) getData("sound_pitch");
        spy = (boolean) getData("spy_function");
    }

    /**
     * Registering all Aliases for all commands.
     * Commands are saved in a map and aliases are saved in config
     */

    //Ohne Map möglich
    private void loadAliases() {
        for (CommandExecutor commandExecutor : CommandBase.COMMANDS.keySet()) {
            PluginCommand pluginCommand = CommandBase.COMMANDS.get(commandExecutor);
            List<String> aliases = msg.getConfigData().getAliasesFromConfig(pluginCommand.getName());
            Utils.registerAliases(aliases, commandExecutor, msg);
        }
    }

    /**
     * Reloading Config
     * The config file will be reloaded and the data will be loaded again from Config file into Map (RAM).
     */
    public void reload() {
        configFile.reload();
        initialize();
        loadAliases();
    }

    /**
     * Replace all defined placeholders as well as other placeholders for messages.
     *
     * @param key          in the file
     * @param player       to replace values for placeholders
     * @param placeholders define more placeholders (1: placeholder, 2: value...)
     * @return formated message that has replaced placeholder
     */
    public String getFormatedMessage(String key, Player player, Object... placeholders) {
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
        if (player != null) {
            msg = msg.replaceAll("(?i)" + Pattern.quote("%senderName%"), player.getName()).replaceAll("(?i)" + Pattern.quote("%playerName%"), player.getName());
            if (PlaceholderAPIManager.isInstalled())
                msg = PlaceholderAPI.setPlaceholders(player, msg);
        }

        //The prefix configured in the config will be replaced in every message automatically. The Minecraft color codes are replaced in every msg asw.
        msg = msg.replaceAll("(?i)" + Pattern.quote("%prefix%"), getData("prefix").toString());
        msg = msg.replaceAll("(?i)" + Pattern.quote("%spy_prefix%"), configData.get("spy_prefix").toString());
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }

    /**
     * This method is used to get data from the configData map which was previously filled with the data set in config.
     *
     * @param key is required to find the values in the configData. Keys are matching with config keys as the map got its keys & values from the config.
     * @return object that is set in configData map
     */
    public Object getData(String key) {
        return configData.get(key);
    }

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

    public boolean isCheckForUpdatesTurnedOn() {
        return checkForUpdates;
    }

    public String getSoundName() {
        return soundName;
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public int getSoundPitch() {
        return soundPitch;
    }

    public boolean isPlaySoundEnabled() {
        return playSound;
    }

    public boolean isSpyEnabled() {
        return spy;
    }

    @Override
    public void onFileCreate() {

    }
}
