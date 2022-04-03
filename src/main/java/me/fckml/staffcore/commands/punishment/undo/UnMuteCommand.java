package me.fckml.staffcore.commands.punishment.undo;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class UnMuteCommand extends BaseCommand {

    public UnMuteCommand() {
        super("unmute", "core.command.unmute");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /unmute <player> <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.UNMUTE_USAGE")));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Profile targetProfile = Profile.getProfileByUUID(target.getUniqueId());
        if (target.getPlayer() == null) targetProfile.load();

        Punishment punishment = targetProfile.getMutedPunishment();

        if (punishment == null) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&cThat player has no mutes yet."));
                return;
            }

            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            String message = StaffCore.getInstance().getMessageHandler().get(sender, "PUNISHMENT.UNMUTE_NO_WARNS_YET");
            sender.sendMessage(CC.translate(message));
            return;
        }

        String reason = StringUtils.getFromArray(args, 1);
        boolean isPublic = reason.contains(" -s");

        punishment.setRemovedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);
        punishment.setRemovedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setRemovedReason(reason.replace(" -s", ""));

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishUnBan(punishment, target.getName(), sender.getName());
    }
}