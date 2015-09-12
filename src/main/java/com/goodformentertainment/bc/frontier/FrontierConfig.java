package com.goodformentertainment.bc.frontier;

import static com.goodformentertainment.bc.Briarcraft.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.goodformentertainment.bc.util.region.Area;
import com.goodformentertainment.bc.util.region.RegionUtil;

import net.canarymod.api.world.World;
import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.utils.PropertiesFile;

public class FrontierConfig {
    private static final String MANAGED_WORLDS = "worlds";
    private static final String WORLD_FRONTIER = "frontier.";

    private final PropertiesFile cfg;

    public FrontierConfig(final Plugin plugin) {
        cfg = Configuration.getPluginConfig(plugin(), "Frontier");

        cfg.setString("foo", "bar");
        cfg.save();
    }

    public Collection<String> getManagedWorldNames() {
        final Set<String> worlds = new HashSet<String>();
        Collections.addAll(worlds, cfg.getStringArray(MANAGED_WORLDS));
        return worlds;
    }

    public void addManagedWorld(final World world) {
        final String[] worldNames = cfg.getStringArray(MANAGED_WORLDS);
        if (!Arrays.asList(worldNames).contains(world.getFqName())) {
            final String[] newNames = new String[worldNames.length + 1];
            System.arraycopy(worldNames, 0, newNames, 0, worldNames.length);
            newNames[worldNames.length] = world.getFqName();

            cfg.setStringArray(MANAGED_WORLDS, newNames);
            cfg.save();
        }
    }

    public boolean removeManagedWorld(final World world) {
        boolean removed = false;
        final String[] worldNames = cfg.getStringArray(MANAGED_WORLDS);
        final List<String> nameList = new ArrayList<String>(Arrays.asList(worldNames));
        if (nameList.remove(world.getFqName())) {
            final String[] newNames = nameList.toArray(new String[nameList.size()]);
            cfg.setStringArray(MANAGED_WORLDS, newNames);
            cfg.removeKey(WORLD_FRONTIER + world.getFqName());
            cfg.save();
            removed = true;
        }
        return removed;
    }

    public Area getRegionBounds(final World world) {
        Area bounds = null;

        final String key = WORLD_FRONTIER + world.getFqName();
        if (cfg.containsKey(key)) {
            final int[] points = cfg.getIntArray(key);

            if (points != null && points.length == 4) {
                final int xMin = points[0];
                final int zMin = points[1];
                final int xMax = points[2];
                final int zMax = points[3];

                bounds = RegionUtil.columnsToArea(xMin, zMin, xMax, zMax);
            }
        }

        return bounds;
    }

    public Area getRegionBounds(final String worldFqName) {
        Area bounds = null;

        final String key = WORLD_FRONTIER + worldFqName;
        if (cfg.containsKey(key)) {
            final int[] points = cfg.getIntArray(key);

            if (points != null && points.length == 4) {
                final int xMin = points[0];
                final int zMin = points[1];
                final int xMax = points[2];
                final int zMax = points[3];

                bounds = RegionUtil.columnsToArea(xMin, zMin, xMax, zMax);
            }
        }

        return bounds;
    }

    public void setRegionBounds(final World world, final int xMin, final int zMin, final int xMax,
            final int zMax) {
        setRegionBounds(world, new Area(xMin, zMin, xMax, zMax));
    }

    public void setRegionBounds(final World world, final Area bounds) {
        addManagedWorld(world);
        final int points[] = RegionUtil.areaToColumnsArray(bounds);
        cfg.setIntArray(WORLD_FRONTIER + world.getFqName(), points);
        cfg.save();
    }
}
