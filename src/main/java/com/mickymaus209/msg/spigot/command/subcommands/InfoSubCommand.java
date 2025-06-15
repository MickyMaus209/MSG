package com.mickymaus209.msg.spigot.command.subcommands;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.SubCommand;
import com.mickymaus209.msg.spigot.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
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
                "§rVersion:§a " + msg.getDescription().getVersion());

        TextComponent downloadText = new TextComponent("§rDownload:§a ");
        TextComponent downloadButton = Utils.getClickAbleUrlMessage("§a[§nCLICK HERE§r§a]", msg.getDescription().getDescription(), "§7Open link");
        downloadText.addExtra(downloadButton);

        player.spigot().sendMessage(downloadText);
    }
}
