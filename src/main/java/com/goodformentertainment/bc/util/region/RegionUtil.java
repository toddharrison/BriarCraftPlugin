package com.goodformentertainment.bc.util.region;

public final class RegionUtil {
    public static final int COLUMNS_PER_CHUNK = 16;
    public static final int CHUNKS_PER_REGION = 32;
    public static final int COLUMNS_PER_REGION = CHUNKS_PER_REGION * COLUMNS_PER_CHUNK;

    // Region 0:0 = [0:0, 512:512)
    // Region -1:-1 = [-512:-512, 0:0)

    public static Area columnsToArea(final int minX, final int minZ, final int maxX,
            final int maxZ) {
        return new Area(minX, minZ, maxX, maxZ);
    }

    public static int[] areaToColumnsArray(final Area bounds) {
        return new int[] { bounds.min.x, bounds.min.z, bounds.max.x, bounds.max.z };
    }

    public static Area regionToColumnBounds(final Area regionBounds) {
        Area columnBounds = null;
        if (regionBounds != null) {
            final int[] regionPoints = areaToColumnsArray(regionBounds);

            final int minX = fromRegionToColumn(regionPoints[0], true);
            final int minZ = fromRegionToColumn(regionPoints[1], true);
            final int maxX = fromRegionToColumn(regionPoints[2], false);
            final int maxZ = fromRegionToColumn(regionPoints[3], false);

            columnBounds = columnsToArea(minX, minZ, maxX, maxZ);
        }
        return columnBounds;
    }

    public static int fromColumnToRegion(final int columnLoc) {
        return (int) Math.floor(columnLoc / (double) COLUMNS_PER_REGION);
    }

    public static int fromRegionToColumn(final int regionLoc, final boolean roundDown) {
        if (roundDown) {
            return regionLoc * COLUMNS_PER_REGION;
        } else {
            return (regionLoc + 1) * COLUMNS_PER_REGION - 1;
        }
    }
}
