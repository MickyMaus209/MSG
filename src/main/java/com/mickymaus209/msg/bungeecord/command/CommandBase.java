package com.mickymaus209.msg.bungeecord.command;

import com.mickymaus209.msg.bungeecord.Msg;
import net.md_5.bungee.api.plugin.Command;


@SuppressWarnings("unused")
public abstract class CommandBase extends Command {
    private final Msg msg;

    /**
     * Registering command in BungeeCord API, registering Command in {@link CommandRegistry} and registering all aliases for this command.
     * @param msg JavaPlugin main object
     * @param name - command name for execution
     */
    public CommandBase(Msg msg, String name) {
        super(name);
        this.msg = msg;
        //CommandRegistry.registerCommand(name, this, msg);
    }

    public Msg getMsg() {
        return msg;
    }
}
