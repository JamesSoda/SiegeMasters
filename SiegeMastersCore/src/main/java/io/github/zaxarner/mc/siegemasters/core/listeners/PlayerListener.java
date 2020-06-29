package io.github.zaxarner.mc.siegemasters.core.listeners;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.WorldManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created on 5/13/2020.
 */
public class PlayerListener implements Listener {

    private SiegeMastersCore plugin;
    private WorldManager worldManager;


    public PlayerListener(SiegeMastersCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getWorldManager();
    }



    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(worldManager.getWorldsOfType(WorldManager.WorldType.SPAWN).contains(event.getEntity().getWorld())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerDealDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(player.getGameMode() == GameMode.SURVIVAL) {
                if(worldManager.getWorldsOfType(WorldManager.WorldType.SPAWN).contains(event.getEntity().getWorld())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            if (worldManager.getWorldsOfType(WorldManager.WorldType.SPAWN).contains(event.getBlock().getWorld())) {
                event.setCancelled(true);
            }
        }
    }

}
