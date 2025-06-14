package com.mickymaus209.msg.spigot.command;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.command.commands.MsgCommand;
import com.mickymaus209.msg.spigot.command.commands.ReplyCommand;
import com.mickymaus209.msg.spigot.command.subcommands.*;
import org.bukkit.entity.Player;

public class CommandHandler {
    private final Msg msg;

    public CommandHandler(Msg msg) {
        this.msg = msg;
    }

    public void registerCommands(){
        CommandRegistry.registerCommand("msg", new MsgCommand(msg), msg);
        CommandRegistry.registerCommand("reply", new ReplyCommand(msg), msg);
    }

    public void registerSubCommands(){
        SubCommandRegistry.register("toggle", new ToggleSubCommand(msg));
        SubCommandRegistry.register("ignore", new IgnoreSubCommand(msg), "mute", "block", "unblock", "unmute", "unignore");
        SubCommandRegistry.register("spy", new SpySubCommand(msg), "unspy");
        SubCommandRegistry.register("reload", new ReloadSubCommand(msg), "rl");
        SubCommandRegistry.register("info", new InfoSubCommand(msg));
    }

    public boolean checkPermission(Player player, String permissionKey) {
        if (!player.hasPermission(permissionKey)) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player));
            return false;
        }
        return true;
    }
}
