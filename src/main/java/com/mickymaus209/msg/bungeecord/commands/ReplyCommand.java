package com.mickymaus209.msg.bungeecord.commands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.customevents.PlayerRepliedEvent;
import com.mickymaus209.msg.bungeecord.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReplyCommand extends CommandBase {
    private final Msg msg;

    public ReplyCommand(String name, Msg msg) {
        super(msg, name);
        this.msg = msg;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can not be used by " + sender.getName()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("reply.use") && (!player.hasPermission("msg.reply"))) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("reply_usage", player, "%command%", "/" + getName(), "%senderName%", player.getName()));
            return;
        }

        if (!MsgRegistry.hasMessagedBefore(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_message_received", player, "%senderName%", player.getName()));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(MsgRegistry.getLastInteraction(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
            return;
        }

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        PlayerData targetData = PlayerData.getPlayerData(target.getUniqueId(), msg);

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        String message = builder.toString();

        TextComponent senderMessage = msg.getConfigData().getFormatedMessage("sender_message", player,
                "%targetName%", target.getName(),
                "%message%", message, "%senderName%", player.getName());

        TextComponent receiverMessage = msg.getConfigData().getFormatedMessage("receiver_message", target,
                "%targetName%", player.getName(),
                "%message%", message, "%senderName%", player.getName());

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                senderMessage = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message);
            if (targetGroupFormat != null)
                receiverMessage = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
        }

        player.sendMessage(senderMessage);
        target.sendMessage(receiverMessage);

        MsgRegistry.registerPlayerInteraction(player.getUniqueId(), target.getUniqueId());
        MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

        //Call Custom Events
        PlayerRepliedEvent repliedEvent = new PlayerRepliedEvent(player, target, message);
        ProxyServer.getInstance().getPluginManager().callEvent(repliedEvent);

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        ProxyServer.getInstance().getPluginManager().callEvent(playerSendMessageEvent);

        //Log Message for spying
        msg.getSpyManager().logSpies(player, target, message);
    }
}
