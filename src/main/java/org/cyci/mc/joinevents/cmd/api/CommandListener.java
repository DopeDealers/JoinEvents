package org.cyci.mc.joinevents.cmd.api;

import org.bukkit.command.CommandSender;

public abstract class CommandListener
{
    private final String name;
    private final String permission;

    public CommandListener(final String name, final String permission) {
        this.name = name;
        this.permission = permission;
    }

    public CommandListener(final String name) {
        this.name = name;
        this.permission = "";
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public abstract void execute(final CommandSender p0, final String[] p1);
}