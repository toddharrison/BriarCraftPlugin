package com.goodformentertainment.bc.frontier;

import static com.goodformentertainment.bc.Briarcraft.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.goodformentertainment.bc.util.region.Area;
import com.goodformentertainment.bc.util.region.Column;
import com.goodformentertainment.bc.util.region.RegionUtil;

import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.api.world.WorldManager;
import net.canarymod.api.world.position.Location;

public class FrontierManager implements IFrontierManager {
    private final FrontierConfig config;

    public FrontierManager(final FrontierConfig config) {
        this.config = config;
    }

    @Override
    public Area getBlockBounds(final World world) {
        return RegionUtil.regionToColumnBounds(config.getRegionBounds(world));
    }

    @Override
    public Area setColumnBounds(final World world, final Column minColumn, final Column maxColumn) {
        final int regionMinX = RegionUtil.fromColumnToRegion(minColumn.x);
        final int regionMinZ = RegionUtil.fromColumnToRegion(minColumn.z);
        final int regionMaxX = RegionUtil.fromColumnToRegion(maxColumn.x);
        final int regionMaxZ = RegionUtil.fromColumnToRegion(maxColumn.z);
        config.setRegionBounds(world, regionMinX, regionMinZ, regionMaxX, regionMaxZ);

        final int columnMinX = RegionUtil.fromRegionToColumn(regionMinX, true);
        final int columnMinZ = RegionUtil.fromRegionToColumn(regionMinZ, true);
        final int columnMaxX = RegionUtil.fromRegionToColumn(regionMaxX, false);
        final int columnMaxZ = RegionUtil.fromRegionToColumn(regionMaxZ, false);
        return RegionUtil.columnsToArea(columnMinX, columnMinZ, columnMaxX, columnMaxZ);
    }

    @Override
    public Area getRegionBounds(final World world) {
        return config.getRegionBounds(world);
    }

    @Override
    public void setRegionBounds(final World world, final Column minColumn, final Column maxColumn) {
        final Area bounds = new Area(minColumn, maxColumn);
        config.setRegionBounds(world, bounds);
    }

    @Override
    public boolean clear(final World world) {
        return config.removeManagedWorld(world);
    }

    @Override
    public boolean inWilderness(final Location location) {
        final Area regionBounds = config.getRegionBounds(location.getWorld());
        final Area blockBounds = RegionUtil.regionToColumnBounds(regionBounds);
        return !blockBounds.contains(new Column(location.getBlockX(), location.getBlockZ()));
    }

    @Override
    public void resetAllWildernesses() {
        final WorldManager worldManager = Canary.getServer().getWorldManager();
        final Collection<String> loadedWorldNames = Arrays
                .asList(worldManager.getLoadedWorldsNames());
        for (final String worldFqName : config.getManagedWorldNames()) {
            if (loadedWorldNames.contains(worldFqName)) {
                log().warn("Unable to reset wilderness in " + worldFqName);
            } else if (worldFqName != null && !worldFqName.isEmpty()) {
                final String worldName = worldFqName.substring(0, worldFqName.indexOf("_"));

                try {
                    final File worldsDir = new File("worlds");
                    if (worldsDir.exists()) {
                        final File regionDir = new File(worldsDir,
                                worldName + "/" + worldFqName + "/region");
                        if (regionDir.exists()) {

                            final Area regionBounds = config.getRegionBounds(worldFqName);

                            final Pattern pattern = Pattern
                                    .compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$");
                            for (final String filename : regionDir.list()) {
                                final Matcher matcher = pattern.matcher(filename);
                                if (matcher.matches()) {
                                    final int x = Integer.parseInt(matcher.group(1));
                                    final int z = Integer.parseInt(matcher.group(2));
                                    if (regionBounds.min.x > x || regionBounds.max.x < x
                                            || regionBounds.min.z > z || regionBounds.max.z < z) {
                                        final File regionFile = new File(regionDir, filename);
                                        if (regionFile.delete()) {
                                            log().info(
                                                    "Deleted " + filename + " for " + worldFqName);
                                        } else {
                                            log().error("Failed deleting " + filename + " for "
                                                    + worldFqName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (final Exception e) {
                    log().error("Error deleting region files", e);
                }
            }
        }
    }
}
