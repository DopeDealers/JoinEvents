package org.cyci.mc.joinevents.actions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.utils.Actions;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 01/Oct/2023 - 8:59 PM
 */
public class HealAction implements Actions {
    @Override
    public void execute(Player player) {
        player.setHealth(20);
    }

    @Override
    public void loadFromConfig(ConfigurationSection config) {

    }
}
