package org.cyci.mc.joinevents.actions;

import me.clip.placeholderapi.PlaceholderAPI;
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
    private final String actionTarget;
    private String actionMessage = null;

    public MessageAction(String actionTarget, String message) {
        this.actionTarget = actionTarget;
        this.actionMessage = actionMessage;
    }

    @Override
    public void execute(Player player) {
        if ("Player".equalsIgnoreCase(actionTarget)) {
            String finalMessage = PlaceholderAPI.setPlaceholders(player, this.actionMessage);
            player.sendMessage(C.c(finalMessage));
        }
    }
}
