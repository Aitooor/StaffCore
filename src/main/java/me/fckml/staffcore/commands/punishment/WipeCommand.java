package me.fckml.staffcore.commands.punishment;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.mongo.CoreMongoDatabase;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.punishment.Punishment;
import me.fckml.staffcore.punishment.PunishmentHelper;
import me.fckml.staffcore.punishment.PunishmentType;
import me.fckml.staffcore.utils.CC;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class WipeCommand extends BaseCommand {

    public WipeCommand() {
        super("wipepunishments", "core.command.wipe");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /wipepunishments <ban|blacklist|mute|warn|kick>"));
            return;
        }

        if (args.length < 1 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            StaffCore.getInstance().getConfigFile().getStringList("WIPE_PUNISHMENTS.USAGE").forEach(message -> sender.sendMessage(CC.translate(message)));
            return;
        }

        PunishmentType type;

        try {
            type = PunishmentType.valueOf(args[0]);
        } catch (Exception e) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(CC.translate("&cInvalid punishment type."));
                return;
            }

            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("WIPE_PUNISHMENTS.INVALID_TYPE")));
            return;
        }

        int erasedPunishments = 0;

        for (Document document : CoreMongoDatabase.getInstance().getPunishments().find()) {
            Punishment punishment = new Punishment();
            PunishmentHelper.load(document, punishment);

            if (punishment.getType() != type) continue;

            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(null);
            punishment.setRemovedReason("Punishment Wiped - " + new Date(System.currentTimeMillis()).toLocaleString());

            PunishmentHelper.save(punishment);

            ++erasedPunishments;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&aA total of &e" + erasedPunishments + " &apunishments."));
            return;
        }

        Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

        sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("WIPE_PUNISHMENTS.WIPED").replace("<total>", erasedPunishments + "")));
    }
}
