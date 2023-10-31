package org.cyci.mc.joinevents.config;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Helped and created by an old friend
 */
public enum Lang {
    PREFIX("messages.prefix", "&a&lJoinEvents &7>"),
    NO_PERM("messages.noperm", "{prefix} &7You do not have the &c{permission} &7permission"),
    RELOAD("messages.reload", "{prefix} &aReloaded the config."),
    NOT_ENOUGH_ARGS("messages.no_arg", "{prefix} &cNot enough args."),
    ON_TIME_COMMAND("messages.time", "{prefix} &7{player_name}'s time is &b{time}&7 minutes"),
    ON_LOGIN_COMMAND("messages.logins", "{prefix} &7{player_name} has &b{logins}&7 logins");

    private final String path;
    private final String def;

    private static FileConfiguration LANG;

    Lang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    public String getPath() {
        return path;
    }

    public String getDefault() {
        return def;
    }

    public String getConfigValue() {
        return LANG.getString(path, def);
    }
    public String getConfigValue(Player player, Map<String, String> replacements) {
        String value = LANG.getString(path, def);
        if (replacements != null && !replacements.isEmpty()) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                value = value.replace(entry.getKey(), entry.getValue());
            }
        }
        return processPlaceholders(player, value);
    }

    public String getConfigValue(Player player, String... args) {
        String value = LANG.getString(path, def);
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i]);
            }
        }
        return processPlaceholders(player, value);
    }

    public String getConfigValue(Player player, String placeholder, String replacement) {
        String value = LANG.getString(path, def);
        if (placeholder != null && replacement != null) {
            value = value.replace(placeholder, replacement);
        }
        return processPlaceholders(player, value);
    }

    private String processPlaceholders(Player player, String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Check if PlaceholderAPI is available
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            input = PlaceholderAPI.setPlaceholders(player, input);
        }

        return input;
    }

    public static void setFile(FileConfiguration config) {
        LANG = config;
    }
}