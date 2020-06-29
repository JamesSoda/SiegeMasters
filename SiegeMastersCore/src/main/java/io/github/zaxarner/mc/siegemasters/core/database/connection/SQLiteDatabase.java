package io.github.zaxarner.mc.siegemasters.core.database.connection;


import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;


/**
 * Created on 4/24/2020.
 */
public class SQLiteDatabase extends Database {
    private final Plugin plugin;

    private Connection connection;

    public SQLiteDatabase(Plugin plugin) {
        super("sqlite_siegemasters");
        this.plugin = plugin;
    }

    @Override
    public List<String> getCreateTablesStatements() {
        List<String> tablesStatements = new ArrayList<>();

        tablesStatements.add("CREATE TABLE IF NOT EXISTS player (" +
                "uuid TEXT PRIMARY KEY," +
                "username TEXT," +
                "firstjoin TEXT DEFAULT CURRENT_TIMESTAMP," +
                "lastonline TEXT DEFAULT CURRENT_TIMESTAMP);");

        tablesStatements.add("CREATE TABLE IF NOT EXISTS player_statistics (" +
                "uuid TEXT," +
                "statistic TEXT," +
                "value REAL," +
                "PRIMARY KEY (uuid, statistic));");

        tablesStatements.add("CREATE TABLE IF NOT EXISTS mutes (" +
                "uuid TEXT PRIMARY KEY," +
                "until TEXT," +
                "reason TEXT," +
                "uuid_muter TEXT);");

        return tablesStatements;
    }

