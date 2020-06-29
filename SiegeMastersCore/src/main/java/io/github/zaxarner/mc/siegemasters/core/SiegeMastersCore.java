package io.github.zaxarner.mc.siegemasters.core;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import io.github.zaxarner.mc.siegemasters.core.cmds.CoreCommand;
import io.github.zaxarner.mc.siegemasters.core.cmds.TestCommand;
import io.github.zaxarner.mc.siegemasters.core.database.DatabaseManager;
import io.github.zaxarner.mc.siegemasters.core.leaderboards.LeaderboardManager;
import io.github.zaxarner.mc.siegemasters.core.listeners.ConnectionListener;
import io.github.zaxarner.mc.siegemasters.core.listeners.PlayerListener;
import io.github.zaxarner.mc.siegemasters.core.listeners.WorldListener;
import io.github.zaxarner.mc.siegemasters.core.tasks.SpawnSpeedTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created on 5/1/2020.
 */
public class SiegeMastersCore extends JavaPlugin {

    private static SiegeMastersCore plugin;
    private static PaperCommandManager commandManager;
    private static DatabaseManager dbManager;
    private static WorldManager worldManager;
    private static MessageManager messageManager;
    private static HologramManager hologramManager;
    private static LeaderboardManager leaderboardManager;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        initializeManagers();
        initializeTasks();
        registerCommands();
        registerEvents();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ConnectionListener.handleJoin(p);
            }
        }, 5L);
    }

    @Override
    public void onDisable() {
        commandManager.unregisterCommands();

        for(Player p : Bukkit.getOnlinePlayers()) {
            dbManager.savePlayerData(p);
        }
    }

    private void initializeManagers() {
        dbManager = new DatabaseManager(plugin);
        worldManager = new WorldManager(this);
        messageManager = new MessageManager(this);
        hologramManager = new HologramManager(this);
        leaderboardManager = new LeaderboardManager(this);
    }

    private void initializeTasks() {
        new SpawnSpeedTask(this);
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new TestCommand());
        commandManager.registerCommand(new CoreCommand(this));

        commandManager.getCommandCompletions().registerAsyncCompletion("reload-options", c ->
                ImmutableList.of("all", "holograms", "messages"));

        List<String> units = new ArrayList<>();
        for(ChronoUnit unit : ChronoUnit.values()) {
            units.add(unit.name());
        }
        commandManager.getCommandCompletions().registerAsyncCompletion("time-units", c -> units);


        commandManager.setFormat(MessageType.HELP, ChatColor.DARK_PURPLE, ChatColor.YELLOW, ChatColor.GOLD);
        commandManager.setFormat(MessageType.INFO, ChatColor.DARK_PURPLE, ChatColor.YELLOW, ChatColor.GOLD);
        commandManager.setFormat(MessageType.ERROR, ChatColor.RED, ChatColor.DARK_PURPLE, ChatColor.GOLD);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.GOLD);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    public static SiegeMastersCore getPlugin() {
        return plugin;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public static void log(String message) {
        getPlugin().getLogger().log(Level.INFO, message);
    }

    public static void log(String message, Level level) {
        getPlugin().getLogger().log(level, message);
    }
}