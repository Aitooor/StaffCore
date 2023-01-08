package online.nasgar.staffcore.commands.punishment;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.mongo.CoreMongoDatabase;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.punishment.PunishmentHelper;
import online.nasgar.staffcore.punishment.PunishmentType;
import online.nasgar.staffcore.utils.CC;
import me.yushust.message.util.StringList;
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

            StringList message = StaffCore.getInstance().getMessageHandler().getMany(sender, "WIPE_PUNISHMENTS.USAGE");
            message.getContents().forEach(line -> sender.sendMessage(CC.translate(line)));
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

        String message = StaffCore.getInstance().getMessageHandler().get(sender, "WIPE_PUNISHMENTS.WIPED");
        sender.sendMessage(CC.translate(message.replace("<total>", Integer.toString(erasedPunishments))));
    }
}
