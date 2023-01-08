package me.fckml.staffcore.commands.staff;

import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.profile.Profile;
import me.fckml.staffcore.staffmode.StaffModeManager;
import me.fckml.staffcore.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FreezeCommand extends BaseCommand {

    public FreezeCommand() {
        super("freeze", Arrays.asList("screenshare", "froze", "ss"), "core.command.freeze");

        this.setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof ConsoleCommandSender) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(CC.translate("&cPlayer not found."));
                    return;
                }

                if (StaffModeManager.getInstance().getFrozenPlayers().remove(target.getUniqueId())) {
                    StaffModeManager.getInstance().unFreezePlayer(target);

                    Bukkit.getOnlinePlayers().forEach(online -> {
                        Profile profile = Profile.getProfileByUUID(online.getUniqueId());

                        online.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("FREEZE.UNFREEZE_MESSAGE_STAFF").replace("<player>", target.getName()).replace("<staff>", sender.getName())));
                    });
                    return;
                }

                StaffModeManager.getInstance().freezePlayer(target);

                Bukkit.getOnlinePlayers().forEach(online -> {
                    Profile profile = Profile.getProfileByUUID(online.getUniqueId());

                    online.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("FREEZE.FREEZE_MESSAGE_STAFF").replace("<player>", target.getName()).replace("<staff>", sender.getName())));
                });
                return;
            }

            Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("COMMANDS.NO_PLAYER")));
                return;
            }

            if (StaffModeManager.getInstance().getFrozenPlayers().remove(target.getUniqueId())) {
                StaffModeManager.getInstance().unFreezePlayer(target);

                Bukkit.getOnlinePlayers().forEach(online -> {
                    Profile onlineProfile = Profile.getProfileByUUID(online.getUniqueId());

                    online.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("FREEZE.UNFREEZE_MESSAGE_STAFF").replace("<player>", target.getName()).replace("<staff>", sender.getName())));
                });
                return;
            }

            StaffModeManager.getInstance().freezePlayer(target);

            Bukkit.getOnlinePlayers().forEach(online -> {
                Profile onlineProfile = Profile.getProfileByUUID(online.getUniqueId());

                online.sendMessage(CC.translate(StaffCore.getInstance().getConfigFile().getString("FREEZE.FREEZE_MESSAGE_STAFF").replace("<player>", target.getName()).replace("<staff>", sender.getName())));
            });
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cUsage: /freeze <player>"));
            return;
        }

        Profile profile = Profile.getProfileByUUID(((Player) sender).getUniqueId());
        StaffCore.getInstance().getConfigFile().getStringList("FREEZE.USAGE").forEach(message -> sender.sendMessage(CC.translate(message)));
    }
}