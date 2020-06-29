package io.github.zaxarner.mc.siegemasters.core.utils;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import io.github.zaxarner.mc.siegemasters.core.database.DatabaseManager;
import io.github.zaxarner.mc.siegemasters.core.statistics.CoreStatistic;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * Created on 5/7/2020.
 */
public class PlayerUtils {

    private static DatabaseManager dbManager = SiegeMastersCore.getPlugin().getDatabaseManager();

    public static void safeTeleport(@NotNull Player player, @Nullable Location location) {
        if(location != null && location.getWorld() != null) {
            player.teleport(location);
        } else {
            SiegeMastersCore.log("Attempted to teleport [" + player.getName() + "] to NULL", Level.WARNING);
        }
    }

    /**
     * Gets the total exp required for a level.
     * @param level
     * @return
     */
    public static int getTotalRequiredExpForLevel(int level) {
        int result = 0;
        for(int i=0; i < level; i++) {
            result += (int) Math.floor(80 * (Math.pow(2, level / 7)));
        }
        return result;
    }

    public static int getLevelFromExp(int exp) {
        int level = 0;
        while(getTotalRequiredExpForLevel(level) < exp) {
            level++;
        }
        return level;
    }

    public static int getExpDifference(int levelStart, int levelTarget) {
        return getTotalRequiredExpForLevel(levelTarget) - getTotalRequiredExpForLevel(levelStart);
    }

    /**
     * Sets the current level of a player and sets remaining exp for next level to 0. Level is not actually stored in
     * the database; it is calculated given the player's total exp. This method simply sets the player's exp to the
     * required exp for level
     * @param player
     * @param level
     */
    public static void setLevel(@NotNull OfflinePlayer player, @NotNull Integer level) {
        setExp(player, getTotalRequiredExpForLevel(level));
    }

    /**
     * Gets the current level of a player. Defaults to 0. Level is not actually stored in the database; it is calculated
     * given the player's total exp.
     * @param player
     * @return
     */
    public static Integer getLevel(@NotNull OfflinePlayer player) {
        return getLevelFromExp(getExp(player));
    }

    /**
     * Sets the current exp of a player.
     * @param player
     * @param exp
     */
    public static void setExp(@NotNull OfflinePlayer player, @NotNull Integer exp) {
        dbManager.setPlayerStatistic(player, CoreStatistic.EXP.name(), exp.doubleValue());
    }

    /**
     * Adds exp to a player
     * @param player
     * @param exp
     */
    public static void addExp(@NotNull OfflinePlayer player, @NotNull Integer exp) {
        dbManager.setPlayerStatistic(player, CoreStatistic.EXP.name(), (double) (getExp(player) + exp));
    }

    /**
     * Gets the current exp of a player. Defaults to 0.
     * @param player
     * @return
     */
    public static Integer getExp(@NotNull OfflinePlayer player) {
        Double exp = dbManager.getPlayerStatistic(player, CoreStatistic.EXP.name());
        if(exp != null) {
            return exp.intValue();
        }
        return 0;
    }
}