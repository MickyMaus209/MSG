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

    /**
     * Registering all commands
     */
    public void registerCommands(){
        new MsgCommand(msg, "msg");
        new ReplyCommand(msg, "reply");
    }

    /**
     * Registering all sub commands
     */
    public void registerSubCommands(){
        SubCommandRegistry.register("toggle", new ToggleSubCommand(msg));
        SubCommandRegistry.register("ignore", new IgnoreSubCommand(msg), "mute", "block", "unblock", "unmute", "unignore");
        SubCommandRegistry.register("spy", new SpySubCommand(msg), "unspy");
        SubCommandRegistry.register("reload", new ReloadSubCommand(msg), "rl");
        SubCommandRegistry.register("info", new InfoSubCommand(msg));
    }

    /**
     * Check if player has permission and send him message if they don't
     * @param player - to be checked for permission
     * @param permissionKey - actual permission to check
     * @return - boolean true if player has permission and false if they don't
     */
    public boolean checkPermission(ProxiedPlayer player, String permissionKey) {
        if (!player.hasPermission(permissionKey)) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player));
            return false;
        }
        return true;
    }
}
