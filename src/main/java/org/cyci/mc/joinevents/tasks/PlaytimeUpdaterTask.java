package org.cyci.mc.joinevents.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyci.mc.joinevents.Registry;

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
            Registry.instance.getPlayerTimeTracker().recordPlaytime(player, 1);
        }
    }
}