package org.cyci.mc.joinevents;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyci.mc.joinevents.cmd.api.CommandListener;
import org.cyci.mc.joinevents.cmd.api.CommandRegistry;
import org.cyci.mc.joinevents.config.ConfigWrapper;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.config.Lang;
import org.cyci.mc.joinevents.db.PlayerTimeTracker;
import org.cyci.mc.joinevents.listeners.InventoryMoveItemEvent;
import org.cyci.mc.joinevents.listeners.PlayerInteract;
import org.cyci.mc.joinevents.listeners.PlayerJoin;
import org.cyci.mc.joinevents.listeners.PlayerLeave;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.manager.ConfigurationManager;
import org.cyci.mc.joinevents.manager.MySQLManager;
import org.cyci.mc.joinevents.tasks.PlaytimeUpdaterTask;
import org.cyci.mc.joinevents.utils.C;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Registry extends JavaPlugin {
    public static Registry instance;
    public ConfigWrapper messagesFile;
    public ConfigWrapper config;
    public static NamespacedKey UNMOVABLE_TAG_KEY;
    public final ConfigurationManager configManager = new ConfigurationManager(this);
    private MySQLManager mysqlManager;
    private PlayerTimeTracker playerTimeTracker;

    @Override
    public void onEnable() {
        instance = this;
        UNMOVABLE_TAG_KEY = new NamespacedKey(instance, "unmovable");
        // Initialize and load configuration files
        messagesFile = new ConfigWrapper(this, "messages.yml");
        config = new ConfigWrapper(this, "config.yml");
        try {
            mysqlManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
        }
        mysqlManager = new MySQLManager(configManager.getMySQLHost(),
                                        configManager.getMySQLPort(),
                                        configManager.getMySQLDatabase(),
                                        configManager.getMySQLUsername(),
                                        configManager.getMySQLPassword());

        playerTimeTracker = new PlayerTimeTracker(mysqlManager);
        try {
            mysqlManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
        }

        getLogger().info("Config loading status: " + (config.getConfig() != null ? "Loaded" : "Not Loaded"));
        getLogger().info("Messages config loading status: " + (messagesFile.getConfig() != null ? "Loaded" : "Not Loaded"));
        saveDefaultConfig();
        try {
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new InventoryMoveItemEvent(), this);
        } catch (Exception e) {
            getLogger().info(e.getMessage());
        } finally {
            getLogger().info("Registered the events");
        }
        new CommandRegistry();
         loadMessages();

        int updateInterval = 1200; // 1200 ticks = 60 seconds (1 minute)
        new PlaytimeUpdaterTask().runTaskTimer(this, 0, updateInterval);
        getLogger().info("Plugin enabled!");
    }
    @Override
    public void onDisable() {
        messagesFile.saveConfig();
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("Plugin disabled!");
    }
    private void loadMessages() {
        Lang.setFile(this.messagesFile.getConfig());
        for (Lang value : Lang.values())
            this.messagesFile.getConfig().addDefault(value.getPath(), value.getDefault());
        this.messagesFile.getConfig().options().copyDefaults(true);
        this.messagesFile.saveConfig();
    }
    public boolean onCommand(final CommandSender s, final Command c, final String string, final String[] args) {
        final CommandListener command = CommandRegistry.getCommand(c.getName());
        if (command == null) {
            return false;
        }
        if (s instanceof Player) {
            final Player player = (Player)s;
            if (command.getPermission().equals("") || player.hasPermission(command.getPermission())) {
                command.execute(s, args);
            }
            else {
                player.sendMessage(C.c(PlaceholderAPI.setPlaceholders(player, Lang.NO_PERM.getConfigValue( new String[] { command.getPermission(), Lang.PREFIX.getConfigValue(null)}))));
            }
            return true;
        }
        command.execute(s, args);
        return true;
    }
    public PlayerTimeTracker getPlayerTimeTracker() {
        return playerTimeTracker;
    }
}
