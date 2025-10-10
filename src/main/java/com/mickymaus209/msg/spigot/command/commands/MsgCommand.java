package com.mickymaus209.msg.spigot.command.commands;

import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.SubCommand;
import com.mickymaus209.msg.spigot.command.SubCommandRegistry;
import com.mickymaus209.msg.spigot.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.spigot.data.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand implements CommandExecutor {
    private final Msg msg;

    public MsgCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can not be used by " + sender.getName());
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label));
            return false;
        }

        String subCommandKey = args[0].toLowerCase();
        SubCommand subCommand = SubCommandRegistry.getSubCommandMap().get(subCommandKey);
        if (subCommand != null)
            subCommand.execute(player, args, label);
        else{
            handlePrivateMessage(player, args, label);
        }

        return false;
    }

    public void handlePrivateMessage(Player player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.use")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("target_can_not_be_sender", player, "%targetName%", target.getName()));
            return;
        }

        PlayerData playerData = msg.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName()));
            return;
        }

        PlayerData targetData = msg.getPlayerDataManager().getPlayerData(target.getUniqueId());

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName()));
            return;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName()));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append(" ");
        }

        String message = builder.toString();

        String playerMsg = msg.getConfigData().getFormatedMessage("sender_message", player,
                "%targetName%", target.getName(),
                "%message%", message);

        String targetMsg = msg.getConfigData().getFormatedMessage("receiver_message", player,
                "%targetName%", target.getName(),
                "%message%", message);

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                playerMsg = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message);
            if (targetGroupFormat != null)
                targetMsg = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
        }

        player.sendMessage(playerMsg);
        target.sendMessage(targetMsg);

        MsgRegistry.registerPlayerInteraction(player.getUniqueId(), target.getUniqueId());
        MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        Bukkit.getPluginManager().callEvent(playerSendMessageEvent);

        msg.getSpyManager().logSpies(player, target, message);
    }
}
