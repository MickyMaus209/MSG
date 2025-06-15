package com.mickymaus209.msg.bungeecord.command.subcommands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.ChatColor;

public class InfoSubCommand implements SubCommand {
    private final Msg msg;

    public InfoSubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args, String label) {
        player.sendMessage(new TextComponent(ChatColor.AQUA + Utils.center("MSG Plugin\n", 70)));
        player.sendMessage(new TextComponent("§rDeveloper: §aMickyMaus209\n" +
                "§rVersion:§a " + msg.getDescription().getVersion()));

        TextComponent downloadText = new TextComponent("§rDownload:§a ");
        TextComponent downloadButton = Utils.getClickAbleUrlMessage("§a[§nCLICK HERE§r§a]", msg.getDescription().getDescription(), "§7Open link");
        downloadText.addExtra(downloadButton);

        player.sendMessage(downloadText);
    }
}
