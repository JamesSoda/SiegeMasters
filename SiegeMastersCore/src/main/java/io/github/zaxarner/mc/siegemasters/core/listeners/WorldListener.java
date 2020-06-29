package io.github.zaxarner.mc.siegemasters.core.listeners;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.logging.Level;

/**
 * Created on 5/7/2020.
 */
public class WorldListener implements Listener {

    private SiegeMastersCore plugin;
    private WorldManager worldManager;

    public WorldListener(SiegeMastersCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getWorldManager();
    }

    @EventHandler
    public void playerTeleportEvent(PlayerTeleportEvent event) {

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to != null && to.getWorld() != null && from.getWorld() != null && to.getWorld() != from.getWorld()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                worldManager.handleWorldChange(event.getPlayer(), from.getWorld(), to.getWorld());
            }, 5L);
        }
    }
}
