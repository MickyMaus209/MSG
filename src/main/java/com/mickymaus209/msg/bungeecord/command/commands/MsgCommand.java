package com.mickymaus209.msg.bungeecord.command.commands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.CommandBase;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.command.SubCommandRegistry;
import com.mickymaus209.msg.bungeecord.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.bungeecord.utils.MessageBuilder;
import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MsgCommand extends CommandBase {
    private final Msg msg;
    private final String mainCommandName;

    public MsgCommand(Msg msg, String mainCommandName) {
        super(mainCommandName);
        this.msg = msg;
        this.mainCommandName = mainCommandName;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        executeCommand(sender, args, mainCommandName);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can not be used by " + sender.getName()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label));
            return;
        }

        String subCommandKey = args[0].toLowerCase();
        SubCommand subCommand = SubCommandRegistry.getSubCommandMap().get(subCommandKey);
        if (subCommand != null)
            subCommand.execute(player, args, label);
        else
            handlePrivateMessage(player, args, label);
    }

    private void handlePrivateMessage(ProxiedPlayer player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.use")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label, "%senderName%", player.getName()));
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

        TextComponent senderMessage = msg.getConfigData().getFormatedMessage("sender_message", player, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName(), "%sender_server%", player.getServer().getInfo().getName(), "%target_server%", target.getServer().getInfo().getName());
        TextComponent receiverMessage = msg.getConfigData().getFormatedMessage("receiver_message", target, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName(), "%sender_server%", player.getServer().getInfo().getName(), "%target_server%", target.getServer().getInfo().getName());

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                senderMessage = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message, "%sender_server%", player.getServer().getInfo().getName(), "%target_server%", target.getServer().getInfo().getName());
            if (targetGroupFormat != null)
                receiverMessage = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message, "%sender_server%", player.getServer().getInfo().getName(), "%target_server%", target.getServer().getInfo().getName());
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
