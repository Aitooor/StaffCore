package online.nasgar.staffcore.commands.punishment.undo;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.punishment.PunishmentHelper;
import online.nasgar.staffcore.utils.CC;
import online.nasgar.staffcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class UnWarnCommand extends BaseCommand {

    public UnWarnCommand() {
        super("unwarn", "core.command.unwarn");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /unwarn <player> <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            String message = StaffCore.getInstance().getMessageHandler().get(sender, "PUNISHMENT.UNWARN_MESSAGE");
            sender.sendMessage(CC.translate(message));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Profile targetProfile = Profile.getProfileByUUID(target.getUniqueId());
        if (target.getPlayer() == null) targetProfile.load();
        Punishment punishment = targetProfile.getWarnPunishment();

        if (punishment == null) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&cThat player has no warns yet."));
                return;
            }

            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            String message = StaffCore.getInstance().getMessageHandler().get(sender, "PUNISHMENT.UNWARN_NO_WARNS_YET");
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