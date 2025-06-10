package com.mickymaus209.msg.bungeecord.listeners;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.commands.MsgCommand;
import com.mickymaus209.msg.bungeecord.registries.CommandRegistry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;
import java.util.stream.Collectors;

public class TabCompleteListener implements Listener {
    private final Msg msg;

    public TabCompleteListener(Msg msg) {
        this.msg = msg;
        ProxyServer.getInstance().getPluginManager().registerListener(msg, this);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String cursor = event.getCursor();

        String[] args = event.getCursor().split(" ");
        System.out.println("test");

        Collection<Command> allCommands = CommandRegistry.getAll();
        allCommands.addAll(msg.getAliasManager().getRegisteredAliasWrappers());


        Command cmd = null;
        for (Command command : allCommands) {
            System.out.println(command.getName());
            if (!cursor.startsWith(command.getName().toLowerCase())) continue;
            cmd = command;
        }
        if (cmd == null) return;

        if (!(cmd instanceof MsgCommand)) return;
        if (args.length != 2) return;
        event.getSuggestions().clear();
        event.getSuggestions().addAll(ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList()));
    }
}
