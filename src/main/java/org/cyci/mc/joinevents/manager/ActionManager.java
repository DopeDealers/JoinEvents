package org.cyci.mc.joinevents.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.cyci.mc.joinevents.utils.Actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Thu - 12/Oct/2023 - 9:15 AM
 */
public class ActionManager {
    private final Map<String, Actions> actions = new HashMap<>();

    public ActionManager(ConfigurationSection configSection) {
        loadActionsFromConfig(configSection);
    }


    // TODO Implement this effectively
    private void loadActionsFromConfig(ConfigurationSection configSection) {
        Set<String> actionClassNames = configSection.getKeys(false);

        for (String actionId : actionClassNames) {
            String actionClassName = configSection.getString(actionId);

            try {
                Class<?> actionClass = Class.forName("org.cyci.mc.joinevents.actions." + actionClassName);

                if (Actions.class.isAssignableFrom(actionClass)) {
                    Constructor<?> constructor = actionClass.getConstructor();
                    Actions customAction = (Actions) constructor.newInstance();
                    customAction.loadFromConfig(configSection.getConfigurationSection(actionId));
                    actions.put(actionId, customAction);
                } else {
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public Actions getAction(String actionId) {
        return actions.get(actionId);
    }
}
