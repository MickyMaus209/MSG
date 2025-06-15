package com.mickymaus209.msg.bungeecord.command.commands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.command.SubCommandRegistry;
import com.mickymaus209.msg.bungeecord.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgCommand extends Command {
    private final Msg msg;

    public MsgCommand(Msg msg, String commandName) {
        super(commandName);
        this.msg = msg;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can not be used by " + sender.getName()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + getName()));
            return;
        }

        String subCommandKey = args[0].toLowerCase();
        SubCommand subCommand = SubCommandRegistry.getSubCommandMap().get(subCommandKey);
        if (subCommand != null)
            subCommand.execute(player, args, getName());
        else
            handlePrivateMessage(player, args);
    }

    private void handlePrivateMessage(ProxiedPlayer player, String[] args) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.use")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + getName(), "%senderName%", player.getName()));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("target_can_not_be_sender", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
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
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1) builder.append(" ");
        }

        String message = builder.toString();

        TextComponent senderMessage = msg.getConfigData().getFormatedMessage("sender_message", player, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());
        TextComponent receiverMessage = msg.getConfigData().getFormatedMessage("receiver_message", target, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());

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

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        ProxyServer.getInstance().getPluginManager().callEvent(playerSendMessageEvent);

        msg.getSpyManager().logSpies(player, target, message);
    }
}
