package org.cyci.mc.joinevents.cmd;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.cmd.api.CommandListener;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.config.Lang;
import org.cyci.mc.joinevents.manager.ConfigManager;
import org.cyci.mc.joinevents.utils.C;


public class MainCmd extends CommandListener {


    public MainCmd() {
        super("je", "joinevents.commands.main");
    }

    IConfig config = new ConfigManager(Registry.instance).getConfig("config.yml", Registry.instance.getConfig());


    /**
     * The execute function is called when a player executes the command.
     *
     *
     * @param final CommandSender sender Send messages to the player who executed this command
     * @param final String[] args Get the arguments that were passed to the command
     *
     * @return A boolean, so you can do something like this:
     *
     */
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) return;

        String noArgs = PlaceholderAPI.setPlaceholders(((Player) sender).getPlayer(), Lang.NOT_ENOUGH_ARGS.getConfigValue(new String[]{"", Lang.PREFIX.getConfigValue(null)}));
        Player player = ((Player) sender).getPlayer();

        if (args.length == 0) {
            player.sendMessage(C.c("Hi im empty"));
        } else if (args[0].equals("reload")) {
            assert player != null;

            if (player.hasPermission("joinevents.commands.reload")) {
                try {
                    // Assuming your ConfigManager constructor takes the Plugin instance as an argument
                    ConfigManager configManager = new ConfigManager(Registry.instance);

                    // Update the configuration and save it
                    configManager.updateConfig("config.yml", Registry.instance.getConfig());
                    configManager.saveConfig();

                    // Inform the player that the configuration has been reloaded
                    sender.sendMessage(C.c(PlaceholderAPI.setPlaceholders(((Player) sender).getPlayer(), Lang.RELOAD.getConfigValue(new String[]{"", Lang.PREFIX.getConfigValue(null)}))));
                } catch (Exception e) {
                    Registry.instance.getLogger().warning(e.getMessage());
                }
            }
        }
    }
}