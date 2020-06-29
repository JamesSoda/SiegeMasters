package io.github.zaxarner.mc.siegemasters.pvp;

import co.aikar.commands.PaperCommandManager;
import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created on 5/1/2020.
 */
public class SiegeMastersPVP extends JavaPlugin {

    private static SiegeMastersPVP plugin;
    private static SiegeMastersCore core;
    private static PaperCommandManager commandManager;


    @Override
    public void onEnable() {
        plugin = this;

        core = (SiegeMastersCore) Bukkit.getPluginManager().getPlugin("SiegeMastersCore");
        if(core == null) {
            log("CORE PLUGIN IS NULL!", Level.WARNING);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        saveDefaultConfig();

        initializeDatabases();
        initializeManagers();
        initializeTasks();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        commandManager.unregisterCommands();
    }

    private void initializeDatabases() {

    }

    private void initializeManagers() {

    }

    private void initializeTasks() {
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("help");


    }

    private void registerEvents() {

    }


    public static SiegeMastersPVP getPlugin() {
        return plugin;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public static void log(String message) {
        getPlugin().getLogger().log(Level.INFO, message);
    }

    public static void log(String message, Level level) {
        getPlugin().getLogger().log(level, message);
    }
}