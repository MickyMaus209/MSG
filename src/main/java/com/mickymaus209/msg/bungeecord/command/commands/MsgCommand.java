package com.mickymaus209.msg.bungeecord.command.commands;

import com.mickymaus209.msg.bungeecord.Msg;
import com.mickymaus209.msg.bungeecord.command.CommandBase;
import com.mickymaus209.msg.bungeecord.command.SubCommand;
import com.mickymaus209.msg.bungeecord.command.SubCommandRegistry;
import com.mickymaus209.msg.bungeecord.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.bungeecord.data.PlayerData;
import com.mickymaus209.msg.common.GroupFormat;
import com.mickymaus209.msg.common.MsgRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MsgCommand extends CommandBase {
    private final Msg msg;

    public MsgCommand(Msg msg, String name) {
        super(msg, name);
        this.msg = msg;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can not be used by " + sender.getName()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            player.sendMessage(getMsg().getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + getName()));
            return;
        }

        String subCommandKey = args[0].toLowerCase();
        SubCommand subCommand = SubCommandRegistry.getSubCommandMap().get(subCommandKey);
        if (subCommand != null)
            subCommand.execute(player, args, getName());
        else
            handlePrivateMessage(player, args);
    }

    private void handlePrivateMessage(ProxiedPlayer player, String[] args) {
        if (msg.getCommandHandler().checkPermission(player, "msg.use")) return;

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + getName(), "%senderName%", player.getName()));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("target_can_not_be_sender", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        PlayerData targetData = PlayerData.getPlayerData(target.getUniqueId(), msg);

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1) builder.append(" ");
        }

        String message = builder.toString();

        TextComponent senderMessage = msg.getConfigData().getFormatedMessage("sender_message", player, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());
        TextComponent receiverMessage = msg.getConfigData().getFormatedMessage("receiver_message", target, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                senderMessage = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message);
            if (targetGroupFormat != null)
                receiverMessage = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
        }

        player.sendMessage(senderMessage);
        target.sendMessage(receiverMessage);

        MsgRegistry.registerPlayerInteraction(player.getUniqueId(), target.getUniqueId());
        MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        ProxyServer.getInstance().getPluginManager().callEvent(playerSendMessageEvent);

        msg.getSpyManager().logSpies(player, target, message);
    }

   /* @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can not be used by " + sender.getName()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        PlayerData playerData = PlayerData.getPlayerData(player.getUniqueId(), msg);

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {

                if (!player.hasPermission("msg.toggle")) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                    return;
                }

                if (!playerData.isDeactivated()) {
                    playerData.setDeactivated(true);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("deactivated", player, "%senderName%", player.getName()));
                } else {
                    playerData.setDeactivated(false);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("activated", player, "%senderName%", player.getName()));
                }
                return;

            } else if (args[0].equalsIgnoreCase("reload")) {

                if (!player.hasPermission("msg.reload")) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                    return;
                }

                PlayerData.saveAllPlayerData();
                PlayerData.reloadAllPlayerData(msg);
                msg.getConfigData().reload();
                msg.getGroupsData().reload();
                msg.getSpyManager().reload();
                player.sendMessage(msg.getConfigData().getFormatedMessage("reloaded", player, "%senderName%", player.getName()));
                return;
            } else if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage(new TextComponent(ChatColor.AQUA + Utils.center("MSG Plugin\n", 70)));
                player.sendMessage(new TextComponent("§rDeveloper: §aMickyMaus209\n" + "§rVersion:§a " + msg.getDescription().getVersion() + "\n" + "§rDownload:§a " + msg.getDescription().getDescription()));
                return;
            } else if (args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("unIgnore") || args[0].equalsIgnoreCase("unblock") || args[0].equalsIgnoreCase("unmute")) {
                if (!player.hasPermission("msg.ignore")) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                    return;
                }
                player.sendMessage(msg.getConfigData().getFormatedMessage("ignore_usage", player, "%command%", "/" + getName() + " " + args[0]));
                return;
            } else if (args[0].equalsIgnoreCase("spy") || args[0].equalsIgnoreCase("unspy")) {
                if (!player.hasPermission("msg.spy")) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                    return;
                }
                if (!msg.getConfigData().isSpyEnabled()) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spy_disabled", player));
                    return;
                }
                player.sendMessage(msg.getConfigData().getFormatedMessage("spy_usage", player, "%command%", "/" + getName() + " " + args[0]));
                return;
            }
        }

        //ADD A Cache limit of 1mb to the PlayerDataFile
        if (args.length == 2 && (args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("mute"))) {
            if (!player.hasPermission("msg.ignore")) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                return;
            }
            UUIDFetcher.getUUID(args[1]).thenAccept(targetUUID -> {
                if (targetUUID == null)
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_not_found", player, "%targetName%", args[1], "%senderName%", player.getName()));
                else {
                    if (targetUUID.equals(player.getUniqueId())) {
                        player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_ignore_yourself", player, "%targetName%", args[1], "%senderName%", player.getName()));
                        return;
                    }
                    if (playerData.hasIgnored(targetUUID))
                        player.sendMessage(msg.getConfigData().getFormatedMessage("player_already_ignored", player, "%targetName%", args[1], "%senderName%", player.getName()));
                    else {
                        playerData.ignore(targetUUID);
                        player.sendMessage(msg.getConfigData().getFormatedMessage("ignored", player, "%targetName%", args[1], "%senderName%", player.getName()));
                    }
                }
            });
            return;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("unIgnore") || args[0].equalsIgnoreCase("unblock") || args[0].equalsIgnoreCase("unmute"))) {
            if (!player.hasPermission("msg.ignore")) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
                return;
            }
            UUIDFetcher.getUUID(args[1]).thenAccept(targetUUID -> {
                if (targetUUID == null)
                    player.sendMessage(msg.getConfigData().getFormatedMessage("player_not_found", player, "%targetName%", args[1], "%senderName%", player.getName()));
                else {
                    if (playerData.hasIgnored(targetUUID)) {
                        playerData.unIgnore(targetUUID);
                        player.sendMessage(msg.getConfigData().getFormatedMessage("un_ignored", player, "%targetName%", args[1], "%senderName%", player.getName()));
                    } else
                        player.sendMessage(msg.getConfigData().getFormatedMessage("player_is_not_ignored", player, "%targetName%", args[1], "%senderName%", player.getName()));
                }
            });
            return;
        }

        //spy
        if (args.length == 2 && (args[0].equalsIgnoreCase("spy"))) {
            //check if enabled
            if (!msg.getConfigData().isSpyEnabled()) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("spy_disabled", player));
                return;
            }
            Spy spy = Spy.getInstance(player);
            //all
            if (args[1].equalsIgnoreCase("all")) {
                if (!spy.isSpyAll()) {
                    spy.setSpyAll(true);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spy_activated_all", player));
                } else {
                    //spy.setSpyAll(false);
                    player.sendMessage(msg.getConfigData().getFormatedMessage("already_spying_everyone", player));
                }
                return;
            }
            //list
            if (args[1].equalsIgnoreCase("list")) {
                Set<ProxiedPlayer> spyingPlayerList = spy.getSpyingPlayers();
                if (spyingPlayerList.isEmpty() && !spy.isSpyAll()) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_anyone", player));
                    return;
                }
                player.sendMessage(msg.getConfigData().getFormatedMessage("spying_list", player, "%spy_list%", spy.isSpyAll() ? "ALL" : spy.getSpyingPlayers()));
                return;
            }

            //Spying player
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
                return;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("can_not_spy_yourself", player, "%targetName%", args[1]));
                return;
            }

            if (spy.isSpying(target)) {
                //spy.removePlayerFromSpyingList(target);
                player.sendMessage(msg.getConfigData().getFormatedMessage("already_spying_player", player, "%targetName%", args[1]));
                return;
            }
            //add to spy
            spy.addPlayerToSpyingList(target);
            player.sendMessage(msg.getConfigData().getFormatedMessage("spy_activated_player", player, "%targetName%", args[1]));
            return;
        }

        //unspy
        if (args.length == 2 && (args[0].equalsIgnoreCase("unspy"))) {
            if (!msg.getConfigData().isSpyEnabled()) {
                player.sendMessage(msg.getConfigData().getFormatedMessage("spy_disabled", player));
                return;
            }
            Spy spy = Spy.getInstance(player);
            if (args[1].equalsIgnoreCase("all")) {
                if (spy.isSpyAll()) {
                    spy.setSpyAll(false);
                    spy.getSpyingPlayers().clear();
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_all", player));
                } else if (!spy.isSpyAll() && !spy.getSpyingPlayers().isEmpty()) {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_everyone", player));
                    spy.getSpyingPlayers().clear();
                } else {
                    player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_anyone", player));
                }
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
            if (spy.isSpying(target)) {
                spy.removePlayerFromSpyingList(target);
                player.sendMessage(msg.getConfigData().getFormatedMessage("spy_deactivated_player", player, "%targetName%", args[1]));
            } else
                player.sendMessage(msg.getConfigData().getFormatedMessage("not_spying_player", player, "%targetName%", args[1]));
            return;
        }


        //Actual msg
        if (!player.hasPermission("msg.use")) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("no_permission", player, "%senderName%", player.getName()));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("msg_usage", player, "%command%", "/" + getName(), "%senderName%", player.getName()));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("player_offline", player, "%senderName%", player.getName()));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("target_can_not_be_sender", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        if (playerData.hasIgnored(target.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("you_ignored_receiver", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        PlayerData targetData = PlayerData.getPlayerData(target.getUniqueId(), msg);

        if (targetData.isDeactivated()) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_deactivated", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        if (targetData.hasIgnored(player.getUniqueId())) {
            player.sendMessage(msg.getConfigData().getFormatedMessage("receiver_ignored_you", player, "%targetName%", target.getName(), "%senderName%", player.getName()));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1) builder.append(" ");
        }

        String message = builder.toString();

        TextComponent senderMessage = msg.getConfigData().getFormatedMessage("sender_message", player, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());

        TextComponent receiverMessage = msg.getConfigData().getFormatedMessage("receiver_message", target, "%targetName%", target.getName(), "%message%", message, "%senderName%", player.getName());

        if (msg.getGroupsData().isEnabled()) {
            GroupFormat senderGroupFormat = msg.getGroupsData().findGroupFormat(player);
            GroupFormat targetGroupFormat = msg.getGroupsData().findGroupFormat(target);

            if (senderGroupFormat != null)
                senderMessage = msg.getGroupsData().formatMessage(senderGroupFormat.getSenderFormat(), player, "%targetName%", target.getName(), "%message%", message);
            if (targetGroupFormat != null)
                receiverMessage = msg.getGroupsData().formatMessage(targetGroupFormat.getReceiverFormat(), player, "%targetName%", target.getName(), "%message%", message);
        }

        player.sendMessage(senderMessage);
        target.sendMessage(receiverMessage);

        MsgRegistry.registerPlayerInteraction(player.getUniqueId(), target.getUniqueId());
        MsgRegistry.registerPlayerInteraction(target.getUniqueId(), player.getUniqueId());

        PlayerSendMessageEvent playerSendMessageEvent = new PlayerSendMessageEvent(player, target, message);
        ProxyServer.getInstance().getPluginManager().callEvent(playerSendMessageEvent);

        msg.getSpyManager().logSpies(player, target, message);
    }

    */
}
