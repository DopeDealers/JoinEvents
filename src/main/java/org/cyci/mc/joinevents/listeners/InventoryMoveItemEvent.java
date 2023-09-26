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

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 26/Sep/2023 - 1:22 AM
 */
public class InventoryMoveItemEvent implements Listener {

    IConfig config = ConfigManager.getConfig("config.yml",  Registry.instance.config.getConfig());

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
                                if (CustomNBTUtil.getStringNBTValue(item, "unmovable").equals("true")) {
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
