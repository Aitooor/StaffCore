package me.fckml.staffcore.profile.listeners;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.redis.packet.handler.IncomingPacketHandler;
import me.fckml.staffcore.redis.packet.listener.PacketListener;
import me.fckml.staffcore.redis.packets.ChatPacket;
import me.fckml.staffcore.redis.packets.StaffJoinedPacket;
import me.fckml.staffcore.redis.packets.StaffLeftPacket;
import me.fckml.staffcore.redis.packets.StaffSwitchedPacket;
import me.fckml.staffcore.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProfilePacketListener implements PacketListener {

    @IncomingPacketHandler
    public void onStaffJoined(StaffJoinedPacket packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("core.staff")) continue;
            Profile profile = Profile.getProfileByUUID(player.getUniqueId());

            player.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("SERVER.JOINED").replace("<server>", packet.getServer()).replace("<player>", packet.getName())));
        }

        Bukkit.getConsoleSender().sendMessage(CC.translate("&b[S] &3" + packet.getName() + " &7has &ajoined &7to &b" + packet.getServer() + "&7."));
    }

    @IncomingPacketHandler
    public void onStaffLeft(StaffLeftPacket packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("core.staff")) continue;
            Profile profile = Profile.getProfileByUUID(player.getUniqueId());

            player.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("SERVER.LEFT").replace("<server>", packet.getServer()).replace("<player>", packet.getName())));
        }

        Bukkit.getConsoleSender().sendMessage(CC.translate("&b[S] &3" + packet.getName() + " &7has &cleft &7left the network."));
    }

    @IncomingPacketHandler
    public void onStaffSwitch(StaffSwitchedPacket packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("core.staff")) continue;
            Profile profile = Profile.getProfileByUUID(player.getUniqueId());

            player.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("SERVER.SWITCHED").replace("<newserver>", packet.getNewServer()).replace("<server>", packet.getServer()).replace("<player>", packet.getName())));
        }

        Bukkit.getConsoleSender().sendMessage(CC.translate("&b[S] &3" + packet.getName() + " &7has &7has &aswitched &7from &b" + packet.getServer() + " &7to &b" + packet.getNewServer() + "&7."));
    }

    @IncomingPacketHandler
    public void onChat(ChatPacket packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getProfileByUUID(player.getUniqueId());
            if (!player.hasPermission("core." + profile.getChat().getPermission())) continue;
            String chat = StaffCore.getInstance().getConfigFile().getString("CHAT." + profile.getChat().name());

            player.sendMessage(CC.translate(chat.replace("<player>", packet.getName()).replace("<server>", packet.getServer()).replace("<message>", packet.getMessage())));
        }

        Bukkit.getConsoleSender().sendMessage(CC.translate("&7[" + packet.getChatType().name() + "] &f(" + packet.getServer() + ") &f" + packet.getName() + "&7: &f" + packet.getMessage()));
    }
}