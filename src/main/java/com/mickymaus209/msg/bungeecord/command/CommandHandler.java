package com.mickymaus209.msg.bungeecord.command;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.commands.MsgCommand;
import com.mickymaus209.msg.bungeecord.command.commands.ReplyCommand;
import com.mickymaus209.msg.bungeecord.command.subcommands.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Handles registration of main commands and subcommands for the plugin.
 * Also provides a helper method to check player permissions with automatic messaging.
 */
public class CommandHandler {
    private final Msg msg;

    public CommandHandler(Msg msg) {
        this.msg = msg;
    }

    /**
     * Registers the main plugin commands using the CommandRegistry.
     * <p>
     * These are the base commands such as /msg and /reply.
     */
    public void registerCommands() {
        // new MsgCommand(msg, "msg");
        //  new ReplyCommand(msg, "reply");
        CommandRegistry.registerCommand(new MsgCommand(msg, "msg"), msg);
        CommandRegistry.registerCommand(new ReplyCommand(msg, "reply"), msg);
    }

    /**
     * Registers subcommands that belong to the base command structure.
     * <p>
     * Each subcommand can be registered with multiple aliases if needed.
     */
    public void registerSubCommands() {
        SubCommandRegistry.register("toggle", new ToggleSubCommand(msg));
        SubCommandRegistry.register("ignore", new IgnoreSubCommand(msg), "mute", "block", "unblock", "unmute", "unignore");
        SubCommandRegistry.register("spy", new SpySubCommand(msg), "unspy");
        SubCommandRegistry.register("reload", new ReloadSubCommand(msg), "rl");
        SubCommandRegistry.register("info", new InfoSubCommand(msg));
    }

    /**
     * Checks whether a player has the specified permission key.
     * Sends a 'no permission' message if the player is not allowed.
     *
     * @param player        The player to check.
     * @param permissionKey The permission node to check against.
     * @return              True if the player has the permission, false otherwise.
     */
    public boolean checkPermission(ProxiedPlayer player, String permissionKey) {
        if (!player.hasPermission(permissionKey)) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player));
            return false;
        }
        return true;
    }
}
