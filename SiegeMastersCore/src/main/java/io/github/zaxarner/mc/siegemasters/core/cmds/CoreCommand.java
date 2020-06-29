package io.github.zaxarner.mc.siegemasters.core.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandContextException;
import co.aikar.commands.ShowCommandHelp;
import co.aikar.commands.annotation.*;
import io.github.zaxarner.mc.siegemasters.core.HologramManager;
import io.github.zaxarner.mc.siegemasters.core.MessageManager;
import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.leaderboards.LeaderboardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created on 5/8/2020.
 */
@CommandPermission("siegemasters.admin")
@CommandAlias("siegemasterscore|smcore|core")
public class CoreCommand extends BaseCommand {

    private SiegeMastersCore plugin;

    private MessageManager messageManager;

    public CoreCommand(SiegeMastersCore plugin) {
        this.plugin = plugin;

        this.messageManager = plugin.getMessageManager();
    }

    @Subcommand("version|ver|v")
    public void onVersion(CommandSender sender) {
        messageManager.message(sender, MessageManager.Message.VERSION);

    }

    @Subcommand("reload")
    @CommandCompletion("@reload-options")
    public void onReload(CommandSender sender, String selection) {

        switch (selection.toUpperCase()) {
            case "HOLOGRAMS":
                plugin.reloadConfig();
                messageManager.message(sender, "{prefix} Reloading HologramManager configuration...");
                reloadHolograms();
                break;
            case "MESSAGES":
                plugin.reloadConfig();
                messageManager.message(sender, "{prefix} Reloading MessageManager configuration...");
                reloadMessages();
                break;
            case "LEADERBOARDS":
                plugin.reloadConfig();
                messageManager.message(sender, "{prefix} Reloading LeaderboardManager configuration...");
                reloadLeaderboards();
                break;
            case "ALL":
                plugin.reloadConfig();
                messageManager.message(sender, "{prefix} Reloading all configurations...");
                reloadHolograms();
                reloadMessages();
                reloadLeaderboards();
                break;
            default:
                messageManager.message(sender, MessageManager.Message.INVALID_ARGUMENT);
                throw new ShowCommandHelp();
        }
    }

    private void reloadHolograms() {
        HologramManager hologramManager = plugin.getHologramManager();
        if(hologramManager != null) {
            hologramManager.load();
        }
    }

    private void reloadLeaderboards() {
        LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
        if(leaderboardManager != null) {
            leaderboardManager.load();
        }
    }

    private void reloadMessages() {
        messageManager.load();
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
}
