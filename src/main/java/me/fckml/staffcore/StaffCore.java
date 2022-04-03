package me.fckml.staffcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.fckml.staffcore.commands.CommandManager;
import me.fckml.staffcore.mongo.CoreMongoDatabase;
import me.fckml.staffcore.profile.ProfileListener;
import me.fckml.staffcore.redis.CoreRedisDatabase;
import me.fckml.staffcore.staffmode.StaffModeManager;
import me.fckml.staffcore.utils.config.ConfigFile;
import me.yushust.message.MessageHandler;
import me.yushust.message.MessageProvider;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSourceDecorator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class StaffCore extends JavaPlugin {

    @Getter private static StaffCore instance;

    public static Gson GSON;

    private ConfigFile configFile;
    private MessageHandler messageHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new ConfigFile(this, "config");

        this.setupNMessages();

        GSON = new GsonBuilder().serializeNulls().create();

        new CoreMongoDatabase();
        new CoreRedisDatabase();

        new CommandManager();

        new StaffModeManager();

        Arrays.asList(new ProfileListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        StaffModeManager.getInstance().getInStaffMode().forEach(uuid -> StaffModeManager.getInstance().setInStaffMode(Bukkit.getPlayer(uuid), false));
    }

    public void setupNMessages() {
        MessageProvider messageProvider = MessageProvider
                .create(
                        MessageSourceDecorator
                                .decorate(BukkitMessageAdapt.newYamlSource(this, "lang/lang_%lang%.yml"))
                                .addFallbackLanguage("en")
                                .get(),
                        config -> {
                            config.specify(Player.class)
                                    .setLinguist(player -> player.spigot().getLocale().split("_")[0])
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.specify(CommandSender.class)
                                    .setLinguist(commandSender -> "en")
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.addInterceptor(s -> ChatColor.translateAlternateColorCodes('&', s));
                            config.specify(ConsoleCommandSender.class)
                                    .setLinguist(commandSender -> "en")
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.addInterceptor(s -> ChatColor.translateAlternateColorCodes('&', s));
                        }
                );

        messageHandler = MessageHandler.of(messageProvider);
    }
}
