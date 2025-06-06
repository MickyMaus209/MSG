package com.mickymaus209.msg.spigot.commands.subcommands;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.commands.CommandHandler;
import com.mickymaus209.msg.spigot.commands.SubCommand;
import com.mickymaus209.msg.spigot.data.PlayerData;
import org.bukkit.entity.Player;

public class ReloadSubCommand implements SubCommand {
    private final Msg msg;

    public ReloadSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(Player player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.reload")) return;

        PlayerData.saveAllPlayerData();
        PlayerData.reloadAllPlayerData(msg);
        msg.getConfigData().reload();
        msg.getGroupsData().reload();
        msg.getSpyManager().reload();

        player.sendMessage(msg.getConfigData().getFormatedMessage("reloaded", player));
    }
}
