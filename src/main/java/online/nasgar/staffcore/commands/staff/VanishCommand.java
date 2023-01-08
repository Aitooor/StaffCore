package online.nasgar.staffcore.commands.staff;

import online.nasgar.staffcore.commands.BaseCommand;
import online.nasgar.staffcore.staffmode.StaffModeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class VanishCommand extends BaseCommand {

    public VanishCommand() {
        super("vanish", Arrays.asList("v", "hide"), "core.command.vanish");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        StaffModeManager.getInstance().setVanished((Player) sender, !StaffModeManager.getInstance().isVanished((Player) sender));
    }
}
