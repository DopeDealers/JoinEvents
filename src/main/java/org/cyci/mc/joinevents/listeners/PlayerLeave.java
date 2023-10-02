package org.cyci.mc.joinevents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.utils.C;

import java.util.ArrayList;
import java.util.List;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Thu - 31/Aug/2023 - 2:27 PM
 */
public class PlayerLeave implements Listener {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());
    /**
     * The rankJoins function is called when a player leaves the server.
     * It checks if the player has any ranks that have join items enabled, and removes them from their inventory.

     *
     * @param PlayerQuitEvent e Get the player who left
     *
     * @return A list of all the items that a player has received from joining
     *
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void rankJoins(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        List<String> rankIds = new ArrayList<>(Registry.instance.config.getConfig().getConfigurationSection("config.ranks").getKeys(false));
        for (String rankId : rankIds) {
            if (config.isRankEnabled(rankId) && config.hasPermission(player, rankId)) {
                for (String itemName : config.getJoinItemNames(rankId)) {
                    String message = config.parseMessage(player, rankId, "quit");
                    Bukkit.broadcast(C.c(message));
                    PlayerInventory inventory = player.getInventory();
                    ItemStack[] contents = inventory.getContents();
                    for (int i = 0; i < contents.length; i++) {
                        ItemStack item = contents[i];
                        if (item != null && config.isCustomItem(item, rankId, itemName, player)) {
                            inventory.setItem(i, null);
                        }
                    }
                    break;
                }
            }
        }
    }
}