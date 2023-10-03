package org.cyci.mc.joinevents.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.utils.C;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 03/Oct/2023 - 1:34 PM
 */
public class IConfigAsyncTask extends BukkitRunnable {
    private final IConfig config;
    private final Player player;
    private final String rankId;
    private final String messageType;

    public IConfigAsyncTask(Plugin plugin, IConfig config, Player player, String rankId, String messageType) {
        this.config = config;
        this.player = player;
        this.rankId = rankId;
        this.messageType = messageType;
        this.runTaskAsynchronously(plugin);
    }

    @Override
    public void run() {
        String message = config.parseMessage(player, rankId, messageType);
        Bukkit.broadcast(C.c(message));
    }
}