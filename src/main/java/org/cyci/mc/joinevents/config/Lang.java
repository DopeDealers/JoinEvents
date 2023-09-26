package org.cyci.mc.joinevents.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Helped and created by an old friend
 */
public enum Lang {
    PREFIX("messages.prefix", "&a&lJoinEvents &7>"),
    NO_PERM("messages.noperm", "{1} &7You do not have the&c {0} &7permission"),
    RELOAD("messages.reload", "{1} &aReloaded the config."),
    NOT_ENOUGH_ARGS("messages.no_arg", "{1} &cNot enough args.");

    private final String path;

    private final String def;

    private static FileConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(FileConfiguration config) {
        LANG = config;
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }
    public String setConfigValue(String args) {
        String value = Lang.LANG.getString(this.path, this.def);
        value = value.replace(this.def, args);
        return value;
    }

    public String getConfigValue(String[] args) {
        String value = LANG.getString(this.path, this.def);
        if (args == null)
            return value;
        if (args.length == 0)
            return value;
        for (int i = 0; i < args.length; i++)
            value = value.replace("{" + i + "}", args[i]);
        return value;
    }
    public String getValue() {
        return LANG.getString(this.path, this.def);
    }
    public boolean getBoolean() {
        return LANG.getBoolean(this.path, Boolean.parseBoolean(this.def));
    }

    public int getInt() {
        return LANG.getInt(this.path, Integer.parseInt(this.def));
    }
}
