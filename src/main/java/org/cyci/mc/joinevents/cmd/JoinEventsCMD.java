package org.cyci.mc.joinevents.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyci.mc.joinevents.Registry;
import org.cyci.mc.joinevents.config.IConfig;
import org.cyci.mc.joinevents.config.Lang;
import org.cyci.mc.joinevents.utils.C;

import java.util.HashMap;
import java.util.Map;

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
    @CommandCompletion("Player")
    public void onTime(CommandSender sender, @Flags("other") Player target) {
        int playtime = Registry.instance.getPlayerTimeTracker().getPlaytime(target.getUniqueId().toString());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{prefix}", Lang.PREFIX.getConfigValue());
        replacements.put("{time}", String.valueOf(playtime));
        String message = Lang.ON_TIME_COMMAND.getConfigValue(target, replacements);
        sender.sendMessage(C.c(message));
    }

    @Subcommand("logins")
    @Description("Display player's login count")
    @CommandCompletion("Player")
    public void onLogins(CommandSender sender, @Flags("other") Player target) {
        int logins = Registry.instance.getPlayerTimeTracker().getLogins(target.getUniqueId().toString());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{prefix}", Lang.PREFIX.getConfigValue());
        replacements.put("{logins}", String.valueOf(logins));
        String message = Lang.ON_LOGIN_COMMAND.getConfigValue(target, replacements);
        sender.sendMessage(C.c(message));
    }

    @Subcommand("reload")
    @Description("Reload the plugin's configuration")
    @CommandPermission("joinevents.commands.reload")
    public void onReload(Player player) {
        Registry.instance.messagesFile.reloadConfig();
        Registry.instance.config.reloadConfig();

        Lang.setFile(Registry.instance.messagesFile.getConfig());
        IConfig.setFile(Registry.instance.config.getConfig());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{prefix}", Lang.PREFIX.getConfigValue());
        String message = Lang.RELOAD.getConfigValue(player, replacements);
        player.sendMessage(C.c(message));
    }

    @Default
    public void onDefault(Player player) {
        StringBuilder page = new StringBuilder();
        page.append("&a&lJoinEvents\n\n");

        page.append("&6Developer: &aPrisk\n");
        page.append("&6Version: &a" + "\n");
        player.sendMessage(C.c(page.toString()));
    }
}
