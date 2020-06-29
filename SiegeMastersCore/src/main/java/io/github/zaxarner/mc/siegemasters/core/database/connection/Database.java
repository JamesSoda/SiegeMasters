package io.github.zaxarner.mc.siegemasters.core.database.connection;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

/**
 * Created on 5/23/2020.
 */
public abstract class Database {

    String databaseName;

    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

    public void checkInitialization() {
        try (Connection connection = getConnection()) {
            if(connection == null) {
                SiegeMastersCore.log("Could not connect to Database!", Level.WARNING);
                return;
            }

            SiegeMastersCore.log("Successfully connected to Database!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public abstract List<String> getCreateTablesStatements();

    @Nullable
    public abstract Connection getConnection();

    public abstract void load();

    protected abstract Boolean executeStatement(@NotNull String statement);

    @Nullable
    public abstract Object queryValue(@NotNull String table, @NotNull String queryColumn, WhereClause... clauses);

    public abstract List<Object> queryColumn(@NotNull String table, @NotNull String queryColumn);

    public abstract Map<UUID, Double> getTopStatistics(@NotNull String statistic, @NotNull Integer count);

    public abstract Boolean setValue(@NotNull String table, @NotNull String column, @NotNull Object value, WhereClause... clauses);

    public abstract void initializeStartingData(@NotNull Player player);

    /**
     *
     * @param uuid
     * @param until
     * @param reason
     * @param banner Either a player or Console. If it is a player, the uuid_muter in the database is the player uuid
     *               If the banner is Console, the uuid_muter in the database is "CONSOLE".
     */
    public abstract void mutePlayer(@NotNull OfflinePlayer player, @NotNull LocalDateTime until, @NotNull String reason,
                                    @NotNull CommandSender banner);

}