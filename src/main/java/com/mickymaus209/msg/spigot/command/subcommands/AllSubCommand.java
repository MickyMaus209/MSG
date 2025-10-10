package com.mickymaus209.msg.spigot.command.subcommands;

import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.SubCommand;
import com.mickymaus209.msg.spigot.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.spigot.data.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AllSubCommand implements SubCommand {
    private final Msg msg;

    public AllSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(Player player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.use.all")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label));
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
                "%targetName%", "ALL",
                "%message%", message);

        String targetMsg = msg.getConfigData().getFormatedMessage("receiver_message", player,
                "%targetName%", "ALL",
                "%message%", message);

        List<Player> sendingPlayers = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if(target.getUniqueId().equals(player.getUniqueId())) continue;

            PlayerData playerData = msg.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (playerData.hasIgnored(target.getUniqueId())) continue;

            PlayerData targetData = msg.getPlayerDataManager().getPlayerData(target.getUniqueId());
            if(targetData.isDeactivated()) continue;
            if(targetData.hasIgnored(player.getUniqueId())) continue;

            sendingPlayers.add(target);

            if (msg.getGroupsData().isEnabled()) {
                GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);
                if (targetGroupFormat != null)
                    targetMsg = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
            }

            target.sendMessage(targetMsg);
            MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

            PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
            Bukkit.getPluginManager().callEvent(playerSendMessageEvent);

            msg.getSpyManager().logSpies(player, target, message);
        }

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat playerGroupFormat = msg.getGroupsData().findGroupFormat(player);
            if (playerGroupFormat != null)
                playerMsg = msg.getGroupsData().formatMessage(playerGroupFormat.getSenderFormat(), player, "%targetName%", "ALL", "%message%", message);
        }

        player.sendMessage(playerMsg);
    }
}
