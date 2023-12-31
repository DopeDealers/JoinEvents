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
import org.cyci.mc.joinevents.tasks.IConfigAsyncTask;
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

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.config.getConfig());
    /**
     * The onPlayerJoinTimeAndJoinTrackers function is called when a player joins the server.
     * It adds the player to the PlayerTimeTracker if they are not already in it, and records their login time.

     *
     * @param PlayerJoinEvent event Get the player that joined
     *
     * @return Nothing
     *
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinTimeAndJoinTrackers(PlayerJoinEvent event) {

        Registry.instance.getPlayerTimeTracker().addPlayerIfNotExists(event.getPlayer());
        Registry.instance.getPlayerTimeTracker().recordLogin(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinScoreBoardAndLeaderBoard(PlayerJoinEvent event) {

    }

    /**
     * The rankJoins function is called when a player joins the server.
     * It checks if the player has permission for any of the ranks, and if they do, it plays a sound, broadcasts a message to all players on the server (if there is one), shows them a boss bar (if there is one), gives them an item in their inventory (if there are any items specified) and shoots fireworks at their location.

     *
     * @param PlayerJoinEvent e Get the player who joined
     *
     * @return A list of strings
     *
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void rankJoins(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ConfigurationSection ranksSection = Registry.instance.config.getConfig().getConfigurationSection("config.ranks");

        if (ranksSection == null) {
            return;
        }

        for (String rankId : ranksSection.getKeys(false)) {
            if (config.isRankEnabled(rankId) && config.hasPermission(player, rankId)) {
                SoundParser soundParser = config.parseNoise(rankId);
                if (soundParser != null) {
                    player.playSound(player.getLocation(), soundParser.getSound(), soundParser.getVolume(), soundParser.getPitch());
                }

                new IConfigAsyncTask(Registry.instance, config, player, rankId, "join");

                BossBarParser bossBarParser = config.parseBossBar(rankId);
                if (bossBarParser != null) {
                    bossBarParser.showToPlayer(player);
                }

                ItemStack joinBook = config.parseJoinBook(player, rankId);
                if (joinBook != null) {
                    player.openBook(joinBook);
                }

                List<FireworkParser> fireworks = config.getFireworks(rankId);
                fireworks.forEach(fireworkParser -> FireworkParser.shootFirework(player.getLocation(), fireworkParser));

                for (String itemName : config.getJoinItemNames(rankId)) {
                    ConfigurationSection joinItemSection = ranksSection.getConfigurationSection(rankId + ".joinItems." + itemName);
                    if (joinItemSection != null) {
                        int slot = joinItemSection.getInt("slot");
                        if (slot >= 0 && slot < player.getInventory().getSize()) {
                            ItemStack joinItem = config.parseJoinItem(rankId, itemName);
                            if (joinItem != null) {
                                player.getInventory().setItem(slot, joinItem);
                            }
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
