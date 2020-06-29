package io.github.zaxarner.mc.siegemasters.core.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.zaxarner.mc.siegemasters.core.HologramManager;
import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * Created on 5/25/2020.
 */
public class Leaderboard {

    public enum Type {
        MONTHLY,
        ALL_TIME;
    }

    private static SiegeMastersCore plugin;
    private static DatabaseManager dbManager;
    private static HologramManager hologramManager;

    private String statistic;
    private Type type;
    private Location location;
    private List<String> header;

    private Hologram hologram;

    public Leaderboard(String statistic, Type type, Location location, List<String> header) {
        this.statistic = statistic;
        this.type = type;
        this.location = location;
        this.header = header;

        plugin = SiegeMastersCore.getPlugin();
        dbManager = SiegeMastersCore.getPlugin().getDatabaseManager();
        hologramManager = plugin.getHologramManager();
    }


    public void refresh() {

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            return;
        }

        // Player UUID and the value of the statistic
        Map<UUID, Double> topStatistics = dbManager.getTopStatistics(statistic, 10);

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(plugin, location);
        }

        hologram.setAllowPlaceholders(true);
        hologram.getVisibilityManager().setVisibleByDefault(true);
        hologram.clearLines();
        for (String line : header) {
            hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
        }

        hologram.appendTextLine("");
        int position = 1;
        for (UUID uuid : topStatistics.keySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            hologram.appendTextLine(ChatColor.GOLD + "#" + position + " " + player.getName() + " : " + topStatistics.get(uuid));

            position++;
        }

        //TODO: Maybe separate this into a separate task... and maybe make cleanupDistance not hard-coded.
        hologramManager.refreshHologram(hologram, 16);
    }

    public void cleanup() {
        hologram.delete();
    }
}