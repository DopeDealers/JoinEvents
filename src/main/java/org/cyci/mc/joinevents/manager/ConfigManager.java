package org.cyci.mc.joinevents.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.ConfigWrapper;
import org.cyci.mc.joinevents.config.IConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 20/Sep/2023 - 1:13 PM
 */
public class ConfigManager {
    private final Plugin plugin;

    private static Map<String, IConfig> cachedConfigs = new HashMap<>();

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.cachedConfigs = new HashMap<>();
    }


    // work on this more
    public static IConfig getConfig(String configName, FileConfiguration fileConfiguration) {
        if (cachedConfigs.containsKey(configName)) {
            return cachedConfigs.get(configName);
        }

        IConfig config = new IConfig(fileConfiguration, configName);
        cachedConfigs.put(configName, config);
        return config;
    }

}