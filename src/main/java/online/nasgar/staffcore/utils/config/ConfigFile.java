package online.nasgar.staffcore.utils.config;

import lombok.Getter;
import online.nasgar.staffcore.StaffCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public class ConfigFile extends AbstractConfigFile {
    
    private File file;
    private YamlConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String name, boolean overwrite) {
        super(plugin, name);
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        
        plugin.saveResource(name + ".yml", overwrite);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        
    }

    public ConfigFile(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    @Override
    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return null;
    }

    @Override
    public String getStringOrDefault(String path, String or) {
        String toReturn = this.getString(path);
        return (toReturn == null) ? or : toReturn;
    }

    @Override
    public int getInteger(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    @Override
    public double getDouble(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0;
    }

    @Override
    public Object get(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.get(path);
        }
        return null;
    }

    @Override
    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            return (List<String>) this.configuration.getStringList(path);
        }
        return null;
    }

    public void reload() {
        File file = new File(StaffCore.getInstance().getDataFolder(), this.getName() + ".yml");
        
        try {
            this.getConfiguration().load(file);
            this.getConfiguration().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File folder = StaffCore.getInstance().getDataFolder();
        try {
            this.getConfiguration().save(new File(folder, this.getName() + ".yml"));
        } catch (Exception ex) {
        }
    }
}