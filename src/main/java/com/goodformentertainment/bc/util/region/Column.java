package com.goodformentertainment.bc.util.region;

public class Column {
    public int x;
    public int z;

    public Column(final int x, final int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(final Object o) {
        boolean equal = false;
        if (o instanceof Column) {
            final Column c = (Column) o;
            equal = x == c.x && z == c.z;
        }
        return equal;
    }

    @Override
    public String toString() {
        return "[x:" + x + ", z:" + z + "]";
    }
}
