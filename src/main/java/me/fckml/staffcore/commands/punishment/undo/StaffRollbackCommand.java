package me.fckml.staffcore.commands.punishment.undo;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.mongo.CoreMongoDatabase;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.TimeUtils;
import me.yushust.message.util.StringList;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class StaffRollbackCommand extends BaseCommand {

    public StaffRollbackCommand() {
        super("staffrollback", "core.command.staffrollback");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /staffrollback <player> <time>"));
            return;
        }

        if (args.length < 2 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            StringList message = StaffCore.getInstance().getMessageHandler().getMany(sender, "STAFF_ROLLBACK.USAGE");
            message.getContents().forEach(line -> sender.sendMessage(CC.translate(line)));
            return;
        }

        int erasedPunishments = 0;

        for (Document document : CoreMongoDatabase.getInstance().getPunishments().find()) {
            Punishment punishment = new Punishment();
            PunishmentHelper.load(document, punishment);

            if (!punishment.isActive() || punishment.getRemovedReason() != null) continue;
            if (!punishment.getStaffName().equalsIgnoreCase(args[0])) continue;
            if (System.currentTimeMillis() - (punishment.getAddedAt() + TimeUtils.parse(args[1])) > 0L) continue;

            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(null);
            punishment.setRemovedReason("StaffRoll Back - " + new Date(System.currentTimeMillis()).toLocaleString());

            PunishmentHelper.save(punishment);

            ++erasedPunishments;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&aA total of &e" + erasedPunishments + " &apunishments have been removed."));
            return;
        }

        Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

        String message = StaffCore.getInstance().getMessageHandler().get(sender, "STAFF_ROLLBACK.CLEARED_PUNISHMENTS");
        sender.sendMessage(CC.translate(message.replace("<total>", Integer.toString(erasedPunishments))));
    }
}
