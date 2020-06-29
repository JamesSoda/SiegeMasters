package io.github.zaxarner.mc.siegemasters.core.tasks;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created on 5/13/2020.
 */
public class SpawnSpeedTask extends BukkitRunnable {

    private WorldManager worldManager;

    public SpawnSpeedTask(SiegeMastersCore plugin) {

        worldManager = plugin.getWorldManager();


        runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {


        for(Player p : Bukkit.getOnlinePlayers()) {
            if(worldManager.getWorldType(p.getWorld()) == WorldManager.WorldType.SPAWN) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
            }
        }

    }
}
