package com.mickymaus209.msg.bungeecord.command;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.commands.MsgCommand;
import com.mickymaus209.msg.bungeecord.command.commands.ReplyCommand;
import com.mickymaus209.msg.bungeecord.command.subcommands.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandHandler {
    private final Msg msg;

    public CommandHandler(Msg msg) {
        this.msg = msg;
    }

    public void registerCommands(){
        new MsgCommand(msg, "msg");
        new ReplyCommand(msg, "reply");
    }

    public void registerSubCommands(){
        SubCommandRegistry.register("toggle", new ToggleSubCommand(msg));
        SubCommandRegistry.register("ignore", new IgnoreSubCommand(msg), "mute", "block", "unblock", "unmute", "unignore");
        SubCommandRegistry.register("spy", new SpySubCommand(msg), "unspy");
        SubCommandRegistry.register("reload", new ReloadSubCommand(msg), "rl");
        SubCommandRegistry.register("info", new InfoSubCommand(msg));
    }

    public boolean checkPermission(ProxiedPlayer player, String permissionKey) {
        if (!player.hasPermission(permissionKey)) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player));
            return false;
        }
        return true;
    }
}
