package me.fckml.staffcore.commands.punishment.execute;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.punishment.PunishmentType;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class BlacklistCommand extends BaseCommand {

    public BlacklistCommand() {
        super("blacklist", Collections.singletonList("bl"), "core.command.blacklist");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /blacklist <player> <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            StaffCore.getInstance().getMessageHandler().send(sender, "PUNISHMENT.BLACKLIST_USAGE");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Profile targetProfile = Profile.getProfileByUUID(target.getUniqueId());
        if (target.getPlayer() == null) targetProfile.load();

        Punishment punishment = targetProfile.getBannedPunishment();

        if (punishment != null) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&c" + target.getName() + " is already banned."));
                return;
            }

            StaffCore.getInstance().getMessageHandler().send(sender, "PUNISHMENT.BLACKLIST_ALREADY_BANNED");
            return;
        }

        punishment = new Punishment();

        String reason = StringUtils.getFromArray(args, 1);
        boolean isPublic = reason.contains(" -s");

        punishment.setUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.BLACKLIST);
        punishment.setTargetID(target.getUniqueId());
        punishment.setAddedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setAddedReason(reason.replace(" -s", ""));
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);
        punishment.setExpiration(-1);

        punishment.setVictimName(target.getName());
        punishment.setStaffName(sender.getName());

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishBan(punishment, target.getName(), sender.getName());
    }
}