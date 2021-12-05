package me.fckml.staffcore.profile;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentType;
import me.fckml.staffcore.redis.CoreRedisDatabase;
import me.fckml.staffcore.redis.packets.ChatPacket;
import me.fckml.staffcore.redis.packets.StaffJoinedPacket;
import me.fckml.staffcore.redis.packets.StaffLeftPacket;
import me.fckml.staffcore.redis.packets.StaffSwitchedPacket;
import me.fckml.staffcore.staffmode.StaffModeManager;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!StaffModeManager.getInstance().getFrozenPlayers().contains(player.getUniqueId())) return;

        StaffModeManager.getInstance().unFreezePlayer(player);

        Tasks.runAsyncTask(() -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.getProfileByUUID(online.getUniqueId());

                StaffCore.getInstance().getConfigFile().getStringList("FREEZE.LOGGED_OUT").forEach(message -> online.sendMessage(CC.translate(message.replace("<player>", player.getName()))));
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!StaffModeManager.getInstance().getFreezeTimer().containsKey(player.getUniqueId())) return;
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());
        String command = event.getMessage().split(" ")[0];
        if (!StaffCore.getInstance().getConfigFile().getStringList("FREEZE.ALLOWED_COMMANDS").contains(command)) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerAsyncChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!StaffModeManager.getInstance().getFreezeTimer().containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (!StaffModeManager.getInstance().getFreezeTimer().containsKey(event.getPlayer().getUniqueId())) return;

        this.onPlayerMoveEvent(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX()) return;
        if (from.getZ() == to.getZ()) return;

        if (!StaffModeManager.getInstance().getFreezeTimer().containsKey(event.getPlayer().getUniqueId())) return;

        event.setTo(event.getFrom());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onAsyncPlayerJoinEvent(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());

        try {
            profile.load();

            if (profile.getName() == null) {
                profile.setName(event.getName());
            }

            if (profile.getName() != null && !profile.getName().equalsIgnoreCase(event.getName())) {
                profile.setName(event.getName());
            }

            if (!profile.getIdentifiers().contains(event.getAddress().getHostAddress())) {
                profile.getIdentifiers().add(event.getAddress().getHostAddress());
            }

            if (profile.getAddress() == null || !profile.getAddress().equalsIgnoreCase(event.getAddress().getHostAddress())) {
                profile.setAddress(event.getAddress().getHostAddress());
            }

            Punishment punishment = profile.getBannedPunishment();

            if (punishment != null) {
                if (punishment.getType() == PunishmentType.BLACKLIST) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, String.join("\n", this.getPunishmentTypeBan(profile, punishment, StaffCore.getInstance().getConfigFile().getStringList("PUNISHMENT.BLACKLIST_KICK_MESSAGE"))));
                    return;
                }

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, String.join("\n", this.getPunishmentTypeBan(profile, punishment, StaffCore.getInstance().getConfigFile().getStringList("PUNISHMENT.BAN_KICK_MESSAGE"))));
                return;
            }

            profile.addToMap();
            profile.save();
        } catch (Exception e) {
            event.setKickMessage(CC.translate("&cThere's an error retrieving your profile!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());

        if (profile.getChat() == ProfileChat.ADMIN_CHAT) {
            event.setCancelled(true);

            CoreRedisDatabase.getInstance().sendPacket(new ChatPacket(player.getName(), StaffCore.getInstance().getConfig().getString("SERVER.GROUP"), event.getMessage(), profile.getChat()));
            return;
        }

        if (profile.getChat() == ProfileChat.STAFF_CHAT) {
            event.setCancelled(true);

            CoreRedisDatabase.getInstance().sendPacket(new ChatPacket(player.getName(), StaffCore.getInstance().getConfig().getString("SERVER.GROUP"), event.getMessage(), profile.getChat()));
            return;
        }

        if (profile.getChat() == ProfileChat.DONOR_CHAT) {
            event.setCancelled(true);

            CoreRedisDatabase.getInstance().sendPacket(new ChatPacket(player.getName(), StaffCore.getInstance().getConfig().getString("SERVER.GROUP"), event.getMessage(), profile.getChat()));
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfileByUUID(player.getUniqueId());

        Tasks.runAsyncTask(() -> {
            if ((TimeUnit.SECONDS.toMillis(10L) + profile.getLastSeen()) - System.currentTimeMillis() > 0L && profile.getServer() != null) {
                CoreRedisDatabase.getInstance().sendPacket(new StaffSwitchedPacket(player.getName(), profile.getServer(), StaffCore.getInstance().getConfig().getString("SERVER.GROUP")));
            } else {
                CoreRedisDatabase.getInstance().sendPacket(new StaffJoinedPacket(player.getName(), StaffCore.getInstance().getConfig().getString("SERVER.GROUP")));
            }

            profile.setServer(StaffCore.getInstance().getConfig().getString("SERVER.GROUP"));
            profile.setLastSeen(System.currentTimeMillis());

            if (profile.getFirstSeen() <= 0L) profile.setFirstSeen(System.currentTimeMillis());
            
            profile.save();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Tasks.runAsyncTask(() -> {
            Profile profile = Profile.getProfileByUUID(event.getPlayer().getUniqueId());
            profile.save();
            profile.removeFromMap();
        });

        if (!event.getPlayer().hasPermission("core.staff")) return;

        String name = event.getPlayer().getName();
        UUID uuid = event.getPlayer().getUniqueId();

        Tasks.runAsyncTaskLater(() -> {
            Profile offlineProfile = Profile.getProfileByUUID(uuid);

            if (!Profile.getProfileMap().containsKey(uuid)) {
                offlineProfile.load();
            }

            if (offlineProfile.getLastSeen() - (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L)) > 0L) return;

            CoreRedisDatabase.getInstance().sendPacket(new StaffLeftPacket(name, StaffCore.getInstance().getConfig().getString("SERVER.GROUP")));
        }, 40L);
    }

    private List<String> getPunishmentTypeBan(Profile profile, Punishment punishment, List<String> lines) {
        List<String> list = new ArrayList<>();

        for (String line : lines) {
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<added_by>")) line = line.replace("<added_by>", punishment.getStaffName());
            if (line.contains("<banned>")) line = line.replace("<banned>", punishment.getVictimName());
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<duration>")) line = line.replace("<duration>", punishment.getRemaining());
            if (line.contains("<context>")) line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.BAN_PERMANENT_MESSAGE"));

            list.add(line);
        }

        return CC.translate(list);
    }
}

