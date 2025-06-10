package com.mickymaus209.msg.spigot.command.commands;

import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.CommandBase;
import com.mickymaus209.msg.spigot.customevents.PlayerRepliedEvent;
import com.mickymaus209.msg.spigot.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.spigot.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends CommandBase {
    private final Msg msg;

    public ReplyCommand(Msg msg, String commandName) {
        super(msg, commandName);
        this.msg = getMsg();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can not be used by " + sender.getName());
            return false;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("reply.use") && (!player.hasPermission("msg.reply"))) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player));
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("reply_usage", player, "%command%", "/" + label));
            return false;
        }

        if (!MsgRegistry.hasMessagedBefore(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_message_received", player));
            return false;
        }

        Player target = Bukkit.getPlayer(MsgRegistry.getLastInteraction(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player));
            return false;
        }

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName()));
            return false;
        }

        PlayerData targetData = PlayerData.getPlayerData(target.getUniqueId(), msg);

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName()));
            return false;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName()));
            return false;
        }

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        String message = builder.toString();
        String playerMsg;
        String targetMsg;

        playerMsg = msg.getConfigData().getFormatedMessage("sender_message", player,
                "%targetName%", target.getName(),
                "%message%", message);

        targetMsg = msg.getConfigData().getFormatedMessage("receiver_message", player,
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

        //Custom Events
        PlayerRepliedEvent repliedEvent = new PlayerRepliedEvent(target, player, message);
        Bukkit.getPluginManager().callEvent(repliedEvent);

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        Bukkit.getPluginManager().callEvent(playerSendMessageEvent);

        //Log message for spying
        msg.getSpyManager().logSpies(player, target, message);
        return false;
    }
}
