package org.cyci.mc.joinevents.listeners;


import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.manager.CooldownManager;

import java.util.List;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 05/Sep/2023 - 7:22 PM
 */
public class PlayerInteract implements Listener {

    IConfig config = ConfigManager.getConfig("config.yml", Registry.instance.config.getConfig());
    CooldownManager cooldownManager = new CooldownManager();


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
                                        CooldownManager.getInstance().addCooldown(player.getUniqueId(), itemName, newCooldownEndTime);
                                        List<String> commandsR = config.getStringList("config.ranks." + rankId + ".joinItems." + itemName + ".commands.right_click");
                                        List<String> commandsL = config.getStringList("config.ranks." + rankId + ".joinItems." + itemName + ".commands.left_click");
                                        if (commandsR != null && !commandsR.isEmpty() && e.getAction().isRightClick()) {
                                            for (String command : commandsR) {
                                                command = PlaceholderAPI.setPlaceholders(player, command);
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                        } else {
                                            for (String command : commandsL) {
                                                command = PlaceholderAPI.setPlaceholders(player, command);
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
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