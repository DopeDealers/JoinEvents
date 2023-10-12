package org.cyci.mc.joinevents.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.utils.C;

/**
 * @project - JoinEvents
 * @author - Phil
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Tue - 03/Oct/2023 - 2:02 PM
 */
// TODO add correct command info and correct configuration for the commands and subcommands
@CommandAlias("je|joinevents")
@CommandPermission("joinevents.commands.main")
public class JoinEventsCMD extends BaseCommand {

    @Subcommand("help")
    @Description("Display the plugin's help page")
    public void onHelp(CommandSender sender) {
        sender.sendMessage("Help page content goes here.");
    }

    @Subcommand("info")
    @Description("Display developer info")
    public void onInfo(CommandSender sender) {
        StringBuilder page = new StringBuilder();
        page.append("&a&lJoinEvents\n\n");

        page.append("&6Developer: &aPrisk\n");
        page.append("&6Version: &a" + "\n");
        sender.sendMessage(C.c(page.toString()));
    }

    @Subcommand("time")
    @Description("Display player's tracked playtime")
    public void onTime(CommandSender sender, @Flags("other") Player target) {
        int playtime = Registry.instance.getPlayerTimeTracker().getPlaytime(target.getUniqueId().toString());
        sender.sendMessage(C.c("&7" + target.getName() + "'s Playtime: &b" + playtime + " &7minutes"));
    }

    @Subcommand("logins")
    @Description("Display player's login count")
    public void onLogins(CommandSender sender, @Flags("other") Player target) {
        int logins = Registry.instance.getPlayerTimeTracker().getLogins(target.getUniqueId().toString());
        sender.sendMessage(C.c("&7" + target.getName() + "'s Logins: &b" + logins));
    }

    @Subcommand("reload")
    @Description("Reload the plugin's configuration")
    @CommandPermission("joinevents.commands.reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage("Reloading plugin configuration...");
    }

    @Default
    public void onDefault(Player player) {
        // Implement default command logic here
        player.sendMessage("Welcome to MyPlugin! Use /myplugin help for more information.");
    }
}
