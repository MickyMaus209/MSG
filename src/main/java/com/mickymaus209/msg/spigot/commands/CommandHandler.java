package com.mickymaus209.msg.spigot.commands;

import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import com.mickymaus209.msg.common.UUIDFetcher;
import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.commands.subcommands.*;
import com.mickymaus209.msg.spigot.customevents.PlayerRepliedEvent;
import com.mickymaus209.msg.spigot.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.spigot.data.PlayerData;
import com.mickymaus209.msg.spigot.spy.Spy;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class CommandHandler {
    private final Msg msg;

    public CommandHandler(Msg msg) {
        this.msg = msg;
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

    public void handleToggle(Player player, PlayerData playerData) {
        if (!checkPermission(player, "msg.toggle")) return;

        playerData.setDeactivated(!playerData.isDeactivated());
        String messageKey = playerData.isDeactivated() ? "deactivated" : "activated";
        player.sendMessage(msg.getConfigData().getFormatedMessage(messageKey, player));
    }

    public void handleReload(Player player) {
        if (!checkPermission(player, "msg.reload")) return;

        PlayerData.saveAllPlayerData();
        PlayerData.reloadAllPlayerData(msg);
        msg.getConfigData().reload();
        msg.getGroupsData().reload();
        msg.getSpyManager().reload();

        player.sendMessage(msg.getConfigData().getFormatedMessage("reloaded", player));
    }

    public void handleInfo(Player player) {
        player.sendMessage(ChatColor.AQUA + Utils.center("MSG Plugin\n", 70));
        player.sendMessage("§rDeveloper: §aMickyMaus209\n" +
                "§rVersion:§a " + msg.getDescription().getVersion() + "\n" +
                "§rDownload:§a " + msg.getDescription().getDescription());
    }

    public void handleIgnore(Player player, PlayerData playerData, String[] args, String label, String subCommand) {
        if (!checkPermission(player, "msg.ignore")) return;

        if (args.length == 1) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("ignore_usage", player, "%command%", "/" + label + " " + subCommand));
            return;
        }

        UUIDFetcher.getUUID(args[1]).thenAccept(targetUUID -> {
            if (targetUUID == null) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("player_not_found", player, "%targetName%", args[1]));
                return;
            }

            boolean isUnignore = subCommand.equalsIgnoreCase("unignore");

            if (targetUUID.equals(player.getUniqueId())) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_ignore_yourself", player, "%targetName%", args[1]));
                return;
            }

            if (isUnignore) {
                if (playerData.hasIgnored(targetUUID)) {
                    playerData.unIgnore(targetUUID);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("un_ignored", player, "%targetName%", args[1]));
                } else
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_is_not_ignored", player, "%targetName%", args[1]));
            } else {
                if (playerData.hasIgnored(targetUUID))
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_already_ignored", player, "%targetName%", args[1]));
                else {
                    playerData.ignore(targetUUID);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("ignored", player, "%targetName%", args[1]));
                }
            }
        });
    }

    public void handleSpy(Player player, String[] args, String label, String subCommand) {
        if (!checkPermission(player, "msg.spy")) return;

        if (!msg.getConfigData().isSpyEnabled()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("spy_disabled", player));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("spy_usage", player, "%command%", "/" + label + " " + subCommand));
            return;
        }

        String arg = args[1].toLowerCase();
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
                Set<Player> spyingPlayers = spy.getSpyingPlayers();
                if (spyingPlayers.isEmpty() && !spy.isSpyAll())
                    player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_anyone", player));
                else {
                    String list = spy.isSpyAll() ? "ALL" : spyingPlayers.stream().map(Player::getName).collect(Collectors.joining(", "));
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spying_list", player, "%spy_list%", list));
                }
                break;

            default:
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
                    return;
                }

                if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_spy_yourself", player, "%targetName%", args[1]));
                    return;
                }

                if (subCommand.equalsIgnoreCase("spy")) {
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

    public void handlePrivateMessage(Player player, String[] args, String label) {
        if (!checkPermission(player, "msg.use")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + label));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("target_can_not_be_sender", player, "%targetName%", target.getName()));
            return;
        }

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName()));
            return;
        }

        PlayerData targetData = PlayerData.getPlayerData(target.getUniqueId(), msg);

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName()));
            return;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName()));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append(" ");
        }

        String message = builder.toString();

        String playerMsg = msg.getConfigData().getFormatedMessage("sender_message", player,
                "%targetName%", target.getName(),
                "%message%", message);

        String targetMsg = msg.getConfigData().getFormatedMessage("receiver_message", player,
                "%targetName%", target.getName(),
                "%message%", message);

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                playerMsg = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message);
            if (targetGroupFormat != null)
                targetMsg = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
        }

        player.sendMessage(playerMsg);
        target.sendMessage(targetMsg);

        MsgRegistry.registerPlayerInteraction(player.getUniqueId(), target.getUniqueId());
        MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        Bukkit.getPluginManager().callEvent(playerSendMessageEvent);

        msg.getSpyManager().logSpies(player, target, message);
    }
}
