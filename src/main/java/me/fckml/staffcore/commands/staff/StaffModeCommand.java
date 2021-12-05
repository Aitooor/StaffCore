package me.fckml.staffcore.commands.staff;

import me.fckml.staffcore.commands.BaseCommand;
import me.fckml.staffcore.staffmode.StaffModeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class StaffModeCommand extends BaseCommand {

    public StaffModeCommand() {
        super("staffmode", Arrays.asList("h", "mod", "modmode"), "core.command.staffmode");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        StaffModeManager.getInstance().setInStaffMode((Player) sender, !StaffModeManager.getInstance().isInStaffMode((Player) sender));
    }
}
