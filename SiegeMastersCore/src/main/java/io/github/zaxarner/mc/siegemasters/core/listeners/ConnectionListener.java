package io.github.zaxarner.mc.siegemasters.core.listeners;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.WorldManager;
import io.github.zaxarner.mc.siegemasters.core.database.DatabaseManager;
import io.github.zaxarner.mc.siegemasters.core.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Created on 5/5/2020.
 */
public class ConnectionListener implements Listener {

    private static SiegeMastersCore plugin;
    private static DatabaseManager dbManager;
    private static WorldManager worldManager;

    public ConnectionListener(SiegeMastersCore plugin) {
        this.plugin = plugin;
        dbManager = plugin.getDatabaseManager();
        worldManager = plugin.getWorldManager();
    }

    @EventHandler
    public void onPlayerConnect(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if(dbManager.isBanned(uuid)) {
            String reason = dbManager.getBanReason(uuid);
            if(reason != null) {
                event.setKickMessage("You have been banned for: " + reason);
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        handleJoin(player);

    }

    public static void handleJoin(Player player) {
        if(player.hasPermission("siegemasters.admin")) {
            player.sendMessage(ChatColor.RED + "Running onJoin method for plugin rework. You are only seeing this because you are an Admin.");
            PlayerUtils.safeTeleport(player, worldManager.getSpawnLocation());
            dbManager.initializeStartingData(player);
        }

        worldManager.refreshPlayerListName(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dbManager.savePlayerData(event.getPlayer());
    }

}