package org.cyci.mc.joinevents.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:26 PM
 */
public class ConfigurationManager {
    private final Registry plugin;
    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());

    public ConfigurationManager(Registry plugin) {
        this.plugin = plugin;
    }

    public String getMySQLHost() {
        return config.getString("config.database.mysql.host");
    }

    public int getMySQLPort() {
        return config.getInt("config.database.mysql.port");
    }

    public String getMySQLDatabase() {
        return config.getString("config.database.mysql.database");
    }

    public String getMySQLUsername() {
        return config.getString("config.database.mysql.username");
    }

    public String getMySQLPassword() {
        return config.getString("config.database.mysql.password");
    }
}