    @Override
    @Nullable
    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        File databaseFile = new File(plugin.getDataFolder(), databaseName + ".db");
        if (!databaseFile.exists()) {
            try {
                if (databaseFile.createNewFile()) {
                    SiegeMastersCore.log("Created File: [" + databaseName + ".db]");

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
            // Is this necessary?
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void load() {
        connection = getConnection();
        if (connection != null) {
            try {
                List<String> statements = getCreateTablesStatements();


                for (String statement : statements) {
                    Statement s = connection.createStatement();
                    s.execute(statement);
                    s.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected Boolean executeStatement(@NotNull String statement) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            if (conn != null) {
                ps = conn.prepareStatement(statement);
                return !ps.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }


    @Override
    @Nullable
    public Object queryValue(@NotNull String table, @NotNull String queryColumn, WhereClause... clauses) {
        Connection conn = null;
        PreparedStatement ps = null;
        Object result = null;

        if (clauses.length <= 0) {
            SiegeMastersCore.log("Attempted to queryValue with no WhereClause! Use queryColumn instead.", Level.WARNING);
            return null;
        }

        try {
            conn = getConnection();
            if (conn != null) {
                StringBuilder statement = new StringBuilder("SELECT " + queryColumn + " FROM " + table + " WHERE ");

                for (WhereClause clause : clauses) {
                    if (clause.getColumnValue() instanceof String || clause.getColumnValue() instanceof Date) {
                        statement.append(clause.getColumnName()).append("='").append(clause.getColumnValue()).append("'");
                    } else {
                        statement.append(clause.getColumnName()).append("=").append(clause.getColumnValue());
                    }
                    statement.append(" AND ");
                }
                String finStatement = statement.substring(0, statement.lastIndexOf(" AND "));

                ps = conn.prepareStatement(finStatement + ";");

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    result = rs.getObject(queryColumn);
                }
                rs.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    @Override
    @Nullable
    public List<Object> queryColumn(@NotNull String table, @NotNull String queryColumn) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Object> result = new ArrayList<>();
        try {
            conn = getConnection();
            if (conn != null) {
                ps = conn.prepareStatement("SELECT " + queryColumn + " FROM " + table + ";");

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    result.add(rs.getObject(queryColumn));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Map<UUID, Double> getTopStatistics(@NotNull String statistic, @NotNull Integer count) {
        Connection conn = null;
        PreparedStatement ps = null;
        Map<UUID, Double> result = new HashMap<>();
        try {
            conn = getConnection();
            if (conn != null) {
                String statement = "SELECT uuid,value FROM player_statistics WHERE statistic='" + statistic + "' " +
                        "ORDER BY value DESC LIMIT " + count;
                ps = conn.prepareStatement(statement);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Object uuidObject = rs.getObject("uuid");
                    Object valueObject = rs.getObject("value");

                    if (uuidObject instanceof String && valueObject instanceof Double) {
                        result.put(UUID.fromString((String) uuidObject), (Double) valueObject);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * Sets column's value to value using the WHERE clauses
     * Can potentially fail if there is no current value to update if the clauses do not specify enough information such
     * that it violates any constraints of the table.
     *
     * @param table
     * @param column
     * @param value
     * @param clauses
     * @return
     */
    @Override
    @Nullable
    public Boolean setValue(@NotNull String table, @NotNull String column, @NotNull Object value, WhereClause... clauses) {
        if (clauses.length <= 0) {
            SiegeMastersCore.log("Attempted to setValue with no WhereClause!", Level.WARNING);
            return false;
        }

        if (queryValue(table, column, clauses) == null) {

            StringBuilder statement = new StringBuilder("INSERT INTO " + table + " ");

            StringBuilder columns = new StringBuilder("(");

            for (WhereClause clause : clauses) {
                columns.append(clause.getColumnName()).append(", ");
            }
            columns.append(column).append(") ");

            StringBuilder values = new StringBuilder("VALUES (");

            for (WhereClause clause : clauses) {
                Object clauseValue = clause.getColumnValue();
                if(clauseValue instanceof String) {

                    values.append("'").append(clauseValue).append("', ");
                } else {
                    values.append(clauseValue).append(", ");
                }
            }
            values.append(value).append(");");

            statement.append(columns).append(values);

            return executeStatement(statement.toString());

        } else {

            StringBuilder statement;

            if (value instanceof String || value instanceof Date) {
                statement = new StringBuilder("UPDATE " + table + " SET " + column + "='" + value + "'");
            } else {
                statement = new StringBuilder("UPDATE " + table + " SET " + column + "=" + value);
            }

            statement.append(" WHERE ");

            for (WhereClause clause : clauses) {
                if (clause.getColumnValue() instanceof String || clause.getColumnValue() instanceof Date) {
                    statement.append(clause.getColumnName()).append("='").append(clause.getColumnValue()).append("'");
                } else {
                    statement.append(clause.getColumnName()).append("=").append(clause.getColumnValue());
                }
                statement.append(" AND ");
            }

            return executeStatement(statement.substring(0, statement.lastIndexOf(" AND ")) + ";");
        }
    }

    @Override
    public void initializeStartingData(@NotNull Player player) {
        executeStatement("INSERT OR IGNORE INTO player (uuid) VALUES(" +
                "'" + player.getUniqueId().toString() + "');");

        executeStatement("UPDATE player SET username='" + player.getName() + "', lastonline=datetime('now') WHERE " +
                "uuid='" + player.getUniqueId().toString() + "';");
    }

    @Override
    public void mutePlayer(@NotNull OfflinePlayer player, @NotNull LocalDateTime until, @NotNull String reason,
                          @NotNull CommandSender muter) {
        executeStatement("INSERT OR IGNORE INTO mutes (uuid) VALUES('" + player.getUniqueId().toString() + "');");

        String muterName = null;

        if(muter instanceof ConsoleCommandSender) {
            muterName = "CONSOLE";
        } else if(muter instanceof OfflinePlayer) {
            muterName = ((OfflinePlayer) muter).getUniqueId().toString();
        }

        if(muterName != null) {
            executeStatement("UPDATE mutes SET until='" + until.toString() + "', reason='" + reason + "', uuid_punisher='"
                    + muterName + "' WHERE " + "uuid='" + player.getUniqueId().toString() + "';");
        }
    }

}
