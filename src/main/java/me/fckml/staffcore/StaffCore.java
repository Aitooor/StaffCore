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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class StaffCore extends JavaPlugin {

    @Getter private static StaffCore instance;

    public static Gson GSON;

    private ConfigFile configFile;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new ConfigFile(this, "config");

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
}
