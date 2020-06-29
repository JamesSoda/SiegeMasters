package io.github.zaxarner.mc.siegemasters.core.leaderboards;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/25/2020.
 */
public class LeaderboardManager {

    private SiegeMastersCore plugin;

    private Map<String, Leaderboard> leaderboards = new HashMap<>();
    private List<Integer> taskIds = new ArrayList<>();

    public LeaderboardManager(SiegeMastersCore plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }

        ConfigurationSection leaderboardManagerSection = plugin.getConfig().getConfigurationSection("leaderboard-manager.");
        if (leaderboardManagerSection != null) {
            if (leaderboardManagerSection.getBoolean("enabled")) {

                for (Leaderboard leaderboard : leaderboards.values()) {
                    leaderboard.cleanup();
                }
                leaderboards.clear();

                // Initialize default values
                int defaultTaskPeriod = leaderboardManagerSection.getInt("task-period");

                ConfigurationSection leaderboardListSection = leaderboardManagerSection.getConfigurationSection("leaderboards.");
                if (leaderboardListSection != null) {
                    for (String name : leaderboardListSection.getKeys(false)) {
                        ConfigurationSection leaderboardSection = leaderboardListSection.getConfigurationSection(name);
                        if (leaderboardSection != null) {
                            Location location = LocationUtils.getLocationFromString(leaderboardSection.getString(".location"));
                            String statistic = leaderboardSection.getString("statistic");
                            List<String> header = leaderboardSection.getStringList("header");


                            if (location != null && location.getWorld() != null && statistic != null) {
                                leaderboards.put(name, new Leaderboard(statistic, Leaderboard.Type.ALL_TIME, location, header));
                            }
                        }
                    }

                    for (String name : leaderboards.keySet()) {
                        Leaderboard leaderboard = leaderboards.get(name);

                        int taskPeriod = defaultTaskPeriod;

                        Object newTaskPeriod = leaderboardManagerSection.get("leaderboards." + name + ".task-period");
                        if (newTaskPeriod instanceof Integer) {
                            taskPeriod = (Integer) newTaskPeriod;
                        }

                        taskIds.add(new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (leaderboard == null || !leaderboards.containsValue(leaderboard))
                                    this.cancel();
                                else
                                    leaderboard.refresh();
                            }
                        }.runTaskTimer(plugin, 0L, taskPeriod).getTaskId());
                    }
                }
            }
        }
    }
}