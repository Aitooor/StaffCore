package me.fckml.staffcore.commands.punishment.execute;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.punishment.PunishmentType;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.StringUtils;
import me.fckml.staffcore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick","core.command.kick");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /kick <player> <reason> [-s]"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.KICK_USAGE")));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&cPlayer not found."));
                return;
            }

            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("PUNISHMENT.KICK_NO_PLAYER")));
            return;
        }

        Profile targetProfile = Profile.getProfileByUUID(target.getUniqueId());
        Punishment punishment = new Punishment();

        String reason = StringUtils.getFromArray(args, 1);
        boolean isPublic = reason.contains(" -s");

        punishment.setUuid(UUID.randomUUID());
        punishment.setType(PunishmentType.KICK);
        punishment.setTargetID(target.getUniqueId());
        punishment.setAddedBy((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishment.setAddedReason(reason.replace(" -s", ""));
        punishment.setAddedAt(System.currentTimeMillis());
        punishment.setSilent(isPublic);

        punishment.setVictimName(target.getName());
        punishment.setStaffName(sender.getName());

        PunishmentHelper.save(punishment);
        PunishmentHelper.publishBan(punishment, target.getName(), sender.getName());

        Tasks.runTask(() -> target.kickPlayer(String.join("\n", this.getPunishmentTypeKick(targetProfile, punishment, args[0], sender.getName(), StaffCore.getInstance().getConfigFile().getStringList("PUNISHMENT.KICK_MESSAGE")))));
    }


    private List<String> getPunishmentTypeKick(Profile profile, Punishment punishment, String senderName, String staffName, List<String> lines) {
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
}