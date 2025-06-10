package com.mickymaus209.msg.bungeecord.command.subcommands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.common.UUIDFetcher;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class IgnoreSubCommand implements SubCommand {
    private final Msg msg;

    public IgnoreSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.ignore")) return;

        if (args.length == 1) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("ignore_usage", player, "%command%", "/" + label.toLowerCase() + " " + args[0].toLowerCase()));
            return;
        }

        UUIDFetcher.getUUID(args[1]).thenAccept(targetUUID -> {
            if (targetUUID == null) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("player_not_found", player, "%targetName%", args[1]));
                return;
            }

            String subCommand = args[0].toLowerCase();
            boolean isUnignore = subCommand.startsWith("un");

            if (targetUUID.equals(player.getUniqueId())) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_ignore_yourself", player, "%targetName%", args[1]));
                return;
            }

            PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

            if (isUnignore) {
                if (playerData.hasIgnored(targetUUID)) {
                    playerData.unIgnore(targetUUID);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("un_ignored", player, "%targetName%", args[1]));
                } else
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_is_not_ignored", player, "%targetName%", args[1]));
            } else {
                if (playerData.hasIgnored(targetUUID))
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_already_ignored", player, "%targetName%", args[1]));
                else {
                    playerData.ignore(targetUUID);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("ignored", player, "%targetName%", args[1]));
                }
            }
        });
    }
}
