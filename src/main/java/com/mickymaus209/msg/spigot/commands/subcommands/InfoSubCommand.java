package com.mickymaus209.msg.spigot.commands.subcommands;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.commands.SubCommand;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InfoSubCommand implements SubCommand {
    private final Msg msg;

    public InfoSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(Player player, String[] args, String label) {
        player.sendMessage(ChatColor.AQUA + Utils.center("MSG Plugin\n", 70));
        player.sendMessage("§rDeveloper: §aMickyMaus209\n" +
                "§rVersion:§a " + msg.getDescription().getVersion() + "\n" +
                "§rDownload:§a " + msg.getDescription().getDescription());
    }
}
