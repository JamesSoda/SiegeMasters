package io.github.zaxarner.mc.siegemasters.core.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.statistics.CoreStatistic;
import me.minidigger.minimessage.MiniMessageParser;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/1/2020.
 */
@CommandAlias("test")
@Description("This is a test command... USING ACF")
@CommandPermission("siegemasters.admin")
public class TestCommand extends BaseCommand {

    private final static ArrayList<String> HOVER_INFO_FORMAT = new ArrayList<String>() {
        {
            add("<gray><username>'s Profile");
            add("");
            add("<gray>Prestige: <prestige>\t<gray>Votes: <white><votes>");
            add("<gray>Level: <white><level>\t<gray>Donated: <gold>$<green><donated>");
            //TODO: Clans
            //add("");
            //add("<gray>Clan Tag: <white>[BARF]");
            //add("<gray>Clan Name: <white>[Be A Regular Fool]");
        }
    };


    private Map<String, String> getPlaceholders(@NotNull Player player) {
        Map<String, String> placeholders = new HashMap<>();
        /*
        CorePlayerProfile playerProfile = SiegeMastersCore.getPlugin().getProfile(player);

        placeholders.put("username", player.getName());
        placeholders.put("display-name", player.getDisplayName());
        placeholders.put("level", "" + playerProfile.getLevel());
        placeholders.put("votes", "" + playerProfile.getVotes());
        placeholders.put("donated", "" + playerProfile.getDonated());
        placeholders.put("prestige", "" + playerProfile.getPrestigeString());
         */

        return placeholders;
    }

    private BaseComponent[] buildPlayerMessage(@NotNull Player player, @NotNull String message) {

        StringBuilder format = new StringBuilder("<hover:show_text:\"");

        for(int i=0; i < HOVER_INFO_FORMAT.size(); i++) {
            String string = HOVER_INFO_FORMAT.get(i);
            String[] columns = string.split("\t");

            if(columns.length == 2) {
                format.append(String.format("%-42s", columns[0])).append(columns[1]);
            } else {
                format.append(string);
            }

            if(i < HOVER_INFO_FORMAT.size() - 1) {
                format.append("\n");
            }
        }

        format.append("\"><display-name> <white>> ").append(message);

        return MiniMessageParser.parseFormat(format.toString(), getPlaceholders(player));
    }

    @Subcommand("message")
    public void onMessage(CommandSender sender) {
        sender.sendMessage("Displaying Test...");


        if (sender instanceof Player) {
            Player player = (Player) sender;

            String message = "This is a test message.";
            BaseComponent[] richMessage = buildPlayerMessage(player, message);

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("siegemasters.admin")) {

                    p.spigot().sendMessage(richMessage);
                }
            }
        }
    }

    @Subcommand("statistic get")
    public void onStatistic(Player sender, String statistic) {
        sender.sendMessage(statistic + ": " + SiegeMastersCore.getPlugin().getDatabaseManager().getPlayerStatistic(sender, statistic.toUpperCase()));
    }

    @Subcommand("statistic set")
    public void onStatisticSet(Player sender, String statistic, Double value) {
        SiegeMastersCore.getPlugin().getDatabaseManager().setPlayerStatistic(sender, statistic.toUpperCase(), value);
    }

    @Default
    @HelpCommand
    public void onTestHelp(CommandSender sender, CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
}