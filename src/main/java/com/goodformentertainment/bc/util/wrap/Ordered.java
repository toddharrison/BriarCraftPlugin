package com.goodformentertainment.bc.util.wrap;

public class Ordered<T> implements Comparable<Ordered<T>> {
    public static <T> Ordered<T> wrap(final int index, final T object) {
        return new Ordered<T>(index, object);
    }

    private final int index;
    private final T object;

    public Ordered(final int index, final T object) {
        this.index = index;
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public T get() {
        return object;
    }

    @Override
    public int compareTo(final Ordered<T> ordered) {
        return index - ordered.index;
    }
}
