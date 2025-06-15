package com.mickymaus209.msg.bungeecord.command.subcommands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.spy.Spy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.stream.Collectors;

public class SpySubCommand implements SubCommand {
    private final Msg msg;

    public SpySubCommand(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args, String label) {
        if (!msg.getCommandHandler().checkPermission(player, "msg.spy")) return;

        if (!msg.getConfigData().isSpyEnabled()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("spy_disabled", player));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("spy_usage", player, "%command%", "/" + label.toLowerCase() + " " + args[0].toLowerCase()));
            return;
        }

        String arg = args[1].toLowerCase();
        String subCommand = args[0].toLowerCase();
        Spy spy = Spy.getInstance(player);

        switch (arg) {
            case "all":
                if (subCommand.equals("spy")) {
                    if (!spy.isSpyAll()) {
                        spy.setSpyAll(true);
                        player.sendMessage(msg.getConfigData().getFormatedMessage("spy_activated_all", player));
                    } else
                        player.sendMessage(msg.getConfigData().getFormatedMessage("already_spying_everyone", player));
                } else if (subCommand.equals("unspy")) { // unspy all
                    if (spy.isSpyAll()) {
                        spy.setSpyAll(false);
                        spy.getSpyingPlayers().clear();
                        player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_all", player));
                    } else if (!spy.getSpyingPlayers().isEmpty()) {
                        spy.getSpyingPlayers().clear();
                        player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_everyone", player));
                    } else
                        player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_anyone", player));
                }
                break;

            case "list":
                Set<ProxiedPlayer> spyingPlayers = spy.getSpyingPlayers();
                if (spyingPlayers.isEmpty() && !spy.isSpyAll())
                    player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_anyone", player));
                else {
                    String list = spy.isSpyAll() ? "ALL" : spyingPlayers.stream().map(ProxiedPlayer::getName).collect(Collectors.joining(", "));
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spying_list", player, "%spy_list%", list));
                }
                break;

            default:
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if (targetPlayer == null) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
                    return;
                }

                if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_spy_yourself", player, "%targetName%", args[1]));
                    return;
                }

                if (subCommand.equalsIgnoreCase("spy")) {
                    if (spy.isSpyAll()) {
                        player.sendMessage(msg.getConfigData().getFormatedMessage("already_spying_everyone", player));
                        return;
                    }
                    if (spy.isSpying(targetPlayer))
                        player.sendMessage(msg.getConfigData().getFormatedMessage("already_spying_player", player, "%targetName%", args[1]));
                    else {
                        spy.addPlayerToSpyingList(targetPlayer);
                        player.sendMessage(msg.getConfigData().getFormatedMessage("spy_activated_player", player, "%targetName%", args[1]));
                    }
                } else if (subCommand.equalsIgnoreCase("unspy")) { // unspy player
                    if (spy.isSpying(targetPlayer)) {
                        spy.removePlayerFromSpyingList(targetPlayer);
                        player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_player", player, "%targetName%", args[1]));
                    } else
                        player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_player", player, "%targetName%", args[1]));
                }
                break;
        }
    }
}
