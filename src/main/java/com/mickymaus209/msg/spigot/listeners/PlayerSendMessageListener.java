package com.mickymaus209.msg.spigot.listeners;

import com.mickymaus209.msg.spigot.Msg;
import com.mickymaus209.msg.spigot.customevents.PlayerSendMessageEvent;
import com.mickymaus209.msg.spigot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSendMessageListener implements Listener {
    private final Msg msg;

    public PlayerSendMessageListener(Msg msg) {
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(this, msg);
    }

    /**
     * Event is called when Player sent private message
     * Here it is used to handle sounds.
     * If it is enabled in config, a sound will be played for the receiver of the private message
     * The type of sound is configured in config.yml
     */
    @EventHandler
    public void onPlayerSendMessage(PlayerSendMessageEvent event) {
        //Player sender = event.getSender();
        Player receiver = event.getReceiver();

        if (!msg.getConfigData().isPlaySoundEnabled()) return;
        String soundName = msg.getConfigData().getSoundName();
        if (!Utils.isValidSound(soundName)) return;
        Sound sound = Sound.valueOf(soundName);

        int volume = msg.getConfigData().getSoundVolume();
        int pitch = msg.getConfigData().getSoundPitch();

        float v = (float) volume;
        float p = (float) pitch;

        //sender.playSound(sender.getLocation(), sound, v, p);
        receiver.playSound(receiver.getLocation(), sound, v, p);
    }
}
