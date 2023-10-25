package org.cyci.mc.joinevents.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.utils.C;

import java.util.List;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Thu - 12/Oct/2023 - 7:52 PM
 */
public class ScoreboardManager {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());

    private org.bukkit.scoreboard.ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardManager(JavaPlugin plugin) {
        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("customObjective", "dummy", C.c("Test"));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        loadScoreboardFromConfig();
    }

    private void loadScoreboardFromConfig() {
        List<String> scoreboardLines = config.getStringList("config.scoreboard.lines");

        if (config.contains("config.scoreboard.title")) {
            String title = config.getString("config.scoreboard.title");
            objective.displayName(C.c(title));
        }

        for (int i = 0; i < scoreboardLines.size() && i < 16; i++) {
            objective.getScore((OfflinePlayer) C.c(scoreboardLines.get(i))).setScore(15 - i);
        }
    }
}
