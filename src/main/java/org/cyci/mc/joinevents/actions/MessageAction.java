package org.cyci.mc.joinevents.actions;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.utils.Actions;
import org.cyci.mc.joinevents.utils.C;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Sun - 01/Oct/2023 - 9:13 PM
 */
public class MessageAction implements Actions {
    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }
    @Override
    public void execute(Player player) {
        String finalMessage = PlaceholderAPI.setPlaceholders(player, this.message);
        player.sendMessage(C.c(finalMessage));
    }

    @Override
    public void loadFromConfig(ConfigurationSection config) {

    }
}
