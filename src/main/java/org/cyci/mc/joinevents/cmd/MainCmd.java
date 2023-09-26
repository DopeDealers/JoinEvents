package org.cyci.mc.joinevents.cmd;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.cmd.api.CommandListener;
import org.cyci.mc.joinevents.config.Lang;
import org.cyci.mc.joinevents.utils.C;


public class MainCmd extends CommandListener {


    public MainCmd() {
        super("je", "joinevents.commands.main");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) return;
        String noArgs = PlaceholderAPI.setPlaceholders(((Player) sender).getPlayer(), Lang.NOT_ENOUGH_ARGS.getConfigValue(new String[]{"", Lang.PREFIX.getConfigValue(null)}));
        Player player = ((Player) sender).getPlayer();
        if (args.length == 0) sender.sendMessage(C.c(noArgs));
        else {
            if (args[0].equals("reload")) {
                assert player != null;
                if (player.hasPermission("joinevents.commands.reload")) {
                    try {
                        Registry.instance.config.reloadConfig();
                    } catch (Exception e) {
                        Registry.instance.getLogger().warning(e.getMessage());
                    } finally {
                        sender.sendMessage(C.c(PlaceholderAPI.setPlaceholders(((Player) sender).getPlayer(), Lang.RELOAD.getConfigValue(new String[]{"", Lang.PREFIX.getConfigValue(null)}))));
                    }
                }
            }
        }
    }
}