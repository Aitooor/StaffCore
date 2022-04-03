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

import java.util.UUID;

public class WarnCommand extends BaseCommand {

    public WarnCommand() {
        super("warn","core.command.warn");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /warn <player> <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            String message = StaffCore.getInstance().getMessageHandler().get(sender, "PUNISHMENT.WARN_USAGE");
            sender.sendMessage(CC.translate(message));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Punishment punishment = new Punishment();

        String reason = StringUtils.getFromArray(args, 1);
        boolean isPublic = reason.contains(" -s");

        punishment.setUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.WARN);
        punishment.setTargetID(target.getUniqueId());
        punishment.setAddedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setAddedReason(reason.replace(" -s", ""));
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);
        punishment.setExpiration(TimeUtils.parse("7d"));

        punishment.setVictimName(target.getName());
        punishment.setStaffName(sender.getName());

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishBan(punishment, target.getName(), sender.getName());
    }
}