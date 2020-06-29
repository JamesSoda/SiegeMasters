package io.github.zaxarner.mc.siegemasters.core.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.database.DatabaseManager;
import jdk.vm.ci.meta.Local;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Created on 5/21/2020.
 */
@CommandAlias("punish")
@CommandPermission("siegemasters.ban")
public class PunishCommand extends BaseCommand {

    private SiegeMastersCore plugin;
    private DatabaseManager dbManager;

    public PunishCommand(SiegeMastersCore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("ban")
    public void banPlayer(CommandSender sender, OnlinePlayer target, String reason) {
        Player player = target.getPlayer();

        if (player.hasPermission("siegemasters.staff")) {
            sender.sendMessage(ChatColor.RED + "You can not ban that player! Remove them from Staff first!");
            return;
        }

        dbManager.banPlayer(player.getUniqueId(), LocalDateTime.MAX, reason, sender);
        player.kickPlayer("You have been banned for: " + reason);
    }

    @Subcommand("ban")
    @CommandCompletion("@players @time-units @nothing @nothing")
    public void banPlayer(CommandSender sender, OnlinePlayer target, String unit, Integer time, String reason) {
        Player player = target.getPlayer();

        if (player.hasPermission("siegemasters.staff")) {
            sender.sendMessage(ChatColor.RED + "You can not ban that player! Remove them from Staff first!");
            return;
        }

        try {
            ChronoUnit chronoUnit = ChronoUnit.valueOf(unit);

            LocalDateTime until = LocalDateTime.now();
            until.plus(time, chronoUnit);

            dbManager.banPlayer(player.getUniqueId(), until, reason, sender);
            player.kickPlayer("You have been banned for: " + reason);
        } catch (EnumConstantNotPresentException ex) {
            sender.sendMessage(ChatColor.RED + "That is not a valid Time unit!");
        }
    }

    //TODO: BAN OFFLINE PLAYERS

}
