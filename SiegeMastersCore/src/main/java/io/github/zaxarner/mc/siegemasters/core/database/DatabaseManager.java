package io.github.zaxarner.mc.siegemasters.core.database;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.database.connection.Database;
import io.github.zaxarner.mc.siegemasters.core.database.connection.SQLiteDatabase;
import io.github.zaxarner.mc.siegemasters.core.database.connection.WhereClause;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 5/24/2020.
 * Used to cache, query, and update information contained in the database.
 */
public class DatabaseManager {

    private SiegeMastersCore plugin;

    private Database database;

    private Map<OfflinePlayer, PlayerData> playerDataMap = new HashMap<>();

    public DatabaseManager(SiegeMastersCore plugin) {
        this.plugin = plugin;

        // This is where I would potentially determine the type of database to use. However, I am only implementing SQLite
        database = new SQLiteDatabase(plugin);

        database.load();
        database.checkInitialization();

        // Save player data every minute. Additionally saved when the player quits.
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player p : Bukkit.getOnlinePlayers()) {
                savePlayerData(p);
            }

            playerDataMap.entrySet().removeIf(entry -> !entry.getKey().isOnline());
        }, 20L * 60, 20L * 60);
    }

    /**
     * Initializes the starting data for a player; the date they first joined and their username.
     *
     * @param player
     */
    public void initializeStartingData(Player player) {
        database.initializeStartingData(player);
    }

    @NotNull
    public PlayerData getPlayerData(@NotNull OfflinePlayer player) {
        if (playerDataMap.containsKey(player))
            return playerDataMap.get(player);

        PlayerData data = new PlayerData(player);
        playerDataMap.put(player, data);

        return data;
    }

    /**
     * If there is a current PlayerData instance for the player in playerDataMap, then save the information contained
     * in the PlayerData instance to the database.
     * @param player
     */
    public void savePlayerData(@NotNull OfflinePlayer player) {
        if(playerDataMap.get(player) != null) {
            PlayerData data = playerDataMap.get(player);

            Map<String, Double> statistics = data.getStatistics();

            for(String statistic : statistics.keySet()) {
                Double value = statistics.get(statistic);
                database.setValue("player_statistics", "value", value,
                        new WhereClause("uuid", player.getUniqueId().toString()),
                        new WhereClause("statistic", statistic));
            }
        }
    }

    @Nullable
    public Double getPlayerStatistic(@NotNull OfflinePlayer player, @NotNull String statistic) {
        statistic = statistic.toUpperCase();
        PlayerData data = getPlayerData(player);

        Double result = data.getStatistic(statistic);
        if (result != null) {
            return result;
        }

        Object resultObj = database.queryValue("player_statistics", "value",
                new WhereClause("uuid", player.getUniqueId().toString()),
                new WhereClause("statistic", statistic));

        if (resultObj instanceof Double) {
            result = (Double) resultObj;
            data.setStatistic(statistic, result);
            return result;
        } else
            return null;
    }

    public void setPlayerStatistic(@NotNull OfflinePlayer player, @NotNull String statistic, @NotNull Double value) {
        statistic = statistic.toUpperCase();
        PlayerData data = getPlayerData(player);

        data.setStatistic(statistic, value);
    }

    @NotNull
    public Map<UUID, Double> getTopStatistics(@NotNull String statistic, @NotNull Integer count) {
        return database.getTopStatistics(statistic, count);
    }

    public void mutePlayer(OfflinePlayer player, LocalDateTime until, String reason, CommandSender banner) {
        database.mutePlayer(player, until, reason, banner);
    }

    public boolean isMuted(@NotNull OfflinePlayer uuid) {
        Date now = new Date(System.currentTimeMillis());

        Object result = database.queryValue("mutes", "until",
                new WhereClause("uuid", uuid.toString()));

        Date mutedUntil = null;

        if (result instanceof java.sql.Date) {
            mutedUntil = new Date(((java.sql.Date) result).getTime());
        }

        if (mutedUntil != null) {
            return now.after(mutedUntil);
        }

        return false;
    }

    @Nullable
    public String getMuteReason(@NotNull OfflinePlayer uuid) {
        if(!isMuted(uuid))
            return null;

        Object result = database.queryValue("mutes", "reason",
                new WhereClause("uuid", uuid.toString()));

        if(result instanceof String)
            return (String) result;

        return null;
    }
}