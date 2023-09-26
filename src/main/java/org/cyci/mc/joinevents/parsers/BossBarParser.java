package org.cyci.mc.joinevents.parsers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 29/Aug/2023 - 3:29 PM
 */
public class BossBarParser {
    private final BarColor color;
    private final BarStyle style;
    private final String title;
    private final int timeLimitInSeconds; // Add a time limit in seconds
    private final float progress;

    public BossBarParser(BarColor color, BarStyle style, String title, int timeLimitInSeconds, float progress) {
        this.color = color;
        this.style = style;
        this.title = title;
        this.timeLimitInSeconds = timeLimitInSeconds;
        this.progress = progress;
    }

    public void showToPlayer(Player player) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);

        Bukkit.getScheduler().runTaskLater(Registry.instance, () -> bossBar.removePlayer(player), timeLimitInSeconds * 20L); // Convert seconds to ticks
    }
}
