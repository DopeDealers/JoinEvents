package org.cyci.mc.joinevents.tasks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.parsers.SoundParser;

import java.util.List;
import java.util.Map;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Mon - 02/Oct/2023 - 4:44 PM
 */

public class PlaytimeUpdaterTask extends BukkitRunnable {
    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String uuid = player.getUniqueId().toString();
            int playtime = Registry.instance.getPlayerTimeTracker().getPlaytime(uuid);

            List<String> rankIds = config.getAllRanks();
            for (String rankId : rankIds) {
                if (config.isRewardEnabled(rankId) && playtime >= config.getRequiredPlaytime(rankId)) {
                    SoundParser soundParser = config.parseCustomRewardNoise(rankId);
                    if (soundParser != null) {
                        player.playSound(player.getLocation(), soundParser.getSound(), soundParser.getVolume(), soundParser.getPitch());
                    }

                    String customCommand = config.getRewardCommand(rankId);
                    if (customCommand != null && !customCommand.isEmpty()) {
                        customCommand = PlaceholderAPI.setPlaceholders(player, customCommand);
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), customCommand);
                    }

                    Map<String, Double> multipliers = config.getRewardMultipliers(rankId);
                    for (String rank : multipliers.keySet()) {
                        double multiplier = multipliers.get(rank);
                        int modifiedPlaytime = (int) (playtime * multiplier);
                        Registry.instance.getPlayerTimeTracker().recordPlaytime(uuid, modifiedPlaytime);
                    }
                }
            }
        }
    }
}