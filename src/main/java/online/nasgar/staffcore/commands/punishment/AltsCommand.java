package online.nasgar.staffcore.commands.punishment;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.profile.Profile;
import online.nasgar.staffcore.utils.CC;
import me.yushust.message.util.StringList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AltsCommand extends BaseCommand {

    public AltsCommand() {
        super("alts", "core.command.alts");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1 && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /alts <player>"));
            return;
        }

        if (args.length < 1 && sender instanceof Player) {
            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

            StringList message = StaffCore.getInstance().getMessageHandler().getMany(sender, "ALTS.USAGE");
            message.getContents().forEach(line -> sender.sendMessage(CC.translate(line)));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        Profile targetProfile = Profile.getProfileByUUID(offlinePlayer.getUniqueId());

        if (offlinePlayer.getPlayer() == null) targetProfile.load();
        targetProfile.findAlts();

        if (sender instanceof ConsoleCommandSender) {
            if (targetProfile.getAlts().isEmpty()) {
                sender.sendMessage(CC.translate("&cThat player has no alts."));
                return;
            }

            sender.sendMessage(CC.translate("&cAlts of Player " + args[0]));
            sender.sendMessage("   ");

            for (UUID id : targetProfile.getAlts()) {
                OfflinePlayer altPlayer = Bukkit.getOfflinePlayer(id);
                Profile altProfile = Profile.getProfileByUUID(altPlayer.getUniqueId());

                if (altPlayer.getPlayer() == null) altProfile.load();
                boolean punished = altProfile.getBannedPunishment() != null, muted = altProfile.getMutedPunishment() != null;

                sender.sendMessage(CC.translate("&7- &e" + altPlayer.getName() + " &7(" + (altPlayer.getPlayer() == null ? "&cOffline" : "&aOnline") + "&7) &7(" + (punished ? "&cBanned" : "No Punishments)") + "&7) &7(" + (muted ? "&cMuted" : "Not Muted") + "&7)"));
            }

            sender.sendMessage(" ");
            return;
        }

        Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());

        if (targetProfile.getAlts().isEmpty()) {
            String message = StaffCore.getInstance().getMessageHandler().get(sender, "ALTS.NO_ALTS");
            sender.sendMessage(CC.translate(message));
            return;
        }

        StringList message = StaffCore.getInstance().getMessageHandler().getMany(sender, "ALTS.ALT_MESSAGE");

        message.getContents().forEach(line -> {
            line = line.replace("<player>", args[0]);

            if (line.equalsIgnoreCase("<alt_format>")) {
                String format = StaffCore.getInstance().getMessageHandler().get(sender, "ALTS.ALT_FORMAT");

                for (UUID id : targetProfile.getAlts()) {
                    OfflinePlayer altPlayer = Bukkit.getOfflinePlayer(id);
                    Profile altProfile = Profile.getProfileByUUID(altPlayer.getUniqueId());

                    if (altPlayer.getPlayer() == null) altProfile.load();
                    boolean punished = altProfile.getBannedPunishment() != null, muted = altProfile.getMutedPunishment() != null;

                    format = format.replace("<online>", altPlayer.isOnline() ? StaffCore.getInstance().getConfigFile().getString("ALTS.ALT_ONLINE") : StaffCore.getInstance().getConfigFile().getString("ALTS.ALT_OFFLINE"));
                    format = format.replace("<banned>", punished ? StaffCore.getInstance().getConfigFile().getString("ALTS.BANNED_FORMAT") : StaffCore.getInstance().getConfigFile().getString("ALTS.NOT_BANNED_FORMAT"));
                    format = format.replace("<muted>", muted ? StaffCore.getInstance().getConfigFile().getString("ALTS.MUTED_FORMAT") : StaffCore.getInstance().getConfigFile().getString("ALTS.NOT_MUTED_FORMAT"));

                    sender.sendMessage(CC.translate(format));
                }
                return;
            }

            sender.sendMessage(CC.translate(line));
        });
    }
}
