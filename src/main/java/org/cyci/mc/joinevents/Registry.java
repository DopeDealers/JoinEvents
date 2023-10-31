package org.cyci.mc.joinevents;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyci.mc.joinevents.cmd.JoinEventsCMD;
import org.cyci.mc.joinevents.config.ConfigWrapper;
import org.cyci.mc.joinevents.config.Lang;
import org.cyci.mc.joinevents.db.PlayerTimeTracker;
import org.cyci.mc.joinevents.listeners.InventoryMoveItemEvent;
import org.cyci.mc.joinevents.listeners.PlayerInteract;
import org.cyci.mc.joinevents.listeners.PlayerJoin;
import org.cyci.mc.joinevents.listeners.PlayerLeave;
import org.cyci.mc.joinevents.manager.ConfigurationManager;
import org.cyci.mc.joinevents.manager.MySQLManager;
import org.cyci.mc.joinevents.tasks.PlaytimeUpdaterTask;
import java.util.concurrent.CompletableFuture;

public final class Registry extends JavaPlugin {
    public static Registry instance;
    public ConfigWrapper messagesFile;
    public ConfigWrapper config;
    public static NamespacedKey UNMOVABLE_TAG_KEY;
    private MySQLManager mysqlManager;
    public PlayerTimeTracker playerTimeTracker;

    @Override
    public void onEnable() {
        instance = this;
        UNMOVABLE_TAG_KEY = new NamespacedKey(instance, "unmovable");

        messagesFile = new ConfigWrapper(this, "messages.yml");
        config = new ConfigWrapper(this, "config.yml");
        ConfigurationManager configManager = new ConfigurationManager(this);
        saveDefaultConfig();
        loadMessages();
        String mysqlHost = configManager.getMySQLHost();
        int mysqlPort = configManager.getMySQLPort();
        String mysqlDatabase = configManager.getMySQLDatabase();
        String mysqlUsername = configManager.getMySQLUsername();
        String mysqlPassword = configManager.getMySQLPassword();

        if (isMySQLConfigValid(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword)) {
            mysqlManager = new MySQLManager(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword);

            mysqlManager.connectAsync().thenRun(() -> {
                getLogger().info("Connected to MySQL!");
                PaperCommandManager manager = new PaperCommandManager(instance);
                manager.registerCommand(new JoinEventsCMD());
                playerTimeTracker = new PlayerTimeTracker(this.mysqlManager.getDataSource());
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

                int updateInterval = 1200;
                new PlaytimeUpdaterTask().runTaskTimer(this, 0, updateInterval);
            }).exceptionally(e -> {
                getLogger().severe("Error: " + e.getMessage());
                e.printStackTrace();
                getLogger().severe("Disabling the plugin due to MySQL connection failure...");
                getServer().getPluginManager().disablePlugin(this);
                return null;
            });
        } else {
            getLogger().severe("MySQL configuration is not valid. Please provide correct MySQL information in your config.yml.");
            getLogger().severe("Disabling the plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }

        getLogger().info("Plugin enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");

        messagesFile.saveConfig();

        Bukkit.getScheduler().cancelTasks(this);

        CompletableFuture<Void> closeConnectionFuture = mysqlManager.closeConnectionAsync();
        closeConnectionFuture
                .thenRun(() -> {
                    getLogger().info("MySQL connection closed successfully.");
                    mysqlManager.shutdown();
                    getLogger().info("Executor service for MySQL manager shut down.");
                    getLogger().info("Plugin disabled!");
                })
                .exceptionally(e -> {
                    getLogger().severe("Error during MySQL connection closure: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }
    public void loadMessages() {
        Lang.setFile(this.messagesFile.getConfig());
        for (Lang value : Lang.values())
            this.messagesFile.getConfig().addDefault(value.getPath(), value.getDefault());
        this.messagesFile.getConfig().options().copyDefaults(true);
        this.messagesFile.saveConfig();
    }
    public PlayerTimeTracker getPlayerTimeTracker() {
        return playerTimeTracker;
    }
    private boolean isMySQLConfigValid(String host, int port, String database, String username, String password) {
        return !host.isEmpty() && port > 0 && !database.isEmpty() && !username.isEmpty() && password != null;
    }
}
