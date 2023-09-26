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
import org.cyci.mc.joinevents.listeners.InventoryMoveItemEvent;
import org.cyci.mc.joinevents.listeners.PlayerInteract;
import org.cyci.mc.joinevents.listeners.PlayerJoin;
import org.cyci.mc.joinevents.listeners.PlayerLeave;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.utils.C;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Registry extends JavaPlugin {
    public static Registry instance;
    public ConfigWrapper messagesFile;
    public ConfigWrapper config;
    public static NamespacedKey UNMOVABLE_TAG_KEY;


    @Override
    public void onEnable() {
        instance = this;
        UNMOVABLE_TAG_KEY = new NamespacedKey(instance, "unmovable");
        // Initialize and load configuration files
        messagesFile = new ConfigWrapper(this, "messages.yml");
        config = new ConfigWrapper(this, "config.yml");

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

        getLogger().info("Plugin enabled!");
    }
    @Override
    public void onDisable() {
        messagesFile.saveConfig();
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
}
