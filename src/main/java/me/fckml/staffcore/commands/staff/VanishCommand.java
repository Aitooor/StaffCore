package me.fckml.staffcore.commands.staff;

import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.staffmode.StaffModeManager;
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
