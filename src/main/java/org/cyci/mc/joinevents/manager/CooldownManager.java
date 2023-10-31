package org.cyci.mc.joinevents.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.utils.C;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author - Phil
 * @project - JoinEvents
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 24/Sep/2023 - 9:12 PM
 */
public class CooldownManager {

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.config.getConfig());    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private static CooldownManager instance;

    private static final Pattern COOLDOWN_PATTERN = Pattern.compile(
            "(?:(\\d+)d\\s*)?(?:(\\d+)h\\s*)?(?:(\\d+)m\\s*)?(?:(\\d+)s)?"
    );
    public CooldownManager() {
    }

    public static CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }

    public long parseCooldownString(String cooldownString) {
        Registry.instance.getLogger().info("Cooldown String: " + cooldownString);
        Matcher matcher = COOLDOWN_PATTERN.matcher(cooldownString);
        if (matcher.matches()) {
            int days = parseGroup(matcher.group(1));
            int hours = parseGroup(matcher.group(2));
            int minutes = parseGroup(matcher.group(3));
            int seconds = parseGroup(matcher.group(4));

            long totalMillis = daysToMillis(days) + hoursToMillis(hours) + minutesToMillis(minutes) + secondsToMillis(seconds);

            return totalMillis;
        }
        return 0;
    }

    private static int parseGroup(String group) {
        return (group != null && !group.isEmpty()) ? Integer.parseInt(group) : 0;
    }

    private static long daysToMillis(int days) {
        return days * 24L * 60 * 60 * 1000L;
    }

    private static long hoursToMillis(int hours) {
        return hours * 60 * 60 * 1000L;
    }

    private static long minutesToMillis(int minutes) {
        return minutes * 60 * 1000L;
    }

    private static long secondsToMillis(int seconds) {
        return seconds * 1000L;
    }

    public void addCooldown(UUID playerId, String customItemName, long endTime) {
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>()).put(customItemName, endTime);
    }

    public boolean hasCooldown(UUID playerId, String customItemName) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        return playerCooldowns != null && playerCooldowns.containsKey(customItemName);
    }

    public static long getCooldownEndTime(UUID playerId, String customItemName) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns != null) {
            Long endTime = playerCooldowns.get(customItemName);
            if (endTime != null) {
                return endTime;
            }
        }
        return 0;
    }

    public void removeCooldown(UUID playerId, String customItemName) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns != null) {
            playerCooldowns.remove(customItemName);
        }
    }

    public void sendCooldownMessage(Player player, String customItemName, long cooldownEndTime) {
        long currentTime = System.currentTimeMillis();

        long remainingTimeMillis = cooldownEndTime - currentTime;
        long remainingSeconds = remainingTimeMillis / 1000;

        long days = remainingSeconds / (24 * 60 * 60);
        long hours = (remainingSeconds % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((remainingSeconds % (24 * 60 * 60)) % (60 * 60)) / 60;
        long seconds = ((remainingSeconds % (24 * 60 * 60)) % (60 * 60)) % 60;

        String playerRank = config.getRankIdForPlayer(player);

        String cooldownPath = "config.ranks." + playerRank + ".joinItems." + customItemName + ".cooldown";
        String cooldownMessage = config.getString(cooldownPath + ".message");

        if (cooldownMessage != null) {
            cooldownMessage = cooldownMessage.replace("%d", String.valueOf(days));
            cooldownMessage = cooldownMessage.replace("%h", String.format("%02d", hours));
            cooldownMessage = cooldownMessage.replace("%m", String.format("%02d", minutes));
            cooldownMessage = cooldownMessage.replace("%s", String.format("%02d", seconds));
            player.sendMessage(C.c(PlaceholderAPI.setPlaceholders(player, cooldownMessage)));
        } else {
            Registry.instance.getLogger().info("Cooldown message is null for item: " + customItemName);
            Registry.instance.getLogger().info("Cooldown path is: " + cooldownPath);
        }
    }
}