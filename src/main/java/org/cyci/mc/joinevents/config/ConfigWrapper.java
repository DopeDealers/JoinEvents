package org.cyci.mc.joinevents.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigWrapper {
    private final Plugin plugin;
    private final String fileName;
    private File configFile;
    private FileConfiguration config;

    public ConfigWrapper(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public String getFileName() {
        return (config != null && configFile != null) ? configFile.getName() : null;
    }

    public File getConfigFile() {
        return (config != null && configFile != null) ? configFile.getAbsoluteFile() : null;
    }

    public void loadConfig(String header) {
        if (config == null) {
            reloadConfig();
        }
        config.options().header(header);
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        final InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save " + fileName);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}