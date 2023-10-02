package org.cyci.mc.joinevents.listeners;


import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.manager.CooldownManager;
import org.cyci.mc.joinevents.utils.Actions;

import java.util.List;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 05/Sep/2023 - 7:22 PM
 */
public class PlayerInteract implements Listener {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());
    CooldownManager cooldownManager = new CooldownManager();

    /**
     * The onPlayerInteractEntity function is an event handler that listens for when a player interacts with an entity.
     * It checks if the item in their main hand is not null, and if it's not air. If it isn't, then we check to see what rank they are.
     * If they have a rank, we get all of the custom items associated with that rank from config and loop through them one by one.
     * For each custom item name in the list of names for this specific player's rank: We check to see if this item matches any of those names (if so, then it's a custom join item).
     *
     *
     * @param PlayerInteractEvent e Get the player who is interacting with the entity
     *
     * @return A playerinteractevent
     *
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR && e.getHand() == EquipmentSlot.HAND) {
            String rankId = config.getRankIdForPlayer(player);
            if (rankId != null) {
                List<String> customItemNames = config.getJoinItemNames(rankId);
                if (customItemNames != null) {
                    for (String itemName : customItemNames) {
                        if (itemName != null && !itemName.isEmpty()) {
                            if (config.isCustomItem(item, rankId, itemName, player)) {
                                long currentTime = System.currentTimeMillis();
                                long cooldownEndTime = CooldownManager.getCooldownEndTime(player.getUniqueId(), itemName);

                                if (currentTime >= cooldownEndTime) {
                                    e.setCancelled(true);
                                    String cooldownPath = "config.ranks." + rankId + ".joinItems." + itemName + ".cooldown";
                                    String cooldownTime = config.getString(cooldownPath + ".time");
                                    if (cooldownTime != null) {
                                        long cooldownMillis = cooldownManager.parseCooldownString(cooldownTime);
                                        long newCooldownEndTime = currentTime + cooldownMillis;
                                        String clickType = e.getAction().isLeftClick() ? "left_click" : "right_click";
                                        CooldownManager.getInstance().addCooldown(player.getUniqueId(), itemName, newCooldownEndTime);
                                        List<String> commands = config.getStringList("config.ranks." + rankId + ".joinItems." + itemName + ".commands." + clickType);
                                        if (commands != null && !commands.isEmpty() && e.getAction().isRightClick()) {
                                            for (String command : commands) {
                                                command = PlaceholderAPI.setPlaceholders(player, command);
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                        }

                                        List<Actions> actions = config.parseActions(rankId, itemName, clickType);

                                        for (Actions action : actions) {
                                            action.execute(player);
                                        }
                                    } else {
                                        Registry.instance.getLogger().info("Cooldown time is null for item: " + itemName);
                                        Registry.instance.getLogger().info("Entire path for config: " + cooldownPath);
                                    }
                                } else {
                                    CooldownManager.getInstance().sendCooldownMessage(player, itemName, cooldownEndTime);
                                    e.setCancelled(true);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}