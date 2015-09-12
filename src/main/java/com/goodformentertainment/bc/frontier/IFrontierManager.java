package com.goodformentertainment.bc.frontier;

import com.goodformentertainment.bc.util.region.Area;
import com.goodformentertainment.bc.util.region.Column;

import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;

public interface IFrontierManager {
    Area getBlockBounds(World world);

    Area setColumnBounds(World world, Column minColumn, Column maxColumn);

    Area getRegionBounds(World world);

    void setRegionBounds(World world, Column minColumn, Column maxColumn);

    boolean clear(World world);

    boolean inWilderness(Location location);

    void resetAllWildernesses();
}
