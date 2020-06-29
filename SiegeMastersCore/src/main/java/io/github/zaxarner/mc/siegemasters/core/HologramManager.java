package io.github.zaxarner.mc.siegemasters.core;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import io.github.zaxarner.mc.siegemasters.core.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/7/2020.
 */
public class HologramManager {

    private SiegeMastersCore plugin;
    private Map<String, Hologram> holograms = new HashMap<>();
    private List<Integer> taskIds = new ArrayList<>();

    public HologramManager(SiegeMastersCore plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {

            ConfigurationSection hologramManagerSection = plugin.getConfig().getConfigurationSection("hologram-manager.");
            if (hologramManagerSection != null) {
                if (hologramManagerSection.getBoolean("enabled")) {

                    // Delete old Holograms so we can initialize them again.
                    for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
                        hologram.delete();
                    }

                    // Initialize default values
                    int defaultCleanupDistance = hologramManagerSection.getInt("cleanup-distance");
                    int defaultTaskPeriod = hologramManagerSection.getInt("task-period");

                    ConfigurationSection hologramsSection = hologramManagerSection.getConfigurationSection("holograms.");
                    if (hologramsSection != null) {
                        for (String name : hologramsSection.getKeys(false)) {
                            Location location = LocationUtils.getLocationFromString(hologramManagerSection.getString("holograms." + name + ".location"));


                            if (location != null && location.getWorld() != null) {
                                Hologram hologram = HologramsAPI.createHologram(plugin, location);
                                hologram.getVisibilityManager().setVisibleByDefault(false);
                                hologram.setAllowPlaceholders(true);
                                for (String line : hologramManagerSection.getStringList("holograms." + name + ".lines")) {
                                    hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
                                }

                                holograms.put(name, hologram);
                            }
                        }

                        for (String name : holograms.keySet()) {
                            Hologram hologram = holograms.get(name);

                            int taskPeriod = defaultTaskPeriod;

                            Object newTaskPeriod = hologramManagerSection.get("holograms." + name + ".task-period");
                            if (newTaskPeriod instanceof Integer) {
                                taskPeriod = (Integer) newTaskPeriod;
                            }

                            int cleanupDistance = defaultCleanupDistance;

                            Object newCleanupDistance = hologramManagerSection.get("holograms." + name + ".cleanup-distance");
                            if (newCleanupDistance instanceof Integer) {
                                cleanupDistance = (Integer) newCleanupDistance;
                            }

                            final int finalCleanupDistance = cleanupDistance;

                            taskIds.add(new BukkitRunnable() {

                                @Override
                                public void run() {
                                    if (hologram == null || !holograms.containsValue(hologram))
                                        this.cancel();
                                    else
                                        refreshHologram(hologram, finalCleanupDistance);
                                }
                            }.runTaskTimer(plugin, 0L, taskPeriod).getTaskId());
                        }
                    }
                }
            }
        }
    }

    public void refreshHologram(@NotNull Hologram hologram, int cleanupDistance) {

        VisibilityManager visibilityManager = hologram.getVisibilityManager();
        Location loc = hologram.getLocation();
        World world = loc.getWorld();
        if (world != null) {

            List<Player> nearbyPlayers = LocationUtils.getNearbyPlayers(loc, cleanupDistance);

            for (Player p : world.getPlayers()) {
                if (nearbyPlayers.contains(p))
                    visibilityManager.showTo(p);
                else
                    visibilityManager.hideTo(p);
            }
        }
    }
}