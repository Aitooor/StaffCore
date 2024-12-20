package online.nasgar.staffcore.commands.punishment.execute;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.punishment.PunishmentHelper;
import online.nasgar.staffcore.punishment.PunishmentType;
import online.nasgar.staffcore.utils.CC;
import online.nasgar.staffcore.utils.StringUtils;
import online.nasgar.staffcore.utils.TimeUtils;
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

            String message = StaffCore.getInstance().getMessageHandler().get(sender, "PUNISHMENT.MUTE_ALREADY_MUTED");
            sender.sendMessage(CC.translate(message));
            return;
        }

        punishment = new Punishment();

        long duration = TimeUtils.parse(args[1]);
        String reason = (duration <= 0? StringUtils.getFromArray(args, 1) : StringUtils.getFromArray(args, 2));
        boolean isPublic = reason.contains(" -s");

        punishment.setUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.MUTE);
        punishment.setTargetID(target.getUniqueId());
        punishment.setAddedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setAddedReason(reason.replace(" -s", ""));
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);
        punishment.setExpiration(-1);

        if (duration <= 0) punishment.setExpiration(duration);

        punishment.setVictimName(target.getName());
        punishment.setStaffName(sender.getName());

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishBan(punishment, target.getName(), sender.getName());
    }
}