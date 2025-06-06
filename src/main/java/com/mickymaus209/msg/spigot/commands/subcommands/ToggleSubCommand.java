package com.mickymaus209.msg.spigot.commands.subcommands;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.commands.CommandHandler;
import com.mickymaus209.msg.spigot.commands.SubCommand;
import com.mickymaus209.msg.spigot.data.PlayerData;
import org.bukkit.entity.Player;

public class ToggleSubCommand implements SubCommand {
    private final Msg msg;

    public ToggleSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(Player player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.toggle")) return;

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        playerData.setDeactivated(!playerData.isDeactivated());
        String messageKey = playerData.isDeactivated() ? "deactivated" : "activated";
        player.sendMessage(msg.getConfigData().getFormatedMessage(messageKey, player));
    }
}
