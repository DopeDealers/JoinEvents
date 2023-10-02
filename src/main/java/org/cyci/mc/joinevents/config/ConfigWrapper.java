package org.cyci.mc.joinevents.config;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.cyci.mc.joinevents.Registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by an old friend
 */
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
        if (config == null || configFile == null) return null;
        else return configFile.getName();
    }

    public File getConfigFile() {
        if (config == null || configFile == null) return null;
        else return configFile.getAbsoluteFile();
    }

    public void loadConfig(String header) {
        if (config == null) {
            reloadConfig();
        }
        config.options().getHeader().add(header);
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = Registry.instance.getResource(this.configFile.getName());
        if (defConfigStream == null) {
            return;
        }
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
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