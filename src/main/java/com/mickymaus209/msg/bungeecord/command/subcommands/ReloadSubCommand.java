package com.mickymaus209.msg.bungeecord.command.subcommands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReloadSubCommand implements SubCommand {
    private final Msg msg;

    public ReloadSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.reload")) return;

        PlayerData.saveAllPlayerData();
        PlayerData.reloadAllPlayerData(msg);
        msg.getConfigData().reload();
        msg.getGroupsData().reload();
        msg.getSpyManager().reload();

        player.sendMessage(msg.getConfigData().getFormatedMessage("reloaded", player));
    }
}
