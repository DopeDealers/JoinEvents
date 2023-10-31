package org.cyci.mc.joinevents.manager;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 31/Oct/2023 - 2:53 PM
 */
public class RewardManager {
    private String name;
    private String command;
    private int requiredPlaytime;
    private Map<String, Double> multipliers;

    public RewardManager(String name, String command, int requiredPlaytime, Map<String, Double> multipliers) {
        this.name = name;
        this.command = command;
        this.requiredPlaytime = requiredPlaytime;
        this.multipliers = multipliers;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public int getRequiredPlaytime() {
        return requiredPlaytime;
    }

    public Map<String, Double> getMultipliers() {
        return multipliers;
    }

    public static RewardManager loadFromConfig(ConfigurationSection section) {
        String name = section.getString("name");
        String command = section.getString("command");
        int requiredPlaytime = section.getInt("required-playtime");
        Map<String, Double> multipliers = new HashMap<>();

        ConfigurationSection multiplierSection = section.getConfigurationSection("multiplier");
        if (multiplierSection != null) {
            for (String rank : multiplierSection.getKeys(false)) {
                double multiplier = multiplierSection.getDouble(rank);
                multipliers.put(rank, multiplier);
            }
        }

        return new RewardManager(name, command, requiredPlaytime, multipliers);
    }
    public static List<RewardManager> loadAllRewards(ConfigurationSection rewardsSection) {
        List<RewardManager> rewards = new ArrayList<>();
        if (rewardsSection != null) {
            for (String rewardKey : rewardsSection.getKeys(false)) {
                ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardKey);
                assert rewardSection != null;
                RewardManager reward = RewardManager.loadFromConfig(rewardSection);
                rewards.add(reward);
            }
        }
        return rewards;
    }
}