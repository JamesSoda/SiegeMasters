package io.github.zaxarner.mc.siegemasters.core.utils;

import io.github.zaxarner.mc.siegemasters.core.SiegeMastersCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created on 5/6/2020.
 */
public class LocationUtils {

    public static String getLocationString(@NotNull Location location, boolean center) {
        return getLocationString(location, center, false);
    }

    public static String getLocationString(@NotNull Location location, boolean center, boolean includeFacing) {
        Location loc = location.clone();
        World world = loc.getWorld();
        if (world == null) {
            SiegeMastersCore.log("Attempted to getLocationString() from location with no world", Level.WARNING);
            return "";
        }


        if (center) {
            loc = getCenter(loc);
        }

        if (includeFacing)
            return world.getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
        else
            return world.getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + location.getZ();
    }

    @Nullable
    public static Location getLocationFromString(String string) {
        string = string.trim();

        if (string.equalsIgnoreCase(""))
            return null;

        String[] parts = string.split(":");

        Location loc = null;

        if (parts.length == 4 || parts.length == 6) {

            World world = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            loc = new Location(world, x, y, z);
        }

        if(loc != null && parts.length == 6) {
            loc.setYaw(Float.parseFloat(parts[4]));
            loc.setPitch(Float.parseFloat(parts[5]));
        }

        return loc;
    }

    public static boolean compare(Location loc1, Location loc2) {
        return loc1.getWorld() == loc2.getWorld() && loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static Location getCenter(Location loc) {
        Location result = loc.clone();

            result.setX(result.getBlockX() + .5);
            result.setY(result.getBlockY() + .5);
            result.setZ(result.getBlockZ() + .5);

        return result;
    }

    public static List<Player> getNearbyPlayers(Location location, double range) {

        List<Player> players = new ArrayList<>();

        World world = location.getWorld();
        if(world == null)
            return players;

        for(Entity ent : world.getNearbyEntities(location, range, range, range)) {
            if(ent instanceof Player)
                players.add((Player) ent);
        }

        return players;
    }

    public static List<Entity> getNearbyEntities(Location location, double range) {

        List<Entity> entities = new ArrayList<>();

        World world = location.getWorld();
        if(world == null)
            return entities;

        entities.addAll(world.getNearbyEntities(location, range, range, range));

        List<Entity> players = new ArrayList<>();
        entities.stream().filter(ent -> ent instanceof Player).forEach(players::add);

        entities.removeAll(players);

        return entities;
    }

}
