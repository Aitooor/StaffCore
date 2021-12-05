package me.fckml.staffcore.utils.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public abstract class AbstractConfigFile {
    
    public static String FILE_EXTENSION = ".yml";
    
    private JavaPlugin plugin;
    private String name;

    public AbstractConfigFile(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract String getString(String string);
    public abstract String getStringOrDefault(String string, String string2);
    public abstract int getInteger(String string);
    public abstract double getDouble(String string);
    public abstract Object get(String string);
    public abstract List<String> getStringList(String list);

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.name;
    }
}
