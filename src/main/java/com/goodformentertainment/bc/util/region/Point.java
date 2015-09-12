package com.goodformentertainment.bc.util.region;

public class Point {
    public int x;
    public int y;
    public int z;

    public Point(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(final Object o) {
        boolean equal = false;
        if (o instanceof Point) {
            final Point p = (Point) o;
            equal = x == p.x && y == p.y && z == p.z;
        }
        return equal;
    }

    @Override
    public String toString() {
        return "[x:" + x + ", y:" + y + ", z:" + z + "]";
    }
}
