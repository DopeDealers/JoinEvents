package org.cyci.mc.joinevents.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.ConfigWrapper;
import org.cyci.mc.joinevents.config.IConfig;

import java.io.File;
import java.io.IOException;
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
    private final File dataFolder;
    private final Map<String, IConfig> cachedConfigs;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.cachedConfigs = new HashMap<>();
    }

    /**
     * The getConfig function is used to get a config object from the cachedConfigs HashMap.
     * If the config has already been loaded, it will be returned from the cache. Otherwise,
     * a new IConfig object will be created and added to the cache before being returned.

     *
     * @param String configName Get the config from the cachedconfigs hashmap
     * @param FileConfiguration fileConfiguration Get the configuration file
     *
     * @return An iconfig object
     *
     */
    public IConfig getConfig(String configName, FileConfiguration fileConfiguration) {
        if (cachedConfigs.containsKey(configName)) {
            return cachedConfigs.get(configName);
        }

        IConfig config = new IConfig(fileConfiguration, configName);
        cachedConfigs.put(configName, config);
        return cachedConfigs.get(configName);
    }

    /**
     * The updateConfig function is used to update a config file that has been cached.
     * This function will save the default config, load the updated config, and then cache it.
     *
     *
     * @param String configName Get the config from the cachedconfigs hashmap
    public iconfig getconfig(string configname) {
            if (cachedconfigs
     * @param FileConfiguration fileConfiguration Load the updated config
     *
     * @return A boolean, true if it was successful and false otherwise
     *
     */
    public void updateConfig(String configName, FileConfiguration fileConfiguration) {
        if (cachedConfigs.containsKey(configName)) {
            try {
                Registry.instance.config.saveDefaultConfig(); // Save the default config
                fileConfiguration.load(Registry.instance.config.getConfigFile()); // Load the updated config
                cachedConfigs.remove(configName);
            } catch (Exception e) {
                Registry.instance.getLogger().info("Failed to update config: \n" + e.getMessage());
            } finally {
                IConfig config = new IConfig(fileConfiguration, configName);
                cachedConfigs.put(configName, config); // Cache the updated config
            }
        }
    }

    /**
     * The saveConfig function saves the configuration file to disk.
     *
     *
     *
     * @return Void
     *
     */
    public void saveConfig() {
        File configFile = new File(dataFolder, Registry.instance.config.getFileName());
        FileConfiguration fileConfiguration = Registry.instance.config.getConfig();

        try {
            fileConfiguration.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + Registry.instance.getConfig().getName() + ": " + e.getMessage());
        }
    }
}