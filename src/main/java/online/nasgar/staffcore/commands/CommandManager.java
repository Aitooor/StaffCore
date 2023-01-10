package online.nasgar.staffcore.commands;

import lombok.Data;
import lombok.Getter;
import online.nasgar.staffcore.commands.punishment.AltsCommand;
import online.nasgar.staffcore.commands.punishment.WipeCommand;
import online.nasgar.staffcore.commands.punishment.execute.*;
import online.nasgar.staffcore.commands.punishment.undo.*;
import online.nasgar.staffcore.commands.staff.*;
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

        this.commands.add(new PremiumChatCommand());
        this.commands.add(new StaffChatCommand());
        this.commands.add(new ModChatCommand());
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

        this.commands.add(new FreezeCommand());

        this.commands.forEach(this::registerCommand);
    }

    public void disable() {
        this.commands.clear();
    }

    public void registerCommand(BukkitCommand command) {
        this.commandMap.register("core", command);
    }
}
