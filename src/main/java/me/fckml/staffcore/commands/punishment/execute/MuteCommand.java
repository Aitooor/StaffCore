package me.fckml.staffcore.commands.punishment.execute;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.punishment.PunishmentType;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.StringUtils;
import me.fckml.staffcore.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class MuteCommand extends BaseCommand {

    public MuteCommand() {
        super("mute", Arrays.asList("tmute", "tempmute", "permmute"), "core.command.mute");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /mute <player> [duration] <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.MUTE_USAGE")));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Profile targetProfile = Profile.getProfileByUUID(target.getUniqueId());
        if (target.getPlayer() == null) targetProfile.load();
        Punishment punishment = targetProfile.getMutedPunishment();

        if (punishment != null) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&c" + target.getName() + " is already muted."));
                return;
            }

            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());
            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.MUTE_ALREADY_MUTED")));
            return;
        }

        punishment = new Punishment();

        long duration = TimeUtils.parse(args[1]);
        String reason = StringUtils.getFromArray(args, 1);
        boolean isPublic = reason.contains(" -s");

        punishment.setUuid(UUID.randomUUID());
        punishment.setType(duration <= 0 ? PunishmentType.BAN : PunishmentType.TEMPBAN);
        punishment.setTargetID(target.getUniqueId());
        punishment.setAddedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setAddedReason(reason.replace(" -s", ""));
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);
        punishment.setExpiration(-1);

        if (punishment.getType() == PunishmentType.TEMPBAN) punishment.setExpiration(duration);

        punishment.setVictimName(target.getName());
        punishment.setStaffName(sender.getName());

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishBan(punishment, target.getName(), sender.getName());
    }
}