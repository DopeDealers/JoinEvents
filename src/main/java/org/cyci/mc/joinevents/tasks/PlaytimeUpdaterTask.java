package org.cyci.mc.joinevents.tasks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.manager.RewardManager;
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
  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      String uuid = player.getUniqueId().toString();
      int playtime = Registry.instance.getPlayerTimeTracker().getPlaytime(uuid);

      ConfigurationSection rewardsSection =
          Registry.instance.config.getConfig().getConfigurationSection("time.rewards");
      if (rewardsSection != null) {
        List<RewardManager> rewards = RewardManager.loadAllRewards(rewardsSection);
        for (RewardManager reward : rewards) {
          if (reward.getRequiredPlaytime() <= playtime) {
            String customCommand = reward.getCommand();
            if (customCommand != null && !customCommand.isEmpty()) {
              customCommand = customCommand.replace("{player}", player.getName());
              Bukkit.getServer()
                  .dispatchCommand(Bukkit.getServer().getConsoleSender(), customCommand);
            }

            Map<String, Double> multipliers = reward.getMultipliers();
            for (Map.Entry<String, Double> entry : multipliers.entrySet()) {
              String rank = entry.getKey();
              double multiplier = entry.getValue();
              int modifiedPlaytime = (int) (playtime * multiplier);
              Registry.instance.getPlayerTimeTracker().recordPlaytime(uuid, modifiedPlaytime);
            }
          }
        }
      }
    }
  }
}