package online.nasgar.staffcore.punishment.listeners;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.punishment.PunishmentType;
import online.nasgar.staffcore.punishment.packets.PunishmentExecutePacket;
import online.nasgar.staffcore.punishment.packets.PunishmentUndoExecutePacket;
import online.nasgar.staffcore.redis.packet.handler.IncomingPacketHandler;
import online.nasgar.staffcore.redis.packet.listener.PacketListener;
import online.nasgar.staffcore.utils.CC;
import online.nasgar.staffcore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PunishPacketListener implements PacketListener {

    @IncomingPacketHandler
    public void onPunishmentExecute(PunishmentExecutePacket packet) {
        Punishment punishment = packet.getPunishment();
        String senderName = packet.getVictimName();
        String staffName = packet.getStaffName();
        Player victim = Bukkit.getPlayer(senderName);

        if (victim != null) {
            Profile profile = Profile.getProfileByUUID(victim.getUniqueId());

            if (punishment.getType() == PunishmentType.TEMPBAN || punishment.getType() == PunishmentType.BAN) {
                Tasks.runTask(() -> victim.kickPlayer(String.join("\n", this.getPunishmentTypeBan(profile, punishment, senderName, staffName, StaffCore.getInstance().getMessageHandler().getMany(victim, "PUNISHMENT.BAN_KICK_MESSAGE").getContents()))));
            } else if (punishment.getType() == PunishmentType.BLACKLIST) {
                Tasks.runTask(() -> victim.kickPlayer(String.join("\n", this.getPunishmentTypeBlacklist(profile, punishment, senderName, staffName, StaffCore.getInstance().getMessageHandler().getMany(victim, "PUNISHMENT.BLACKLIST_KICK_MESSAGE").getContents()))));
            } else if (punishment.getType() == PunishmentType.WARN) {
                victim.sendMessage(CC.translate("&cYou have been warned."));
                victim.sendMessage(CC.translate("&cExpires in: &f7 days."));
                victim.sendMessage(CC.translate("&cReason: &f" + punishment.getAddedReason()));
            }
        }

        Tasks.runAsyncTask(() -> Bukkit.getOnlinePlayers().forEach(online -> {
            Profile onlineProfile = Profile.getProfileByUUID(online.getUniqueId());

            if (punishment.isSilent() && !online.hasPermission("core.silent")) return;

            StaffCore.getInstance().getMessageHandler().getMany(online, this.getConfigPathByPunishmentType(punishment)).forEach(line -> {
                if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
                if (line.contains("<added_by>")) line = line.replace("<added_by>", punishment.getStaffName());
                if (line.contains("<banned>")) line = line.replace("<banned>", punishment.getVictimName());
                if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
                if (line.contains("<duration>"))
                    line = line.replace("<duration>", punishment.getRemaining());

                if (punishment.getType() != PunishmentType.BLACKLIST) {
                    if (line.contains("<context>"))
                        line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getMessageHandler().get(online, "PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getMessageHandler().get(online, "PUNISHMENT.BAN_PERMANENT_MESSAGE"));
                }

                online.sendMessage(CC.translate(line));
            });
        }));

        StaffCore.getInstance().getMessageHandler().getMany(Bukkit.getConsoleSender(), this.getConfigPathByPunishmentType(punishment)).forEach(line -> {
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<added_by>")) line = line.replace("<added_by>", punishment.getStaffName());
            if (line.contains("<banned>")) line = line.replace("<banned>", punishment.getVictimName());
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<duration>")) line = line.replace("<duration>", punishment.getRemaining());

            if (punishment.getType() != PunishmentType.BLACKLIST) {
                if (line.contains("<context>"))
                    line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_PERMANENT_MESSAGE"));
            }

            Bukkit.getConsoleSender().sendMessage(CC.translate(line));
        });
    }

    @IncomingPacketHandler
    public void onPunishmentUndoExecute(PunishmentUndoExecutePacket packet) {
        Punishment punishment = packet.getPunishment();
        String senderName = packet.getVictimName();
        String staffName = packet.getStaffName();
        Player victim = Bukkit.getPlayer(senderName);

        if (victim != null) {
            if (punishment.getType() == PunishmentType.MUTE) {
                StaffCore.getInstance().getMessageHandler().getMany(victim, "PUNISHMENT.UNMUTE_MESSAGE").forEach(message -> {
                    message = message.replace("<reason>", punishment.getAddedReason());

                    victim.sendMessage(CC.translate(message));
                });
            } else if (punishment.getType() == PunishmentType.WARN) {
                StaffCore.getInstance().getMessageHandler().getMany(victim, "PUNISHMENT.UNWARN_MESSAGE").forEach(message -> {
                    message = message.replace("<reason>", punishment.getAddedReason());

                    victim.sendMessage(CC.translate(message));
                });
            }
        }

        Tasks.runAsyncTask(() -> Bukkit.getOnlinePlayers().forEach(online -> {
            Profile onlineProfile = Profile.getProfileByUUID(online.getUniqueId());

            if (punishment.isSilent() && !online.hasPermission("core.silent")) return;

            StaffCore.getInstance().getMessageHandler().getMany(online, this.getConfigPathByUndoPunishmentType(punishment)).forEach(line -> {
                if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
                if (line.contains("<added_by>")) line = line.replace("<added_by>", punishment.getStaffName());
                if (line.contains("<banned>")) line = line.replace("<banned>", punishment.getVictimName());
                if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
                if (line.contains("<duration>"))
                    line = line.replace("<duration>", punishment.getRemaining());

                if (punishment.getType() != PunishmentType.BLACKLIST) {
                    if (line.contains("<context>"))
                        line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getMessageHandler().get(online, "PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getMessageHandler().get(online, "PUNISHMENT.BAN_PERMANENT_MESSAGE"));
                }
                online.sendMessage(CC.translate(line));
            });
        }));

        StaffCore.getInstance().getMessageHandler().getMany(Bukkit.getConsoleSender(), this.getConfigPathByUndoPunishmentType(punishment)).forEach(line -> {
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<added_by>")) line = line.replace("<added_by>", punishment.getStaffName());
            if (line.contains("<banned>")) line = line.replace("<banned>", punishment.getVictimName());
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<duration>")) line = line.replace("<duration>", punishment.getRemaining());

            if (punishment.getType() != PunishmentType.BLACKLIST) {
                if (line.contains("<context>"))
                    line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_PERMANENT_MESSAGE"));
            }

            Bukkit.getConsoleSender().sendMessage(CC.translate(line));
        });
    }

    private List<String> getPunishmentTypeBan(Profile profile, Punishment punishment, String senderName, String staffName, List<String> lines) {
        List<String> list = new ArrayList<>();

        for (String line : lines) {
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<added_by>")) line = line.replace("<added_by>", staffName);
            if (line.contains("<banned>")) line = line.replace("<banned>", senderName);
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<duration>")) line = line.replace("<duration>", punishment.getRemaining());
            if (line.contains("<context>"))
                line = line.replace("<context>", (punishment.getType() == PunishmentType.TEMPBAN) ? StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_TEMPORARY_MESSAGE") : StaffCore.getInstance().getMessageHandler().get(Bukkit.getConsoleSender(), "PUNISHMENT.BAN_PERMANENT_MESSAGE"));

            list.add(line);
        }

        return CC.translate(list);
    }

    private List<String> getPunishmentTypeBlacklist(Profile profile, Punishment punishment, String senderName, String staffName, List<String> lines) {
        List<String> list = new ArrayList<>();

        for (String line : lines) {
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<added_by>")) line = line.replace("<added_by>", staffName);
            if (line.contains("<banned>")) line = line.replace("<banned>", senderName);
            if (line.contains("<reason>")) line = line.replace("<reason>", punishment.getAddedReason());
            if (line.contains("<duration>")) line = line.replace("<duration>", punishment.getRemaining());

            list.add(line);
        }

        return CC.translate(list);
    }

    private String getConfigPathByPunishmentType(Punishment type) {
        switch (type.getType()) {
            case TEMPBAN:
            case BAN: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.BAN_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.BAN_SILENT_MESSAGE";
            }
            case BLACKLIST: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.BLACKLIST_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.BLACKLIST_SILENT_MESSAGE";
            }
            case WARN: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.WARN_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.WARN_SILENT_MESSAGE";
            }
            case MUTE: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.MUTE_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.MUTE_SILENT_MESSAGE";
            }
            case KICK: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.KICK_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.KICK_SILENT_MESSAGE";
            }
        }

        return "";
    }

    private String getConfigPathByUndoPunishmentType(Punishment type) {
        switch (type.getType()) {
            case BAN: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.UNBAN_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.UNBAN_SILENT_MESSAGE";
            }
            case BLACKLIST: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.UNBLACKLIST_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.UNBLACKLIST_SILENT_MESSAGE";
            }
            case WARN: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.UNWARN_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.UNWARN_SILENT_MESSAGE";
            }
            case MUTE: {
                if (!type.isSilent()) {
                    return "PUNISHMENT.UNMUTE_PUBLIC_MESSAGE";
                }

                return "PUNISHMENT.UNMUTE_SILENT_MESSAGE";
            }
        }

        return "";
    }
}
