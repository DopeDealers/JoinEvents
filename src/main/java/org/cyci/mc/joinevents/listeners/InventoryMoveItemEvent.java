package org.cyci.mc.joinevents.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.utils.CustomNBTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 26/Sep/2023 - 1:22 AM
 */
public class InventoryMoveItemEvent implements Listener {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());

    /**
     * The onItemMove function is an event handler that prevents players from moving items in their inventory.
     * This function is called whenever a player clicks on an item in their inventory, and it checks to see if the item
     * has been marked as unmovable by the plugin. If so, then it cancels the click event and prevents them from moving
     * or dropping that item. This function also checks to make sure that they are not clicking on air (an empty slot).

     *
     * @param InventoryClickEvent e Get the player who clicked, and the item they clicked
     *
     * @return A boolean, but i don't know what to return
     *
     */
    @EventHandler
    public void onItemMove(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item != null && item.getType() != Material.AIR) {
            String rankId = config.getRankIdForPlayer(player);
            if (rankId != null) {
                List<String> customItemNames = config.getJoinItemNames(rankId);
                if (customItemNames != null) {
                    for (String itemName : customItemNames) {
                        if (itemName != null && !itemName.isEmpty()) {
                            if (config.isCustomItem(item, rankId, itemName, player)) {
                                if (!Objects.requireNonNull(CustomNBTUtil.getStringNBTValue(item, "unmovable")).isEmpty()) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
