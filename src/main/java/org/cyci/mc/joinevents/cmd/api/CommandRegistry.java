package org.cyci.mc.joinevents.cmd.api;

import org.cyci.mc.joinevents.cmd.MainCmd;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {
    private static List<CommandListener> commands;

    public CommandRegistry() {
        if (CommandRegistry.commands == null) {
            CommandRegistry.commands = new ArrayList<CommandListener>();
        }
        CommandRegistry.commands.add(new MainCmd());

    }

    public static CommandListener getCommand(final String name) {
        for (final CommandListener commands : getCommands()) {
            if (commands.getName().equalsIgnoreCase(name)) {
                return commands;
            }
        }
        return null;
    }

    private static List<CommandListener> getCommands() {
        return CommandRegistry.commands;
    }
}
