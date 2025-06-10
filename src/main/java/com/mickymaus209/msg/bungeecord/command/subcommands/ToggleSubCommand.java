package com.mickymaus209.msg.bungeecord.command.subcommands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ToggleSubCommand implements SubCommand {
    private final Msg msg;

    public ToggleSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.toggle")) return;

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        playerData.setDeactivated(!playerData.isDeactivated());
        String messageKey = playerData.isDeactivated() ? "deactivated" : "activated";
        player.sendMessage(msg.getConfigData().getFormatedMessage(messageKey, player));
    }
}
