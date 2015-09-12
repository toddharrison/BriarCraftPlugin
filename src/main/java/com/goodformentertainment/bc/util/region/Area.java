package com.goodformentertainment.bc.util.region;

public class Area {
    public Column min;
    public Column max;

    public Area() {
        this(0, 0, 0, 0);
    }

    public Area(final int minX, final int minZ, final int maxX, final int maxZ) {
        min = new Column(minX, minZ);
        max = new Column(maxX, maxZ);
    }

    public Area(final Column min, final Column max) {
        this.min = min;
        this.max = max;
    }

    public boolean contains(final Column col) {
        return col.x >= min.x && col.x <= max.x && col.z >= min.z && col.z <= max.z;
    }

    @Override
    public boolean equals(final Object o) {
        boolean equal = false;
        if (o instanceof Area) {
            final Area a = (Area) o;
            equal = min.equals(a.min) && max.equals(a.max);
        }
        return equal;
    }

    @Override
    public String toString() {
        return "{ min: " + min + ", max: " + max + " }";
    }
}
