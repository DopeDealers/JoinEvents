package org.cyci.mc.joinevents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.parsers.BossBarParser;
import org.cyci.mc.joinevents.utils.C;
import org.cyci.mc.joinevents.parsers.FireworkParser;
import org.cyci.mc.joinevents.parsers.SoundParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 20/Aug/2023 - 11:02 PM
 */
public class PlayerJoin  implements Listener {

    IConfig config = ConfigManager.getConfig("config.yml",  Registry.instance.config.getConfig());

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rankJoins(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        List<String> rankIds = new ArrayList<>(Registry.instance.config.getConfig().getConfigurationSection("config.ranks").getKeys(false));
        for (String rankId : rankIds) {
            if (config.isRankEnabled(rankId) && config.hasPermission(player, rankId)) {
                SoundParser soundParser = config.parseNoise(rankId);
                if (soundParser != null) {
                    player.playSound(player.getLocation(), soundParser.getSound(), soundParser.getVolume(), soundParser.getPitch());
                }
                String message = config.parseMessage(player, rankId, "join");
                Bukkit.broadcast(C.c(message));

                BossBarParser bossBarParser = config.parseBossBar(rankId);
                if (bossBarParser != null) {
                    bossBarParser.showToPlayer(player);
                }

                ItemStack joinBook = config.parseJoinBook(player, rankId);
                if (joinBook != null) {
                    player.openBook(joinBook);
                }

                List<FireworkParser> fireworks = config.getFireworks(rankId);
                for (FireworkParser fireworkParser : fireworks) {
                    FireworkParser.shootFirework(player.getLocation(), fireworkParser);
                }

                for (String itemName : config.getJoinItemNames(rankId)) {
                    ItemStack joinItem = config.parseJoinItem(rankId, itemName, player);
                    ConfigurationSection test = Registry.instance.config.getConfig().getConfigurationSection("config.ranks." + rankId + ".joinItems." + itemName);
                    if (joinItem != null && test != null) {
                        int slot = test.getInt("slot");
                        if (slot >= 0 && slot < player.getInventory().getSize()) {
                            player.getInventory().setItem(slot, joinItem);
                        } else {
                            Registry.instance.getLogger().warning("Invalid slot specified for join item: " + itemName);
                        }
                    }
                }
                break;
            }
        }
    }
}
