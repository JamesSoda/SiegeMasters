package io.github.zaxarner.mc.siegemasters.core.database;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/24/2020.
 */
public class PlayerData {

    private OfflinePlayer offlinePlayer;

    private Date bannedUntil;
    private Date mutedUntil;

    private Date firstJoin;
    private Date lastJoin;

    private Map<String, Double> statistics = new HashMap<>();
    private Map<String, Double> monthly_statistics = new HashMap<>();

    public PlayerData(@NotNull OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public void refreshLastJoin() {
        lastJoin = new Date(System.currentTimeMillis());
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    @Nullable
    public Double getStatistic(String statistic) {
        statistic = statistic.toUpperCase();
        return statistics.get(statistic);
    }

    public void setStatistic(String statistic, Double value) {
        statistic = statistic.toUpperCase();
        statistics.put(statistic, value);
    }

    public Map<String, Double> getStatistics() {
        return statistics;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(Date bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public Date getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(Date mutedUntil) {
        this.mutedUntil = mutedUntil;
    }
}
