package com.mickymaus209.msg.bungeecord.utils;

import com.mickymaus209.msg.bungeecord.data.ConfigData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MessageBuilder {
    private Object[] placeholders;
    private final ConfigData configData;
    private final ProxiedPlayer player;

    public MessageBuilder(ConfigData configData, ProxiedPlayer player, Object... placeholders) {
        this.placeholders = placeholders;
        this.configData = configData;
        this.player = player;
    }

    public MessageBuilder addPlaceholder(Object key, Object value) {
        List<Object> placeholdersList = new ArrayList<>(Arrays.asList(placeholders));
        placeholdersList.add(key);
        placeholdersList.add(value);
        placeholders = placeholdersList.toArray();
        return this;
    }

    /*
            MessageBuilder sMsg = new MessageBuilder(msg.getConfigData(), player, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName(), "%sender_server%", player.getServer().getInfo().getName(), "%target_server%", target.getServer().getInfo().getName());
        TextComponent sMsg1 = sMsg.build("sender_message");
        TextComponent sMsg2 = sMsg.build("receiver_message");
     */

    public TextComponent build(String key){
        String msg = configData.getData(key).toString();

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
        msg = msg.replaceAll("(?i)" + Pattern.quote("%prefix%"), configData.getData("prefix").toString());
        msg = msg.replaceAll("(?i)" + Pattern.quote("%spy_prefix%"), configData.getData("spy_prefix").toString());
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return new TextComponent(msg);
    }
}
