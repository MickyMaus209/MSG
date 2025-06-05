package com.mickymaus209.msg.bungeecord.commands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.registries.CommandRegistry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;


public abstract class CommandBase extends Command {
    private final Msg msg;

    /**
     * Registering command in BungeeCord API, registering Command in CommandRegistry and registering all aliases for this command.
     * @param msg JavaPlugin main object
     * @param name - command name for execution
     */
    public CommandBase(Msg msg, String name) {
        super(name);
        this.msg = msg;
        ProxyServer.getInstance().getPluginManager().registerCommand(msg, this);
        CommandRegistry.registerCommand(name, this, msg);
        msg.getAliasManager().registerAliases(name);
    }

    public Msg getMsg() {
        return msg;
    }
}
