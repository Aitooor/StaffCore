package online.nasgar.staffcore.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> translate(List<String> string) {
        return string.stream().map(CC::translate).collect(Collectors.toList());
    }
}
