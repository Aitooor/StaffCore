package me.fckml.staffcore.commands;

import lombok.Data;
import lombok.Getter;
import me.fckml.staffcore.commands.punishment.AltsCommand;
import me.fckml.staffcore.commands.punishment.WipeCommand;
import me.fckml.staffcore.commands.punishment.execute.*;
import me.fckml.staffcore.commands.punishment.undo.*;
import me.fckml.staffcore.commands.staff.AdminChatCommand;
import me.fckml.staffcore.commands.staff.StaffChatCommand;
import me.fckml.staffcore.commands.staff.StaffModeCommand;
import me.fckml.staffcore.commands.staff.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommandManager {

    @Getter
    private static CommandManager instance;

    private final CommandMap commandMap;
    private final List<BaseCommand> commands;

    public CommandManager() {
        instance = this;

        this.commandMap = Bukkit.getCommandMap();
        this.commands = new ArrayList<>();

        this.commands.add(new StaffChatCommand());
        this.commands.add(new AdminChatCommand());

        this.commands.add(new BanCommand());
        this.commands.add(new BlacklistCommand());
        this.commands.add(new KickCommand());
        this.commands.add(new MuteCommand());
        this.commands.add(new WarnCommand());

        this.commands.add(new StaffRollbackCommand());
        this.commands.add(new UnBanCommand());
        this.commands.add(new UnBlacklistCommand());
        this.commands.add(new UnMuteCommand());
        this.commands.add(new UnWarnCommand());

        this.commands.add(new AltsCommand());
        this.commands.add(new WipeCommand());

        this.commands.add(new VanishCommand());
        this.commands.add(new StaffModeCommand());

        this.commands.forEach(this::registerCommand);
    }

    public void disable() {
        this.commands.clear();
    }

    public void registerCommand(BukkitCommand command) {
        this.commandMap.register("core", command);
    }
}
