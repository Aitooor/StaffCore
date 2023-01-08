package online.nasgar.staffcore.commands;

import online.nasgar.staffcore.utils.CC;
import online.nasgar.staffcore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand extends BukkitCommand {
    
    private boolean forPlayersOnly;
    private boolean executeAsync;

    public BaseCommand(String name) {
        this(name, new ArrayList<String>());
    }

    public BaseCommand(String name, List<String> aliases) {
        this(name, aliases, false);
    }

    public BaseCommand(String name, String permission) {
        this(name, new ArrayList<String>(), permission);
    }

    public BaseCommand(String name, boolean forPlayersOnly) {
        this(name, new ArrayList<String>(), null, forPlayersOnly);
    }

    public BaseCommand(String name, List<String> aliases, String permission) {
        this(name, aliases, permission, false);
    }

    public BaseCommand(String name, List<String> aliases, boolean forPlayersOnly) {
        this(name, aliases, null, forPlayersOnly);
    }

    public BaseCommand(String name, String permission, boolean forPlayersOnly) {
        this(name, new ArrayList<>(), permission, forPlayersOnly);
    }

    public BaseCommand(String name, List<String> aliases, String permission, boolean forPlayersOnly) {
        super(name);
        this.setAliases(aliases);
        this.setPermission(permission);
        this.forPlayersOnly = forPlayersOnly;
    }

    protected boolean checkConsoleSender(CommandSender sender) {
        return !(sender instanceof ConsoleCommandSender);
    }

    protected boolean checkOfflinePlayer(CommandSender sender, OfflinePlayer offlinePlayer, String name) {
        return offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline();
    }

    protected boolean checkPlayer(CommandSender sender, Player player, String name) {
        return player != null;
    }

    protected boolean checkPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.forPlayersOnly && sender instanceof ConsoleCommandSender) {
            sender.sendMessage(CC.translate("&cOnly for console"));
            return true;
        }

        if (sender instanceof Player && this.getPermission() != null && !sender.hasPermission(this.getPermission())) {
            Player player = (Player) sender;

            sender.sendMessage(CC.translate("&cYou don't have permissions."));
            return true;
        }

        if (this.executeAsync) {
            Tasks.runAsyncTask(() -> this.execute(sender, args));
        } else {
            this.execute(sender, args);
        }
        return true;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public void setExecuteAsync(boolean executeAsync) {
        this.executeAsync = executeAsync;
    }
}