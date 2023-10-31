package org.cyci.mc.joinevents.config;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.utils.C;

import java.util.Collections;
import java.util.List;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 27/Sep/2023 - 2:53 PM
 */
public class CustomConfigGui {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.config.getConfig());

    /**
     * The openRankEditor function opens a GUI for the player to edit ranks.
     *
     *
     * @param Player player Open the inventory for that specific player
     *
     * @return Nothing so it should be void
     *
     */
    public void openRankEditor(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9);

        for (String rankId : config.getAllRanks()) {
            boolean isEnabled = config.isRankEnabled(rankId);

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            meta.displayName(C.c(rankId));
            meta.lore((List<? extends Component>) C.c(isEnabled ? Collections.singletonList("Enabled").toString() : Collections.singletonList("Disabled").toString()));

            item.setItemMeta(meta);

            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }
}
